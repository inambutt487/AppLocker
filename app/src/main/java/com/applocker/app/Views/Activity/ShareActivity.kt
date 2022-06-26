package com.applocker.app.Views.Activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.applocker.app.BuildConfig
import com.applocker.app.R
import com.applocker.app.Utils.AppLocker
import com.applocker.app.Utils.BaseActivity
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetFontScale(resources.configuration, this)
        AppLocker.getInstance().doForCreate(this)
        makeFullScreen()
        setStatusBar(this@ShareActivity, R.color.colorPrimary, R.color.white)
        setContentView(R.layout.activity_share)

        back.setOnClickListener {
           onBackPressed()
        }


        shareImg.setOnClickListener {

            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            
                            
                            """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
            }

        }

        gmailImg.setOnClickListener {

            try {

                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            
                            
                            """.trimIndent()
                shareSkype(this@ShareActivity, shareMessage);
            } catch (e: Exception) {
            }


        }

        instaImg.setOnClickListener {

            try {

                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            
                            
                            """.trimIndent()

                val emailIntent = Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "", null
                    )
                )
                emailIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(emailIntent, "Send Email..."))


            } catch (e: Exception) {
            }

        }

    }

    fun shareSkype(context: Context, message: String) {
        val intent = context.packageManager.getLaunchIntentForPackage("com.skype.raider")
        intent!!.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.type = "text/plain"
        context.startActivity(intent)
    }
}