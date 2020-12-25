package com.aurora.authenticator

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import com.aurora.authenticator.databinding.ActivityResultBinding
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi

class ResultActivity : Activity() {

    private lateinit var B: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        B = ActivityResultBinding.inflate(layoutInflater)
        setContentView(B.root)
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val email = intent.getStringExtra(MainActivity.AUTH_EMAIL)
            val token = intent.getStringExtra(MainActivity.AUTH_TOKEN)
            retrieveAc2dmToken(email, token)
        }
    }

    private fun retrieveAc2dmToken(Email: String?, oAuthToken: String?) {
        task {
            AC2DMTask().getAC2DMResponse(Email, oAuthToken)
        } successUi {
            B.status.text = ("SUCCESS")
            B.email.text = ("Email : " + it["Email"])
            B.token.text = ("Token : " + it["Token"])
            B.auth.text = ("Auth : " + it["Auth"])
            B.name.text = ("Name : " + it["firstName"])
        } failUi {
            B.status.text = ("FAILED")
            B.status.setTextColor(Color.RED)
            Toast.makeText(this, "Failed to generate AC2DM Auth Token", Toast.LENGTH_LONG).show()
        }
    }
}