package io.github.jqssun.airplay.ui.theme
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
object ThemeMode { const val SYSTEM=0; const val LIGHT=1; const val DARK=2 }
@Composable
fun AirPlayTheme(themeMode: Int=ThemeMode.DARK, dynamicColor: Boolean=false, content: @Composable () -> Unit) {
 MaterialTheme(colorScheme=BrandDarkColors, shapes=Shapes(small=RoundedCornerShape(16.dp),medium=RoundedCornerShape(22.dp),large=RoundedCornerShape(28.dp)), content={
  Box(Modifier.fillMaxSize().background(Color.Black)) {
   Canvas(Modifier.fillMaxSize()) { repeat(95) { i -> drawCircle(Color.White.copy(alpha=if(i%7==0) .3f else .1f), if(i%7==0) 1.1f else .65f, Offset(((i*73+19)%997)/997f*size.width,((i*137+41)%991)/991f*size.height)) } }
   content()
  }
 })
}
