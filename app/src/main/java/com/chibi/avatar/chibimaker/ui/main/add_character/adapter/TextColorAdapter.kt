package com.chibi.avatar.chibimaker.ui.main.add_character.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.chibi.avatar.chibimaker.R
import com.chibi.avatar.chibimaker.core.base.BaseAdapter
import com.chibi.avatar.chibimaker.core.extention.dp
import com.chibi.avatar.chibimaker.core.extention.gone
import com.chibi.avatar.chibimaker.core.extention.onClick
import com.chibi.avatar.chibimaker.core.extention.visible
import com.chibi.avatar.chibimaker.data.model.addcharacter.SelectedAddModel
import com.chibi.avatar.chibimaker.databinding.ItemTextColorBinding

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