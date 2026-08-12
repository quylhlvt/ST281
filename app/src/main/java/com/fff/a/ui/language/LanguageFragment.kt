package com.fff.a.ui.language

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fff.a.core.base.BackPressHandler
import com.fff.a.core.base.BaseFragment
import com.fff.a.core.extention.invisible
import com.fff.a.core.extention.onClick
import com.fff.a.R
import com.fff.a.core.extention.popBack
import com.fff.a.core.extention.setTextActionBar
import com.fff.a.core.extention.toHomeFromLanguage
import com.fff.a.core.extention.toIntroFromLanguage
import com.fff.a.core.extention.toSettingFromLang
import com.fff.a.core.extention.visible
import com.fff.a.core.helper.LanguageHelper
import com.fff.a.core.helper.SharedPreferencesManager
import com.fff.a.utils.LanguageManager
import com.fff.a.core.helper.LanguageHelper.setLocale
import com.fff.a.core.helper.SharedPreferencesManager.isLanuageScreen
import com.fff.a.databinding.FragmentLanguageBinding
import com.fff.a.utils.LanguageManager.updateLanguage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.system.exitProcess

@AndroidEntryPoint
class LanguageFragment : BaseFragment<FragmentLanguageBinding, LanguageViewModel>(
    FragmentLanguageBinding::inflate, LanguageViewModel::class.java
), BackPressHandler {
    private val languageAdapter by lazy { LanguageAdapter(requireContext()) }
    private var isFromSetting = false

    override fun onBackPressed(): Boolean {

        when {
            isFromSetting -> {
                toSettingFromLang()
            }
            SharedPreferencesManager.isLanuageScreen() -> {
                popBack()
            }
            else -> {
                requireActivity().finishAffinity()
                exitProcess(0)
            }
        }
        return true
    }
    override fun setupPreViews() {

        val isFirst = !SharedPreferencesManager.isLanuageScreen()


        binding.recycleLanguage.apply {
            adapter = languageAdapter
            itemAnimator = null
        }

        val currentLang = SharedPreferencesManager.isLanguageKey()
        viewModel.setFirstLanguage(isFirst = isFirst)
        viewModel.loadLanguages(currentLang)

        val list = viewModel.languageList.value
        if (list.isNotEmpty()) {
            languageAdapter.submitList(list)
        }
    }
    private fun updateActionBar(isFirst: Boolean) {
        binding.apply {
            if (isFirst) {
                setTextActionBar(actionBar.tvStart, getString(R.string.language))
                actionBar.btnActionBarRight.setImageResource(R.drawable.select_language)
            } else {
                setTextActionBar(actionBar.tvCenter, getString(R.string.language))
                actionBar.btnActionBarLeft.visible()
//                actionBar.btnActionBarRight.setImageResource(R.drawable.select_language)
            }
        }
        updateDoneButtonVisibility()
    }

    private fun updateDoneButtonVisibility() {
        val hasSelectedLanguage = viewModel.codeLang.value.isNotEmpty()
        if (hasSelectedLanguage) {
            binding.actionBar.btnActionBarRight.visible()
        } else {
            binding.actionBar.btnActionBarRight.invisible()
        }
    }
    override fun viewListener() {
        binding.apply {
            actionBar.btnActionBarRight.onClick {
                handleDone()
            }
            actionBar.btnActionBarLeft.onClick( 500) {
                // Dùng chung logic với onBackPressed
                onBackPressed()
            }

        }
        
        handleRcv()
    }

    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): FragmentLanguageBinding = FragmentLanguageBinding.inflate(inflater, container, false)

    override fun initView() {
        binding.apply {
            actionBar.btnActionBarLeft.invisible()
            actionBar.btnActionBarRight.setImageResource(R.drawable.select_language)
        }
        isFromSetting = findNavController().previousBackStackEntry?.destination?.id == R.id.setting
        binding.actionBar.apply {
            btnActionBarRight.invisible()
            btnActionBarLeft.setImageResource(R.drawable.back_app)
            tvStart.isSelected = true
            tvCenter.isSelected = true
        }


        // ✅ Bỏ initRcv() — đã làm trong setupPreViews
        updateActionBar(viewModel.isFirstLanguage.value)
    }


    override fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isFirstLanguage.collect { isFirst ->
                        updateActionBar(isFirst)
                    }
                }
                launch {
                    viewModel.languageList.collect { list ->
                        if (list.isNotEmpty()) {
                            languageAdapter.submitList(list)
                        }
                    }
                }
                launch {
                    viewModel.codeLang.collect {
                        updateDoneButtonVisibility()
                    }
                }
            }
        }
    }



    override fun bindViewModel() {
    }

    private fun initRcv() {
        binding.recycleLanguage.apply {
            adapter = languageAdapter
            itemAnimator = null

        }
    }
    private fun handleRcv() {
        binding.apply {
            languageAdapter.onItemClick = { code ->
                viewModel.selectLanguage(code)
                updateDoneButtonVisibility()
            }
        }
    }

    private fun handleDone() {
        val code = viewModel.codeLang.value
        if (code.isEmpty()) {
            showToast(R.string.not_select_lang)
            return
        }

        sharedPreferences.setLanguageKey(code)
        LanguageHelper.setLocale(requireContext(), code)
        LanguageManager.updateLanguage(code)

        if (viewModel.isFirstLanguage.value) {
            sharedPreferences.setLanuageScreen(true)
            Log.d("LANG", "Navigating to Intro")
            toIntroFromLanguage()
        }  else {
        // Update locale cho Activity context ngay lập tức
        val locale = Locale(code)
        val config = Configuration(requireActivity().resources.configuration)
        config.setLocale(locale)
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)

        // Rồi mới navigate
        toHomeFromLanguage()
    }
    }
}
