package com.food.diydrink.foodmaker.core.dialog

import android.content.Context
import android.graphics.Color
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.core.base.BaseDialog
import com.food.diydrink.foodmaker.core.extention.onClick
import com.food.diydrink.foodmaker.databinding.DialogColorPickerBinding


class ChooseColorDialog(context: Context) : BaseDialog<DialogColorPickerBinding>(context,maxWidth = true, maxHeight = false) {
    override val layoutId: Int = R.layout.dialog_color_picker
    override val isCancelOnTouchOutside: Boolean =false
    override val isCancelableByBack: Boolean = false

    var onDoneEvent: ((Int) -> Unit) = {}
    var onCloseEvent: (() -> Unit) = {}
    var onDismissEvent: (() -> Unit) = {}
    private var color = Color.WHITE
    override fun initView() {

        binding.apply {
            colorPickerView.apply {
                hueSliderView = hueSlider
            }
        }
    }

    override fun initAction() {
        binding.apply {
            colorPickerView.setOnColorChangedListener { color = it }
            btnCancle.onClick { onCloseEvent.invoke() }
            btnSave.onClick { onDoneEvent.invoke(color) }
        }
    }

    override fun onDismissListener() {
        onDismissEvent.invoke()
    }

}