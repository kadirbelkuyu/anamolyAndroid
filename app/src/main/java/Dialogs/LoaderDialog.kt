package Dialogs

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.anamoly.R

class LoaderDialog(val contexts: Context) :
    android.app.AlertDialog(contexts) {

    val dialogView: View?

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        val inflater = this.layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_loader, null)
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

    }

}
