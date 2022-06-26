package com.applocker.app.Utils

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.applocker.app.BuildConfig
import com.applocker.app.Views.Dailog.PermissionActivity


open class Utils {

    companion object {

        @JvmStatic
        fun show_Messgae(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        @JvmStatic
        fun haveOverlayPermission(context: Context): Boolean? {
            return Settings.canDrawOverlays(context)
        }

        @JvmStatic
        fun askForSystemOverlayPermission(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                    context
                )
            ) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName())
                )
                context.startActivity(intent)
                var bundle: Bundle?= null
                bundle = Bundle()
                bundle!!.putString("Title", "Overlay Permission")
                bundle!!.putString("Message", "Enable Overlay Permission")
                context.startActivity(
                    Intent(
                        context,
                        PermissionActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtras(bundle)
                )
            }
        }

        @JvmStatic
        fun haveAppUsage(context: Context): Boolean {
            return try {
                val packageManager: PackageManager = context.getPackageManager()
                val applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0)
                val appOpsManager =
                    context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                var mode = 0
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    mode = appOpsManager.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName
                    )
                }
                mode == AppOpsManager.MODE_ALLOWED
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

        @JvmStatic
        fun askForAppUsagePermission(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !haveAppUsage(context)) {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                context.startActivity(intent)

                var bundle: Bundle?= null
                bundle = Bundle()
                bundle.putString("Title", "Usage Permission")
                bundle.putString("Message", "Enable Usage Permission")
                context.startActivity(
                    Intent(
                        context,
                        PermissionActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtras(bundle)
                )
            }
        }

        @JvmStatic
        fun hasStoragePermissionGranted(context: Activity?): Boolean {
            val result1 =
                ContextCompat.checkSelfPermission(context!!, permission.READ_EXTERNAL_STORAGE)
            val result2 =
                ContextCompat.checkSelfPermission(context, permission.WRITE_EXTERNAL_STORAGE)
            return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
        }

        @JvmStatic
        fun requestStoragePermission(context: Activity?) {
            //below android 11
            ActivityCompat.requestPermissions(
                context!!,
                arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE),
                BaseActivity.permission_STORAGE
            )
        }

        @JvmStatic
        fun hasCameraPermissionGranted(context: Activity?): Boolean {
            val result1 = ContextCompat.checkSelfPermission(context!!, permission.CAMERA)
            return result1 == PackageManager.PERMISSION_GRANTED
        }

        @JvmStatic
        fun requestCameraPermission(context: Activity?) {
            //below android 11
            ActivityCompat.requestPermissions(
                context!!,
                arrayOf(permission.CAMERA),
                BaseActivity.permission_CAMERA
            )
        }


        @JvmStatic
        fun requestSpyPermission(context: Activity?) {
            //below android 11
            ActivityCompat.requestPermissions(
                context!!,
                arrayOf(
                    permission.CAMERA,
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE
                ),
                BaseActivity.permission_CAMERA
            )
        }

        @JvmStatic
        fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        @JvmStatic
        fun ignoreBattery(context: Context) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)) {
                    @SuppressLint("BatteryLife") val intent =
                        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    intent.data = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    context.startActivity(intent)
                }
            }
        }


        @JvmStatic
        fun isIgnoringBatteryOptimizations(context: Context): Boolean {
            val pwrm =
                context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
            val name = context.applicationContext.packageName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return pwrm.isIgnoringBatteryOptimizations(name)
            }
            return true
        }


    }
}