package com.xn1ch1.qrscavengerhunt

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ActivityHunt : BaseActivity() {

    private lateinit var scan: ImageView
    private lateinit var back: ImageView
    private lateinit var next: ImageView

    private lateinit var title: TextView
    private lateinit var clueText: TextView
    private lateinit var clueError: TextView

    private lateinit var errorSound: MediaPlayer
    private lateinit var solvedSound: MediaPlayer
    private lateinit var finishedSound: MediaPlayer

    private lateinit var cluePrefs: SharedPreferences

    private var clue: Clue = Clue()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hunt)

        cluePrefs = this.getSharedPreferences(Prefs.PREFS_FILENAME, 0)

        clue.number = prefs.currentClue

        errorSound = MediaPlayer.create(this, R.raw.hunt_error)
        solvedSound = MediaPlayer.create(this, R.raw.hunt_solved)
        finishedSound = MediaPlayer.create(this, R.raw.hunt_finish)

        scan = findViewById(R.id.huntClueScan)
        back = findViewById(R.id.huntClueBack)
        next = findViewById(R.id.huntSolvedNext)

        clueText = findViewById(R.id.huntClueText)
        title = findViewById(R.id.huntClueTitle)
        clueError = findViewById(R.id.huntClueError)

        clueText.text = clue.clueText
        title.text = getString(R.string.clueNumber, prefs.currentClue + 1)

        scan.setOnClickListener {
            checkCameraPerms()
        }
        back.setOnClickListener {
            finish()
        }
        next.setOnClickListener {
            nextClue()
        }

        hideErrorLayout()
        hideFinishedLayout()
        hideSolvedLayout()
        showClueLayout()

//        prefs.huntStarted = false
//        prefs.currentClue = 0

        if (prefs.clueCount == prefs.currentClue) {
            prefs.huntStarted = false
            prefs.currentClue = 0
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

                hideClueLayout()

                val code = data?.getStringExtra("barcode").toString()

                if (code == clue.code) {

                    if (prefs.clueCount == prefs.currentClue + 1) {

                        prefs.huntStarted = false
                        prefs.currentClue = 0

                        showFinishedLayout()
                        finishedSound.start()

                    } else {

                        prefs.currentClue = prefs.currentClue + 1

                        showSolvedLayout()
                        solvedSound.start()

                    }

                } else {

                    showErrorLayout()
                    errorSound.start()

                }

            }
        }

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

    private fun checkCameraPerms() {

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

    private fun nextClue() {
        recreate()
    }


    private fun showClueLayout() {
        title.visibility = View.VISIBLE
        clueText.visibility = View.VISIBLE
        scan.visibility = View.VISIBLE
        back.visibility = View.VISIBLE
    }

    private fun hideClueLayout() {
        title.visibility = View.INVISIBLE
        clueText.visibility = View.INVISIBLE
        scan.visibility = View.INVISIBLE
        back.visibility = View.INVISIBLE
    }

    private fun showErrorLayout() {
        clueError.visibility = View.VISIBLE
        back.visibility = View.VISIBLE
    }

    private fun hideErrorLayout() {
        clueError.visibility = View.INVISIBLE
        back.visibility = View.INVISIBLE
    }

    private fun showSolvedLayout() {
        findViewById<View>(R.id.huntSolvedTitle).visibility = View.VISIBLE
        findViewById<View>(R.id.huntSolvedImage).visibility = View.VISIBLE
        back.visibility = View.VISIBLE
        next.visibility = View.VISIBLE
    }

    private fun hideSolvedLayout() {
        findViewById<View>(R.id.huntSolvedTitle).visibility = View.INVISIBLE
        findViewById<View>(R.id.huntSolvedImage).visibility = View.INVISIBLE
        back.visibility = View.INVISIBLE
        next.visibility = View.INVISIBLE
    }

    private fun showFinishedLayout() {
        findViewById<View>(R.id.huntFinishedImage).visibility = View.VISIBLE
        findViewById<View>(R.id.huntFinishedTitle).visibility = View.VISIBLE
        findViewById<View>(R.id.huntFinishedMessage).visibility = View.VISIBLE
        findViewById<ConstraintLayout>(R.id.huntLayout).setOnClickListener {
            finish()
        }
    }

    private fun hideFinishedLayout() {
        findViewById<View>(R.id.huntFinishedImage).visibility = View.INVISIBLE
        findViewById<View>(R.id.huntFinishedTitle).visibility = View.INVISIBLE
        findViewById<View>(R.id.huntFinishedMessage).visibility = View.INVISIBLE
    }

    inner class Clue {

        var number: Int = 0

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

    }

}