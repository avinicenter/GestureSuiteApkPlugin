package com.gesturesuite.apkplugin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.provider.Settings

class Utils {

    companion object{

        fun requestOverlayDrawPermission(act: Activity, requestCode: Int) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + act.packageName))
                act.startActivityForResult(intent, requestCode)
            } catch (e: Exception) {
                AlertDialog.Builder(act).apply {
                    setMessage("Unable to open the draw overlay settings screen. You will have to do it manually. Locate Gesture Suite Apk Plugin in that settings screen and enable it.")
                    setPositiveButton("Ok"){ dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }
        }

        @SuppressLint("NewApi") //NON-NLS
        fun canStartActivitiesFromBackground(con: Context): Boolean {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                return true
            }
            val result: Boolean = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                try {
                    Settings.canDrawOverlays(con)
                } catch (e: NoSuchMethodError) {
                    canAccessFeatureUsingReflection(con, 24)
                }
            } else {
                true
            }
            return result
        }

        private fun canAccessFeatureUsingReflection(context: Context, featureInt: Int): Boolean {
            return try {
                val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val clazz: Class<*> = AppOpsManager::class.java
                val dispatchMethod = clazz.getMethod("checkOp", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java)
                val mode = dispatchMethod.invoke(
                    manager,
                    featureInt, Binder.getCallingUid(), context.applicationContext.packageName
                ) as Int
                AppOpsManager.MODE_ALLOWED == mode
            } catch (e: Exception) {
                false
            }
        }


        fun askAllFilesAccess(act: Context) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:" + act.applicationContext.packageName)
                act.startActivity(intent)
            } catch (e: Exception) {
                val text = "Failed to open special Manifest.permission screen. Click \"OK\" and you will be redirected to Gesture Suite app details screen. Then go to Permissions click on \"Files and media\" permission and check \"Allow management of all files\""
                AlertDialog.Builder(act).setMessage(text).setPositiveButton("OK"){ dialog, _ ->
                    dialog.dismiss()
                    openAndroidPermissionSettings(act)
                }.show()
            }
        }

        fun canAccessAllFiles(): Boolean {
            return if (VERSION.SDK_INT >= VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                true
            }
        }

        fun openAndroidPermissionSettings(act: Context) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", act.packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            act.startActivity(intent)
        }

    }

}