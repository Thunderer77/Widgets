package com.example.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.camera2.CameraManager
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi

class WidgetProvider: AppWidgetProvider() {
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var cameraM:CameraManager
    var isFlash = false

    override fun onUpdate(
        context:Context,
        appWidgetManager:AppWidgetManager?,
        appWidgetIds:IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val views:RemoteViews = RemoteViews(context.packageName, R.layout.widget_view)

        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = "TOGGLE_FLASH"
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.flashButton, pendingIntent)

        appWidgetManager?.updateAppWidget(appWidgetIds, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "TOGGLE_FLASH") {
            sharedPreferences = context.getSharedPreferences("FlashState", Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
            cameraM = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            isFlash = sharedPreferences.getBoolean("isFlash", false)
            flashLightOnOrOff()
        }
    }

    private fun flashLightOnOrOff() {
        isFlash = !isFlash
        editor.putBoolean("isFlash", isFlash).apply()
        val cameraListId = cameraM.cameraIdList[0]
        cameraM.setTorchMode(cameraListId, isFlash)
    }
}