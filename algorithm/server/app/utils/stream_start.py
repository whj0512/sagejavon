"""Bound the first answer wait without changing the plain-text stream protocol."""
from queue import Empty, Queue
from threading import BoundedSemaphore, Lock, Thread


class StreamStartBusy(RuntimeError):
    pass


# Per process: never accumulate unlimited background model calls after timeouts.
_startup_slots = BoundedSemaphore(4)


def first_chunk_with_timeout(stream, timeout):
    if not _startup_slots.acquire(blocking=False):
        stream.close()
        raise StreamStartBusy('Model startup capacity exhausted')

    result = Queue(maxsize=1)
    state_lock = Lock()
    abandoned = False

    def read_first():
        try:
            try:
                item = (True, next(stream, None))
            except Exception as exc:
                item = (False, exc)
            with state_lock:
                discard = abandoned
                if not discard:
                    result.put_nowait(item)
            if discard:
                # Only this thread may close a generator still executing next().
                stream.close()
        finally:
            _startup_slots.release()

    worker = Thread(target=read_first, name='model-stream-start', daemon=True)
    try:
        worker.start()
    except Exception:
        _startup_slots.release()
        stream.close()
        raise
    try:
        succeeded, value = result.get(timeout=timeout)
    except Empty:
        with state_lock:
            abandoned = True
            # Handle publication racing with the deadline. The worker has
            # finished next() if it published, so closing is now safe.
            published = not result.empty()
        if published:
            stream.close()
        raise TimeoutError('Timed out waiting for the first model answer') from None
    if not succeeded:
        stream.close()
        raise value
    return value
