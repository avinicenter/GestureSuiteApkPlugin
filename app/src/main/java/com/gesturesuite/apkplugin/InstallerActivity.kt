package com.gesturesuite.apkplugin

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.MimeTypeMap

class InstallerActivity: BaseActivity() {

    private var shouldFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent != null) {
            handleIntent(intent)
        }
        else{
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if(shouldFinish){
            finish()
        }
    }

    private fun handleIntent(intent: Intent) {
        if(hasStoragePermission() && Utils.canStartActivitiesFromBackground(this)) {
            startInstallFlow(this, intent)
        }
        else{
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


    private fun startInstallFlow(context: Activity, intent: Intent){
        val extraKey = context.getString(R.string.intentActionExtra)
        intent.getStringExtra(extraKey)?.let { filePath ->
            var uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                PluginFileProvider.getUriFromFile(context, filePath)
            } else {
                Uri.parse("file://" + filePath)
            }

            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk")
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            try{
                context.startActivity(installIntent)
                shouldFinish = true
            }catch (e:Exception){
                AlertDialog.Builder(this)
                    .setMessage("Could not start installation flow")
                    .setPositiveButton("OK"){dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }.show()
            }
        }
    }

}