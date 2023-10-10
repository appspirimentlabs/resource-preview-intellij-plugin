package io.github.appspirimentlabs.vectorpreview.util

import androidx.compose.ui.graphics.Color


fun String.toColor(): Color? {
    return try {
        if (contains("#")) {
            val colorhex = if (length > 6) {
                takeLast(6)
            } else padEnd(6, '0')

            val red = Integer.valueOf(colorhex.subSequence(0, 2).toString(), 16)
            val green = Integer.valueOf(colorhex.subSequence(2, 4).toString(), 16)
            val blue = Integer.valueOf(colorhex.subSequence(4, 6).toString(), 16)
            Color(red, green, blue)
        } else null
    } catch (e:Exception){
        null
    }
}