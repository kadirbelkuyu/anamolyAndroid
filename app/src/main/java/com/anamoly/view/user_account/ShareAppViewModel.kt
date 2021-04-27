package com.anamoly.view.user_account

import Config.BaseURL
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.anamoly.R
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse

/**
 * Created on 25-03-2020.
 */
class ShareAppViewModel(application: Application) : AndroidViewModel(application) {
    val projectRepository = ProjectRepository()
    var newDeepLink = "code not found"

    fun makeGetReferralCode(params: HashMap<String, String>): LiveData<CommonResponse?> {
        return projectRepository.getReferralCode(params)
    }

    fun onClick(context: Context, id: Int) {
        when (id) {
            R.id.tv_share_facebook -> {
                Intent().apply {
                    action = Intent.ACTION_SEND
                    var hasFaceBook = true
                    if (isPackageInstalled("com.facebook.katana", context.packageManager)) {
                        setPackage("com.facebook.katana")
                    } else if (isPackageInstalled("com.facebook.lite", context.packageManager)) {
                        setPackage("com.facebook.lite")
                    } else if (isPackageInstalled("com.facebook.orca", context.packageManager)) {
                        setPackage("com.facebook.orca")
                    } else if (isPackageInstalled("com.facebook.mlite", context.packageManager)) {
                        setPackage("com.facebook.mlite")
                    } else {
                        hasFaceBook = false
                    }
                    if (hasFaceBook) {
                        putExtra(
                            Intent.EXTRA_TEXT,
                            newDeepLink
                        )
                        type = "text/plain"
                        context.startActivity(this)
                    }
                }
            }
            R.id.tv_share_whatsapp -> {
                if (isPackageInstalled("com.whatsapp", context.packageManager)) {
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        setPackage("com.whatsapp")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            newDeepLink
                        )
                        type = "text/plain"
                        context.startActivity(this)
                    }
                }
            }
            R.id.tv_share_email -> {
                Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "", null
                    )
                ).apply {
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        "${context.resources.getString(R.string.have_you_tried)} ${context.resources.getString(
                            R.string.app_name
                        )} ${context.resources.getString(R.string.yet)}"
                    )
                    putExtra(
                        Intent.EXTRA_TEXT,
                        newDeepLink
                    )
                    context.startActivity(Intent.createChooser(this, "Send email..."))
                }
            }
            R.id.tv_share_tnc -> {
                Intent(context, AppInstructionActivity::class.java).apply {
                    putExtra(
                        "title",
                        context.resources.getString(R.string.terms_and_conditions_apply)
                    )
                    putExtra("url", BaseURL.TNC_URL)
                    context.startActivity(this)
                }
            }
        }
    }

    private fun isPackageInstalled(
        packageName: String,
        packageManager: PackageManager
    ): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

}