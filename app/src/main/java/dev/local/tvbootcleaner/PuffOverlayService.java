package dev.local.tvbootcleaner;
import android.accessibilityservice.*;import android.content.*;import android.graphics.PixelFormat;import android.media.AudioManager;import android.os.*;import android.view.*;import android.view.accessibility.*;import android.widget.*;import android.content.res.ColorStateList;

public class PuffOverlayService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {
 private final Handler handler=new Handler();
 private final ShortcutGesture gesture=new ShortcutGesture();
 private WindowManager windows;private AudioManager audio;private SharedPreferences prefs;
 private VitalsSampler vitals;private VitalsViews.Badge badge;private VitalsViews.Graph graph;private TextView numbers;
 private View overlay;private TextView detail;private ProgressBar spinner;private boolean home=false,expanded=false,consumed=false,destroyed=false;private String displayState="";private long settleUntil=0;
 private final Runnable dismiss=()->collapse();
 private final Runnable update=()->refresh();
 private final Runnable tick=new Runnable(){public void run(){if(destroyed)return;refresh();if(home||SystemClock.elapsedRealtime()<settleUntil)handler.postDelayed(this,1200);}};
 private final BroadcastReceiver screen=new BroadcastReceiver(){public void onReceive(Context c,Intent i){if(Intent.ACTION_SCREEN_OFF.equals(i.getAction())){home=false;hide();handler.removeCallbacks(tick);}else schedule();}};
 protected void onServiceConnected(){settleUntil=SystemClock.elapsedRealtime()+30000;vitals=new VitalsSampler(this,()->updateLabels());windows=(WindowManager)getSystemService(WINDOW_SERVICE);audio=(AudioManager)getSystemService(AUDIO_SERVICE);prefs=getSharedPreferences("config",0);prefs.registerOnSharedPreferenceChangeListener(this);IntentFilter f=new IntentFilter(Intent.ACTION_SCREEN_OFF);f.addAction(Intent.ACTION_SCREEN_ON);registerReceiver(screen,f);schedule();}
 public void onAccessibilityEvent(AccessibilityEvent e){settleUntil=Math.max(settleUntil,SystemClock.elapsedRealtime()+4000);schedule();}
 public void onInterrupt(){home=false;hide();}
 public void onSharedPreferenceChanged(SharedPreferences p,String key){handler.removeCallbacks(update);handler.post(update);}
 private void schedule(){if(prefs==null||destroyed)return;handler.removeCallbacks(update);handler.post(update);handler.removeCallbacks(tick);handler.postDelayed(tick,1200);}
 private boolean onHome(){
  if(!((PowerManager)getSystemService(POWER_SERVICE)).isInteractive())return false;
  try{
   boolean found=false;
   for(AccessibilityWindowInfo w:getWindows()){
    try{
     if(w.getType()==AccessibilityWindowInfo.TYPE_ACCESSIBILITY_OVERLAY)continue;
     if(w.getType()!=AccessibilityWindowInfo.TYPE_APPLICATION){if(w.isFocused())return false;continue;}
     AccessibilityNodeInfo root=w.getRoot();if(root==null)return false;
     String pkg;try{pkg=String.valueOf(root.getPackageName());}finally{root.recycle();}
     if(!"com.google.android.tvlauncher".equals(pkg))return false;
     found=true;
    }finally{w.recycle();}
   }
   return found;
  }catch(Exception e){return false;}
 }
 private boolean eligible(){return home&&prefs.getBoolean("overlay_enabled",true)&&!audio.isMusicActive();}
 private boolean busy(){long stamp=prefs.getLong("busy_since",0);return prefs.getBoolean("busy",false)&&prefs.getInt("busy_boot",-2)==android.provider.Settings.Global.getInt(getContentResolver(),"boot_count",-1)&&System.currentTimeMillis()-stamp<180000;}
 private void refresh(){if(prefs==null||destroyed)return;home=onHome();if(!eligible()){hide();gesture.reset();return;}vitals.visible(true);if(overlay==null)show(false);updateLabels();if(prefs.getBoolean("preview_overlay",false)){prefs.edit().putBoolean("preview_overlay",false).apply();show(true);}}
 private void updateLabels(){if(vitals!=null){if(badge!=null){badge.busy=busy();badge.invalidate();}if(graph!=null)graph.invalidate();if(numbers!=null)numbers.setText("CPU "+Vitals.percent(vitals.data.cpu)+"    ·    RAM "+Vitals.percent(vitals.data.ram)+"\n"+(vitals.data.totalKb>0?vitals.data.usedKb/1024+" / "+vitals.data.totalKb/1024+" MiB in use":"Waiting for TV readings…"));}if(detail==null)return;String s=prefs.getString("status","Ready when you are");String label;
  if(expanded)label=busy()?"Tidying up… Your apps and sign-ins stay safe.":s;
  else if(busy())label="Tidying up…";
  else if(System.currentTimeMillis()-prefs.getLong("finished_at",0)<7000)label=s.contains("Cleaned ")?"All done":s.contains("Stopped after")||s.contains("Skipped")?"Cleanup skipped":"Check setup";
  else label=MainActivity.shortcutLabel(prefs);
  if(!label.equals(displayState)){detail.setText(label);displayState=label;}
  if(spinner!=null)spinner.setVisibility(busy()?View.VISIBLE:View.GONE);
 }
 private void show(boolean full){
  if(!eligible())return;removeView();expanded=full;displayState="";
  LinearLayout panel=new LinearLayout(this){public boolean dispatchKeyEvent(KeyEvent event){if(event.getKeyCode()==KeyEvent.KEYCODE_BACK&&expanded){if(event.getAction()==KeyEvent.ACTION_UP)collapse();return true;}return super.dispatchKeyEvent(event);}};panel.setOrientation(1);int pad=PuffStyle.d(this,full?16:6);panel.setPadding(pad,pad,pad,pad);panel.setBackground(PuffStyle.glass(PuffStyle.d(this,full?28:48)));panel.setElevation(0);
  badge=new VitalsViews.Badge(this,vitals.data);
  if(full){LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.addView(badge,new LinearLayout.LayoutParams(PuffStyle.d(this,64),PuffStyle.d(this,64)));LinearLayout words=new LinearLayout(this);words.setOrientation(1);words.setPadding(PuffStyle.d(this,12),0,0,0);words.addView(PuffStyle.text(this,"Puff",22,PuffStyle.WHITE));numbers=PuffStyle.text(this,"",14,PuffStyle.MUTED);words.addView(numbers);top.addView(words);panel.addView(top);graph=new VitalsViews.Graph(this,vitals.data);panel.addView(graph,new LinearLayout.LayoutParams(-1,PuffStyle.d(this,52)));TextView legend=PuffStyle.text(this,"",11,PuffStyle.MUTED);android.text.SpannableString legendText=new android.text.SpannableString("● CPU   ● RAM   ·   90s history / 5s samples");legendText.setSpan(new android.text.style.ForegroundColorSpan(0xff83ddf5),0,5,0);legendText.setSpan(new android.text.style.ForegroundColorSpan(0xffc5b8ff),8,13,0);legend.setText(legendText);panel.addView(legend);
  }else panel.addView(badge,new LinearLayout.LayoutParams(PuffStyle.d(this,68),PuffStyle.d(this,68)));
  detail=PuffStyle.text(this,"",12,PuffStyle.MUTED);detail.setMaxLines(3);
  TextView first=null;
  if(full){
   first=PuffStyle.button(this,"✦   Clean apps",()->clean("manual"));panel.addView(first,PuffStyle.row(this));
   panel.addView(PuffStyle.button(this,"Cast · iPhone & Android",()->{hide();startActivity(new Intent(this,io.github.jqssun.airplay.MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));}),PuffStyle.row(this));
   LinearLayout bottom=new LinearLayout(this);TextView settings=PuffStyle.button(this,"Settings",()->{hide();startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));});TextView close=PuffStyle.button(this,"Close",()->collapse());
   LinearLayout.LayoutParams half=new LinearLayout.LayoutParams(0,-2,1);half.rightMargin=PuffStyle.d(this,8);bottom.addView(settings,half);bottom.addView(close,new LinearLayout.LayoutParams(0,-2,1));panel.addView(bottom,PuffStyle.row(this));detail.setPadding(0,PuffStyle.d(this,14),0,0);panel.addView(detail);
   panel.setOnKeyListener((v,key,event)->{if(key==KeyEvent.KEYCODE_BACK){if(event.getAction()==KeyEvent.ACTION_UP)collapse();return true;}return false;});
  }else{panel.setContentDescription("Open Puff cleanup controls");panel.setClickable(true);panel.setOnClickListener(v->show(true));}
  int flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;if(!full)flags|=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
  WindowManager.LayoutParams lp=new WindowManager.LayoutParams(PuffStyle.d(this,full?340:80),-2,WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,flags,PixelFormat.TRANSLUCENT);lp.gravity=Gravity.BOTTOM|Gravity.RIGHT;lp.x=PuffStyle.d(this,32);lp.y=PuffStyle.d(this,32);lp.setTitle("Puff Home controls");
  panel.setOnTouchListener((v,e)->{if(e.getAction()==MotionEvent.ACTION_OUTSIDE){collapse();return true;}return false;});
  try{overlay=panel;windows.addView(panel,lp);if(first!=null){first.setFocusableInTouchMode(true);first.requestFocus();}updateLabels();}catch(Exception e){overlay=null;expanded=false;}
  handler.removeCallbacks(dismiss);if(full)handler.postDelayed(dismiss,20000);
 }
 private void clean(String action){if(busy())return;startForegroundService(new Intent(this,CleanupService.class).setAction(action));handler.postDelayed(update,120);handler.removeCallbacks(dismiss);handler.postDelayed(dismiss,20000);}
 private void collapse(){if(overlay!=null&&expanded){home=onHome();if(eligible())show(false);else hide();}}
 private void removeView(){if(overlay!=null){try{windows.removeView(overlay);}catch(Exception ignored){}overlay=null;}detail=null;spinner=null;badge=null;graph=null;numbers=null;}
 private void hide(){if(vitals!=null)vitals.visible(false);handler.removeCallbacks(dismiss);removeView();expanded=false;displayState="";}
 protected boolean onKeyEvent(KeyEvent event){
  int key=event.getKeyCode();if(expanded&&key==KeyEvent.KEYCODE_BACK){if(event.getAction()==KeyEvent.ACTION_UP)collapse();return true;}
  if(expanded){handler.removeCallbacks(dismiss);handler.postDelayed(dismiss,20000);}
  if(prefs==null||key!=prefs.getInt("shortcut_key",0))return false;
  home=onHome();if(!eligible()){gesture.reset();boolean had=consumed;if(event.getAction()==KeyEvent.ACTION_UP)consumed=false;return had;}
  if(event.getAction()==KeyEvent.ACTION_DOWN){consumed=true;gesture.down(event.getEventTime(),event.getRepeatCount());return true;}
  if(event.getAction()==KeyEvent.ACTION_UP){consumed=false;if(gesture.up(event.getEventTime(),prefs.getBoolean("shortcut_long",false)))show(!expanded);return true;}
  return false;
 }
 public void onDestroy(){destroyed=true;handler.removeCallbacksAndMessages(null);hide();if(prefs!=null)prefs.unregisterOnSharedPreferenceChangeListener(this);try{unregisterReceiver(screen);}catch(Exception ignored){}super.onDestroy();}
}
