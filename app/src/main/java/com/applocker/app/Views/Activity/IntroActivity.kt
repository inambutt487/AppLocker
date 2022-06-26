package com.applocker.app.Views.Activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.applocker.app.Adapter.AdapterConfig
import com.applocker.app.R
import com.applocker.app.Utils.AppLocker
import com.applocker.app.Utils.BaseActivity

class IntroActivity : BaseActivity() {

    private var slides: IntArray? = null
    private var btnNext: Button? = null

    private var viewPager: ViewPager? = null
    private var pagerAdapter: AdapterConfig? = null
    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLocker.getInstance().doForCreate(this)
        resetFontScale(resources.configuration, this)
        makeFullScreen()
        setStatusBar(this, R.color.white, R.color.colorPrimary)
        setContentView(R.layout.activity_con_intro)


        context = this@IntroActivity


        initViews()
        setUpViewPager()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.acIntroSlide_viewPager) as ViewPager
        btnNext = findViewById(R.id.nextBtn) as Button
        viewPager!!.setOnPageChangeListener(viewPagerPageChangeListener)
        btnNext!!.setOnClickListener(onButtonsClicked)
    }


    private fun setUpViewPager() {
        slides = intArrayOf(
            R.layout.layout_intro,
            R.layout.layout_intro,
            R.layout.layout_intro,
            R.layout.layout_intro,
            R.layout.layout_intro,
            R.layout.layout_intro
        )
        pagerAdapter = AdapterConfig(this, slides)
        viewPager!!.adapter = pagerAdapter
    }

    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == slides!!.size - 1) {
                // last page. make button text to GOT IT
                btnNext!!.text = getString(R.string.Continue)
            } else {
                // still pages are left
                btnNext!!.text = getString(R.string.next)
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
        }
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    var onButtonsClicked =
        View.OnClickListener { view ->
            when (view.id) {
                R.id.nextBtn -> {
                    val current: Int = getItem(+1)
                    if (current < slides!!.size) {
                        viewPager!!.currentItem = current
                    } else {

                    }
                }
            }
        }

    private fun getItem(i: Int): Int {
        return viewPager!!.currentItem + i
    }
}