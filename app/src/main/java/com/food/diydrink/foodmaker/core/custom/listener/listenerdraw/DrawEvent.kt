package com.food.diydrink.foodmaker.core.custom.listener.listenerdraw

import android.view.MotionEvent
import com.food.diydrink.foodmaker.core.custom.DrawView


interface DrawEvent {
    fun onActionDown(tattooView: DrawView?, event: MotionEvent?)
    fun onActionMove(tattooView: DrawView?, event: MotionEvent?)
    fun onActionUp(tattooView: DrawView?, event: MotionEvent?)
}