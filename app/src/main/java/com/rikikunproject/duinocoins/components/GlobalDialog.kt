package com.rikikunproject.duinocoins.components

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object GlobalDialog {

    fun showAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}