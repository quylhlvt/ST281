package com.food.diydrink.foodmaker.core.extention
// Extension functions (updated for AndroidX Fragment)
import androidx.fragment.app.Fragment
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat
import com.food.diydrink.foodmaker.core.helper.RateHelper
import com.food.diydrink.foodmaker.core.helper.SharedPreferencesManager
import com.food.diydrink.foodmaker.utils.state.RateState

fun Fragment.shareApp() {
    ShareCompat.IntentBuilder(requireContext())
        .setType("text/plain")
        .setChooserTitle("Chooser title")
        .setText("http://play.google.com/store/apps/details?id=${requireContext().packageName}")
        .startChooser()
}

fun Fragment.policy() {
    val url = "https://sites.google.com/view/food-maker-diy-food-drink/home "
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}

fun Fragment.rateApp(
    sharePreference: SharedPreferencesManager,
    onRateResult: (RateState) -> Unit = {}
) {
    RateHelper.showRateDialog(requireActivity(), sharePreference, onRateResult)
}