package com.food.diydrink.foodmaker.ui.main.setting

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.core.base.BaseFragment
import com.food.diydrink.foodmaker.core.extention.gone
import com.food.diydrink.foodmaker.core.extention.onClick
import com.food.diydrink.foodmaker.core.extention.policy
import com.food.diydrink.foodmaker.core.extention.popBack
import com.food.diydrink.foodmaker.core.extention.select
import com.food.diydrink.foodmaker.core.extention.setImageActionBar
import com.food.diydrink.foodmaker.core.extention.setTextActionBar
import com.food.diydrink.foodmaker.core.extention.shareApp
import com.food.diydrink.foodmaker.core.extention.toHomeFromSetting
import com.food.diydrink.foodmaker.core.extention.toLangFromSetting
import com.food.diydrink.foodmaker.core.extention.visible
import com.food.diydrink.foodmaker.core.helper.RateHelper
import com.food.diydrink.foodmaker.databinding.FragmentSettingBinding
import com.food.diydrink.foodmaker.utils.state.RateState
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>( FragmentSettingBinding::inflate, SettingViewModel::class.java) {

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
    ): FragmentSettingBinding = FragmentSettingBinding.inflate(inflater, container, false)

    override fun initView() {
        binding.apply {

            setupActionBar()
            setupActionTiltleBar()
            setupRateButton()
        }
    }

    private fun FragmentSettingBinding.setupActionBar() {
        actionBar.apply {
            setImageActionBar(
                btnActionBarLeft,
                R.drawable.back_app
            )
        }
    }
 private fun FragmentSettingBinding.setupActionTiltleBar() {


            binding.apply {
                txt1.isSelected = true
                txt2.isSelected = true
                txt3.isSelected = true
                txt4.isSelected = true

                setTextActionBar(
                    actionBar.tvCenter,
                    getString(R.string.settings)
                )
                actionBar.tvCenter.isSelected =true
            }

    }



    private fun FragmentSettingBinding.setupRateButton() {
        if (sharedPreferences.isRateRequest()) {
            btnRate.gone()
        } else {
            btnRate.visible()
        }
    }



    override fun viewListener() {
        binding.apply {
            setupActionBarListeners()
            setupNavigationListeners()
        }
    }

    private fun FragmentSettingBinding.setupActionBarListeners() {
        actionBar.btnActionBarLeft.onClick {
            popBack()
        }
    }

    private fun FragmentSettingBinding.setupNavigationListeners() {
        btnLang.onClick {
            toLangFromSetting()
        }

        btnPolicy.onClick {
            policy()
        }

        btnRate.onClick {
            RateHelper.showRateDialog(requireActivity(), sharedPreferences) { state ->
                if (state != RateState.CANCEL) {
                    btnRate.gone()
                    showToast(R.string.have_rated)
                }
            }
        }

        btnShare.onClick {
            shareApp()
        }
    }


    override fun observeData() {}

    override fun bindViewModel() {}
}