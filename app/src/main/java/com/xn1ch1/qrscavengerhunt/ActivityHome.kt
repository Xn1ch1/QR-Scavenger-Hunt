package com.xn1ch1.qrscavengerhunt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView

class ActivityHome : BaseActivity() {

    private lateinit var hunt: ImageView
    private lateinit var clues: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        hunt = findViewById(R.id.hunt)
        clues = findViewById(R.id.clues)

        clues.setOnClickListener {
            setClues()
        }
        hunt.setOnClickListener {
            goHunt()
        }

        setButtons()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        setButtons()

    }

    private fun setButtons() {
        if (prefs.huntStarted) {
            clues.alpha = 0.2f
            hunt.alpha = 1f
        } else {
            clues.alpha = 1f
            hunt.alpha = 0.2f
        }
    }

    private fun setClues() {
        if (prefs.huntStarted) {
            val builder = AlertDialog.Builder(this)
            with (builder) {
                setMessage(resources.getString(R.string.huntAlreadyStarted))
                setNeutralButton(resources.getString(R.string.ok), null)
                show()
            }
        } else {
            val intent = Intent(this, ActivitySetClues::class.java)
            startActivityForResult(intent, ACT_SET_CLUES)
        }
    }

    private fun goHunt() {
        if (!prefs.huntStarted) {
            val builder = AlertDialog.Builder(this)
            with (builder) {
                setMessage(resources.getString(R.string.huntNotStarted))
                setNeutralButton(resources.getString(R.string.ok), null)
                show()
            }
        } else {
            val intent = Intent(this, ActivityHunt::class.java)
            startActivityForResult(intent, ACT_HUNT)
        }
    }
}