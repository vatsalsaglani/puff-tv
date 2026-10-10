package dev.local.tvbootcleaner;
import android.app.*;import android.os.*;import android.content.*;import android.util.Base64;
import com.tananaev.adblib.*;import java.io.*;import java.net.*;import java.util.concurrent.*;import java.util.concurrent.atomic.AtomicBoolean;

public class CleanupService extends Service {
 private final AtomicBoolean running=new AtomicBoolean(false);
 private volatile boolean cancelled=false; private volatile Thread worker; private volatile Socket socket;
 private final ScheduledExecutorService timer=Executors.newSingleThreadScheduledExecutor();
 public IBinder onBind(Intent i){return null;}
 public void onCreate(){super.onCreate();NotificationManager n=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
  n.createNotificationChannel(new NotificationChannel("cleanup","Startup cleanup",NotificationManager.IMPORTANCE_LOW));
  startForeground(101,new Notification.Builder(this,"cleanup").setContentTitle("Puff is tidying up").setContentText("Checking startup apps; stops automatically when finished.").setSmallIcon(android.R.drawable.ic_menu_manage).build());
 }
 private void status(String s){getSharedPreferences("config",0).edit().putString("status",s).commit();android.util.Log.i("TVBootCleaner",s);}
 public int onStartCommand(Intent i,int flags,int id){
  if(!running.compareAndSet(false,true))return START_NOT_STICKY;
  final String action=i==null?"boot":i.getAction();
  getSharedPreferences("config",0).edit().putBoolean("busy",true).putInt("busy_boot",android.provider.Settings.Global.getInt(getContentResolver(),"boot_count",-1)).putLong("busy_since",System.currentTimeMillis()).apply();
  worker=new Thread(()->{try{execute(action);}catch(Exception e){if(!cancelled)status("Could not finish: "+e.getClass().getSimpleName()+". Check USB debugging and authorize again.");}finally{closeSocket();stopSelf();}},"TVBootCleanup");worker.start();return START_NOT_STICKY;
 }
 private void closeSocket(){try{if(socket!=null)socket.close();}catch(Exception ignored){}}
 public void onDestroy(){getSharedPreferences("config",0).edit().putBoolean("busy",false).putLong("finished_at",System.currentTimeMillis()).apply();cancelled=true;closeSocket();if(worker!=null)worker.interrupt();timer.shutdownNow();stopForeground(true);super.onDestroy();}
 private void execute(String action)throws Exception{
  boolean boot="boot".equals(action),auth="authorize".equals(action);
  android.content.SharedPreferences p=getSharedPreferences("config",0);
  int bootCount=android.provider.Settings.Global.getInt(getContentResolver(),"boot_count",-1);
  if(boot){
   if(!p.getBoolean("enabled",false))return;
   if(bootCount<0){status("Skipped: cannot identify this restart.");return;}
   if(p.getInt("handled_boot",-2)==bootCount)return;
   p.edit().putInt("handled_boot",bootCount).commit();
   long delay=Math.max(0,50000-SystemClock.elapsedRealtime());
   status("Waiting briefly for startup, then cleaning once…");if(delay>0)Thread.sleep(delay);
  }
  AdbBase64 base64=data->Base64.encodeToString(data,Base64.NO_WRAP);
  File priv=new File(getFilesDir(),"local-adb-private"),pub=new File(getFilesDir(),"local-adb-public");
  AdbCrypto crypto;
  if(priv.exists()&&pub.exists())crypto=AdbCrypto.loadAdbKeyPair(base64,priv,pub);
  else{crypto=AdbCrypto.generateAdbKeyPair(base64);crypto.saveAdbKeyPair(priv,pub);}
  AdbConnection connection=null;
  long deadline=SystemClock.elapsedRealtime()+(auth?65000:25000);
  if(boot)deadline=Math.min(deadline,150000);
  while(!cancelled&&SystemClock.elapsedRealtime()<deadline){
   try{
    socket=new Socket();socket.connect(new InetSocketAddress("127.0.0.1",5555),2000);
    connection=AdbConnection.create(socket,crypto);
    if(connection.connect(auth?60:5,TimeUnit.SECONDS,!auth))break;
   }catch(Exception e){if(auth)throw e;}
   if(connection!=null)try{connection.close();}catch(Exception ignored){}
   connection=null;closeSocket();if(!auth)Thread.sleep(2000);else break;
  }
  if(cancelled)return;
  if(connection==null){status("Skipped: local debugging unavailable. Open this app and authorize this TV.");return;}
  final Socket connectedSocket=socket;
  ScheduledFuture<?> timeout=timer.schedule(()->{try{connectedSocket.close();}catch(Exception ignored){}},45,TimeUnit.SECONDS);
  try{
   if(auth){String result=shell(connection,"echo TVBC_AUTH_OK");if(!result.contains("TVBC_AUTH_OK"))throw new IOException("Authorization check failed");p.edit().putBoolean("authorized",true).commit();status("Connected to this TV. Turn on automatic cleanup to enable it after restarts.");return;}
   if(boot&&!p.getBoolean("enabled",false))return;
   String script=readAsset("cleanup.sh");
   String result=shell(connection,"sh -c '"+script.replace("'","'\\''")+"'");
   int pos=result.lastIndexOf("TVBC_RESULT:");
   if(pos<0)throw new IOException("Incomplete cleanup response");
   String summary=result.substring(pos+12).trim();
   status((boot?"Automatic":"Manual")+" · "+new java.text.SimpleDateFormat("HH:mm",java.util.Locale.getDefault()).format(new java.util.Date())+"\n"+summary);
  }finally{timeout.cancel(false);connection.close();}
 }
 private String readAsset(String path)throws IOException{ByteArrayOutputStream out=new ByteArrayOutputStream();try(InputStream in=getAssets().open(path)){byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1)out.write(b,0,n);}return out.toString("UTF-8");}
 private String shell(AdbConnection c,String command)throws Exception{
  ByteArrayOutputStream out=new ByteArrayOutputStream();AdbStream stream=c.open("shell:"+command);
  try{while(!stream.isClosed()){try{byte[] b=stream.read();if(out.size()+b.length>131072)throw new IOException("Response too large");out.write(b);}catch(IOException e){if(stream.isClosed())break;throw e;}}}finally{stream.close();}
  return out.toString("UTF-8");
 }
}
