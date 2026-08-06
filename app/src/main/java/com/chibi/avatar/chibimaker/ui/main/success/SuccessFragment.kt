package com.chibi.avatar.chibimaker.ui.main.success

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.chibi.avatar.chibimaker.R
import com.chibi.avatar.chibimaker.core.base.BaseFragment
import com.chibi.avatar.chibimaker.core.extention.InternetExtension.isInternetAvailable
import com.chibi.avatar.chibimaker.core.extention.checkPermissions
import com.chibi.avatar.chibimaker.core.extention.goToSettings
import com.chibi.avatar.chibimaker.core.extention.loadImage
import com.chibi.avatar.chibimaker.core.extention.onClick
import com.chibi.avatar.chibimaker.core.extention.onClick1
import com.chibi.avatar.chibimaker.core.extention.safeNavigate
import com.chibi.avatar.chibimaker.core.extention.setImageActionBar
import com.chibi.avatar.chibimaker.core.extention.setTextActionBar
import com.chibi.avatar.chibimaker.core.extention.toCleanSelections
import com.chibi.avatar.chibimaker.core.extention.visible
import com.chibi.avatar.chibimaker.core.helper.PermissionRequestHelper
import com.chibi.avatar.chibimaker.databinding.FragmentSuccessBinding
import com.chibi.avatar.chibimaker.databinding.FragmentViewBinding
import com.chibi.avatar.chibimaker.ui.main.customize.CustomizeFragment
import com.chibi.avatar.chibimaker.ui.onboarding.permission.PermissionViewModel
import com.chibi.avatar.chibimaker.utils.share.SocialShareManager
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
@AndroidEntryPoint
class SuccessFragment : BaseFragment<FragmentSuccessBinding, SuccessViewModel>(
    FragmentSuccessBinding::inflate,
    SuccessViewModel::class.java
) {
    private val storageHelper = PermissionRequestHelper()

    private val permissionViewModel: PermissionViewModel by activityViewModels()
    private val socialShareManager by lazy(LazyThreadSafetyMode.NONE) {
        SocialShareManager(requireContext())
    }
    private var currentImagePath: String = ""
    private var isReturningFromExternalScreen = false
    private val imagePath: String by lazy { arguments?.getString("imagePath") ?: "" }
    private val imageType: Int    by lazy { arguments?.getInt("imageType", 0) ?: 0 }
    private val idEdit: String    by lazy { arguments?.getString("idEdit") ?: "" }

    companion object {
        private const val EXTERNAL_SCREEN_RESTORE_DELAY_MS = 500L
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSuccessBinding = FragmentSuccessBinding.inflate(inflater, container, false)

    override fun onResume() {
        super.onResume()
        hideLoadingSafe()
        hideGlobalDialogSafe()
        if (!isReturningFromExternalScreen || view == null) return

        restoreWindowInteractions()
        restoreViewInteractions()
        binding.root.post {
            if (!isAdded || view == null) return@post
            restoreWindowInteractions()
            restoreViewInteractions()
        }
        binding.root.postDelayed({
            if (!isAdded || view == null) return@postDelayed
            restoreWindowInteractions()
            restoreViewInteractions()
            isReturningFromExternalScreen = false
        }, EXTERNAL_SCREEN_RESTORE_DELAY_MS)
    }

    private fun restoreViewInteractions() {
        binding.root.isEnabled = true
        binding.actionBar.root.isEnabled = true
        binding.actionBar.btnActionBarLeft.isEnabled = true
        binding.actionBar.btnActionBarLeft.isClickable = true
        binding.actionBar.btnActionBarNextToRight.isEnabled = true
        binding.actionBar.btnActionBarNextToRight.isClickable = true
        binding.actionBar.btnActionBarRight.isEnabled = true
        binding.actionBar.btnActionBarRight.isClickable = true
        binding.btnBottomLeft.isEnabled = true
        binding.btnBottomLeft.isClickable = true
        binding.btnBottomRight.isEnabled = true
        binding.btnBottomRight.isClickable = true
        binding.btnBottomLeftSocial.isEnabled = true
        binding.btnBottomLeftSocial.isClickable = true
        binding.btnBottomRightSocial.isEnabled = true
        binding.btnBottomRightSocial.isClickable = true
        binding.root.requestLayout()
        binding.root.invalidate()
    }

    private fun restoreWindowInteractions() {
        requireActivity().window.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        requireActivity().window.decorView.isEnabled = true
    }

    override fun initView() {

        currentImagePath = imagePath
        binding.apply {
            setImageActionBar(actionBar.btnActionBarLeft, R.drawable.back_app)
            loadImage(requireContext(), imagePath, imvImage)
            txtLeftSocial.apply  {
                isSelected =true
            }
            txtRightSocial.apply  {
                isSelected =true
            }
            txtLeft.apply  {
                isSelected =true
                visible();
                text = getString(R.string.my_food) }
            txtRight.apply {
                isSelected =true
                visible();
                text = getString(R.string.download) }
            setTextActionBar(actionBar.tvCenter, getString(R.string.successful))
            setImageActionBar(actionBar.btnActionBarNextToRight, R.drawable.ic_share)
            setImageActionBar(actionBar.btnActionBarRight, R.drawable.ic_home)
            tvSuccess.visible()
        }
    }

    override fun viewListener() {
        binding.apply {
            actionBar.btnActionBarLeft.onClick1 { findNavController().navigateUp() }

            // Home
            actionBar.btnActionBarRight.onClick1 {
                    findNavController().navigate(
                        R.id.action_success_to_home, null,
                        androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true).build()
                    )


            }
            // Share
            actionBar.btnActionBarNextToRight.onClick( 1500) { shareImage() }

            // MyCreation
            btnBottomLeft.onClick1 {
                    findNavController().navigate(
                        R.id.action_success_to_myPony, null,
                        androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, false).build()
                    )


            }

            // Download
            btnBottomRight.onClick1 { downloadImage() }
            btnBottomLeftSocial.onClick1 {
                shareToSocialApp(SocialShareManager.SocialApp.FACEBOOK)
            }
            btnBottomRightSocial.onClick1 {
                shareToSocialApp(SocialShareManager.SocialApp.INSTAGRAM)
            }
        }
    }

    private fun shareToSocialApp(app: SocialShareManager.SocialApp) {
        val path = currentImagePath.takeIf { it.isNotBlank() } ?: imagePath
        when (socialShareManager.shareImage(path, app)) {
            SocialShareManager.ShareResult.Started -> isReturningFromExternalScreen = true
            SocialShareManager.ShareResult.ImageNotFound -> showToast(getString(R.string.image_not_found))
            is SocialShareManager.ShareResult.AppNotAvailable -> showToast(getString(if (app == SocialShareManager.SocialApp.FACEBOOK) R.string.facebook_not_available else R.string.instagram_not_available))
            is SocialShareManager.ShareResult.Failed -> showToast(getString(R.string.share_failed))
        }
    }
    private fun shareImage() {
        if (imagePath.isEmpty()) return
        val uri = androidx.core.content.FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            java.io.File(imagePath)
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        isReturningFromExternalScreen = true
        startActivity(android.content.Intent.createChooser(intent, getString(R.string.share)))
    }
