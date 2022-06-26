package com.applocker.app.Views.Activity

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.applocker.app.R
import com.applocker.app.Utils.AppLocker
import com.applocker.app.Utils.BaseActivity
import kotlinx.android.synthetic.main.activity_premium_dailog.*

class PremiumActivityDailog : BaseActivity() {

    private var close: ImageView? = null

    private var adHandler: Handler? = null
    private var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetFontScale(resources.configuration, this)
        AppLocker.getInstance().doForCreate(this)
        makeFullScreen()
        setStatusBar(this@PremiumActivityDailog, R.color.colorPrimary, R.color.white)
        setContentView(R.layout.activity_premium_dailog)

        initializeBilling(this@PremiumActivityDailog)

        close = findViewById(R.id.close)
        close!!.setVisibility(View.GONE)

        go_premium.setOnClickListener {
            val skuDetails = consoleProductDeatils
            if (skuDetails != null) {
                tinyDB.putBoolean("isPurchased", true)
                lunchPruchaseFlow(this@PremiumActivityDailog)
            } else {
                Toast.makeText(
                    this@PremiumActivityDailog,
                    "No package Available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    override fun onStart() {
        super.onStart()
        if (billingClient.connectionState != 2) {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                        // The BillingClient is ready. You can query purchases here.
                        showProducts(this@PremiumActivityDailog)
                    }
                }

                override fun onBillingServiceDisconnected() {

                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    BillingConnection(this@PremiumActivityDailog)
                }
            })
        }
    }

    override fun onResume() {

        adHandler = Handler()
        runnable = Runnable {
            close!!.visibility = View.VISIBLE
            close!!.setOnClickListener {
                startActivity(MainActivity::class.java, null)
            }
        }
        adHandler!!.postDelayed(runnable!!, 3000)
        super.onResume()
    }

    override fun onPause() {
        adHandler!!.removeCallbacks(runnable!!) //stop handler when activity not visible
        super.onPause()
    }

    override fun onStop() {
        adHandler!!.removeCallbacks(runnable!!) //stop handler when activity not visible
        super.onStop()
    }

}