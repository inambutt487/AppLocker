package com.applocker.app.Views.Dailog

import android.app.Activity
import android.os.Bundle
import android.widget.CompoundButton
import com.applocker.app.Config.AppConfig
import com.applocker.app.R
import com.applocker.app.Utils.Utils.Companion.hasCameraPermissionGranted
import com.applocker.app.Utils.Utils.Companion.hasStoragePermissionGranted
import com.applocker.app.Utils.Utils.Companion.haveOverlayPermission
import kotlinx.android.synthetic.main.dialog_permission.*

class PermissionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_permission)
        setFinishOnTouchOutside(false)

        val bundle = intent.extras
        val Title = bundle!!.getString("Title")
        val Message = bundle.getString("Message")

        chek_permission_checkbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                finish()
            }

        })

        desc.text = "$Message"
    }
}