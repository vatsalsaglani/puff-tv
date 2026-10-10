package dev.local.tvbootcleaner;
import android.content.Context;import android.os.Handler;import android.util.Base64;import com.tananaev.adblib.*;import java.io.*;import java.net.*;import java.util.concurrent.TimeUnit;
/** One bounded loopback connection while the Home control is visible; no disk history. */
final class VitalsSampler {
 private final Context context;private final Handler main=new Handler();private final Runnable changed;final Vitals data=new Vitals();private volatile Run current;
 VitalsSampler(Context c,Runnable callback){context=c;changed=callback;}
 void visible(boolean yes){if(yes&&current==null){data.reset();Run r=new Run();current=r;r.start();}else if(!yes&&current!=null){Run r=current;current=null;r.interrupt();r.close();}}
 private class Run extends Thread{
  volatile Socket socket;AdbConnection connection;
  void close(){try{if(socket!=null)socket.close();}catch(Exception ignored){}}
  public void run(){try{while(current==this){try{
   if(connection==null){AdbCrypto crypto=AdbCrypto.loadAdbKeyPair(b->Base64.encodeToString(b,Base64.NO_WRAP),new File(context.getFilesDir(),"local-adb-private"),new File(context.getFilesDir(),"local-adb-public"));socket=new Socket();socket.connect(new InetSocketAddress("127.0.0.1",5555),1500);socket.setSoTimeout(8000);connection=AdbConnection.create(socket,crypto);if(!connection.connect(3,TimeUnit.SECONDS,true))throw new IOException("Not authorized");}
   AdbStream stream=connection.open("shell:cat /proc/stat /proc/meminfo");ByteArrayOutputStream out=new ByteArrayOutputStream();try{while(!stream.isClosed()){try{byte[] b=stream.read();if(out.size()+b.length>32768)throw new IOException("Too much data");out.write(b);}catch(IOException e){if(stream.isClosed())break;throw e;}}}finally{stream.close();}
   String raw=out.toString("UTF-8");main.post(()->{if(current==this){data.accept(raw);changed.run();}});Thread.sleep(5000);
  }catch(InterruptedException e){break;}catch(Exception e){close();if(connection!=null)try{connection.close();}catch(Exception ignored){}connection=null;main.post(()->{if(current==this){data.reset();changed.run();}});try{Thread.sleep(10000);}catch(InterruptedException done){break;}}}
  }finally{close();if(connection!=null)try{connection.close();}catch(Exception ignored){}}}
 }
}
