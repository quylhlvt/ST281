package com.fff.a.ui.onboarding.intro

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.fff.a.core.base.BackPressHandler
import com.fff.a.core.base.BaseFragment
import com.fff.a.core.extention.onClick
import com.fff.a.core.extention.toHome
import com.fff.a.core.extention.toPermission
import com.fff.a.databinding.FragmentIntroBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class IntroFragment : BaseFragment<FragmentIntroBinding, IntroViewModel>(
    FragmentIntroBinding::inflate,
    IntroViewModel::class.java
), BackPressHandler {
    @Inject
    lateinit var introAdapter: IntroAdapter

    override fun viewListener() {
        binding.btnNextPager.root.onClick(200) {
            Log.d("PERF", "1. Button clicked: ${System.currentTimeMillis()}")
            viewModel.nextPage(binding.viewPager2.currentItem, introAdapter.itemCount)
        }
        binding.viewPager2.registerOnPageChangeCallback(object :
            OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//                if (position == 1) {
//                    binding.nativeAds.gone()
//                } else {
//                    binding.nativeAds.visible()
//                }
            }
        })
//        binding.viewPager2.registerOnPageChangeCallback(object :
//            ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                if (position == 1) {
//                    binding.nativeAds.gone()
//                } else {
//                    binding.nativeAds.visible()
//                }
//            }
//        })
    }


    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentIntroBinding = FragmentIntroBinding.inflate(inflater, container, false)

    override fun initView() {

        binding.viewPager2.adapter = introAdapter
        binding.dotsIndicator.attachTo(binding.viewPager2)
        setOnChangeViewPager2()
//        binding.textView.text = "Home Fragment"
//        binding.btnTest.setOnClickListener {
//            showSnackbar("Xin chào từ Home!")
//        }
    }

    override fun observeData() {
//        viewModel.data.observe(viewLifecycleOwner) { text ->
//            binding.textView.text = text
//        }
    }

    override fun bindViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                introAdapter.submitList(state.pagesSplash)
                binding.apply {
                    viewPager2.currentItem = state.page
                    btnNextPager.tvButton.text = getString(state.textButtonRes)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.singleEvent.collect { event ->
                when (event) {
                    is IntroSingleEvent.NavigateToNextScreen ->
                        if (sharedPreferences.isPermissionScreen())
                            toHome()
                        else
                            toPermission()
                }
            }
        }
    }


    private fun setOnChangeViewPager2() {
        binding.viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                viewModel.getPage(binding.viewPager2.currentItem, introAdapter.itemCount)
            }
        })
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finishAffinity()
        exitProcess(0)
        return  true
    }
}