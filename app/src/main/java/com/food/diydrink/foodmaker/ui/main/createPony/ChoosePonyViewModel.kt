package com.food.diydrink.foodmaker.ui.main.createPony

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.ViewModelActivity
import com.food.diydrink.foodmaker.core.base.BaseFragment
import com.food.diydrink.foodmaker.core.extention.setImageActionBar
import com.food.diydrink.foodmaker.data.datalocal.manager.AppDataManager
import com.food.diydrink.foodmaker.data.model.custom.CustomModel
import com.food.diydrink.foodmaker.databinding.FragmentChoosePonyBinding
import com.food.diydrink.foodmaker.databinding.ItemChooseBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── VIEWMODEL ─────────────────────────────────────────────────────────────────

@HiltViewModel
class ChoosePonyViewModel @Inject constructor(
    appDataManager: AppDataManager
) : ViewModel() {
    // State của màn Category nằm trong ViewModel và dùng chung nguồn cache local.
    // Fragment bị destroy/recreate khi đi Custom rồi quay lại cũng không mất list.
    val templates = appDataManager.templates
}

// ── ADAPTER ───────────────────────────────────────────────────────────────────


// ── FRAGMENT ──────────────────────────────────────────────────────────────────