// ViewFragment.kt

    // Thêm vào ViewFragment
    private fun downloadImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { performDownload(); return }
        val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        when {
            requireContext().checkPermissions(arrayOf(permission)) -> performDownload()
            permissionViewModel.shouldGoToSettings(isStorage = true) -> {
                isReturningFromExternalScreen = true
                activity?.goToSettings()
            }
            else -> downloadPermissionLauncher.launch(arrayOf(permission))
        }
    }

    private val downloadPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                permissionViewModel.onStorageGranted()
                performDownload()
            } else {
                permissionViewModel.onStorageDenied()
                // ✅ Chỉ toast, KHÔNG check goToSettings ở đây
                // goToSettings sẽ được check ở downloadImage() lần nhấn tiếp theo
                showToast(getString(R.string.download_failed_please_try_again_later))
            }
        }
    private fun performDownload() {
        viewModel.downloadFile(requireContext(), imagePath) { success ->
            showToast(
                if (success) getString(R.string.download_success, getString(R.string.app_name))
                else getString(R.string.download_failed_please_try_again_later)
            )
        }
    }

    private fun confirmDelete() {
        showConfirmDialog(
            title = getString(R.string.delete),
            message = getString(R.string.are_you_sure_want_to_delete_this_item),
            onYes = {
                viewModel.deleteFile(
                    path     = imagePath,
                    isAvatar = imageType == 1,
                    idEdit   = idEdit,
                    onDone   = {
                        findNavController().navigateUp()
                    }
                )
            },
            onNo = null
        )
    }
    private fun navigateToEdit() {
        if (idEdit.isEmpty() || imageType != 1) return

        val customized = viewModelActivity.customizedCharacters.value
            .firstOrNull { it.id == idEdit }
            ?: run { showToast("Character not found"); return }

        val templateIndex = viewModelActivity.getTemplateIndexForCustomized(idEdit)
            .takeIf { it >= 0 }
            ?: run { showUnstableNetworkDialog(); return }  // ✅ không tìm thấy template → có thể do chưa load online

        val template = viewModelActivity.templates.value.getOrNull(templateIndex)

        // ✅ Thêm check: online template + mất mạng + online templates < 2
        if (template?.id?.startsWith("online_") == true) {
            val onlineTemplateCount = viewModelActivity.templates.value
                .count { it.id.startsWith("online_") }
            if (!isInternetAvailable(requireContext()) || onlineTemplateCount < 2) {
                showUnstableNetworkDialog()
                return
            }
        }

        val args = CustomizeFragment.newArgs(
            templateIndex   = templateIndex,
            isEdit          = true,
            customizedId    = idEdit,
            savedSelections = customized.selections.toCleanSelections(),
            isFlipped       = customized.isFlipped
        )
        findNavController().safeNavigate(R.id.action_view_to_customize, args)
    }

    private fun showToast(msg: String) =
        android.widget.Toast.makeText(requireContext(), msg, android.widget.Toast.LENGTH_SHORT).show()

    override fun observeData() {
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("updated_image_path")
            ?.observe(viewLifecycleOwner) { newPath ->
                if (newPath.isNullOrEmpty()) return@observe
                currentImagePath = newPath
                loadImage(requireContext(), currentImagePath, binding.imvImage)
            }
    }
    override fun bindViewModel() {}
}
