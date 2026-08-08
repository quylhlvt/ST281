package com.food.diydrink.foodmaker.ui.main.add_character.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.core.base.BaseAdapter
import com.food.diydrink.foodmaker.core.extention.dp
import com.food.diydrink.foodmaker.core.extention.gone
import com.food.diydrink.foodmaker.core.extention.onClick
import com.food.diydrink.foodmaker.core.extention.visible
import com.food.diydrink.foodmaker.data.model.addcharacter.SelectedAddModel
import com.food.diydrink.foodmaker.databinding.ItemTextColorBinding

class TextColorAdapter :
    BaseAdapter<SelectedAddModel, ItemTextColorBinding>(ItemTextColorBinding::inflate) {
    var onChooseColorClick: (() -> Unit) = {}
    var onTextColorClick: ((Int, Int) -> Unit) = { _, _ -> }

    private var currentSelected = 1


    override fun onBind(binding: ItemTextColorBinding, item: SelectedAddModel, position: Int) {
        val context = binding.root.context
        binding.apply {
            // ── Luôn setup đúng content trước ──────────────────
            if (position == 0) {
                imvColor.gone()
                btnAddColor.visible()
                root.onClick { onChooseColorClick.invoke() }
            } else {
                imvColor.visible()
                btnAddColor.gone()
                imvColor.setBackgroundColor(item.color)
                root.onClick { onTextColorClick.invoke(item.color, position) }
            }

            // ── Sau đó mới apply selected state ────────────────
            if (item.isSelected) {
                frameShadown.visible()
                frame.apply {
                    strokeColor = ContextCompat.getColor(context, R.color.app_color)

                }
            } else {
                frameShadown.gone()
                frame.apply {
                    strokeColor = ContextCompat.getColor(context, R.color.transparent)

                }
            }
        }
    }

        fun submitItem(position: Int, list: ArrayList<SelectedAddModel>) {
            if (position != currentSelected) {
                items.clear()
                items.addAll(list)

                notifyItemChanged(currentSelected)
                notifyItemChanged(position)

                currentSelected = position
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun submitListReset(list: ArrayList<SelectedAddModel>) {
            items.clear()
            items.addAll(list)
            currentSelected = 1
            notifyDataSetChanged()
        }
    }