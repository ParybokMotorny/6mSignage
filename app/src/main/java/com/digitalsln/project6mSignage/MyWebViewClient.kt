package com.digitalsln.project6mSignage

import android.webkit.WebView

import android.webkit.WebViewClient


class MyWebViewClient : WebViewClient() {
   override fun onPageFinished(view: WebView, url: String) {
      try {
         while (view.progress < 100) {
            view.reload()
         }
      } catch (e: Exception) {
         e.printStackTrace()
      }
   }
}