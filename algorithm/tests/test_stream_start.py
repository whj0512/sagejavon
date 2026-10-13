"""First-answer deadlines must not create unlimited background work."""
from threading import BoundedSemaphore, Event
import unittest
from unittest.mock import patch

from server.app.utils import stream_start


class StreamStartTests(unittest.TestCase):
    def test_timeout_limits_background_calls_and_releases_slot(self):
        slots = BoundedSemaphore(1)
        release = Event()
        closed = Event()

        def slow():
            try:
                release.wait(2)
                yield 'late'
            finally:
                closed.set()

        with patch.object(stream_start, '_startup_slots', slots):
            try:
                with self.assertRaises(TimeoutError):
                    stream_start.first_chunk_with_timeout(slow(), 0.02)
                with self.assertRaises(stream_start.StreamStartBusy):
                    stream_start.first_chunk_with_timeout(iter_generator(), 0.02)
            finally:
                release.set()
            self.assertTrue(closed.wait(1))
            # Semaphore release is in the worker's finally, after close().
            acquired = slots.acquire(timeout=1)
            self.assertTrue(acquired)
            if acquired:
                slots.release()

    def test_ready_stream_continues_on_request_thread(self):
        stream = iter_generator()
        self.assertEqual(stream_start.first_chunk_with_timeout(stream, 1), 'first')
        self.assertEqual(next(stream), 'second')
        stream.close()


def iter_generator():
    yield 'first'
    yield 'second'


if __name__ == '__main__':
    unittest.main()
