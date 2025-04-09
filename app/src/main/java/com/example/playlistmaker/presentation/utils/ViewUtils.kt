package com.example.playlistmaker.presentation.utils

import android.content.Context
import android.util.TypedValue

object ViewUtils {
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}