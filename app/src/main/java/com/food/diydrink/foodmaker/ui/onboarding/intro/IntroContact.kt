package com.food.diydrink.foodmaker.ui.onboarding.intro

import androidx.annotation.StringRes
import com.food.diydrink.foodmaker.R
import com.food.diydrink.foodmaker.data.model.intro.IntroModel

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