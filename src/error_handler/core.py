def capture(func, *args, **kwargs):
    try:
        return func(*args, **kwargs), None
    except Exception as error:
        return None, error
