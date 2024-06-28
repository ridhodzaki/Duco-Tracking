package com.rikikunproject.duinocoins

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Handler
import android.util.Log
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.rikikunproject.duinocoins.components.GlobalDialog.showAlert
import com.rikikunproject.duinocoins.model.SuccessResponse
import com.rikikunproject.duinocoins.network.ApiConfig.apiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Implementation of App Widget functionality.
 */
class WalletWidget : AppWidgetProvider() {
    private lateinit var context: Context

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        this.context = context
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
            scheduleUpdate(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        private const val UPDATE_INTERVAL = 60 * 1000L // 1 menit (dalam milidetik)

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Implementasikan logika untuk memperbarui widget dengan data coin yang telah di mining
            // ...

            val views = RemoteViews(context.packageName, R.layout.widget_wallet)

            // Ambil data dari API dan perbarui widget
            fetchDataAndUpdateWidget(context, appWidgetManager, appWidgetId, views)

            // Tambahkan PendingIntent untuk membuka aplikasi saat widget diklik
            // ...

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun scheduleUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val handler = Handler()
            val runnable = object : Runnable {
                override fun run() {
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                    handler.postDelayed(this, UPDATE_INTERVAL)
                }
            }

            handler.postDelayed(runnable, UPDATE_INTERVAL)
        }

        private fun fetchDataAndUpdateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            views: RemoteViews
        ) {
            val sharedPreferences = context.getSharedPreferences("MyPreferences", AppCompatActivity.MODE_PRIVATE)
            val savedUsername = sharedPreferences.getString("username", null)
            Log.e(TAG, "${savedUsername}")

            if (savedUsername !== null) {
                // Implementasikan logika untuk mengambil data dari API menggunakan Retrofit
                val call: Call<SuccessResponse> = apiService.getUserWidget(savedUsername.toString())

                call.enqueue(object : Callback<SuccessResponse> {
                    override fun onResponse(
                        call: Call<SuccessResponse>,
                        response: Response<SuccessResponse>
                    ) {
                        if (response.isSuccessful) {
                            val userData = response.body()
                            if (userData != null) {
                                // Perbarui tampilan widget dengan data baru
                                views.setTextViewText(R.id.appwidget_username, "Hi, ${savedUsername}")
                                var balance = userData.result.balance.balance.toString()
                                var partsBalance = balance.split(".")
                                views.setTextViewText(R.id.appwidget_balance, "${partsBalance[0]}.")
                                views.setTextViewText(R.id.appwidget_balance_comma, "${partsBalance[1]} DUCO")
                                appWidgetManager.updateAppWidget(appWidgetId, views)
                            } else {
                                showAlert(context, "Error", "Response body is null")
                            }
                        } else {
                            showAlert(context, "Error", "Response unsuccessful")
                        }
                    }

                    override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                        showAlert(context, "Error", "Failure: ${t.message}")
                    }
                })
            } else {
                views.setTextViewText(R.id.appwidget_username, "You are not logged in yet")
                views.setTextViewText(R.id.appwidget_balance, "-")
                views.setTextViewText(R.id.appwidget_balance_comma, "")
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}


//internal fun updateAppWidget(
//    context: Context,
//    appWidgetManager: AppWidgetManager,
//    appWidgetId: Int
//) {
//        // Construct the RemoteViews object
//        val views = RemoteViews(context.packageName, R.layout.wallet)
//
//        // Setel teks untuk TextView di dalam RemoteViews
//        views.setTextViewText(R.id.appwidget_username, "Please Login on my app for this widget")
//        views.setTextViewText(R.id.appwidget_balance, "Balance is not detected")
//
//        // Instruksikan manajer widget untuk memperbarui widget
//        appWidgetManager.updateAppWidget(appWidgetId, views)
//}