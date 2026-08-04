package com.chibi.avatar.chibimaker.ui.onboarding.permission

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chibi.avatar.chibimaker.R
import com.chibi.avatar.chibimaker.core.base.BackPressHandler
import com.chibi.avatar.chibimaker.core.base.BaseFragment
import com.chibi.avatar.chibimaker.core.extention.checkPermissions
import com.chibi.avatar.chibimaker.core.extention.goToSettings
import com.chibi.avatar.chibimaker.core.extention.gone
import com.chibi.avatar.chibimaker.core.extention.onClick
import com.chibi.avatar.chibimaker.core.extention.requestPermission
import com.chibi.avatar.chibimaker.core.extention.select
import com.chibi.avatar.chibimaker.core.extention.setTextActionBar
import com.chibi.avatar.chibimaker.core.extention.toHomeFromPermission
import com.chibi.avatar.chibimaker.core.extention.visible
import com.chibi.avatar.chibimaker.core.helper.PermissionHelper
import com.chibi.avatar.chibimaker.core.helper.StringHelper
import com.chibi.avatar.chibimaker.databinding.FragmentPermissionBinding
import com.chibi.avatar.chibimaker.utils.key.RequestKey
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class PermissionFragment : BaseFragment<FragmentPermissionBinding, PermissionViewModel>(
    FragmentPermissionBinding::inflate, PermissionViewModel::class.java
), BackPressHandler {
    override fun viewListener() {
        binding.swPermission.onClick(1500) { handlePermissionRequest(isStorage = true) }
        binding.swNotification.onClick(1500) { handlePermissionRequest(isStorage = false) }
        binding.tvContinue.onClick(1000) {

                    handleContinue()}

    }
    private fun isNetworkAvailable(): Boolean {
        val cm = requireContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE)
                as android.net.ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            cm.activeNetworkInfo?.isConnected == true
        }
    }
    private fun updateContinueMargin() {
//        val marginPx = if (!isNetworkAvailable()) {
//            200.dp(requireContext())
//        } else {
//            10.dp(requireContext())
//        }
//        val params = binding.tvContinue.layoutParams as? ViewGroup.MarginLayoutParams
//        params?.bottomMargin = marginPx  // hoặc topMargin tuỳ layout
//        binding.tvContinue.layoutParams = params
    }
    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): FragmentPermissionBinding = FragmentPermissionBinding.inflate(inflater, container, false)

    override fun initView() {
        updateContinueMargin()
        binding.setupActionBar()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            binding.btnStorage.visible()
            binding.btnNotification.gone()
        } else {
            binding.btnNotification.visible()
            binding.btnStorage.gone()
        }
        // cập nhật UI switch khi vào màn
        updatePermissionUI(requireContext().checkPermissions(PermissionHelper.storagePermission), true)
        updatePermissionUI(requireContext().checkPermissions(PermissionHelper.notificationPermission), false)
    }

    private fun FragmentPermissionBinding.setupActionBar() {
        actionBar.apply {
            tvStart.select()
            setTextActionBar(tvStart, getString(R.string.permission))
        }
    }

// ❌ Xóa 2 dòng này
// private var storageDenyCount = 0
// private var notificationDenyCount = 0

    private fun handlePermissionRequest(isStorage: Boolean) {
        val perms = if (isStorage) PermissionHelper.storagePermission
        else PermissionHelper.notificationPermission

        when {
            requireContext().checkPermissions(perms) ->
                showToast(if (isStorage) R.string.granted_storage else R.string.granted_notification)

            // ✅ Dùng ViewModel thay vì local count
            viewModel.shouldGoToSettings(isStorage) -> activity?.goToSettings()

            else -> requestPermission(
                perms,
                if (isStorage) RequestKey.STORAGE_PERMISSION_CODE
                else RequestKey.NOTIFICATION_PERMISSION_CODE
            )
        }
    }
    override fun onResume() {
        super.onResume()
        // ✅ Cập nhật lại UI khi quay về từ Settings hoặc sau khi grant
        updatePermissionUI(
            requireContext().checkPermissions(PermissionHelper.storagePermission),
            true
        )
        updatePermissionUI(
            requireContext().checkPermissions(PermissionHelper.notificationPermission),
            false
        )
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val granted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        when (requestCode) {
            RequestKey.STORAGE_PERMISSION_CODE -> {
                if (granted) {
                    viewModel.onStorageGranted()
                } else {
                    viewModel.onStorageDenied()
                }
                // ✅ Luôn update UI dù granted hay denied
                updatePermissionUI(granted, true)
            }
            RequestKey.NOTIFICATION_PERMISSION_CODE -> {
                if (granted) {
                    viewModel.onNotificationGranted()
                } else {
                    viewModel.onNotificationDenied()
                }
                // ✅ Luôn update UI dù granted hay denied
                updatePermissionUI(granted, false)
            }
        }
    }

    private fun updatePermissionUI(granted: Boolean, isStorage: Boolean) {
        val imageView = if (isStorage) binding.swPermission else binding.swNotification
        imageView.setImageResource(if (granted) R.drawable.switch_on else R.drawable.switch_off)
    }


    override fun observeData() {}

    override fun initText() {
        binding.actionBar.tvCenter.select()
        val textRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            R.string.to_access_13 else R.string.to_access

        binding.txtPermission.text = buildString {
            append(getString(R.string.allow))
            append(" ")
            append(getString(R.string.app_name))
            append(" ")
            append(getString(textRes))
        }
    }

    private fun handleContinue() {
        sharedPreferences.setPermissionScreen(true)
        toHomeFromPermission()
    }

    override fun bindViewModel() {}

    private fun createColoredText(
        @androidx.annotation.StringRes textRes: Int,
        @androidx.annotation.ColorRes colorRes: Int,
        font: Int = R.font.baloo2_bold
    ) = StringHelper.changeColor(requireContext(), getString(textRes), colorRes, font)

    override fun onBackPressed(): Boolean {
        requireActivity().finishAffinity()
        exitProcess(0)
        return true
    }
}