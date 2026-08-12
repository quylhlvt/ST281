package com.fff.a

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.fff.a.core.extention.OuterStrokeShadownTextView
import com.fff.a.R

import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp                     // QUAN TRỌNG NHẤT – KHÔNG ĐƯỢC THIẾU
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val mmkvDir = File(filesDir, "mmkv_store").also { it.mkdirs() }
//        AppOpenManager.getInstance().disableAppResumeWithActivity(MyApplication::class.java)

        MMKV.initialize(this, mmkvDir.absolutePath)
        Log.d("MyApplication", "MMKV initialized at: ${mmkvDir.absolutePath}")
        Thread {
            try {
                // 1. Font — giảm ~100-300ms cho lần đầu
                ResourcesCompat.getFont(this, R.font.baloo2_extrabold)
                // 2. Drawable — thread-safe
                ContextCompat.getDrawable(this, R.drawable.img_bg_home)
                ContextCompat.getDrawable(this, R.drawable.img_title_home)
            } catch (e: Exception) {
                Log.e("MyApplication", "Warm up error: ${e.message}")
            }

            // 3. OuterStrokeShadownTextView — main thread
            Handler(Looper.getMainLooper()).post {
                try {
                    OuterStrokeShadownTextView(this).apply {
                        typeface = ResourcesCompat.getFont(
                            this@MyApplication, R.font.baloo2_extrabold
                        )
                    }
                } catch (e: Exception) { }
            }
        }.start()
    }

}