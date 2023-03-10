package com.digitalsln.project6mSignage

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PowerManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import com.digitalsln.project6mSignage.databinding.ActivityMainBinding
import com.digitalsln.project6mSignage.databinding.HandMadeStartAppDialogBinding
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

    private var dialogMain: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preventFromSleeping()
        initWebView()

        if (!Consts.isAppStartedFromBroadcast) {
            showHandMadeStartAppDialog()
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

    private fun showHandMadeStartAppDialog() {
        val dialogBinding = HandMadeStartAppDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(this)
        dialogMain = dialog
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.run {
            playButton.requestFocus()

            playButton.setOnClickListener {
                dialog.dismiss()
                binding.webView.loadUrl("https://test.6lb.menu/signage/1")
            }
            playModeButton.setOnClickListener {
                showPlayModeDialog()
            }
            resetAllSettingsButton.setOnClickListener {
                showResetSettingsDialog()
            }
        }

        dialog.show()
    }

    private fun showPlayModeDialog(){

        val dialogBinding = PlayModeDialogBinding.inflate(layoutInflater)

        val choice = loadPlayModePreferences(getPreferences(Context.MODE_PRIVATE))

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.run {

            when (choice) {
                PlayModeDialogChoice.REAL -> {
                    realButton.requestFocus()
                }
                PlayModeDialogChoice.TEST -> {
                    testButton.requestFocus()
                }
            }

            realButton.setOnClickListener {
                dialog.dismiss()
                savePlayModePreferences(getPreferences(Context.MODE_PRIVATE), PlayModeDialogChoice.REAL)
                binding.webView.loadUrl("https://6lb.menu/signage")
                dialogMain?.dismiss()
            }
            testButton.setOnClickListener {
                dialog.dismiss()
                savePlayModePreferences(getPreferences(Context.MODE_PRIVATE), PlayModeDialogChoice.TEST)
                binding.webView.loadUrl("https://test.6lb.menu/signage")
                dialogMain?.dismiss()
            }
        }

        dialog.show()
    }

    private fun showResetSettingsDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.dialog_exit_do_you_want_to_close_app)
            .setPositiveButton(R.string.dialog_exit_yes) { _, _ ->
                resetAllSettings()
                restartApp()
            }.setNegativeButton(R.string.dialog_exit_no) { _, _ ->
                showHandMadeStartAppDialog()
            }
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

    private fun resetAllSettings() {
        binding.webView.run{
            clearCache(true)
            clearHistory()
            clearFormData()
            clearMatches()
            clearSslPreferences()
        }
    }

    private fun savePlayModePreferences(sharedPref: SharedPreferences, choice: PlayModeDialogChoice) {
        val editor = sharedPref.edit()
        editor.putInt(PLAY_MODE_CHOICE_CODE, choice.code)
        editor.apply()
    }

    private fun loadPlayModePreferences(sharedPref: SharedPreferences): PlayModeDialogChoice {
        val choice = sharedPref.getInt(PLAY_MODE_CHOICE_CODE, 0)
        return PlayModeDialogChoice.getChoice(choice)
    }

    companion object {
        const val MAGICAL_NUMBER = 3

        const val PLAY_MODE_CHOICE_CODE = "8"
    }
}