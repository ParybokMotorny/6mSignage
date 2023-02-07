package com.digitalsln.project6mSignage

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import com.digitalsln.project6mSignage.databinding.ActivityMainBinding
import com.digitalsln.project6mSignage.databinding.NonAutoSettingDialogBinding
import com.digitalsln.project6mSignage.databinding.PlayModeDialogBinding


class MainActivity : FragmentActivity() {

    private var _binding: ActivityMainBinding? = null

    // values for non-sleeping
    private val powerManager: PowerManager by lazy {
        getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    private val wakeLock: PowerManager.WakeLock by lazy {
        powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelocktag")
    }

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preventFromSleeping()
        initWebView()

        if (!Consts.isAppStartedFromBroadcast) {
            showExitDialog()
        }

        binding.webView.loadUrl("https://test.6lb.menu/signage/1")
    }

    override fun onResume() {
        super.onResume()
        binding.webView.reload()
    }

    private fun initWebView() {
        with(binding.webView) {
            settings.loadWithOverviewMode = false
            settings.useWideViewPort = false
            settings.domStorageEnabled = true
            settings.allowContentAccess = true
            binding.webView.webViewClient = WebViewClient()
            setInitialScale(100)
            activateJS(this)
        }
    }

    private fun preventFromSleeping() {
        wakeLock.acquire()
    }

    private fun restartApp() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val mPendingIntentId: Int = MAGICAL_NUMBER
        val mPendingIntent = PendingIntent.getActivity(
            applicationContext,
            mPendingIntentId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val mgr = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
        System.exit(0)
    }

    private fun showExitDialog() {

        val dialogBinding = NonAutoSettingDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.run {
            playButton.setOnClickListener {
                dialog.dismiss()
            }
            playModeButton.setOnClickListener {
                dialog.dismiss()
                showPlayModeDialog()
            }
            resetAllSettingsButton.setOnClickListener {
                dialog.dismiss()
                resetAllSettings()
                restartApp()
            }
        }

        dialog.show()
    }

    private fun showPlayModeDialog(){
        val dialogBinding = PlayModeDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.run {
            realButton.setOnClickListener {
                dialog.dismiss()
                binding.webView.loadUrl("https://6lb.menu/signage")
            }
            testButton.setOnClickListener {
                dialog.dismiss()
                binding.webView.loadUrl("https://test.6lb.menu/signage")
            }
        }

        dialog.show()
    }

    override fun onDestroy() {
        wakeLock.release()
        super.onDestroy()
    }

    private fun activateJS(webView: WebView) {
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
    }

    private fun resetAllSettings(){
        // TODO
    }

    companion object {
        const val MAGICAL_NUMBER = 3
    }
}