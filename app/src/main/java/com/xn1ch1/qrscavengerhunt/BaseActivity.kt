package com.xn1ch1.qrscavengerhunt

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

open class BaseActivity : Activity() {

	fun Activity.hideKeyboard() {
		hideKeyboard(currentFocus?: View(this))
	}

	private fun Context.hideKeyboard(view: View) {
		val inputMethodManager =
				getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
		inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
	}

	fun Fragment.hideKeyboard() {
		view?.let { activity?.hideKeyboard(it) }
	}

	fun Activity.showKeyboard() {
		showKeyboard(currentFocus ?: View(this))
	}

	private fun Context.showKeyboard(view: View) {
		val inputMethodManager =
				getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
		inputMethodManager.showSoftInput(view, InputMethodManager.HIDE_IMPLICIT_ONLY)
	}

	fun Fragment.showKeyboard() {
		view?.let { activity?.showKeyboard(it) }
	}

	companion object {
		const val ACT_SET_CLUES = 0
		const val ACT_HUNT = 1
		const val ACT_BARCODE = 2
		const val CAMERA_PERMISSION_REQUEST_CODE = 99
	}

}