package com.example.chatapp

import android.app.ProgressDialog
import android.content.Context
import android.widget.ProgressBar

fun Context.createDialog(msg: String, isCancelable: Boolean): ProgressDialog {

    return ProgressDialog(this).apply {
        setCancelable(isCancelable)
        setMessage(msg)
        setCanceledOnTouchOutside(false)
    }
}