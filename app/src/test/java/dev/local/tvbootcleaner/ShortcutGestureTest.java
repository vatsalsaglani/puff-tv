package dev.local.tvbootcleaner;
public class ShortcutGestureTest {
 static void check(boolean v,String message){if(!v)throw new AssertionError(message);}
 @org.junit.Test public void remoteGestures(){ShortcutGesture g=new ShortcutGesture();
 g.down(100,0);check(!g.up(150,false),"single press");g.down(300,0);check(g.up(350,false),"double press");
 g.down(500,0);check(!g.up(550,false),"triple must not retrigger");g.reset();
 g.down(100,0);g.up(150,false);g.down(900,0);check(!g.up(950,false),"slow double rejected");g.reset();
 g.down(100,0);g.down(400,1);check(g.up(800,true),"held repeat keeps original down");
 g.down(1000,0);check(!g.up(1100,true),"short press rejected in long mode");
 check(!g.up(5000,true),"orphan release rejected");g.down(100,0);g.reset();check(!g.up(900,true),"leaving Home resets gesture");
 g.down(100,0);check(!g.up(900,false),"long hold cannot count as double");
 System.out.println("PASS: shortcut single, double, triple, slow, hold, repeat, orphan and reset");}
}
