package com.gesturesuite.apkplugin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class Receiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if(it.action == context?.getString(R.string.intentAction)){
                val extraKey = context?.getString(R.string.intentActionExtra)
                context?.let { context ->
                    if(Utils.canStartActivitiesFromBackground(context)) {
                        context.startActivity(
                            Intent(context, InstallerActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(extraKey, intent.getStringExtra(extraKey))
                        )
                    }
                    else{
                        Toast.makeText(context, "Open the Gesture Suite Apk Plugin and grant the draw overlay permission", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}