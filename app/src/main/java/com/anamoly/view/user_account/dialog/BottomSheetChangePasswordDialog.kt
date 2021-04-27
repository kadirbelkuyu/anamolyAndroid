package com.anamoly.view.user_account.dialog

import Config.BaseURL
import Dialogs.LoaderDialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.view.MotionEvent
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.user_account.MyProfileViewModel
import kotlinx.android.synthetic.main.dialog_bottom_change_password.view.*
import utils.ConnectivityReceiver
import utils.SessionManagement


class BottomSheetChangePasswordDialog : BottomSheetDialogFragment(), View.OnTouchListener {

    lateinit var rootView: View
    lateinit var contexts: Context
    lateinit var myProfileViewModel: MyProfileViewModel

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation_slidebottom
        dialog!!.window!!.statusBarColor = Color.TRANSPARENT
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.dialog_bottom_change_password, container, false)

        myProfileViewModel = ViewModelProviders.of(this).get(MyProfileViewModel::class.java)

        rootView.ll_change_password.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = rootView.ll_change_password.height
        }

        rootView.view_dash.background = CommonActivity.getCircleBG2()

        rootView.et_change_password_old.setOnTouchListener(this)
        rootView.et_change_password_new.setOnTouchListener(this)
        rootView.et_change_password_re_new.setOnTouchListener(this)

        rootView.btn_change_password.setOnClickListener {
            attemptChangePassword()
        }

        return rootView
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val editText: EditText = v!! as EditText

        val DRAWABLE_LEFT = 0
        val DRAWABLE_TOP = 1
        val DRAWABLE_RIGHT = 2
        val DRAWABLE_BOTTOM = 3
        if (event!!.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= editText.right - editText.compoundDrawables.get(
                    DRAWABLE_RIGHT
                ).bounds.width()
            ) { // your action here
                if (editText.transformationMethod.javaClass.simpleName == "PasswordTransformationMethod") {
                    editText.transformationMethod = SingleLineTransformationMethod()
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_eye,
                        0
                    )
                } else {
                    editText.transformationMethod = PasswordTransformationMethod()
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_eye_hide,
                        0
                    )
                }
                return true
            }
        }
        return false
    }

    private fun attemptChangePassword() {
        rootView.et_change_password_old.error = null
        rootView.et_change_password_new.error = null
        rootView.et_change_password_re_new.error = null

        val ic_error = CommonActivity.setErrorDrawable(contexts)

        var cancel = false
        var focuseView: View? = null

        val password_old = rootView.et_change_password_old.text.toString()
        val password_new = rootView.et_change_password_new.text.toString()
        val password_re_new = rootView.et_change_password_re_new.text.toString()

        if (password_new != password_re_new) {
            rootView.et_change_password_re_new.setError(
                resources.getString(R.string.error_password_not_match),
                ic_error
            )
            focuseView = rootView.et_change_password_re_new
            cancel = true
        }

        if (password_new.isEmpty()) {
            rootView.et_change_password_new.setError(
                resources.getString(R.string.error_field_required),
                ic_error
            )
            focuseView = rootView.et_change_password_new
            cancel = true
        } else if (!CommonActivity.isValidPassword(password_new)) {
            rootView.et_change_password_new.setError(
                resources.getString(R.string.error_password_required), ic_error
            )
            focuseView = rootView.et_change_password_new
            cancel = true
        }

        if (password_old.isEmpty()) {
            rootView.et_change_password_old.setError(
                resources.getString(R.string.error_field_required),
                ic_error
            )
            focuseView = rootView.et_change_password_old
            cancel = true
        } else if (!CommonActivity.isValidPassword(password_old)) {
            rootView.et_change_password_old.setError(
                resources.getString(R.string.error_password_required), ic_error
            )
            focuseView = rootView.et_change_password_old
            cancel = true
        }

        if (cancel) {
            focuseView?.requestFocus()
        } else {
            if (ConnectivityReceiver.isConnected) {
                makeChangePassword(password_old, password_new, password_re_new)
            } else {
                ConnectivityReceiver.showSnackbar(contexts)
            }
        }

    }

    private fun makeChangePassword(c_password: String, n_password: String, r_password: String) {
        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(contexts, BaseURL.KEY_ID)
        params["c_password"] = c_password
        params["n_password"] = n_password
        params["r_password"] = r_password

        val loaderDialog = LoaderDialog(contexts)
        loaderDialog.show()

        myProfileViewModel.changePassword(params)
        myProfileViewModel.changePasswordResponseLiveData!!.observe(
            this,
            Observer { response: CommonResponse? ->
                loaderDialog.dismiss()
                if (response != null) {
                    if (response.responce!!) {
                        CommonActivity.showToast(contexts, response.data!!)
                        SessionManagement(contexts).logoutSessionLogin()
                        dismiss()
                    } else {
                        CommonActivity.showToast(contexts, response.message!!)
                    }
                }
            })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contexts = context
    }

}