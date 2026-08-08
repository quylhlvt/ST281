package com.food.diydrink.foodmaker.core.helper

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

object StringHelper {
    fun changeColor(
        context: Context,
        text: String,
        color: Int,
        fontfamily: Int,
    ): SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            ForegroundColorSpan(context.getColor(color)),
            0,
            text.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val font = ResourcesCompat.getFont(context, fontfamily)
        val typefaceSpan = CustomTypefaceSpan("", font)
        spannableString.setSpan(
            typefaceSpan, 0, text.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

}