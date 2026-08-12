package com.fff.a.ui.onboarding.intro

import androidx.annotation.StringRes
import com.fff.a.data.model.intro.IntroModel
import com.fff.a.R

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