package com.fff.a.ui.main.add_character.adapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fff.a.core.base.BaseAdapter
import com.fff.a.core.extention.onClick
import com.fff.a.data.model.addcharacter.SelectedAddModel
import com.fff.a.databinding.ItemSpeechBinding
import com.fff.a.utils.DataLocal
import com.facebook.shimmer.ShimmerDrawable

class SpeechAdapter  : BaseAdapter<SelectedAddModel, ItemSpeechBinding>(ItemSpeechBinding::inflate) {
    var onItemClick: ((String) -> Unit) = {}
    var currentSelected = -1

    override fun onBind(binding: ItemSpeechBinding, item: SelectedAddModel, position: Int) {
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
