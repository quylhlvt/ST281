package com.food.diydrink.foodmaker.ui.main.add_character.adapter

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.core.base.BaseAdapter
import com.food.diydrink.foodmaker.core.extention.gone
import com.food.diydrink.foodmaker.core.extention.loadImage
import com.food.diydrink.foodmaker.core.extention.onClick
import com.food.diydrink.foodmaker.core.extention.visible
import com.food.diydrink.foodmaker.data.model.addcharacter.SelectedAddModel
import com.food.diydrink.foodmaker.databinding.ItemStickerBinding
import com.food.diydrink.foodmaker.utils.DataLocal
import com.facebook.shimmer.ShimmerDrawable

class StickerAdapter : BaseAdapter<SelectedAddModel, ItemStickerBinding>(ItemStickerBinding::inflate) {
    var onItemClick: ((String) -> Unit) = {}
    var currentSelected = -1

    override fun onBind(binding: ItemStickerBinding, item: SelectedAddModel, position: Int) {
        val shimmerDrawable = ShimmerDrawable().apply { setShimmer(DataLocal.shimmer1) }

        binding.apply {
            Glide.with(imvImage)
                .load(item.path)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(256)
                .dontAnimate()
                .placeholder(shimmerDrawable)
                .into(imvImage)
            root.onClick {
                selectItem(position)          // ← was missing entirely
                onItemClick.invoke(item.path)
            }
        }
    }

    fun selectItem(position: Int) {           // ← changed private → public
        if (position == currentSelected) return
        val old = currentSelected
        currentSelected = position
        if (old >= 0) notifyItemChanged(old)
        notifyItemChanged(position)
    }
}