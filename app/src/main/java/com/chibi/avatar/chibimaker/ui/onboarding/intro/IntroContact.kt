package com.chibi.avatar.chibimaker.ui.onboarding.intro

import androidx.annotation.StringRes
import com.chibi.avatar.chibimaker.R
import com.chibi.avatar.chibimaker.data.model.intro.IntroModel

class IntroContact {
}
data class IntroUiState(
    val pagesSplash: List<IntroModel>? = emptyList(),
    val page: Int = 0,
    @StringRes val textButtonRes: Int = R.string.next
)

sealed class IntroSingleEvent {
    data object NavigateToNextScreen : IntroSingleEvent()
}