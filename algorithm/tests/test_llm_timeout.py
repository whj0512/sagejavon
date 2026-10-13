"""Real SDK timeout regressions using a local HTTP server; no provider calls."""
import importlib.util
import os
from pathlib import Path
import sys
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from threading import Thread
import time
from types import ModuleType
import unittest
from unittest.mock import Mock, patch

import httpx
from openai import APITimeoutError


def load_llm(provider, **env):
    dependencies = {
        'server.logger.logger_config': {'my_logger': Mock()},
        'sparkai.llm.llm': {'ChatSparkLLM': Mock(), 'ChunkPrintHandler': Mock()},
        'sparkai.core.messages': {'ChatMessage': Mock()},
    }
    modules = {}
    for name, attributes in dependencies.items():
        module = ModuleType(name)
        module.__dict__.update(attributes)
        modules[name] = module
    path = Path(__file__).resolve().parents[1] / 'server/rag/generation/llm.py'
    spec = importlib.util.spec_from_file_location('llm_under_test', path)
    module = importlib.util.module_from_spec(spec)
    config = {
        'LLM_NAME': provider, 'OPENAI_API_KEY': 'test',
        'ZHIPUAI_API_KEY': 'test.test', 'DEEPSEEK_API_KEY': 'test',
        'OLLAMA_BASE_URL': 'http://localhost:11434',
        'GPT_MODEL_NAME': 'test', 'LLM_READ_TIMEOUT_SECONDS': '0.05',
        'LLM_CONNECT_TIMEOUT_SECONDS': '0.05',
        **env,
    }
    with patch.dict(sys.modules, modules), patch.dict(os.environ, config):
        spec.loader.exec_module(module)
    return module.llm_generator


class TimeoutTests(unittest.TestCase):
    def test_provider_clients_have_bounded_waits(self):
        for provider in ('OpenAI', 'DeepSeek', 'Ollama', 'ZhipuAI'):
            with self.subTest(provider=provider):
                generator = load_llm(provider)
                try:
                    self.assertEqual(generator.client.timeout.read, 0.05)
                    self.assertEqual(generator.client.timeout.connect, 0.05)
                    if provider != 'ZhipuAI':
                        self.assertEqual(generator.client.max_retries, 0)
                finally:
                    generator.client.close()

    def test_silent_upstream_times_out_without_retry(self):
        class SilentHandler(BaseHTTPRequestHandler):
            calls = 0

            def do_POST(self):
                type(self).calls += 1
                time.sleep(0.3)
                self.send_response(200)
                self.end_headers()

            def log_message(self, *_):
                pass

        server = ThreadingHTTPServer(('127.0.0.1', 0), SilentHandler)
        thread = Thread(target=server.serve_forever, daemon=True)
        thread.start()
        generator = load_llm('OpenAI')
        generator.client.base_url = httpx.URL(f'http://127.0.0.1:{server.server_port}/v1/')
        try:
            with self.assertRaises(APITimeoutError):
                generator.generate('hello', is_streaming=True)
            self.assertEqual(SilentHandler.calls, 1)
        finally:
            generator.client.close()
            server.shutdown()
            server.server_close()
            thread.join()

    def test_upstream_stalls_after_first_chunk(self):
        class StalledHandler(BaseHTTPRequestHandler):
            def do_POST(self):
                self.send_response(200)
                self.send_header('Content-Type', 'text/event-stream')
                self.end_headers()
                self.wfile.write(b'data: {"choices":[{"index":0,"delta":{"content":"hello"}}]}\n\n')
                self.wfile.flush()
                time.sleep(0.3)

            def log_message(self, *_):
                pass

        server = ThreadingHTTPServer(('127.0.0.1', 0), StalledHandler)
        thread = Thread(target=server.serve_forever, daemon=True)
        thread.start()
        generator = load_llm('OpenAI')
        generator.client.base_url = httpx.URL(f'http://127.0.0.1:{server.server_port}/v1/')
        stream = None
        try:
            stream = generator.generate('hello', is_streaming=True)
            self.assertEqual(next(stream).choices[0].delta.content, 'hello')
            with self.assertRaises(httpx.ReadTimeout):
                next(stream)
        finally:
            if stream is not None:
                stream.close()
            generator.client.close()
            server.shutdown()
            server.server_close()
            thread.join()


if __name__ == '__main__':
    unittest.main()
