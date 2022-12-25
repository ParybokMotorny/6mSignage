package com.example.helloworld

import android.os.Bundle
import android.webkit.WebSettings
import androidx.fragment.app.FragmentActivity
import com.example.helloworld.databinding.ActivityMainBinding

class MainActivity : FragmentActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.webView?.let{ webView ->
            webView.settings.allowContentAccess = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.settings.builtInZoomControls = true
            webView.settings.domStorageEnabled = true

            // activating js
            webView.settings.javaScriptEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true

            webView.loadUrl("https://test.6lb.menu/signage/1")
        }
    }
}