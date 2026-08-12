package com.fff.a.ui.main.myPony.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.fff.a.core.base.BaseAdapter
import com.fff.a.core.extention.gone
import com.fff.a.core.extention.loadImage
import com.fff.a.core.extention.onClick
import com.fff.a.core.extention.visible
import com.fff.a.data.model.mypony.MyAlbumModel
import com.fff.a.R
import com.fff.a.databinding.ItemMyAvatarBinding


class MyAvatarAdapter(val context: Context) :
    BaseAdapter<MyAlbumModel, ItemMyAvatarBinding>(ItemMyAvatarBinding::inflate) {
    var onItemClick: ((MyAlbumModel) -> Unit) = {}
    var onLongClick: ((Int) -> Unit) = {}
    var onItemTick: ((Int) -> Unit) = {}

    var onEditClick: ((String) -> Unit) = {}
    var onDeleteClick: ((String) -> Unit) = {}

    override fun onBind(binding: ItemMyAvatarBinding, item: MyAlbumModel, position: Int) {
        binding.apply {
            loadImage(root, item.path, imvImage)

            if (item.isShowSelection) {
                btnSelect.visible()
                btnEdit.gone()
                btnDelete.gone()
            } else {
                btnSelect.gone()
                btnEdit.visible()
                btnDelete.visible()
            }

            btnSelect.setImageResource(
                if (item.isSelected) R.drawable.ic_selected else R.drawable.ic_not_select
            )


            // Click luôn navigate, không check selection mode
            root.onClick { onItemClick.invoke(item) }

            root.setOnLongClickListener {
                if (items.any { it.isShowSelection }) return@setOnLongClickListener false
                onLongClick.invoke(position)
                true
            }

            btnEdit.onClick { onEditClick.invoke(item.idEdit) }
            btnDelete.onClick { onDeleteClick.invoke(item.path) }
            btnSelect.onClick { onItemTick.invoke(position) } // chỉ tick button mới toggle
        }
    }
}