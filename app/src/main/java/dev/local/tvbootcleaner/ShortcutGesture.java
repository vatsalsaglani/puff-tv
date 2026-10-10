package dev.local.tvbootcleaner;

/** No Android dependencies: release-based gestures never trigger from key repeat. */
public final class ShortcutGesture {
 private long down=-1, firstUp=-1;
 public void reset(){down=-1;firstUp=-1;}
 public void down(long time, int repeats){if(repeats==0)down=time;}
 public boolean up(long time, boolean longPress){
  if(down<0)return false;
  long held=time-down;down=-1;
  if(longPress){firstUp=-1;return held>=650&&held<10000;}
  if(held>=650){firstUp=-1;return false;}
  if(firstUp>=0&&time-firstUp<=450){firstUp=-1;return true;}
  firstUp=time;return false;
 }
}
