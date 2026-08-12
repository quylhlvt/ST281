package com.fff.a.ui.main.createPony

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerDrawable
import com.fff.a.core.base.BaseAdapter
import com.fff.a.data.model.custom.CustomModel
import com.fff.a.utils.DataLocal
import com.fff.a.databinding.ItemChooseBinding

class ChoosePonyAdapter(
    private val onClick: (character: CustomModel, position: Int) -> Unit
) : BaseAdapter<CustomModel, ItemChooseBinding>(ItemChooseBinding::inflate) {

    override fun onBind(binding: ItemChooseBinding, item: CustomModel, position: Int) {
        val shimmerDrawable = ShimmerDrawable().apply { setShimmer(DataLocal.shimmer1) }

        Glide.with(binding.root.context)
            .load(item.avatar)
            .placeholder(shimmerDrawable)
            .into(binding.imvImage)

        binding.root.setOnClickListener { onClick(item, position) }
    }
}