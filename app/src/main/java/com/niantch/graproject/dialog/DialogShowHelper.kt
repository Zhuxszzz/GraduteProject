package com.niantch.graproject.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.util.Log

/**
 * @description: Manipulate Dialog for safety reason, avoid activity lifecycle causing errors
 * @since 2021-02-08 20:27
 * @author jitaoguo@tencent.com
 */
object DialogShowHelper {
    private const val TAG = "DialogShowUtils"

    @JvmStatic
    fun show(dialog: Dialog?): Boolean {
        if (dialog == null) {
            Log.w(TAG, "dialog is null")
            return false
        }
        val context = findActivityContext(dialog.context)

        if (context is Activity && context.isFinishing) {
            Log.w(TAG, "activity - $context is finishing, do not show dialog")
            return false
        }

        try {
            if (dialog.isShowing) {
                Log.i(TAG, "dialog - $dialog is already showing")
            } else {
                dialog.show()
                return true
            }
        } catch (ex: Throwable) {
            Log.e(TAG, ex.toString())
        }
        return false
    }

    @JvmStatic
    fun dismiss(dialog: Dialog?): Boolean {
        if (!isDialogAvailable(dialog, "dismiss")) {
            return false
        }
        try {
            dialog!!.dismiss()
            return true
        } catch (ex: Throwable) {
            Log.e(TAG, ex.toString())
        }
        return false
    }

    fun cancel(dialog: Dialog): Boolean {
        if (!isDialogAvailable(dialog, "cancel")) {
            return false
        }
        try {
            dialog.cancel()
            return true
        } catch (ex: Throwable) {
            Log.e(TAG, ex.toString())
        }
        return false
    }

    private fun isDialogAvailable(dialog: Dialog?, tips: String): Boolean {
        if (dialog == null) {
            Log.w(TAG, "$tips dialog is null")
            return false
        }
        if (!dialog.isShowing) {
            Log.w(TAG, "$tips dialog - $dialog is not showing")
            return false
        }
        val context = findActivityContext(dialog.context)

        if (context is Activity && context.isFinishing) {
            return false
        }
        return true
    }

    /**
     * Context will be wrapped into ContextThemeWrapper in Dialog
     * This method will find activity or base context
     */
    private fun findActivityContext(context: Context): Context {
        var currentContext = context
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return currentContext
    }
}