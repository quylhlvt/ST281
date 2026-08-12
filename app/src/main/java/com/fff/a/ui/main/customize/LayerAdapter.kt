package com.fff.a.ui.main.customize

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fff.a.R
import com.fff.a.core.base.BaseAdapter
import com.fff.a.core.extention.visible
import com.fff.a.data.model.custom.BodyPartModel
import com.fff.a.data.model.custom.ColorModel
import com.fff.a.databinding.ItemColorBinding
import com.fff.a.databinding.ItemLayerBinding
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.fff.a.core.extention.dp
import com.fff.a.core.extention.dpToPx
import com.fff.a.core.extention.gone
import com.fff.a.core.extention.invisible
import com.fff.a.databinding.ItemBottomCustomBinding
import com.fff.a.utils.DataLocal
import com.facebook.shimmer.ShimmerDrawable
import androidx.core.graphics.drawable.toDrawable

// ── NAV ADAPTER ───────────────────────────────────────────────────────────────
class NavAdapter :
    BaseAdapter<BodyPartModel, ItemBottomCustomBinding>(ItemBottomCustomBinding::inflate) {

    var posNav = 0
    var onClick: ((Int) -> Unit)? = null

    fun setPos(pos: Int) {
        val old = posNav; posNav = pos
        if (old != pos) {
            notifyItemChanged(old); notifyItemChanged(pos)
        }
    }

    override fun onBind(binding: ItemBottomCustomBinding, item: BodyPartModel, position: Int) {
        val shimmerDrawable = ShimmerDrawable().apply { setShimmer(DataLocal.shimmer) }

        binding.apply {
            val ctx = root.context

            imvImage.background = ContextCompat.getColor(
                ctx,
                if (posNav == position) R.color.app_color2 else R.color.white2
            ).toDrawable()

        Glide.with(imvImage)
            .load(item.nav)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(256)
            .dontAnimate()
            .placeholder(shimmerDrawable)
            .error(shimmerDrawable)
            .into(imvImage)

        root.setOnClickListener { onClick?.invoke(position) }
    }}
}

// ── COLOR ADAPTER ─────────────────────────────────────────────────────────────
class ColorAdapter : BaseAdapter<ColorModel, ItemColorBinding>(ItemColorBinding::inflate) {

    var posColor = 0
    var onClick: ((Int) -> Unit)? = null

    fun setPos(pos: Int) {
        val old = posColor; posColor = pos
        if (old != pos) {
            notifyItemChanged(old); notifyItemChanged(pos)
        }
    }

    override fun onBind(binding: ItemColorBinding, item: ColorModel, position: Int) {
        val isWhite = item.color.removePrefix("#").equals("FFFFFF", ignoreCase = true)
        val isSelected = posColor == position

        binding.colorSelected.isVisible = !isSelected && !isWhite
        binding.colorSelectedWhite.isVisible = !isSelected && isWhite

        val colorInt = runCatching {
            Color.parseColor("#${item.color.removePrefix("#").ifEmpty { "FFFFFF" }}")
        }.getOrDefault(Color.WHITE)

        DrawableCompat.setTint(binding.viewColor.background.mutate(), colorInt)
        binding.root.setOnClickListener { onClick?.invoke(position) }
    }
}

// ── PART ADAPTER ──────────────────────────────────────────────────────────────
class PartAdapter : BaseAdapter<String, ItemLayerBinding>(ItemLayerBinding::inflate) {

    var posPath: Int = 0
    var listThumb: List<String> = emptyList()
    var onClick: ((Int, String) -> Unit)? = null

    fun setPos(pos: Int) {
        val old = posPath; posPath = pos
        if (old != pos) {
            notifyItemChanged(old); notifyItemChanged(pos)
        }
    }

    override fun onBind(binding: ItemLayerBinding, item: String, position: Int) {
        val shimmerDrawable = ShimmerDrawable().apply { setShimmer(DataLocal.shimmer) }

        binding.apply {
            if (posPath == position) {
                forcus.visible()
            } else {
                forcus.gone()
            }
        when (item) {
            "none" -> {
                Glide.with(imvImage).clear(imvImage)
                imvImage.setImageResource(R.drawable.ic_none)
            }

            "dice" -> {
                Glide.with(imvImage).clear(imvImage)
                imvImage.setImageResource(R.drawable.ic_dice)
            }

            else -> {

                val thumbPath = listThumb.getOrElse(position) { item }
                Glide.with(imvImage)
                    .load(thumbPath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(256)
                    .placeholder(shimmerDrawable)
                    .dontAnimate()
                    .into(imvImage)
            }
        }
        root.setOnClickListener { onClick?.invoke(position, item) }
    }}
}
