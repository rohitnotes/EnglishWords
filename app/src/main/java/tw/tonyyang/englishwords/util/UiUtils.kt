package tw.tonyyang.englishwords.util

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class UiUtils {

    companion object {
        fun getProgressDialog(context: Context, message: String): AlertDialog {
            val llPadding = 30
            val ll = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(llPadding, llPadding, llPadding, llPadding)
                gravity = Gravity.CENTER
            }
            var llParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
            ll.layoutParams = llParam

            val progressBar = ProgressBar(context).apply {
                isIndeterminate = true
                setPadding(0, 0, llPadding, 0)
                layoutParams = llParam
            }

            llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                llParam.gravity = Gravity.CENTER
            }
            val tvText = TextView(context)
            tvText.text = message
            tvText.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            tvText.textSize = 20.toFloat()
            tvText.layoutParams = llParam

            ll.addView(progressBar)
            ll.addView(tvText)

            val builder = AlertDialog.Builder(context).apply {
                setCancelable(false)
                setView(ll)
            }

            val dialog = builder.create()
            val window = dialog.window
            if (window != null) {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog.window?.attributes)
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                dialog.window?.attributes = layoutParams
            }
            return dialog
        }
    }
}