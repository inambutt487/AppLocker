package com.applocker.app.Views.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.applocker.app.R
import com.applocker.app.Utils.AppLocker
import com.applocker.app.Utils.BaseActivity
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.activity_feedback.back
import kotlinx.android.synthetic.main.activity_share.*

class FeedbackActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetFontScale(resources.configuration, this)
        AppLocker.getInstance().doForCreate(this)
        makeFullScreen()
        setStatusBar(this@FeedbackActivity, R.color.colorPrimary, R.color.white)
        setContentView(R.layout.activity_feedback)

        back.setOnClickListener {
            onBackPressed()
        }

        if (descEd.text.toString().isEmpty()) {
            Toast.makeText(this@FeedbackActivity, "Please Write your Feedback.", Toast.LENGTH_SHORT).show()
        } else {


            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "*/*"
            intent.setPackage("com.google.android.gm")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("inteliwere@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, titleEd.text.toString())
            intent.putExtra(Intent.EXTRA_TEXT, descEd.text.toString())
            startActivity(Intent.createChooser(intent, "Share PDF File"))

            onBackPressed()

        }


    }

}