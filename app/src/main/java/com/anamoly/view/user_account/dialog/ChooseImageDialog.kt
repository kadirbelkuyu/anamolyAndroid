package com.anamoly.view.user_account.dialog

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.anamoly.R
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.dialog_choose_image.view.*

class ChooseImageDialog(val contexts: Context, val onClickListener: OnClickListener) :
    android.app.AlertDialog(contexts) {

    init {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_choose_image, null)
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
                view = View(context)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        PushDownAnim.setPushDownAnimTo(dialogView.tv_choose_image_cancel)

        dialogView.tv_choose_image_camera.setOnClickListener {
            onClickListener.cameraClick()
            dismiss()
        }
        dialogView.tv_choose_image_gallery.setOnClickListener {
            onClickListener.galleryClick()
            dismiss()
        }
        dialogView.tv_choose_image_cancel.setOnClickListener {
            onClickListener.cancelClick()
            dismiss()
        }

    }

    interface OnClickListener {
        public fun cameraClick()
        public fun galleryClick()
        public fun cancelClick()
    }

}
