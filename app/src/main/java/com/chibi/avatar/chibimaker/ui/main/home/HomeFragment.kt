package com.chibi.avatar.chibimaker.ui.main.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chibi.avatar.chibimaker.R
import com.chibi.avatar.chibimaker.ViewModelActivity
import com.chibi.avatar.chibimaker.core.base.BackPressHandler
import com.chibi.avatar.chibimaker.core.base.BaseFragment
import com.chibi.avatar.chibimaker.core.extention.InternetExtension.isInternetAvailable
import com.chibi.avatar.chibimaker.core.extention.InternetExtension.isNetworkConnected
import com.chibi.avatar.chibimaker.core.extention.OuterStrokeShadownTextView
import com.chibi.avatar.chibimaker.core.extention.gone
import com.chibi.avatar.chibimaker.core.extention.onClick
import com.chibi.avatar.chibimaker.core.extention.setImageActionBar
import com.chibi.avatar.chibimaker.core.extention.toSettingFromHome
import com.chibi.avatar.chibimaker.core.helper.RateHelper
import com.chibi.avatar.chibimaker.core.helper.RateHelper.showRateDialog
import com.chibi.avatar.chibimaker.databinding.FragmentHomeBinding
import com.chibi.avatar.chibimaker.ui.main.createPony.ChoosePonyFragment
import com.chibi.avatar.chibimaker.utils.state.RateState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.System.exit
import kotlin.system.exitProcess

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate, HomeViewModel::class.java
), BackPressHandler {

    private val mainViewModel: ViewModelActivity by activityViewModels()
    private var countRate = 0

    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

    override fun initView() {
        binding.actionBar.apply {
            setImageActionBar(btnActionBarRight, R.drawable.ic_settings)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PERF2", "HomeFragment onViewCreated: ${System.currentTimeMillis()}")
    }

    override fun onResume() {
        super.onResume()
        Log.d("PERF2", "HomeFragment onResume: ${System.currentTimeMillis()}")
    }
    override fun onFragmentStart() {
        if (!isAdded || isDetached) return

    }

    override fun onFragmentStop() {
        if (!isAdded || isDetached) return

    }
    override fun viewListener() {
        binding.apply {
            btnCreate.onClick { navigateWithCheck(R.id.action_home_to_createPony) }
            btnMyAlbum.onClick { findNavController().navigate(R.id.action_home_to_myPony) }
            btnRandom.onClick(1000) {
                navigateWithCheck(R.id.action_home_to_random)
            }
            btnChallenge.onClick(1000) {
                navigateWithCheck(R.id.action_home_to_cosplay)
            }
            actionBar.btnActionBarRight.onClick { toSettingFromHome() }
        }
    }

    private fun navigateWithCheck(destination: Int) {
        val hasNetwork = isInternetAvailable(requireContext()) &&
            isNetworkConnected(requireContext())
        val hasData = viewModelActivity.templates.value.isNotEmpty()

        when {
            !hasNetwork -> showUnstableNetworkDialog()
            !hasData -> {
                mainViewModel.fetchOnlineTemplates()
                showLoadingDataDialog()
            }
            else -> findNavController().navigate(destination)
        }
    }

    override fun observeData() {
        binding.root.post {
            Log.d("PERF2", "HomeFragment first frame: ${System.currentTimeMillis()}")
            if (!isAdded || isDetached) return@post
            binding.tv1.isSelected = true
            binding.tv2.isSelected = true
            binding.tv3.isSelected = true
            binding.tv4.isSelected = true

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mainViewModel.error.collect { error ->
                        error?.let { Log.e("HomeFragment", "❌ $it") }
                    }
                }
            }
        }
    }

    override fun bindViewModel() {}

    override fun onBackPressed(): Boolean {
        countRate = sharedPreferences.isBackRequest() + 1
        sharedPreferences.setBackRequest(countRate)
        if (!sharedPreferences.isRateRequest() && countRate % 2 == 0) {
            showRateDialog(requireActivity(), sharedPreferences) { state ->
                if (state != RateState.CANCEL) showToast(R.string.have_rated)
                requireActivity().finishAffinity()
                android.os.Process.killProcess(android.os.Process.myPid())
                exitProcess(0)
            }
        } else {
            requireActivity().finishAffinity()
            exitProcess(0)
        }
        return true
    }
}
