package dev.local.tvbootcleaner;
import java.util.*;
/** Kernel counters, not process CPU or load average. Memory excludes reclaimable RAM. */
final class Vitals {
 float cpu=-1,ram=-1;long totalKb,usedKb;long lastTotal=-1,lastIdle=-1;final ArrayList<float[]> history=new ArrayList<>();
 void reset(){cpu=ram=-1;lastTotal=lastIdle=-1;history.clear();}
 void accept(String raw){long total=-1,idle=0,mem=0,available=-1;
  for(String line:raw.split("\n")){String[] p=line.trim().split("\\s+");try{
   if(p[0].equals("cpu")&&p.length>=5){total=0;for(int i=1;i<Math.min(9,p.length);i++)total+=Long.parseLong(p[i]);idle=Long.parseLong(p[4])+(p.length>5?Long.parseLong(p[5]):0);}
   if(p[0].equals("MemTotal:"))mem=Long.parseLong(p[1]);if(p[0].equals("MemAvailable:"))available=Long.parseLong(p[1]);
  }catch(Exception ignored){}}
  cpu=-1;if(lastTotal>=0&&total>lastTotal&&idle>=lastIdle)cpu=clamp(100f*(1f-(float)(idle-lastIdle)/(total-lastTotal)));
  lastTotal=total;lastIdle=idle;ram=mem>0&&available>=0?clamp(100f*(mem-available)/mem):-1;totalKb=mem;usedKb=Math.max(0,mem-available);
  history.add(new float[]{cpu,ram});if(history.size()>19)history.remove(0);
 }
 private float clamp(float v){return Math.max(0,Math.min(100,v));}
 static String percent(float v){return v<0?"—":Math.round(v)+"%";}
}
