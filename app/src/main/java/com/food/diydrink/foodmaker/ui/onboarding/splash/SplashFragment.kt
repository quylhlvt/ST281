
package com.food.diydrink.foodmaker.ui.onboarding.splash

import android.animation.ValueAnimator
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.ViewModelActivity
import com.food.diydrink.foodmaker.core.base.BackPressHandler
import com.food.diydrink.foodmaker.core.base.BaseFragment
import com.food.diydrink.foodmaker.core.extention.toIntro
import com.food.diydrink.foodmaker.core.extention.toLanguage
import com.food.diydrink.foodmaker.core.helper.SharedPreferencesManager.isLanuageScreen
import com.food.diydrink.foodmaker.databinding.FragmentSplashBinding
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>(
    FragmentSplashBinding::inflate,
    SplashViewModel::class.java
) , BackPressHandler {
    private val mainViewModel: ViewModelActivity by activityViewModels()
    private var pendingNavigate = false
    private var navigateJob: Job? = null

    private var progressAnimator: ValueAnimator? = null
    private var currentOverlayFraction = 1f
    private var hasNavigated = false

    companion object {
        private const val MIN_SPLASH_MS  = 3_000L
        private const val API_TIMEOUT_MS = 8_000L
    }

    // ── INIT ──────────────────────────────────────────────────────────────────

    override fun initView() {
        // ✅ Warm up font — giữ nguyên, nhẹ
        ResourcesCompat.getFont(requireContext(), R.font.baloo2_extrabold)


        checkAndClearDataIfNewVersion()
//        interCallBack = object : InterCallback() {
//            override fun onNextAction() {
//                super.onNextAction()
                viewModel.triggerNavigate()
//            }
//        }

//        Admob.getInstance().loadSplashInterAds(
//            requireActivity(), getString(R.string.inter_splash), 30000, 3000, interCallBack
//        )
    }

    private fun checkAndClearDataIfNewVersion() {
        val context = requireContext()
        val currentVersion = context.packageManager
            .getPackageInfo(context.packageName, 0).versionCode
        val savedVersion = sharedPreferences.getVersionCode()

        if (savedVersion != currentVersion) {
            // ✅ Xóa MMKV
            MMKV.defaultMMKV().clearAll()

            // ✅ Xóa SharedPreferences (giữ lại language)
            sharedPreferences.clearAll()

            // ✅ Xóa file cache
            context.filesDir.deleteRecursively()
            context.cacheDir.deleteRecursively()
            context.externalCacheDir?.deleteRecursively()

            // ✅ Set lại version SAU khi clear
            sharedPreferences.setVersionCode(currentVersion)

            lifecycleScope.launch {  // ✅ đổi từ viewLifecycleOwner.lifecycleScope
                mainViewModel.forceReloadAll()
            }
        }
    }
    override fun viewListener() {}

    // Trong Fragment, observeData():
    override fun observeData() {
        viewModel.startSplashTimer(
            hasNetwork = isNetworkAvailable(),
            hasOnlineTemplates = mainViewModel.templates.value.any { it.id.startsWith("online_") },
            templatesFlow = mainViewModel.templates,
            imagesReadyFlow = mainViewModel.imagesReady,
            localDataReadyFlow = mainViewModel.localDataReady
        )
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dataReadySignal.first { it }
            if (!isNetworkAvailable()) viewModel.triggerNavigate()
            else withTimeoutOrNull(30_000L) { viewModel.navigateSignal.first { it } }
                ?: viewModel.triggerNavigate()
        }

    }

    override fun bindViewModel() {}

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSplashBinding = FragmentSplashBinding.inflate(inflater, container, false)

    // ── PROGRESS ──────────────────────────────────────────────────────────────

    // ── NAVIGATE ──────────────────────────────────────────────────────────────

    private fun goToHome() {
        if (hasNavigated) return
        hasNavigated = true  // ✅ set trước

        if (!isAdded || isDetached || isRemoving || activity == null) {
            pendingNavigate = true  // ✅ defer sang onResume
            return
        }
        doNavigate()
    }
    private fun doNavigate() {
        if (!isLanuageScreen()) { toLanguage(); return }
        toIntro()
    }
    // ── NETWORK ───────────────────────────────────────────────────────────────

    private fun isNetworkAvailable(): Boolean = try {
        val cm = requireContext().getSystemService(ConnectivityManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val caps = cm.getNetworkCapabilities(cm.activeNetwork ?: return false) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            cm.activeNetworkInfo?.isConnected == true
        }
    } catch (e: Exception) { false }

    // ── LIFECYCLE ─────────────────────────────────────────────────────────────
    override fun onPause() {
        super.onPause()
        progressAnimator?.pause()
        navigateJob?.cancel()  // ✅ Cancel khi pause, onResume sẽ tạo lại
    }

    override fun onResume() {
        super.onResume()
        progressAnimator?.resume()

        if (hasNavigated) return

        // ✅ Check ngay nếu đã ready
        if (viewModel.navigateSignal.value) {
            goToHome()
            return
        }

        // ✅ Cancel job cũ trước khi tạo mới, tránh chồng chéo
        navigateJob?.cancel()
        navigateJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateSignal.first { it }
            goToHome()
        }
    }

    override fun onDestroyView() {
        navigateJob?.cancel()
        navigateJob = null
        progressAnimator?.cancel()
        progressAnimator = null
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        return true
    }
}
