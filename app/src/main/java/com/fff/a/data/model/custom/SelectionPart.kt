package com.fff.a.data.model.custom

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectionPart(val nav: Int,
                         val color: Int,
                         val layer: Int): Parcelable
