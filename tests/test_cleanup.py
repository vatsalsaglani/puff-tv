import os,subprocess,tempfile
from pathlib import Path
source=(Path(__file__).resolve().parents[1]/'app/src/main/assets/cleanup.sh').read_text()
for name,focus,media,expected in [
 ('home','com.google.android.tvlauncher/.MainActivity','state=PlaybackState {state=1,',12),
 ('streaming foreground','com.netflix.ninja/.MainActivity','state=PlaybackState {state=1,',0),
 ('active playback','com.google.android.tvlauncher/.MainActivity','state=PlaybackState {state=3,',0),
 ('buffering','com.google.android.tvlauncher/.MainActivity','state=PlaybackState {state=6,',0),
 ('missing foreground','','state=PlaybackState {state=1,',0),
 ('diagnostic timeout','com.google.android.tvlauncher/.MainActivity','DUMP TIMEOUT',0),
]:
 with tempfile.TemporaryDirectory() as d:
  base=Path(d); calls=base/'calls'
  (base/'dumpsys').write_text('#!/bin/sh\ncase "$*" in *activity*) printf "mResumedActivity: ActivityRecord{x u0 %s t1}\\n" "$TEST_FOCUS";; *) printf "MEDIA SESSION SERVICE\\n%s\\n" "$TEST_MEDIA";; esac\n')
  (base/'am').write_text('#!/bin/sh\nprintf "%s\\n" "$*" >> "$TEST_CALLS"\n')
  for cmd in ['dumpsys','am']:(base/cmd).chmod(0o755)
  script=source.replace('/data/local/tmp/tvbootcleaner-last.txt',str(base/'result'))
  env=dict(os.environ,PATH=str(base)+':'+os.environ['PATH'],TEST_FOCUS=focus,TEST_MEDIA=media,TEST_CALLS=str(calls))
  result=subprocess.run(['/bin/sh'],input=script,text=True,capture_output=True,env=env)
  count=len(calls.read_text().splitlines()) if calls.exists() else 0
  assert count==expected,(name,count,result.stdout,result.stderr)
  assert 'TVBC_RESULT:' in result.stdout
  print('PASS',name,count)
