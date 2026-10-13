# coding=utf-8
# Gunicorn configuration variables
bind = "0.0.0.0:7000"
workers = 3
# Keep worker heartbeats running while a model request waits for network I/O.
worker_class = "gthread"
threads = 4
timeout = 120
accesslog = "access.log"  # Access logs file
errorlog = "-"    # Disable gunicorn access logs
loglevel = "info"
