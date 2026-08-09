package com.food.diydrink.foodmaker.ui.main.createPony

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerDrawable
import com.food.diydrink.foodmaker.core.base.BaseAdapter
import com.food.diydrink.foodmaker.data.model.custom.CustomModel
import com.food.diydrink.foodmaker.databinding.ItemChooseBinding
import com.food.diydrink.foodmaker.utils.DataLocal

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