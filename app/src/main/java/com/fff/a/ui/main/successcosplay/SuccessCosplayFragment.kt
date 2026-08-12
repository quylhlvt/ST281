package com.fff.a.ui.main.successcosplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fff.a.R
import com.fff.a.core.base.BaseFragment
import com.fff.a.core.extention.onClick
import com.fff.a.core.extention.popBack
import com.fff.a.core.extention.select
import com.fff.a.core.extention.setImageActionBar
import com.fff.a.databinding.FragmentSuccessCosplayBinding
import com.fff.a.ui.main.cosplay.CosplayViewModel
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
                imvImage.setImageBitmap(userBitmap)
            }

            val matchPercent = viewModelActivity.cosplayPercent.coerceIn(0, 100)
            percent.text = "$matchPercent%"
        }
    }
    private fun FragmentSuccessCosplayBinding.setupActionBar() {
        actionBar.apply {
            tvCenter.select()
            setImageActionBar(
                btnActionBarRight,
                R.drawable.ic_home
            )
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
                    val factory = HiltViewModelFactory(requireContext(), it)
                    val cosplayViewModel = ViewModelProvider(it, factory)[CosplayViewModel::class.java]
                    cosplayViewModel.randomize()
                }

                viewModelActivity.shouldRestartShow = true  // ← báo ShowFragment reset
                popBack()

        }
    }


    override fun observeData() {}

    override fun bindViewModel() {}
}
