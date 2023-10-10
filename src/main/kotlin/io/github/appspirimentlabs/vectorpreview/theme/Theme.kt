package io.github.appspirimentlabs.vectorpreview.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import io.github.appspirimentlabs.vectorpreview.theme.intellij.SwingColor

private val DarkGreenColorPalette = darkColors(
    primary = green200,
    primaryVariant = green700,
    secondary = teal200,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    error = Color.Red,
)

private val LightGreenColorPalette = lightColors(
    primary = green500,
    primaryVariant = green700,
    secondary = teal200,
    onPrimary = Color.White,
    onSurface = Color.Black
)
val toolbarBackground: Color get()= EditorColorsManager.getInstance().schemeForCurrentUITheme.defaultBackground.rgb.let {
    Color(it)
}
val tooltipBackground: Color get()= EditorColorsManager.getInstance().schemeForCurrentUITheme.getColor(EditorColors.NOTIFICATION_BACKGROUND)?.rgb?.let {
    Color(it)
}?:Color.LightGray
val tooltipText: Color get()= EditorColorsManager.getInstance().schemeForCurrentUITheme.getColor(EditorColors.CARET_COLOR)?.rgb?.let {
    Color(it)
}?:Color.LightGray

@Composable
fun WidgetTheme(
    darkTheme: Boolean = false,
    content: @Composable() () -> Unit,
) {
    val colors = if (darkTheme) DarkGreenColorPalette else LightGreenColorPalette
    val swingColor = SwingColor()

    MaterialTheme(
        colors = colors.copy(
            background = swingColor.background,
            onBackground = swingColor.onBackground,
            surface = swingColor.background,
            onSurface = swingColor.onBackground,
        ),
        typography = typography,
        shapes = shapes,
        content = content
    )
}