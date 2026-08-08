package com.food.diydrink.foodmaker.ui.main.successcosplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.core.base.BaseFragment
import com.food.diydrink.foodmaker.core.extention.onClick
import com.food.diydrink.foodmaker.core.extention.popBack
import com.food.diydrink.foodmaker.core.extention.select
import com.food.diydrink.foodmaker.core.extention.setImageActionBar
import com.food.diydrink.foodmaker.core.extention.setTextActionBar
import com.food.diydrink.foodmaker.databinding.FragmentSuccessCosplayBinding
import com.food.diydrink.foodmaker.ui.main.cosplay.CosplayViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessCosplayFragment : BaseFragment<FragmentSuccessCosplayBinding, SuccessCosplayViewModel>( FragmentSuccessCosplayBinding::inflate, SuccessCosplayViewModel::class.java) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressHandler()
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSuccessCosplayBinding = FragmentSuccessCosplayBinding.inflate(inflater, container, false)

    override fun initView() {
        binding.apply {
            txtShow.isSelected = true
            setupActionBar()
            val userBitmap = viewModelActivity.userResultBitmap
            if (userBitmap != null && !userBitmap.isRecycled) {
                imvImage2.setImageBitmap(userBitmap)
            }

            // imvImage3 = ảnh cosplay gốc
            val cosplayBitmap = viewModelActivity.cosplayBitmap
            if (cosplayBitmap != null && !cosplayBitmap.isRecycled) {
              imvImage3.setImageBitmap(cosplayBitmap)
            }

            val percent = viewModelActivity.cosplayPercent
            binding.tvMatchPercent.text = "$percent/100"
            val starCount = when (percent) {
                0 -> 0
                in 1..20 -> 1
                in 21..40 -> 2
                in 41..70 -> 3
                in 71..98 -> 4
                in 99..100 -> 5
                else -> 0
            }
            binding.ll1.rating = starCount.toFloat()
            updateProgressBar(percent)
        }
    }
    private fun updateProgressBar(percent: Int) {
        binding.progressTrack.post {
            // progressFill đã có sẵn chiều rộng thực tế theo margin trong XML.
            // Chỉ scale trên chính chiều rộng đó, để 100% luôn phủ kín thanh.
            val fillW = binding.progressFill.width.toFloat()
            val targetScale = (percent.coerceIn(0, 100)) / 100f

            binding.progressFill.pivotX = 0f
            binding.progressFill.pivotY = binding.progressFill.height / 2f
            binding.progressFill.scaleX = targetScale
            binding.progressFill.scaleY = 1f

            val starW = binding.imgStar.width.toFloat()
            // Đưa tâm ngôi sao tới đúng đầu mút của phần fill ở mọi kích thước màn hình.
            binding.imgStar.translationX =
                binding.progressFill.left + fillW * targetScale -
                    starW / 2f - binding.imgStar.left
        }
    }
    private fun FragmentSuccessCosplayBinding.setupActionBar() {
        actionBar.apply {
            tvCenter.select()
            setImageActionBar(
                btnActionBarRight,
                R.drawable.ic_home
            )
//            setTextActionBar(
//                tvCenter,
//                getString(R.string.successfully)
//            )
        }
    }







    override fun viewListener() {
        binding.apply {
            setupActionBarListeners()
            setupNavigationListeners()
        }
    }

    private fun FragmentSuccessCosplayBinding.setupActionBarListeners() {
        actionBar.btnActionBarRight.onClick {

                findNavController().navigate(R.id.action_successCosplay_to_home)

        }
    }

    private fun FragmentSuccessCosplayBinding.setupNavigationListeners() {
        btnTryAgain.onClick {

                val cosplayEntry = runCatching {
                    findNavController().getBackStackEntry(R.id.cosplay)
                }.getOrNull()

                cosplayEntry?.let {
                    val factory = androidx.hilt.navigation.HiltViewModelFactory(requireContext(), it)
                    val cosplayViewModel = androidx.lifecycle.ViewModelProvider(it, factory)[CosplayViewModel::class.java]
                    cosplayViewModel.randomize()
                }

                viewModelActivity.shouldRestartShow = true  // ← báo ShowFragment reset
                popBack()

        }
    }


    override fun observeData() {}

    override fun bindViewModel() {}
}
