package io.github.jqssun.airplay.ui.theme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
val BrandDarkColors = darkColorScheme(
 primary=Color(0xFFF1F1F3), onPrimary=Color.Black,
 primaryContainer=Color(0xFF171717), onPrimaryContainer=Color.White,
 secondary=Color(0xFFD4D4D8), onSecondary=Color.Black,
 secondaryContainer=Color(0xFF161616), onSecondaryContainer=Color.White,
 tertiary=Color(0xFFC8C8CE), onTertiary=Color.Black,
 background=Color.Black, onBackground=Color(0xFFF4F4F5),
 surface=Color.Black, onSurface=Color(0xFFF4F4F5),
 surfaceVariant=Color(0xFF0B0B0B), onSurfaceVariant=Color(0xFFB7B7BD),
 surfaceTint=Color.Transparent, outline=Color(0xFF454545), outlineVariant=Color(0xFF292929),
 surfaceContainer=Color(0xFF080808), surfaceContainerHigh=Color(0xFF101010), surfaceContainerHighest=Color(0xFF171717),
 error=Color(0xFFFFB4AB), onError=Color(0xFF690005)
)
val BrandLightColors = BrandDarkColors
