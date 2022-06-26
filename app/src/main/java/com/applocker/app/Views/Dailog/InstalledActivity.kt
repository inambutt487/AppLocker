package com.applocker.app.Views.Dailog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import com.applocker.app.Database.CommLockInfoManager
import com.applocker.app.R
import com.applocker.app.Utils.AppInfoExtractor
import kotlinx.android.synthetic.main.dialog_installed.*
import android.content.Intent
import android.content.pm.PackageManager
import android.content.ActivityNotFoundException


class InstalledActivity : Activity() {

    var mLockInfoManager: CommLockInfoManager? = null
    val appInfoExtractor = AppInfoExtractor(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mLockInfoManager = CommLockInfoManager(this@InstalledActivity)

        setContentView(R.layout.dialog_installed)
        setFinishOnTouchOutside(false)
        val bundle = intent.extras
        val appName = bundle!!.getString("appName")
        val packageName = bundle.getString("packageName")

        app_icon.setImageDrawable(appInfoExtractor.getAppIconByPackageName(packageName))

        text1.text = appName
        text2.text = "Want to Lock $appName"
        publishBtn.setOnClickListener(View.OnClickListener {

            mLockInfoManager!!.lockCommApplication(packageName)
            finish()
        })

        openBtn.setOnClickListener(View.OnClickListener {
            openApp(this, packageName)
            finish()
        })
    }

    fun openApp(context: Context, packageName: String?): Boolean {
        val manager = context.packageManager
        return try {
            val i = manager.getLaunchIntentForPackage(packageName!!)
                ?: return false
            //throw new ActivityNotFoundException();
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            context.startActivity(i)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

}