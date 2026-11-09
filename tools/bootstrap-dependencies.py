#!/usr/bin/env python3
"""Download only the compile/test dependencies, verifying every file before use."""
import hashlib
import json
import os
import shutil
import tempfile
import urllib.request
from pathlib import Path

root = Path(__file__).resolve().parent.parent
offline = os.environ.get('REULTIMATE_PACK_DIR')
for entry in json.loads((root / 'tools/dependencies.json').read_text()):
    target = root / 'libs/pack' / entry['path']
    def matches(path):
        return path.is_file() and hashlib.sha256(path.read_bytes()).hexdigest() == entry['sha256']
    if matches(target):
        continue
    target.parent.mkdir(parents=True, exist_ok=True)
    fd, temporary = tempfile.mkstemp(prefix='.dependency-', dir=target.parent)
    os.close(fd)
    temporary = Path(temporary)
    try:
        local = Path(offline) / entry['path'] if offline else None
        if local and matches(local):
            shutil.copyfile(local, temporary)
        else:
            with urllib.request.urlopen(entry['url'], timeout=120) as response, temporary.open('wb') as out:
                shutil.copyfileobj(response, out)
        if not matches(temporary):
            raise RuntimeError('Checksum mismatch: ' + entry['path'])
        os.replace(temporary, target)
        print('Prepared ' + entry['path'])
    finally:
        temporary.unlink(missing_ok=True)
