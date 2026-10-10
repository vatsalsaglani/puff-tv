package dev.local.tvbootcleaner;
import android.content.Context;import android.graphics.*;import android.os.SystemClock;import android.view.View;
final class VitalsViews {
 static final int CPU=0xff92e5ec,RAM=0xffc4b5fd;
 static class Badge extends View {
  final Paint p=new Paint(3);final PuffStyle.Cloud cloud;final Vitals data;boolean busy;
  Badge(Context c,Vitals d){super(c);data=d;cloud=new PuffStyle.Cloud(c);cloud.layout(0,0,100,100);setContentDescription("Puff: CPU and memory status. Open controls.");}
  protected void onDraw(Canvas c){super.onDraw(c);float size=Math.min(getWidth(),getHeight());c.save();c.translate((getWidth()-size)/2,(getHeight()-size)/2);c.scale(size/100,size/100);
   p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(5);p.setStrokeCap(Paint.Cap.ROUND);RectF ring=new RectF(8,8,92,92);p.setColor(0x30ffffff);c.drawArc(ring,110,140,false,p);c.drawArc(ring,-70,140,false,p);
   p.setColor(CPU);if(data.cpu>=0)c.drawArc(ring,110,140*data.cpu/100,false,p);p.setColor(RAM);if(data.ram>=0)c.drawArc(ring,-70,140*data.ram/100,false,p);
   c.save();c.translate(16,16);c.scale(.68f,.68f);cloud.draw(c);c.restore();
   if(busy){p.setStrokeWidth(2.5f);p.setColor(PuffStyle.MINT);c.drawArc(new RectF(1,1,99,99),(SystemClock.uptimeMillis()%1800)*.2f,55,false,p);postInvalidateDelayed(80);}
   c.restore();
  }
 }
 static class Graph extends View {
  final Paint p=new Paint(3);final Vitals data;Graph(Context c,Vitals d){super(c);data=d;setContentDescription("CPU and memory history while Home is visible, up to 90 seconds");}
  protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight();p.setStrokeWidth(1);p.setColor(0x18ffffff);for(int i=1;i<4;i++)c.drawLine(0,h*i/4,w,h*i/4,p);
   for(int metric=0;metric<2;metric++){Path path=new Path();boolean drawing=false;for(int i=0;i<data.history.size();i++){float value=data.history.get(i)[metric];if(value<0){drawing=false;continue;}float x=w*(18-data.history.size()+1+i)/18f,y=3+(h-6)*(1-value/100);if(!drawing){path.moveTo(x,y);drawing=true;}else path.lineTo(x,y);}p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(PuffStyle.d(getContext(),2));p.setColor(metric==0?CPU:RAM);c.drawPath(path,p);p.setStyle(Paint.Style.FILL);}
  }
 }
}
