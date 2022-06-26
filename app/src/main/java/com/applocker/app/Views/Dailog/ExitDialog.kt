package com.applocker.app.Views.Dailog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import com.applocker.app.R
import kotlinx.android.synthetic.main.exit_dialog.*

class ExitDialog(internal var _activity: Activity) : Dialog(_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exit_dialog)


        exitBtn.setOnClickListener {
            dismiss()
        }

        noBtn.setOnClickListener {
            dismiss()
            _activity?.finishAffinity()
        }
    }
}