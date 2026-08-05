
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
            val eventName = "click_item_${number}"
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
                val safeIndex = viewModel.templates.value.indexOfFirst { it.id == character.id }
                if (safeIndex < 0) {
                    showToast(getString(R.string.download_failed_please_try_again_later)); return@ChoosePonyAdapter
                }
                navigateToCustomize(character, safeIndex)
            }
        }

        binding.recycleChoose.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter       = this@ChoosePonyFragment.adapter
            itemAnimator  = null
        }
    }
    private fun navigateToCustomize(character: CustomModel, index: Int) {
        val templates = viewModel.templates.value
        // ID là khóa ổn định để tìm index trong list gốc.
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
                ARG_TEMPLATE_ID to character.id
            )
        )
    }
    override fun viewListener() {
        binding.actionBar.btnActionBarLeft.setOnClickListener { navigateBack() }
    }

    private fun navigateBack() {
        findNavController().navigateUp()
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
                        viewModel.templates,
                        mainViewModel.isFetchingOnlineFlow
                    ) { templates, isFetching -> Pair(templates, isFetching) }
                        .collect { (templates, isFetching) ->
                            // Giữ danh sách đã tải/cache khi mất mạng. Việc mở một
                            // template online vẫn được kiểm tra ở click listener.
                            adapter.submitList(templates)

                            if (isFirstLoad && !isFetching) {
                                isFirstLoad = false
                                if (templates.size <= 1) showNoInternetDialog()
                            }
                        }
                }

                launch {
                    viewModel.templates.collect { templates ->
                        val hasOnline = templates.any { it.id.startsWith("online_") }
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
            val templates = viewModel.templates.value
            // Không xóa item online khỏi UI khi offline vì dữ liệu đã có trong cache.
            adapter.submitList(templates)

            // Fetch online nếu có mạng mà chưa có data online
            if (hasInternet) {
                val hasOnline = templates.any { it.id.startsWith("online_") }
                if (!hasOnline && !mainViewModel.isFetchingOnlineFlow.value) {
                    mainViewModel.fetchOnlineTemplates()
                }
            }
        }
    }

    override fun bindViewModel() {}

}
