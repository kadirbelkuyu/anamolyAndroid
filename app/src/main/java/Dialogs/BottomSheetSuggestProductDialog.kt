package Dialogs

import Config.BaseURL
import Config.GlobleVariable
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.FrameLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.user_account.dialog.ChooseImageDialog
import com.anamoly.view.search.SearchProductViewModel
import kotlinx.android.synthetic.main.dialog_bottom_suggest_product.view.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import utils.ImageComprasser
import utils.SessionManagement
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class BottomSheetSuggestProductDialog : BottomSheetDialogFragment() {

    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE1 = 101
    private val GALLERY_REQUEST_CODE1 = 201

    internal var imagefile1: File? = null

    lateinit var rootView: View
    lateinit var contexts: Context

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
        rootView = inflater.inflate(R.layout.dialog_bottom_suggest_product, container, false)

        rootView.ll_suggest_product.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = rootView.ll_suggest_product.height
        }

        rootView.view_dash.background = CommonActivity.getCircleBG2()

        val searchProductViewModel =
            ViewModelProviders.of(this).get(SearchProductViewModel::class.java)

        rootView.cl_suggest_product.setOnClickListener {
            Dexter.withActivity(contexts as Activity)
                .withPermissions(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            showImageChooser()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }

        rootView.btn_suggest_product_send.setOnClickListener {
            val suggetion = rootView.et_suggest_product.text.toString()

            if (suggetion.isNotEmpty()) {
                val loaderDialog = LoaderDialog(contexts)
                loaderDialog.show()

                var imageBody: MultipartBody.Part? = null
                if (imagefile1 != null) {
                    val requestFile: RequestBody =
                        RequestBody.create("multipart/form-data".toMediaType(), imagefile1!!)
                    imageBody =
                        MultipartBody.Part.createFormData("image", imagefile1!!.name, requestFile)
                }
                searchProductViewModel.sendProductSuggestion(
                    SessionManagement.UserData.getSession(
                        contexts,
                        BaseURL.KEY_ID
                    ), suggetion, imageBody
                )
                    .observe(this, androidx.lifecycle.Observer { response: CommonResponse? ->
                        loaderDialog.dismiss()
                        if (response != null) {
                            if (response.responce!!) {
                                CommonActivity.showToast(contexts, response.message!!)
                                dismiss()
                            } else {
                                CommonActivity.showToast(contexts, response.message!!)
                            }
                        }
                    })
            }
        }

        return rootView
    }

    private fun showImageChooser() {

        val chooseImageDialog =
            ChooseImageDialog(contexts, object : ChooseImageDialog.OnClickListener {
                override fun cameraClick() {
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        // Ensure that there's a camera activity to handle the intent
                        takePictureIntent.resolveActivity(contexts.packageManager)?.also {
                            // Create the File where the photo should go
                            val photoFile: File? = try {
                                createImageFile()
                            } catch (ex: IOException) {
                                null
                            }

                            // Continue only if the File was successfully created
                            photoFile?.also {
                                val photoURI: Uri = FileProvider.getUriForFile(
                                    contexts,
                                    contexts.packageName + ".fileprovider",
                                    it
                                )
                                takePictureIntent.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    photoURI
                                )
                                startActivityForResult(
                                    takePictureIntent,
                                    CAMERA_CAPTURE_IMAGE_REQUEST_CODE1
                                )
                            }
                        }
                    }


                }

                override fun galleryClick() {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    galleryIntent.type = "image/*"
                    // Start the Intent
                    startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE1)
                }

                override fun cancelClick() {

                }
            })
        chooseImageDialog.show()

    }

    var currentPhotoPath: String = ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", GlobleVariable.LOCALE).format(Date())
        val storageDir: File = contexts.cacheDir
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE1) {
            if (resultCode == Activity.RESULT_OK) {

                val file = File(currentPhotoPath)
                if (file.exists()) {
                    imagefile1 = ImageComprasser.compressImage(contexts, file)

                    val bitmap = BitmapFactory.decodeFile(imagefile1!!.absolutePath)

                    rootView.iv_suggest_product_img.setImageBitmap(bitmap)
                    rootView.ll_suggest_product_placeholder.visibility = View.GONE
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                CommonActivity.showToast(contexts, "User cancelled image capture")
            } else {
                // failed to capture image
                CommonActivity.showToast(contexts, "Sorry! Failed to capture image")
            }

        } else if (requestCode == GALLERY_REQUEST_CODE1) {
            if (data != null) {
                try {
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                    // Get the cursor
                    val cursor = contexts.contentResolver.query(
                        selectedImage!!,
                        filePathColumn, null, null, null
                    )
                    // Move to first row
                    cursor!!.moveToFirst()

                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val imgDecodableString = cursor.getString(columnIndex)

                    val b = BitmapFactory.decodeFile(imgDecodableString)

                    val file = File(imgDecodableString)
                    cursor.close()

                    if (file.exists()) {
                        imagefile1 = ImageComprasser.compressImage(contexts, file)

                        Glide.with(this)
                            .load(imagefile1!!)
                            //.fit().centerCrop()
                            .centerCrop()
                            /*.transform(
                            RoundedCornersTransformation(
                                CommonActivity.dpToPx(
                                    contexts,
                                    8F
                                ), 0
                            )
                        )*/
                            .into(rootView.iv_suggest_product_img)

                        // Set the Image in ImageView after decoding the String
                        //rootView.iv_suggest_product_img.setImageBitmap(b)
                        rootView.ll_suggest_product_placeholder.visibility = View.GONE
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contexts = context
    }

}