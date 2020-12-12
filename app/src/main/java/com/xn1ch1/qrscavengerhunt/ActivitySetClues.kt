package com.xn1ch1.qrscavengerhunt

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ActivitySetClues : BaseActivity() {

	private lateinit var clueTable: LinearLayout

	private var clues = HashMap<Int, Clue>(10)

	private lateinit var cluePrefs: SharedPreferences

	private var clueSet = 0

	private lateinit var clueInput: EditText
	private lateinit var clueInputDone: TextView
	private lateinit var clueInputClear: TextView
	private lateinit var clueInputCancel: TextView
	private lateinit var clueInputOverlay: ConstraintLayout

	@SuppressLint("InflateParams")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_set_clues)

		cluePrefs = this.getSharedPreferences(Prefs.PREFS_FILENAME, 0)

		clueTable = findViewById(R.id.clueTable)
		clueInput = findViewById(R.id.setCluesTextInput)
		clueInputClear = findViewById(R.id.setCluesInputClear)
		clueInputCancel = findViewById(R.id.setCluesInputCancel)
		clueInputDone = findViewById(R.id.setCluesInputDone)
		clueInputOverlay = findViewById(R.id.clueInputOverlay)

		clueInputClear.setOnClickListener {
			clueInput.setText("")
			clueInput.requestFocus()
		}
		clueInputCancel.setOnClickListener {
			hideClueInput()
		}
		clueInputDone.setOnClickListener {
			with (clues[clueSet]!!) {
				clueText = clueInput.text.toString()
				hideClueInput()
				setClueCheck()
				setClueText()
			}
		}

		for (i in 0..9) {

			clues[i] = Clue()

			with (clues[i]!!) {

				number = i

				row = LayoutInflater.from(applicationContext).inflate(
						R.layout.set_clue_row,
						null
				) as ConstraintLayout

				rowMessage = row.findViewById(R.id.text)
				rowNumber = row.findViewById(R.id.number)
				rowClueButton = row.findViewById(R.id.textCheck)
				rowScanButton = row.findViewById(R.id.codeCheck)

				with (rowScanButton) {
					setOnClickListener {
						clueSet = i
						if (code.isNullOrEmpty()) {
							setCluesScanBarcode()
						} else {
							replaceCode()
						}
					}
				}

				with (rowClueButton) {
					setOnClickListener {
						clueSet = i
						clueInput.setText(clueText)
						showClueInput()
					}
				}

				rowNumber.text = (number + 1).toString()
				rowMessage.text = clueText
				clueTable.addView(row)

				setCodeCheck()
				setClueCheck()
				setClueText()

			}
		}

		findViewById<ImageView>(R.id.setCluesBackButton).setOnClickListener {
			onBackPressed()
		}
		findViewById<ImageView>(R.id.setCluesResetButton).setOnClickListener {
			resetData()
		}
		findViewById<ImageView>(R.id.setCluesFinishButton).setOnClickListener {
			startHunt()
		}

		hideClueInput()

	}

	override fun onBackPressed() {

		if (clueInputOverlay.visibility == View.VISIBLE) {
			hideClueInput()
		} else {
			super.onBackPressed()
			setResult(RESULT_OK, intent)
			finish()
		}

	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

		if (resultCode == RESULT_OK) {
			if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
				if (ContextCompat.checkSelfPermission(
								this,
								Manifest.permission.CAMERA
						) == PackageManager.PERMISSION_GRANTED
				) {
					openCameraWithScanner()
				}
			} else if (requestCode == ACT_BARCODE) {
				val code = data?.getStringExtra("barcode").toString()

				if (!code.isBlank() && codeExists(code)) {
					val builder = AlertDialog.Builder(this)
					with(builder) {
						setMessage(resources.getString(R.string.alreadyAssigned, code))
						setNeutralButton(resources.getString(R.string.ok), null)
						show()
					}
				} else {
					clues[clueSet]?.code = code
					clues[clueSet]?.setCodeCheck()
				}
			}
		}

	}

	private fun replaceCode() {

		val builder = AlertDialog.Builder(this)
		with (builder) {
			setMessage(getString(R.string.removeCode, clues[clueSet]?.code))
			setNegativeButton(getString(R.string.no), null)
			setPositiveButton(getString(R.string.yes)) { _, _ ->
				clues[clueSet]?.code = ""
				clues[clueSet]?.setClueCheck()
				setCluesScanBarcode()
			}
			show()
		}
	}

	private fun setCluesScanBarcode() {

		if (ContextCompat.checkSelfPermission(
						this,
						Manifest.permission.CAMERA
				) == PackageManager.PERMISSION_GRANTED
		) {
			openCameraWithScanner()
		} else {
			ActivityCompat.requestPermissions(
					this,
					arrayOf(Manifest.permission.CAMERA),
					CAMERA_PERMISSION_REQUEST_CODE
			)
		}

	}

	private fun showClueInput() {

		clueInputOverlay.visibility = View.VISIBLE
		clueInput.requestFocus()

		showKeyboard()

	}

	private fun hideClueInput() {

		hideKeyboard()

		clueInputOverlay.visibility = View.INVISIBLE
		clueInput.clearFocus()
		clueInput.setText("")


	}

	override fun onRequestPermissionsResult(
			requestCode: Int,
			permissions: Array<out String>,
			grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)

		if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				openCameraWithScanner()
			} else if (!ActivityCompat.shouldShowRequestPermissionRationale(
							this,
							Manifest.permission.CAMERA
					)
			) {
				val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
				val uri: Uri = Uri.fromParts("package", packageName, null)
				intent.data = uri
				startActivityForResult(intent, CAMERA_PERMISSION_REQUEST_CODE)
			}
		}

	}

	private fun openCameraWithScanner() {

		val intent = Intent(this, ActivityScanCode::class.java)
		startActivityForResult(intent, ACT_BARCODE)

	}

	private fun codeExists(code: String): Boolean {

		var exists = false

		clues.forEach { (_, clue) ->
			if (clue.code == code) {
				exists = true
			}
		}

		return exists

	}

	private fun startHunt() {

		var last = false
		var i = 0

		clues.forEach { (_, clue) ->

			if ((clue.code == "" && clue.clueText != "") || (clue.code != "" && clue.clueText == "")) {
            	val builder = AlertDialog.Builder(this)
				with (builder) {
					setMessage(getString(R.string.clueError, (clue.number + 1).toString()))
					setNeutralButton(getString(R.string.ok), null)
					show()
				}
				return
			} else if (clue.code == "" && clue.clueText == "") {
				last = true
			} else if (last && (clue.code != "" || clue.clueText != "")) {
				val builder = AlertDialog.Builder(this)
				with (builder) {
					setMessage(getString(R.string.clueError, (clue.number + 1).toString()))
					setNeutralButton(getString(R.string.ok), null)
					show()
				}
				return
			} else {
				i++
			}

		}

		prefs.clueCount = i
		prefs.huntStarted = true
		prefs.currentClue = 0
		onBackPressed()

	}

	private fun resetData() {
		val dialogClickListener = DialogInterface.OnClickListener { _, which ->
			when (which) {
				DialogInterface.BUTTON_NEGATIVE -> return@OnClickListener
				DialogInterface.BUTTON_POSITIVE -> {

					clues.forEach { (_, clue) ->
						clue.code = ""
						clue.clueText = ""
						clue.setClueText()
						clue.setClueCheck()
						clue.setCodeCheck()
					}

				}
			}
		}
		val builder = AlertDialog.Builder(this)
		with (builder) {
			setMessage(resources.getString(R.string.resetCluesQuestion))
			setPositiveButton(resources.getString(R.string.yes), dialogClickListener)
			setNegativeButton(resources.getString(R.string.no), dialogClickListener)
			show()
		}
	}

	inner class Clue {

		var number: Int = 0

		lateinit var row: ConstraintLayout
		lateinit var rowMessage: TextView
		lateinit var rowNumber: TextView
		lateinit var rowClueButton: ImageView
		lateinit var rowScanButton: ImageView

		var clueText: String?
			get() = cluePrefs.getString("clueText$number", "")
			set(value) {
				cluePrefs.edit().putString("clueText$number", value).apply()
			}

		var code: String?
			get() = cluePrefs.getString("code$number", "")
			set(value) {
				cluePrefs.edit().putString("code$number", value).apply()
			}

		fun setClueText() {
			if (clueText.isNullOrBlank()) {
				rowMessage.text = getString(R.string.clueNotSet)
				rowMessage.alpha = 0.5f
			} else {
				rowMessage.text = clueText
				rowMessage.alpha = 1f
			}
		}

		fun setClueCheck() {
			if (clueText.isNullOrBlank()) {
				rowClueButton.setImageResource(R.drawable.set_clue_clue_text_unchecked)
			} else {
				rowClueButton.setImageResource(R.drawable.set_clue_clue_text_checked)
			}
		}

		fun setCodeCheck() {
			if (code.isNullOrBlank()) {
				rowScanButton.setImageResource(R.drawable.set_clue_scan_unchecked)
			} else {
				rowScanButton.setImageResource(R.drawable.set_clue_scan_checked)
			}
		}


	}

}