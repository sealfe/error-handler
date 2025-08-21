from subprocess import run
from traceback import extract_tb
from pathlib import Path


def capture(func, *args, **kwargs):
    try:
        return func(*args, **kwargs), None
    except Exception as error:
        return None, trace_commits(error)


def trace_commits(error):
    frames = extract_tb(error.__traceback__)
    records = []
    for frame in frames:
        path = Path(frame.filename).resolve()
        cmd = [
            "git",
            "-C",
            str(path.parent),
            "log",
            "-1",
            "--pretty=%H|%an|%ad|%s",
            "--",
            str(path),
        ]
        result = run(cmd, capture_output=True, text=True)
        records.append(
            {"file": str(path), "line": frame.lineno, "commit": result.stdout.strip()}
        )
    return {"error": str(error), "records": records}
