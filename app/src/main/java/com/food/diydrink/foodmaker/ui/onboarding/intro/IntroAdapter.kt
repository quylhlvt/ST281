package com.food.diydrink.foodmaker.ui.onboarding.intro

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.food.diydrink.foodmaker.core.base.BaseAdapter
import com.food.diydrink.foodmaker.core.extention.loadImage
import com.food.diydrink.foodmaker.core.extention.select
import com.food.diydrink.foodmaker.core.extention.strings
import com.food.diydrink.foodmaker.data.model.intro.IntroModel
import com.food.diydrink.foodmaker.databinding.ItemIntroBinding
import javax.inject.Inject
//import kotlin.io.root

private val pageIntroDiff = object : DiffUtil.ItemCallback<IntroModel>() {
    override fun areItemsTheSame(oldItem: IntroModel, newItem: IntroModel) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: IntroModel, newItem: IntroModel) = oldItem == newItem
}

class IntroAdapter@Inject constructor() :
    ListAdapter<IntroModel, PagerIntroViewHolder>(pageIntroDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PagerIntroViewHolder(
            ItemIntroBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: PagerIntroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
class PagerIntroViewHolder(
    private val binding: ItemIntroBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(pager: IntroModel) {
        binding.run {
            if (adapterPosition == 1) {
                // Page 0 — hiện 2 dòng, không marquee
                tvContent.maxLines = 2
                tvContent.ellipsize = null
                tvContent.isSelected = false
                tvContent.isSingleLine = false
            } else {
                // Page 1, 2 — chạy chữ marquee
                tvContent.maxLines = 1
                tvContent.isSingleLine = true
                tvContent.ellipsize = android.text.TextUtils.TruncateAt.MARQUEE
                tvContent.isSelected = true
            }
            tvContent.text = root.context.strings(pager.content)
            imvImage.setImageResource(pager.image)
//            loadImage(root, pager.image, imvImage, false)
//            loadImage(root, pager.imageBottom, imvImageBottom, false)
        }
    }
}
