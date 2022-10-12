package com.gesturesuite.apkplugin

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

open class BaseActivity: AppCompatActivity() {

    fun checkPermissions() {
        if(!hasStoragePermission()){
            requestPermission()
        }
        else if(!Utils.canAccessAllFiles()){
            showAllFilesAccessDialog()
        }
        else if(!Utils.canStartActivitiesFromBackground(this)){
            showDrawOverlayPermissionDialog()
        }
    }

    private fun showAllFilesAccessDialog(){
        AlertDialog.Builder(this)
            .setMessage("In order to install apk files the \"All Files Access\" is needed. Please grant this permission")
            .setPositiveButton("Grant permission"){dialog, _ ->
                dialog.dismiss()
                Utils.askAllFilesAccess(this)
            }
            .setOnDismissListener { checkPermissions() }
            .setCancelable(false)
            .show()
    }

    private fun showDrawOverlayPermissionDialog(){
        AlertDialog.Builder(this)
            .setMessage("The draw overlay permission is required in order to start apk installation flows. Please grant this permission")
            .setPositiveButton("Grant permission"){dialog, _ ->
                dialog.dismiss()
                Utils.requestOverlayDrawPermission(this, 10)
            }
            .setOnDismissListener { checkPermissions() }
            .setCancelable(false)
            .show()
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
    }

    fun hasStoragePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(!hasStoragePermission()){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                AlertDialog.Builder(this).setMessage("You denied the storage permission")
                    .setPositiveButton("Request again"){ dialog, _ ->
                        dialog.dismiss()
                        requestPermission()
                    }
                    .setNegativeButton("Cancel"){ dialog, _ ->
                        dialog.dismiss()
                        checkPermissions()
                    }
                    .show()
            }
            else{
                AlertDialog.Builder(this).setMessage("You permanently denied the storage permission. Go to the app's settings to enable it.")
                    .setPositiveButton("Go to settings"){ dialog, _ ->
                        dialog.dismiss()
                        Utils.openAndroidPermissionSettings(this)
                    }
                    .setNegativeButton("Cancel"){ dialog, _ ->
                        dialog.dismiss()
                        checkPermissions()
                    }
                    .show()
            }
        }
        else{
            checkPermissions()
        }
    }

}