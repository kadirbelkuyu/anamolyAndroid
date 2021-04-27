package com.anamoly.view.user_account

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.EditText
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.anamoly.R
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import utils.SessionManagement

/**
 * Created on 23-03-2020.
 */
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    val projectRepository = ProjectRepository()
    val isContactValid = MutableLiveData<Boolean>()

    fun attemptContactUs(
        context: Context,
        nameEdit: EditText,
        phoneEdit: EditText,
        messageEdit: EditText
    ) {

        nameEdit.error = null
        phoneEdit.error = null
        messageEdit.error = null

        val name = nameEdit.text.toString()
        val phone = phoneEdit.text.toString()
        val message = messageEdit.text.toString()

        var focusView: View? = null
        var cancel = false

        if (message.isEmpty()) {
            messageEdit.error = context.resources.getString(R.string.error_field_required)
            focusView = messageEdit
            cancel = true
        }

        if (phone.isEmpty()) {
            phoneEdit.error = context.resources.getString(R.string.error_field_required)
            focusView = phoneEdit
            cancel = true
        }

        if (name.isEmpty()) {
            nameEdit.error = context.resources.getString(R.string.error_field_required)
            focusView = nameEdit
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
            isContactValid.value = false
        } else {
            isContactValid.value = true
        }

    }

    fun makeSendContact(params: HashMap<String, String>): LiveData<CommonResponse?> {
        return projectRepository.sendContact(params)
    }

    fun intentCall(context: Context) {
        val number = SessionManagement.PermanentData.getSession(context, "app_contact")
        Dexter.withActivity(context as Activity)
            .withPermission(android.Manifest.permission.CALL_PHONE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")).apply {
                        context.startActivity(this)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                }
            }).check()
    }

    fun intentEmail(context: Context) {
        val email = SessionManagement.PermanentData.getSession(context, "app_email")
        Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null
            )
        ).apply {
            putExtra(Intent.EXTRA_SUBJECT, "Subject")
            putExtra(Intent.EXTRA_TEXT, "Body")
            context.startActivity(Intent.createChooser(this, "Send email..."))
        }
    }

    fun intentWhatsapp(context: Context) {
        val number = SessionManagement.PermanentData.getSession(context, "app_whatsapp")
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/$number")
            context.startActivity(this)
        }
    }

}