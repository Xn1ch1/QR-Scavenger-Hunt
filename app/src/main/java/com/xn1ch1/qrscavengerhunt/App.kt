package com.xn1ch1.qrscavengerhunt

import android.app.Application

val prefs: Prefs by lazy {
	App.prefs
}

class App : Application() {

	companion object {
		lateinit var prefs: Prefs
	}

	override fun onCreate() {

		prefs = Prefs(applicationContext)
		super.onCreate()

	}

}