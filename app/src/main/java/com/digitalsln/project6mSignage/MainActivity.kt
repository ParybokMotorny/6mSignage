package com.digitalsln.project6mSignage

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import com.digitalsln.project6mSignage.databinding.ActivityMainBinding


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

            // code from stackoverflow to solve white screen in old api
//            settings.javaScriptEnabled = true
            settings.allowContentAccess = true
            binding.webView.webViewClient = WebViewClient()

//            settings.mixedContentMode = 0
//            binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            setInitialScale(100)
            activateJS(this)
            loadUrl("https://test.6lb.menu/signage/1")

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
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_exit_exit)
            .setMessage(R.string.dialog_exit_do_you_want_to_close_app)
            .setPositiveButton(R.string.dialog_exit_yes) { _, _ ->
                restartApp()
            }.setNegativeButton(R.string.dialog_exit_no) { _, _ -> }
            .create()
            .show()
    }

    override fun onDestroy() {
        wakeLock.release()
        super.onDestroy()
    }

    private fun activateJS(webView: WebView) {
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
    }

    companion object {
        const val MAGICAL_NUMBER = 3
    }
}