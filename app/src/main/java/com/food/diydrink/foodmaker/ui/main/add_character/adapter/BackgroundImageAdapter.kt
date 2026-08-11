package com.food.diydrink.foodmaker.ui.main.add_character.adapter

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.core.base.BaseAdapter
import com.food.diydrink.foodmaker.core.extention.dp
import com.food.diydrink.foodmaker.core.extention.gone
import com.food.diydrink.foodmaker.core.extention.loadFromAsset
import com.food.diydrink.foodmaker.core.extention.loadImage
import com.food.diydrink.foodmaker.core.extention.onClick
import com.food.diydrink.foodmaker.core.extention.select
import com.food.diydrink.foodmaker.core.extention.visible
import com.food.diydrink.foodmaker.data.model.addcharacter.SelectedAddModel
import com.food.diydrink.foodmaker.databinding.ItemBackgroundImageBinding
import com.food.diydrink.foodmaker.utils.DataLocal
import com.facebook.shimmer.ShimmerDrawable


class BackgroundImageAdapter : BaseAdapter<SelectedAddModel, ItemBackgroundImageBinding>(
    ItemBackgroundImageBinding::inflate
) {
    var onAddImageClick: (() -> Unit) = {}
    var onNoneImageClick: (() -> Unit) = {}
    var onBackgroundImageClick: ((String, Int) -> Unit) = { _, _ -> }
    var currentSelected = -1

    override fun onBind(binding: ItemBackgroundImageBinding, item: SelectedAddModel, position: Int) {
        val context = binding.root.context
        val shimmerDrawable = ShimmerDrawable().apply { setShimmer(DataLocal.shimmer1) }

        binding.apply {
            if (currentSelected == position) {
                materiaForcus.visible()
            } else {
                materiaForcus.gone()
            }
            if (position == 1) {
                lnlAddItem.visible()
                imvImage.gone()
                imvImageNone.gone()
                lnlAddItem.onClick { onAddImageClick() }
            } else if (position ==0){
                imvImageNone.visible()
                imvImage.gone()
                lnlAddItem.gone()
                imvImageNone.onClick { onNoneImageClick() }
            }
            else {
                lnlAddItem.gone()
                imvImageNone.gone()
                imvImage.visible()
                if (imvImage.tag != item.path) {
                    imvImage.tag = item.path
                    Glide.with(imvImage)
                        .load(item.path)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .override(256)
                        .dontAnimate()
                        .placeholder(shimmerDrawable)
                        .into(imvImage)
                }
                imvImage.onClick { onBackgroundImageClick(item.path, position) }
            }
        }
    }

    fun selectItem(position: Int) {
        if (position == currentSelected) return
        val old = currentSelected
        currentSelected = position
        if (old >= 0) notifyItemChanged(old)
        if (position >= 0) notifyItemChanged(position)
    }

    fun clearSelection() {
        if (currentSelected < 0) return
        val old = currentSelected
        currentSelected = -1
        notifyItemChanged(old)
    }
}
