package Dialogs

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.anamoly.R
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.dialog_common_alert.view.*

class CommonAlertDialog :
    android.app.AlertDialog {

    val contexts: Context
    var title: String = ""
    var desc: String? = null
    var cancelText: String? = null
    var okText: String? = null
    var onClickListener: OnClickListener? = null
    var hideCancel: Boolean = false
    var hideOk: Boolean = false

    constructor(
        contexts: Context,
        title: String,
        desc: String?,
        cancelText: String?,
        okText: String?,
        onClickListener: OnClickListener?
    ) : super(contexts) {
        this.contexts = contexts
        setCanceledOnTouchOutside(false)

        this.title = title
        this.desc = desc
        this.cancelText = cancelText
        this.okText = okText
        this.onClickListener = onClickListener


        val inflater = this.layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_common_alert, null)
        this.setView(dialogView)
        try {
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        try {
            val imm =
                (contexts as Activity).getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = contexts.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(contexts)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        bindView()

    }

    constructor(
        contexts: Context,
        title: String,
        desc: String?,
        cancelText: String?,
        okText: String?,
        hideCancel: Boolean,
        hideOk: Boolean,
        onClickListener: OnClickListener?
    ) : super(contexts) {
        this.contexts = contexts
        setCanceledOnTouchOutside(false)

        this.title = title
        this.desc = desc
        this.cancelText = cancelText
        this.okText = okText
        this.hideCancel = hideCancel
        this.hideOk = hideOk
        this.onClickListener = onClickListener


        val inflater = this.layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_common_alert, null)
        this.setView(dialogView)
        try {
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        try {
            val imm =
                (contexts as Activity).getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = contexts.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(contexts)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        bindView()

    }

    val dialogView: View

    private fun bindView() {
        dialogView.tv_common_alert_dialog_title.text = title
        if (!desc.isNullOrEmpty()) {
            dialogView.tv_common_alert_dialog_desc.text = desc
        } else {
            dialogView.tv_common_alert_dialog_desc.visibility = View.GONE
        }
        if (!cancelText.isNullOrEmpty()) {
            dialogView.tv_alert_dialog_cancel.text = cancelText
        }
        if (!okText.isNullOrEmpty()) {
            dialogView.tv_alert_dialog_ok.text = okText
        }

        if (hideCancel) {
            dialogView.tv_alert_dialog_cancel.visibility = View.GONE
        }

        if (hideOk) {
            dialogView.tv_alert_dialog_ok.visibility = View.GONE
        }

        PushDownAnim.setPushDownAnimTo(
            dialogView.tv_alert_dialog_cancel,
            dialogView.tv_alert_dialog_ok
        )
        dialogView.tv_alert_dialog_cancel.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.cancelClick()
            }
            dismiss()
        }
        dialogView.tv_alert_dialog_ok.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.okClick()
            }
            dismiss()
        }
    }

    interface OnClickListener {
        public fun cancelClick()
        public fun okClick()
    }

}
