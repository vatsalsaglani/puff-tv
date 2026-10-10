package dev.local.tvbootcleaner;
import android.content.*;import android.graphics.*;import android.graphics.drawable.*;import android.view.*;import android.widget.*;

final class PuffStyle {
 static final int WHITE=0xfff4f7fa, MUTED=0xffaebbc9, MINT=0xffa6eddb, INK=0xff13292b;
 static int d(Context c,float n){return (int)(n*c.getResources().getDisplayMetrics().density+.5f);}
 static GradientDrawable glass(float radius){GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xf23b4656,0xf0212937});g.setCornerRadius(radius);g.setStroke(1,0x40ffffff);return g;}
 static GradientDrawable solid(int color,float radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(radius);return g;}
 static TextView text(Context c,String s,int size,int color){TextView t=new TextView(c);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setFontFeatureSettings("kern");return t;}
 static TextView button(Context c,String s,Runnable run){TextView t=text(c,s,18,WHITE);t.setGravity(Gravity.CENTER_VERTICAL);t.setPadding(d(c,20),d(c,14),d(c,20),d(c,14));t.setMinHeight(d(c,54));t.setFocusable(true);t.setClickable(true);t.setBackground(solid(0x26ffffff,d(c,17)));t.setOnClickListener(v->run.run());
  t.setOnFocusChangeListener((v,on)->{t.setTextColor(on?INK:WHITE);t.setBackground(solid(on?MINT:0x26ffffff,d(c,17)));v.animate().scaleX(on?1.015f:1).scaleY(on?1.015f:1).setDuration(120).start();});return t;
 }
 static LinearLayout.LayoutParams row(Context c){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=d(c,10);return p;}
 static class Cloud extends View {
  final Paint p=new Paint(3);Cloud(Context c){super(c);setContentDescription("Puff, a smiling cloud with a sparkle");}
  protected void onDraw(Canvas c){super.onDraw(c);c.save();float s=Math.min(getWidth(),getHeight())/100f;c.translate((getWidth()-100*s)/2,(getHeight()-100*s)/2);c.scale(s,s);
   p.setShader(new LinearGradient(10,20,90,85,new int[]{0xffecfff8,MINT,0xff78c8c7},null,Shader.TileMode.CLAMP));p.setStyle(Paint.Style.FILL);
   Path cloud=new Path();cloud.moveTo(27,77);cloud.cubicTo(5,77,5,45,27,44);cloud.cubicTo(31,16,66,18,72,42);cloud.cubicTo(96,39,102,77,77,77);cloud.close();c.drawPath(cloud,p);p.setShader(null);
   p.setColor(INK);c.drawOval(36,51,41,59,p);c.drawOval(59,51,64,59,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2.8f);p.setStrokeCap(Paint.Cap.ROUND);c.drawArc(43,56,57,66,10,160,false,p);p.setStyle(Paint.Style.FILL);
   p.setColor(0x559780ab);c.drawOval(29,60,37,64,p);c.drawOval(64,60,72,64,p);
   p.setColor(0xffffedb2);Path star=new Path();star.moveTo(83,12);star.lineTo(86,21);star.lineTo(95,24);star.lineTo(86,27);star.lineTo(83,36);star.lineTo(80,27);star.lineTo(71,24);star.lineTo(80,21);star.close();c.drawPath(star,p);c.restore();
  }
 }
}
