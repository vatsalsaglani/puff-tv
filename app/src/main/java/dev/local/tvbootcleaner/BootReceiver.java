package dev.local.tvbootcleaner;
import android.content.*;
public class BootReceiver extends BroadcastReceiver {
 public void onReceive(Context c, Intent i) {
  if (Intent.ACTION_BOOT_COMPLETED.equals(i.getAction()) && c.getSharedPreferences("config",0).getBoolean("enabled",false))
   try { c.startForegroundService(new Intent(c,CleanupService.class).setAction("boot")); }
   catch (RuntimeException e) { c.getSharedPreferences("config",0).edit().putString("status","Automatic cleanup was restricted by Android. Open Puff to clean manually.").apply(); }
 }
}
