
package com.chibi.avatar.chibimaker.ui.main.createPony

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.chibi.avatar.chibimaker.R
import com.chibi.avatar.chibimaker.ViewModelActivity
import com.chibi.avatar.chibimaker.core.base.BackPressHandler
import com.chibi.avatar.chibimaker.core.base.BaseFragment
import com.chibi.avatar.chibimaker.core.extention.InternetExtension.isInternetAvailable
import com.chibi.avatar.chibimaker.core.extention.InternetExtension.isNetworkConnected
import com.chibi.avatar.chibimaker.core.extention.safeNavigate
import com.chibi.avatar.chibimaker.core.extention.setImageActionBar
import com.chibi.avatar.chibimaker.core.extention.setTextActionBar
import com.chibi.avatar.chibimaker.data.model.custom.CustomModel
import com.chibi.avatar.chibimaker.databinding.FragmentChoosePonyBinding
import com.chibi.avatar.chibimaker.ui.main.customize.CustomizeFragment.Companion.ARG_TEMPLATE_ID
import com.chibi.avatar.chibimaker.ui.main.customize.CustomizeFragment.Companion.ARG_TEMPLATE_INDEX
import com.chibi.avatar.chibimaker.ui.main.customize.CustomizeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChoosePonyFragment : BaseFragment<FragmentChoosePonyBinding, ChoosePonyViewModel>(
    FragmentChoosePonyBinding::inflate,
    ChoosePonyViewModel::class.java
), BackPressHandler {
    private val mainViewModel: ViewModelActivity by activityViewModels()
    private lateinit var adapter: ChoosePonyAdapter
    private var isFirstLoad = true
    private val isCouple: Boolean get() = arguments?.getBoolean(ARG_IS_COUPLE, false) ?: false

    private val nativeCollapId: Int
        get() = if (isCouple) R.string.native_cl_categoryCouple else R.string.native_cl_category

    private val nativeCategoryId: Int
        get() = if (isCouple) R.string.native_categoryCouple else R.string.native_category

    private fun templatesForMode(templates: List<CustomModel>): List<CustomModel> =
        templates.filter { it.isCoupleTemplate() == isCouple }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentChoosePonyBinding = FragmentChoosePonyBinding.inflate(inflater, container, false)

    override fun onFragmentStart() {
        if (!isAdded || isDetached) return

    }

    override fun onFragmentStop() {
        if (!isAdded || isDetached) return

    }
    override fun initView() {


        setImageActionBar(binding.actionBar.btnActionBarLeft, R.drawable.back_app)
        setTextActionBar(
            binding.actionBar.tvCenter,
            getString(R.string.category)
        )

        adapter = ChoosePonyAdapter { character, position ->
            val number = character.id.filter { it.isDigit() }
            val eventName = if (isCouple) "click_item_couple${number}" else "click_item_${number}"
            Log.d("logevent", "$eventName- ${character.avatar}")
            if (character.id.startsWith("online_")) {
                if (!isInternetAvailable(requireContext())) {
                    showUnstableNetworkDialog(); return@ChoosePonyAdapter
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    val hasInternet = withContext(Dispatchers.IO) {
                        isNetworkConnected(requireContext())
                    }
                    if (!hasInternet) showUnstableNetworkDialog()
                    else
                        navigateToCustomize(character, position)

                }
            } else {
                // ✅ Offline item — verify data tồn tại trước khi navigate
                val safeIndex = mainViewModel.templates.value.indexOfFirst { it.id == character.id }
                if (safeIndex < 0) {
                    showToast(getString(R.string.download_failed_please_try_again_later)); return@ChoosePonyAdapter
                }
                navigateToCustomize(character, safeIndex)
            }
        }

        binding.recycleChoose.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter       = this@ChoosePonyFragment.adapter
            itemAnimator  = null
        }
    }
    private fun navigateToCustomize(character: CustomModel, index: Int) {
        val templates = mainViewModel.templates.value
        // `index` thuộc list đã filter theo single/couple, nên chỉ dùng như một
        // fast-path. ID mới là khóa ổn định để tìm index trong list gốc.
        val correctIndex = if (templates.getOrNull(index)?.id == character.id) {
            index
        } else {
            templates.indexOfFirst { it.id == character.id }
                .takeIf { it >= 0 }
                ?: run {
                    showToast(getString(R.string.download_failed_please_try_again_later))
                    return
                }
        }
        findNavController().safeNavigate(
            R.id.action_createPony_to_custom,
            bundleOf(
                ARG_TEMPLATE_INDEX to correctIndex,
                ARG_TEMPLATE_ID to character.id,
                // Truyền tiếp mode từ Home -> Category -> Customize.
                CustomizeFragment.ARG_IS_COUPLE to isCouple
            )
        )
    }
    override fun viewListener() {
        binding.actionBar.btnActionBarLeft.setOnClickListener { navigateBack() }
    }

    private fun navigateBack() {
        if (isCouple) {
        findNavController().navigateUp()
        } else {
            findNavController().navigateUp()
        }
    }

    override fun onBackPressed(): Boolean {
        navigateBack()
        return true
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    kotlinx.coroutines.flow.combine(
                        mainViewModel.templates,
                        mainViewModel.isFetchingOnlineFlow
                    ) { templates, isFetching -> Pair(templates, isFetching) }
                        .collect { (templates, isFetching) ->
                            // ✅ Lọc theo trạng thái mạng
                            val hasInternet = withContext(Dispatchers.IO) {
                                isInternetAvailable(requireContext())
                            }
                            val modeTemplates = templatesForMode(templates)
                            val filteredTemplates = if (hasInternet) {
                                modeTemplates
                            } else {
                                modeTemplates.filter { !it.id.startsWith("online_") }
                            }

                            adapter.submitList(filteredTemplates)

                            if (isFirstLoad && !isFetching) {
                                isFirstLoad = false
                                if (filteredTemplates.size <= 1) showNoInternetDialog()
                            }
                        }
                }

                launch {
                    mainViewModel.templates.collect { templates ->
                        val hasOnline = templatesForMode(templates).any { it.id.startsWith("online_") }
                        if (!hasOnline && !mainViewModel.isFetchingOnlineFlow.value) {
                            val hasInternet = withContext(Dispatchers.IO) {
                                isInternetAvailable(requireContext())
                            }
                            if (hasInternet) mainViewModel.fetchOnlineTemplates()
                        }
                    }
                }

                launch {
                    mainViewModel.error.collect { error ->
                        error?.let { showSnackbar(it) }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            val hasInternet = withContext(Dispatchers.IO) {
                isInternetAvailable(requireContext())
            }
            val templates = mainViewModel.templates.value
            val modeTemplates = templatesForMode(templates)
            val filtered = if (hasInternet) {
                modeTemplates
            } else {
                modeTemplates.filter { !it.id.startsWith("online_") }
            }
            adapter.submitList(filtered)

            // Fetch online nếu có mạng mà chưa có data online
            if (hasInternet) {
                val hasOnline = modeTemplates.any { it.id.startsWith("online_") }
                if (!hasOnline && !mainViewModel.isFetchingOnlineFlow.value) {
                    mainViewModel.fetchOnlineTemplates()
                }
            }
        }
    }

    override fun bindViewModel() {}

    companion object {
        const val ARG_IS_COUPLE = "is_couple"
    }
}
