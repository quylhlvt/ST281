package com.fff.a.ui.main.random

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fff.a.core.base.BaseFragment
import com.fff.a.core.extention.InternetExtension
import com.fff.a.core.extention.gone
import com.fff.a.core.extention.onClick
import com.fff.a.R
import com.fff.a.core.extention.InternetExtension.isInternetAvailable
import com.fff.a.core.extention.InternetExtension.isNetworkConnected
import com.fff.a.core.extention.popBack
import com.fff.a.core.extention.select
import com.fff.a.core.extention.setImageActionBar
import com.fff.a.core.extention.visible
import com.fff.a.ui.main.customize.CustomizeFragment
import com.fff.a.core.extention.setTextActionBar
import com.fff.a.databinding.FragmentRandomBinding
import com.fff.a.ui.main.cosplay.CosplayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.forEach

@AndroidEntryPoint
class RandomFragment : BaseFragment<FragmentRandomBinding, RandomViewModel>(
    FragmentRandomBinding::inflate,
    RandomViewModel::class.java
) {
    private  var  count = 0
    // Thêm hàm này vào RandomFragment
    private fun isOnlineTemplate(templateIndex: Int): Boolean {
        return viewModelActivity.templates.value.getOrNull(templateIndex)
            ?.id?.startsWith("online_") == true
    }

    private fun checkOnlineNetworkOrShowDialog(templateIndex: Int): Boolean {
        if (!isOnlineTemplate(templateIndex)) return false
        return when {
            !InternetExtension.isInternetAvailable(requireContext()) -> { showUnstableNetworkDialog(); true }
            !InternetExtension.isNetworkConnected(requireContext()) -> { showUnstableNetworkDialog(); true }
            else -> false
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressHandler()
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    popBack()
                }
            }
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentRandomBinding = FragmentRandomBinding.inflate(inflater, container, false)

    override fun onFragmentStart() {
        if (!isAdded || isDetached) return

    }

    override fun onFragmentStop() {
        if (!isAdded || isDetached) return

    }
    override fun initView() {

        Glide.with(binding.imageGif).load(R.drawable.gif).into(binding.imageGif)
        binding.setupActionBar()
        setSaveButtonEnabled(false)
        binding.txtShow.isSelected = true
    }

    private fun FragmentRandomBinding.setupActionBar() {
        actionBar.apply {
            tvCenter.select()
            setImageActionBar(btnActionBarLeft, R.drawable.back_app)
        }
    }

    override fun viewListener() {
        binding.apply {
            actionBar.btnActionBarLeft.onClick {
                    popBack()

            }
            // ✅ Nút random — check internet nếu template online
            random.onClick {
                count++

                val action = {
                    val isOnline = isNetworkConnected(requireContext()) &&
                            isInternetAvailable(requireContext())

                    val currentIndex = viewModel.randomItem.value?.templateIndex ?: -1

                    if (currentIndex < 0 || !checkOnlineNetworkOrShowDialog(currentIndex)) {
                        viewModel.randomize(isOnline = isOnline)
                    }
                }

                if (count > 1) {

                        action()

                } else {
                    action()
                }
            }
            btnEdit.onClick {
                if (!btnEdit.isEnabled) return@onClick
                val item = viewModel.randomItem.value ?: return@onClick
                if (checkOnlineNetworkOrShowDialog(item.templateIndex)) return@onClick
                val templateId = viewModelActivity.templates.value
                    .getOrNull(item.templateIndex)?.id ?: ""
                val args = CustomizeFragment.newArgs(
                    templateIndex = item.templateIndex,
                    templateId = templateId,
                    isEdit = false,
                    savedSelections = item.selections
                )
                findNavController().navigate(R.id.action_random_to_custom, args)
            }
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.randomItem.collectLatest { item ->
                item ?: return@collectLatest

                // ✅ Nếu đã có cache bitmap thì không render lại
                val cached = viewModel.cachedBitmap
                if (cached != null && !cached.isRecycled) {
                    showBitmap(cached)
                    return@collectLatest
                }

                renderCharacter(item)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // ✅ Khi back về: dùng lại bitmap đã cache
        val cached = viewModel.cachedBitmap
        if (cached != null && !cached.isRecycled) {
            showBitmap(cached)
        }
    }

    private fun renderCharacter(item: RandomViewModel.RandomItem) {
        setSaveButtonEnabled(false)
        viewLifecycleOwner.lifecycleScope.launch {
            val paths = item.resolvedPaths.filterNotNull()
            if (paths.isEmpty()) return@launch
            binding.imvImage.setImageDrawable(null)
            binding.imageGif.visible()

            var networkDialogShown = false
            var waitingForNetwork = false
            var bitmaps: List<Bitmap> = emptyList()
            while (isActive) {
                val loaded = withContext(Dispatchers.IO) {
                    paths.map { path ->
                        async {
                            runCatching {
                                Glide.with(requireContext()).asBitmap().load(path)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .override(800).submit().get()
                            }.getOrNull()
                        }
                    }.awaitAll()
                }
                bitmaps = loaded.filterNotNull()
                if (bitmaps.size == paths.size) break

                val hasNetwork = InternetExtension.isNetworkConnected(requireContext()) &&
                        InternetExtension.isInternetAvailable(requireContext())
                if (!hasNetwork) {
                    waitingForNetwork = true
                    if (!networkDialogShown) {
                        showUnstableNetworkDialog()
                        networkDialogShown = true
                    }
                    delay(200)
                    continue
                }
                if (waitingForNetwork) {
                    viewModel.randomize(isOnline = true)
                    return@launch
                }
                delay(200)
            }
            if (!isActive || bitmaps.size != paths.size) return@launch

            val merged = mergeBitmaps(bitmaps)

            // ✅ Lưu vào cache
            viewModel.setCachedBitmap(merged)

            withContext(Dispatchers.Main) {
                showBitmap(merged)
                binding.imageGif.gone()
            }
        }
    }

    private fun showBitmap(bitmap: Bitmap) {
        binding.imvImage.apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageBitmap(bitmap)
        }
        binding.imageGif.gone()
        setSaveButtonEnabled(true)
    }

    private fun setSaveButtonEnabled(enabled: Boolean) {
        binding.btnEdit.isEnabled = enabled
        binding.btnEdit.isClickable = enabled
        binding.btnEdit.background =
            ContextCompat.getDrawable(
                binding.root.context,
                if (enabled) R.drawable.bg_frame_random_edit
                else R.drawable.bg_frame_random_unedit
            )    }

    private fun mergeBitmaps(bitmaps: List<Bitmap>): Bitmap {
        val size = 800
        val merged = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(merged)
        bitmaps.forEach { bmp ->
            val scaled = if (bmp.width == size && bmp.height == size) bmp
            else Bitmap.createScaledBitmap(bmp, size, size, true)
            canvas.drawBitmap(scaled, 0f, 0f, null)
            if (scaled != bmp) scaled.recycle()
        }
        return merged
    }

    override fun bindViewModel() {}
}
