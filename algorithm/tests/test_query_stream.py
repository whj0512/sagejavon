"""Streaming route regressions, without models, credentials or databases."""
import importlib.util
import os
from pathlib import Path
import sys
from types import ModuleType, SimpleNamespace
import unittest
from unittest.mock import Mock, patch

from flask import Flask
from flask_cors import CORS


def load_queries():
    dependencies = {
        'langchain.schema.document': {'Document': object},
        'server.app.utils.decorators': {'token_required': lambda f: f},
        'server.app.utils.sqlite_client': {'get_db_connection': Mock()},
        'server.app.utils.diskcache_client': {'diskcache_client': Mock()},
        'server.app.utils.diskcache_lock': {'diskcache_lock': Mock()},
        'server.logger.logger_config': {'my_logger': Mock()},
        'server.rag.generation.llm': {'llm_generator': Mock()},
        'server.rag.pre_retrieval.query_transformation.rewrite': {'detect_query_lang': Mock()},
        'server.rag.post_retrieval.rerank.flash_ranker': {'RerankRequest': Mock(), 'reranker': Mock()},
        'server.rag.retrieval.vector_search': {'vector_search': Mock()},
    }
    modules = {}
    for name, attributes in dependencies.items():
        module = ModuleType(name)
        module.__dict__.update(attributes)
        modules[name] = module
    path = Path(__file__).resolve().parents[1] / 'server/app/queries.py'
    spec = importlib.util.spec_from_file_location('stream_queries_under_test', path)
    queries = importlib.util.module_from_spec(spec)
    with patch.dict(sys.modules, modules), patch.dict(os.environ, {
        'USE_PREPROCESS_QUERY': '0', 'USE_RERANKING': '0', 'USE_DEBUG': '0',
    }):
        spec.loader.exec_module(queries)
    return queries


class QueryStreamTests(unittest.TestCase):
    def setUp(self):
        self.queries = load_queries()
        self.queries.diskcache_client.get.return_value = None
        self.queries.save_user_query_history = Mock()
        app = Flask(__name__)
        CORS(app, supports_credentials=False, origins='*')
        app.register_blueprint(self.queries.queries_bp)
        self.client = app.test_client()

    def post(self):
        return self.client.post(
            '/open_kf_api/queries/smart_query_stream',
            json={'query': 'hello', 'user_id': 'test-user'},
            headers={'Origin': 'http://localhost:3003'},
        )

    def test_preflight(self):
        response = self.client.options('/open_kf_api/queries/smart_query_stream', headers={
            'Origin': 'http://localhost:3003',
            'Access-Control-Request-Method': 'POST',
            'Access-Control-Request-Headers': 'content-type',
        })
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.headers['Access-Control-Allow-Origin'], 'http://localhost:3003')

    def test_start_failure_has_error_status_and_cors(self):
        self.queries.generate_answer = Mock(side_effect=RuntimeError('private provider error'))
        response = self.post()
        self.assertEqual(response.status_code, 502)
        self.assertEqual(response.headers['Access-Control-Allow-Origin'], 'http://localhost:3003')
        self.assertEqual(response.json['retcode'], -30000)
        self.assertNotIn('private provider error', response.get_data(as_text=True))

    def test_lazy_failure_has_cors(self):
        def broken_stream():
            raise RuntimeError('first read failed')
            yield
        self.queries.generate_answer = Mock(return_value=broken_stream())
        self.assertEqual(self.post().status_code, 502)

    def test_content_and_usage_chunks(self):
        self.queries.generate_answer = Mock(return_value=iter([
            SimpleNamespace(choices=[SimpleNamespace(delta=SimpleNamespace(content=None))]),
            SimpleNamespace(choices=[SimpleNamespace(delta=SimpleNamespace(content='hello'))]),
            SimpleNamespace(choices=[], usage={'total_tokens': 1}),
        ]))
        response = self.post()
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.get_data(as_text=True), 'hello')
        self.queries.save_user_query_history.assert_called_once_with('test-user', 'hello', 'hello', True)

    def test_empty_answer_is_error(self):
        self.queries.generate_answer = Mock(return_value=iter([]))
        self.assertEqual(self.post().status_code, 502)

    def test_midstream_failure_is_not_success(self):
        def broken_stream():
            yield SimpleNamespace(choices=[SimpleNamespace(delta=SimpleNamespace(content='partial'))])
            raise RuntimeError('stream disconnected')
        self.queries.generate_answer = Mock(return_value=broken_stream())
        response = self.post()
        with self.assertRaisesRegex(RuntimeError, 'stream disconnected'):
            response.get_data()
        self.queries.save_user_query_history.assert_not_called()


if __name__ == '__main__':
    unittest.main()
