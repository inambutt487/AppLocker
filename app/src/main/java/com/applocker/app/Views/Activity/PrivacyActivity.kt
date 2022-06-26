package com.applocker.app.Views.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.applocker.app.R
import kotlinx.android.synthetic.main.activity_privacy.*

class PrivacyActivity : AppCompatActivity() {

    var html="<div class=\"CjVfdc\"><span class=\" Ztu2ge\"><h1>\n" +
            "  Privacy Policy\n" +
            "  </h1></span></div>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">PDF Master built the Document Scanner app as a Free app. This SERVICE is provided by PDF Master at no cost and is intended for use as is.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">This page is used to inform visitors regarding my policies with the collection, use, and disclosure of Personal Information if anyone decided to use my Service.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">If you choose to use my Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that I collect is used for providing and improving the Service. I will not use or share your information with anyone except as described in this Privacy Policy.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which is accessible at Document Scanner unless otherwise defined in this Privacy Policy.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Information Collection and Use</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">For a better experience, while using our Service, I may require you to provide us with certain personally identifiable information, including but not limited to Camera, Storage. The information that I request will be retained on your device and is not collected by me in any way.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">The app does use third party services that may collect information used to identify you.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">Link to privacy policy of third party service providers used by the app</p>\n" +
            "<ul class=\"n8H08c UVNKR\">\n" +
            "<li class=\"TYR86d zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://www.google.com/policies/privacy/\" target=\"_blank\" rel=\"noopener\">Google Play Services</a></p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://support.google.com/admob/answer/6128543?hl=en\" target=\"_blank\" rel=\"noopener\">AdMob</a></p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://firebase.google.com/policies/analytics\" target=\"_blank\" rel=\"noopener\">Google Analytics for Firebase</a></p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://firebase.google.com/support/privacy/\" target=\"_blank\" rel=\"noopener\">Firebase Crashlytics</a></p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://www.google.com/url?q=https%3A%2F%2Fwww.facebook.com%2Fabout%2Fprivacy%2Fupdate%2Fprintable&amp;sa=D&amp;sntz=1&amp;usg=AFQjCNFLa1x0FKy8-ldlfA0ltwmF2rYThw\" target=\"_blank\" rel=\"noopener\">Facebook</a></p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><a class=\"XqQF9c rXJpyf\" href=\"https://policies.google.com/privacy\" target=\"_blank\" rel=\"noopener\">Fabric</a></p>\n" +
            "</li>\n" +
            "</ul>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Log Data</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">I want to inform you that whenever you use my Service, in a case of an error in the app I collect data and information (through third party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (&ldquo;IP&rdquo;) address, device name, operating system version, the configuration of the app when utilizing my Service, the time and date of your use of the Service, and other statistics.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Cookies</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">Cookies are files with a small amount of data that are commonly used as anonymous unique identifiers. These are sent to your browser from the websites that you visit and are stored on your device's internal memory.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">This Service does not use these &ldquo;cookies&rdquo; explicitly. However, the app may use third party code and libraries that use &ldquo;cookies&rdquo; to collect information and improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Service Providers</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">I may employ third-party companies and individuals due to the following reasons:</p>\n" +
            "<ul class=\"n8H08c UVNKR\">\n" +
            "<li class=\"TYR86d zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">To facilitate our Service;</p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">To provide the Service on our behalf;</p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">To perform Service-related services; or</p>\n" +
            "</li>\n" +
            "<li class=\"TYR86d zfr3Q\" dir=\"ltr\">\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">To assist us in analyzing how our Service is used.</p>\n" +
            "</li>\n" +
            "</ul>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">I want to inform users of this Service that these third parties have access to your Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Security</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">I value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and I cannot guarantee its absolute security.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Links to Other Sites</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by me. Therefore, I strongly advise you to review the Privacy Policy of these websites. I have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Children&rsquo;s Privacy</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">These Services do not address anyone under the age of 13. I do not knowingly collect personally identifiable information from children under 13. In the case I discover that a child under 13 has provided me with personal information, I immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact me so that I will be able to do necessary actions.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Changes to This Privacy Policy</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">I may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. I will notify you of any changes by posting the new Privacy Policy on this page.</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">This policy is effective as of 2020-12-21</p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\"><strong>Contact Us</strong></p>\n" +
            "<p class=\"CDt4Ke zfr3Q\" dir=\"ltr\">If you have any questions or suggestions about my Privacy Policy, do not hesitate to contact me at inteliwere@gmail.com.</p>"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

        webview.loadData(html, "text/html; charset=utf-8", "UTF-8")
    }
}