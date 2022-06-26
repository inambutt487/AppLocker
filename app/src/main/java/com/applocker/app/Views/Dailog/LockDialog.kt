package com.applocker.app.Views.Dailog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import com.applocker.app.Model.CommLockInfo
import com.applocker.app.R
import kotlinx.android.synthetic.main.lock_dialog.*


class LockDialog(internal var _activity: Activity, app: String?, lock: LockedType, app_package: String) :
    Dialog(_activity) {

    var app: String? = app
    val lock : LockedType = lock;
    var app_package: String= app_package

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lock_dialog)

        text1!!.setText("Please Select Lock for $app")

        patternLock.setOnClickListener {
            lock.lock(2, app_package)
            dismiss()
        }

        pinLock.setOnClickListener {
            lock.lock(1, app_package)
            dismiss()
        }

    }

    public interface LockedType{
        fun lock(type: Int, app_package: String)
    }

}