package com.applocker.app.Views.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.applocker.app.R
import com.applocker.app.Utils.AppLocker
import com.applocker.app.Utils.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.WindowManager





class SplashActivity : BaseActivity() {

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private var animation: Animation? = null
    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLocker.getInstance().doForCreate(this)
        FirebaseToken(this@SplashActivity)
        getNotification()
        resetFontScale(resources.configuration, this)
        makeFullScreen()
        setStatusBar(this, R.color.colorPrimary, R.color.white)
        setContentView(R.layout.activity_splash)

        context = this@SplashActivity
        handler = Handler()
        animation = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.bottom_to_original
        )
        splash.setAnimation(animation)
    }

    private fun getNotification() {
        if (intent.extras != null) {
            var Title: String?
            var Message: String?
            var Icon: String?
            var Action: String?
            var Link: String?
            var Rate: String?
            var Image: String?
            Image = null
            Rate = Image
            Link = Rate
            Action = Link
            Icon = Action
            Message = Icon
            Title = Message
            try {
                if (intent.extras!!["Action"] != null && intent.extras!!["Link"] != null) {
                    if (intent.extras!!["Title"] != null) {
                        Title = intent.extras!!["Title"].toString()
                        if (Title == null || Title.length == 0) {
                            Title = null
                        }
                    }
                    if (intent.extras!!["Message"] != null) {
                        Message = intent.extras!!["Message"].toString()
                        if (Message == null || Message.length == 0) {
                            Message = null
                        }
                    }
                    if (intent.extras!!["Icon"] != null) {
                        Icon = intent.extras!!["Icon"].toString()
                        if (Icon == null || Icon.length == 0) {
                            Icon = null
                        }
                    }
                    if (intent.extras!!["Action"] != null) {
                        Action = intent.extras!!["Action"].toString()
                        if (Action == null || Action.length == 0) {
                            Action = null
                        }
                    }
                    if (intent.extras!!["Link"] != null) {
                        Link = intent.extras!!["Link"].toString()
                        if (Link == null || Link.length == 0) {
                            Link = null
                        }
                    }
                    if (intent.extras!!["Rate"] != null) {
                        Rate = intent.extras!!["Rate"].toString()
                        if (Rate == null || Rate.length == 0) {
                            Rate = null
                        }
                    }
                    if (intent.extras!!["Image"] != null) {
                        Image = intent.extras!!["Image"].toString()
                        if (Image == null || Image.length == 0) {
                            Image = null
                        }
                    }
                    Log.d("SplashActivity", Title + Message + Icon + Action + Link + Rate)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val bundle = Bundle()
                    bundle.putString(getString(R.string.messagetitle), Title)
                    bundle.putString(getString(R.string.messageBody), Message)
                    bundle.putString(getString(R.string.messageIcon), Icon)
                    bundle.putString(getString(R.string.messageAction), Action)
                    bundle.putString(getString(R.string.messageLink), Link)
                    bundle.putString(getString(R.string.messageRate), Rate)
                    bundle.putString(getString(R.string.messageImage), Image)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        runnable = Runnable {
            startActivity(Intent(this@SplashActivity, LoaderActivity::class.java))
            finish()
        }
        handler!!.postDelayed(runnable!!, 1500)
    }

    override fun onPause() {
        handler!!.removeCallbacks(runnable!!) //stop handler when activity not visible
        super.onPause()
    }
}