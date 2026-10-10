#!/system/bin/sh
# Fixed allowlist only. Skip all cleanup if playback or foreground is uncertain.
report() {
  printf 'TVBC_RESULT: %s\n' "$1"
  { date; cat /proc/sys/kernel/random/boot_id; printf '%s\n' "$1"; } > /data/local/tmp/tvbootcleaner-last.txt
}
safe_now() {
  reason="Home is not ready yet"
  activity=$(dumpsys -t 2 activity activities)
  case "$activity" in *'DUMP TIMEOUT'*|*'hung system process'*) reason="TV safety check timed out"; return 2;; esac
  focus=$(printf '%s\n' "$activity" | grep 'mResumedActivity:')
  case "$focus" in
    ""|*"mResumedActivity: null"*|*"com.google.android.boot.appsplashscreen/"*) return 2;;
    *' com.google.android.tvlauncher/'*|*' com.mediatek.wwtv.tvcenter/'*|*' dev.local.tvbootcleaner/'*) ;;
    *) reason="another app is open"; return 1;;
  esac
  media=$(dumpsys -t 2 media_session)
  case "$media" in *'DUMP TIMEOUT'*|*'hung system process'*) reason="TV safety check timed out"; return 2;; esac
  case "$media" in *'MEDIA SESSION SERVICE'*) ;; *) reason="playback status is unavailable"; return 2;; esac
  if printf '%s\n' "$media" | grep -Eq 'state=PlaybackState.*state=(3|4|5|6|8|9|10|11),'; then reason="playback is active"; return 1; fi
  return 0
}
count=0
retries=0
for pkg in com.spotify.tv.android com.sonyliv in.startv.hotstar com.graymatrix.did com.fancode.tv com.shemaroo.shemarootv com.apple.atve.androidtv.appletv com.netflix.ninja com.amazon.amazonvideo.livingroom com.google.android.youtube.tv com.google.android.youtube.tvmusic com.google.android.videos; do
  while true; do
    safe_now
    safety=$?
    [ "$safety" -eq 0 ] && break
    if [ "$safety" -eq 2 ] && [ "$retries" -lt 4 ]; then
      retries=$((retries+1))
      sleep 2
      continue
    fi
    report "Stopped after $count apps: $reason."
    exit 0
  done
  result=$(am force-stop --user 0 "$pkg" 2>&1)
  if [ -n "$result" ]; then report "Cleanup incomplete at $pkg: $result"; exit 1; fi
  count=$((count+1))
done
if [ "$count" -eq 1 ]; then report "Cleaned 1 streaming app. It remains available to open."; else report "Cleaned $count streaming apps. All remain available to open."; fi
