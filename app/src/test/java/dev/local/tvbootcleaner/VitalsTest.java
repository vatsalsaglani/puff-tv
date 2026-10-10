package dev.local.tvbootcleaner;
import org.junit.Test;
import static org.junit.Assert.*;
public class VitalsTest {
 @Test public void countersAndAvailableMemory() {
  Vitals v=new Vitals();
  v.accept("cpu 10 0 10 70 10 0 0 0 200 200\nMemTotal: 1000 kB\nMemAvailable: 400 kB");
  assertEquals(-1,v.cpu,0);assertEquals(60,v.ram,.01);
  v.accept("cpu 20 0 20 140 20 0 0 0 999 999\nMemTotal: 1000 kB\nMemAvailable: 600 kB");
  assertEquals(20,v.cpu,.01);assertEquals(40,v.ram,.01);
 }
 @Test public void restartMissingAndHistory() {
  Vitals v=new Vitals();
  for(int n=0;n<30;n++)v.accept("cpu "+n+" 0 0 "+(n*3)+"\nMemTotal: 1000 kB\nMemAvailable: 200 kB");
  assertEquals(19,v.history.size());assertEquals(25,v.cpu,.01);
  v.accept("cpu 0 0 0 0\n");assertEquals(-1,v.cpu,0);assertEquals(-1,v.ram,0);
  v.reset();assertEquals(0,v.history.size());assertEquals(-1,v.cpu,0);
 }
}
