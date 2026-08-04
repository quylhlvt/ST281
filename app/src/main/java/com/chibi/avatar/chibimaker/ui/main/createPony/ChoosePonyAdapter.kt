package com.chibi.avatar.chibimaker.ui.main.createPony

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.chibi.avatar.chibimaker.core.base.BaseAdapter
import com.chibi.avatar.chibimaker.data.model.custom.CustomModel
import com.chibi.avatar.chibimaker.databinding.ItemChooseBinding

class ChoosePonyAdapter(
    private val onClick: (character: CustomModel, position: Int) -> Unit
) : BaseAdapter<CustomModel, ItemChooseBinding>(ItemChooseBinding::inflate) {

    override fun onBind(binding: ItemChooseBinding, item: CustomModel, position: Int) {
        binding.sflShimmer.startShimmer()
        binding.sflShimmer.visibility = View.VISIBLE

        Glide.with(binding.root.context)
            .load(item.avatar)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.sflShimmer.stopShimmer()
                    binding.sflShimmer.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.sflShimmer.stopShimmer()
                    binding.sflShimmer.visibility = View.GONE
                    return false
                }
            })
            .into(binding.imvImage)

        binding.root.setOnClickListener { onClick(item, position) }
    }
}