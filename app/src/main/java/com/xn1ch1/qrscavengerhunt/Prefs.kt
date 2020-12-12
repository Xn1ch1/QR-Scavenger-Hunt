package com.xn1ch1.qrscavengerhunt

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {

	private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

	var huntStarted: Boolean
		get() = prefs.getBoolean(HUNT_STARTED, false)
		set(value) = prefs.edit().putBoolean(HUNT_STARTED, value).apply()

	var currentClue: Int
		get() = prefs.getInt(CURRENT_CLUE, 0)
		set(value) = prefs.edit().putInt(CURRENT_CLUE, value).apply()

	var clueCount: Int
		get() = prefs.getInt(CLUE_COUNT, 0)
		set(value) = prefs.edit().putInt(CLUE_COUNT, value).apply()

	companion object {

		const val PREFS_FILENAME = "com.xn1ch1.qrscavengerhunt.prefs"

		const val CURRENT_CLUE = "currentClue"
		const val HUNT_STARTED = "huntStarted"
		const val CLUE_COUNT = "clueCount"

	}

}