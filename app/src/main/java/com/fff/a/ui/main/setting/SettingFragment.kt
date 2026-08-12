package com.fff.a.ui.main.setting

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.fff.a.core.base.BaseFragment
import com.fff.a.core.extention.gone
import com.fff.a.core.extention.onClick
import com.fff.a.core.extention.policy
import com.fff.a.R
import com.fff.a.core.extention.popBack
import com.fff.a.core.extention.setImageActionBar
import com.fff.a.core.extention.setTextActionBar
import com.fff.a.core.extention.shareApp
import com.fff.a.core.extention.select
import com.fff.a.core.extention.toHomeFromSetting
import com.fff.a.core.extention.toLangFromSetting
import com.fff.a.core.extention.visible
import com.fff.a.core.helper.RateHelper
import com.fff.a.utils.state.RateState
import com.fff.a.databinding.FragmentSettingBinding
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