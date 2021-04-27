package com.anamoly.view.user_account

import Config.BaseURL
import Config.GlobleVariable
import Dialogs.LoaderDialog
import Interfaces.ActionBarLoadListener
import Interfaces.OnEditProfileSave
import Interfaces.OnTimeSelected
import Models.AddressModel
import Models.TimeSlotModel
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.checkout.dialog.BottomSheetCheckoutAddressDialog
import com.anamoly.view.otp.OtpActivity
import com.anamoly.view.user_account.adapter.SituationAllAdapter
import com.anamoly.view.user_account.dialog.BottomSheetChangePasswordDialog
import com.anamoly.view.user_account.dialog.BottomSheetEditProfileDialog
import com.anamoly.view.user_account.dialog.ChooseImageDialog
import com.xiaofeng.flowlayoutmanager.Alignment
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import kotlinx.android.synthetic.main.activity_my_profile.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import utils.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MyProfileActivity : CommonActivity(true), View.OnClickListener {

    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE1 = 101
    private val GALLERY_REQUEST_CODE1 = 201

    val MEDIA_TYPE_IMAGE = 1
    val MEDIA_TYPE_GALLERY = 2

    private lateinit var fileUri: Uri
    internal var imagefile1: File? = null

    val situationList = ArrayList<Int>()

    lateinit var situationAllAdapter: SituationAllAdapter

    lateinit var loaderDialog: LoaderDialog

    lateinit var myProfileViewModel: MyProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerActionbarListener(object : ActionBarLoadListener {
            override fun onLoaded() {
                showSetting()
            }
        })
        myProfileViewModel = ViewModelProviders.of(this).get(MyProfileViewModel::class.java)
        setContentView(R.layout.activity_my_profile)
        setHeaderTitle(resources.getString(R.string.my_profile))

        loaderDialog = LoaderDialog(this)

        situationAllAdapter = SituationAllAdapter(this, situationList)

        val flowLayoutManager = FlowLayoutManager()
        flowLayoutManager.isAutoMeasureEnabled = true
        if (LanguagePrefs.getLang(this).equals("ar")) {
            flowLayoutManager.setAlignment(Alignment.RIGHT)
        } else {
            flowLayoutManager.setAlignment(Alignment.LEFT)
        }

        rv_my_profile_situation.apply {
            layoutManager = flowLayoutManager
            adapter = situationAllAdapter
        }

        updateAddress()

        updateSituation()

        iv_my_profile_img.setOnClickListener(this)
        iv_my_profile_edit_full_name.setOnClickListener(this)
        iv_my_profile_edit_email.setOnClickListener(this)
        iv_my_profile_edit_phone.setOnClickListener(this)
        iv_my_profile_edit_address.setOnClickListener(this)
        iv_my_profile_edit_situation.setOnClickListener(this)
        btn_my_profile_change_password.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        var title = ""
        var value = ""
        when (v!!.id) {
            R.id.iv_my_profile_img -> {
                checkPermission()
            }
            R.id.iv_my_profile_edit_full_name -> {
                title = resources.getString(R.string.full_name)
                value = SessionManagement.UserData.getSession(this, BaseURL.KEY_NAME)
            }
            R.id.iv_my_profile_edit_email -> {
                title = resources.getString(R.string.email_id)
                value = SessionManagement.UserData.getSession(this, BaseURL.KEY_EMAIL)
            }
            R.id.iv_my_profile_edit_phone -> {
                title = resources.getString(R.string.phone_no)
                value = SessionManagement.UserData.getSession(this, BaseURL.KEY_MOBILE)
            }
            R.id.iv_my_profile_edit_address -> {
                val bottomSheetCheckoutAddressDialog = BottomSheetCheckoutAddressDialog(object :
                    OnTimeSelected {
                    override fun onSelected(timeSlotModel: TimeSlotModel?) {
                        updateAddress()
                    }
                })
                bottomSheetCheckoutAddressDialog.contexts = this
                if (bottomSheetCheckoutAddressDialog.isVisible) {
                    bottomSheetCheckoutAddressDialog.dismiss()
                } else {
                    val args = Bundle()
                    bottomSheetCheckoutAddressDialog.arguments = args
                    bottomSheetCheckoutAddressDialog.show(
                        supportFragmentManager,
                        bottomSheetCheckoutAddressDialog.tag
                    )
                }
            }
            R.id.iv_my_profile_edit_situation -> {
                Intent(this, AddEditSituationActivity::class.java).apply {
                    startActivityForResult(this, 9835)
                }
            }
            R.id.btn_my_profile_change_password -> {
                val bottomSheetChangePasswordDialog = BottomSheetChangePasswordDialog()
                bottomSheetChangePasswordDialog.contexts = this
                if (bottomSheetChangePasswordDialog.isVisible) {
                    bottomSheetChangePasswordDialog.dismiss()
                } else {
                    val args = Bundle()
                    bottomSheetChangePasswordDialog.arguments = args
                    bottomSheetChangePasswordDialog.show(
                        supportFragmentManager,
                        bottomSheetChangePasswordDialog.tag
                    )
                }
            }
        }

        if (title.isNotEmpty()) {
            val bottomSheetEditProfileDialog =
                BottomSheetEditProfileDialog(object : OnEditProfileSave {
                    override fun onSave(title: String, newValue: String, newValue2: String) {
                        if (title == resources.getString(R.string.first_name)) {
                            if (ConnectivityReceiver.isConnected) {
                                makeUpdateName(newValue, newValue2)
                            } else {
                                ConnectivityReceiver.showSnackbar(this@MyProfileActivity)
                            }
                        } else if (title == resources.getString(R.string.email_id)) {
                            if (ConnectivityReceiver.isConnected) {
                                makeupdateEmail(newValue)
                            } else {
                                ConnectivityReceiver.showSnackbar(this@MyProfileActivity)
                            }
                        } else if (title == resources.getString(R.string.phone_no)) {
                            if (ConnectivityReceiver.isConnected) {
                                makeupdatePhone(newValue)
                            } else {
                                ConnectivityReceiver.showSnackbar(this@MyProfileActivity)
                            }
                        }
                    }
                })
            bottomSheetEditProfileDialog.contexts = this
            if (bottomSheetEditProfileDialog.isVisible) {
                bottomSheetEditProfileDialog.dismiss()
            } else {
                val args = Bundle()
                if (title == resources.getString(R.string.full_name)) {
                    args.putString("title", resources.getString(R.string.first_name))
                    args.putString(
                        "value",
                        SessionManagement.UserData.getSession(this, "user_firstname")
                    )
                    args.putString("title2", resources.getString(R.string.last_name))
                    args.putString(
                        "value2",
                        SessionManagement.UserData.getSession(this, "user_lastname")
                    )
                } else {
                    args.putString("title", title)
                    args.putString("value", value)
                }
                bottomSheetEditProfileDialog.arguments = args
                bottomSheetEditProfileDialog.show(
                    supportFragmentManager,
                    bottomSheetEditProfileDialog.tag
                )
            }
        }
    }

    private fun updateHeader() {
        Glide.with(this)
            .load(
                BaseURL.IMG_PROFILE_URL + SessionManagement.UserData.getSession(
                    this,
                    BaseURL.KEY_IMAGE
                )
            )
            .placeholder(R.color.colorTextHint)
            .error(R.color.colorTextHint)
            .into(iv_my_profile_img)

        val full_name = "${SessionManagement.UserData.getSession(
            this,
            "user_firstname"
        )} ${SessionManagement.UserData.getSession(this, "user_lastname")}"

        tv_my_profile_full_name.text = full_name
        tv_my_profile_email2.text = SessionManagement.UserData.getSession(this, BaseURL.KEY_EMAIL)
        tv_my_profile_email.text = SessionManagement.UserData.getSession(this, BaseURL.KEY_EMAIL)
        tv_my_profile_phone2.text = SessionManagement.UserData.getSession(this, BaseURL.KEY_MOBILE)
        tv_my_profile_phone.text = SessionManagement.UserData.getSession(this, BaseURL.KEY_MOBILE)
    }

    private fun updateAddress() {
        val address = SessionManagement.UserData.getSession(this, "addresses")
        if (address.isNotEmpty() && address != "null") {
            val addressModelList = ArrayList<AddressModel>()

            val gson = Gson()
            val type = object : TypeToken<ArrayList<AddressModel>>() {}.type
            addressModelList.addAll(gson.fromJson<ArrayList<AddressModel>>(address, type))

            if (addressModelList.size > 0) {
                val addressModel = addressModelList[0]

                val fullAddress =
                    "${addressModel.postal_code}, ${addressModel.house_no}, ${addressModel.add_on_house_no}, ${addressModel.city}, ${addressModel.street_name}"

                tv_my_profile_address.text = fullAddress
            }
        }
    }

    private fun makeUpdateName(user_firstname: String, user_lastname: String) {
        val params = ArrayList<NameValuePair>()
        params.add(
            NameValuePair(
                "user_id",
                SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
            )
        )
        params.add(NameValuePair("user_firstname", user_firstname))
        params.add(NameValuePair("user_lastname", user_lastname))

        val task = CommonAsyTask(
            BaseURL.POST, params,
            BaseURL.UPDATE_NAME_URL, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String, message: String) {

                    SessionManagement.UserData.setSession(
                        this@MyProfileActivity,
                        "user_firstname",
                        user_firstname
                    )

                    SessionManagement.UserData.setSession(
                        this@MyProfileActivity,
                        "user_lastname",
                        user_lastname
                    )

                    updateHeader()

                }

                override fun VError(responce: String, code: String) {
                    showToast(this@MyProfileActivity, responce)
                }
            }, BaseURL.PROGRESSDIALOG, this
        )
        task.execute()
    }

    private fun makeupdateEmail(email: String) {
        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
        params["user_email"] = email

        if (loaderDialog.isShowing) {
            loaderDialog.dismiss()
        }
        loaderDialog.show()

        myProfileViewModel.updateEmail(params)
        myProfileViewModel.updateEmailResponseLiveData!!.observe(
            this,
            Observer { response: CommonResponse? ->
                if (loaderDialog.isShowing) {
                    loaderDialog.dismiss()
                }
                if (response != null) {
                    if (response.responce!!) {
                        val jsonObject = JSONObject(response.data!!)

                        val user_id = jsonObject.getString("user_id")
                        val user_email = jsonObject.getString("user_email")

                        Intent(this@MyProfileActivity, OtpActivity::class.java).apply {
                            putExtra("user_id", user_id)
                            putExtra("user_email", user_email)
                            startActivity(this)
                        }

                    } else {
                        CommonActivity.showToast(this@MyProfileActivity, response.message!!)
                    }
                }
            })
    }

    private fun makeupdatePhone(phone: String) {
        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
        params["user_phone"] = phone

        if (loaderDialog.isShowing) {
            loaderDialog.dismiss()
        }
        loaderDialog.show()

        myProfileViewModel.updatePhone(params)
        myProfileViewModel.updatePhoneResponseLiveData!!.observe(
            this,
            Observer { response: CommonResponse? ->
                if (loaderDialog.isShowing) {
                    loaderDialog.dismiss()
                }
                if (response != null) {
                    if (response.responce!!) {
                        SessionManagement.UserData.setSession(
                            this@MyProfileActivity,
                            BaseURL.KEY_MOBILE,
                            phone
                        )
                        updateHeader()
                        CommonActivity.showToast(this@MyProfileActivity, response.message!!)
                    } else {
                        CommonActivity.showToast(this@MyProfileActivity, response.message!!)
                    }
                }
            })
    }

    private fun makeUploadPhoto() {

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaType(), imagefile1!!)

        // MultipartBody.Part is used to send also the actual file name
        // MultipartBody.Part is used to send also the actual file name
        val imageBody: MultipartBody.Part =
            MultipartBody.Part.createFormData("user_image", imagefile1!!.name, requestFile)

        if (loaderDialog.isShowing) {
            loaderDialog.dismiss()
        }
        loaderDialog.show()

        myProfileViewModel.uploadProfilePicture(
            SessionManagement.UserData.getSession(
                this,
                BaseURL.KEY_ID
            ), imageBody
        )
        myProfileViewModel.uploadImageResponseLiveData!!.observe(
            this,
            Observer { response: CommonResponse? ->
                if (loaderDialog.isShowing) {
                    loaderDialog.dismiss()
                }
                if (response != null) {
                    if (response.responce!!) {
                        SessionManagement.UserData.setSession(
                            this@MyProfileActivity,
                            BaseURL.KEY_IMAGE,
                            response.data!!
                        )
                        updateHeader()
                    } else {
                        CommonActivity.showToast(this@MyProfileActivity, response.message!!)
                    }
                }
            })
    }

    fun updateSituation() {
        situationList.clear()

        val family = SessionManagement.UserData.getSession(this, "family")
        if (family.isNotEmpty() && family != "null") {
            val jsonObject = JSONObject(family)
            for (i in 0 until jsonObject.getString("no_of_adults").toInt()) {
                situationList.add(1)
            }
            for (i in 0 until jsonObject.getString("no_of_child").toInt()) {
                situationList.add(2)
            }
            for (i in 0 until jsonObject.getString("no_of_dogs").toInt()) {
                situationList.add(3)
            }
            for (i in 0 until jsonObject.getString("no_of_cats").toInt()) {
                situationList.add(4)
            }

            situationAllAdapter.notifyDataSetChanged()
        }
    }

    private fun checkPermission() {
        Dexter.withActivity(this)
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

    // show alertdialog with custom layout
    private fun showImageChooser() {

        val chooseImageDialog = ChooseImageDialog(this, object : ChooseImageDialog.OnClickListener {
            override fun cameraClick() {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Ensure that there's a camera activity to handle the intent
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        // Create the File where the photo should go
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            null
                        }

                        // Continue only if the File was successfully created
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                this@MyProfileActivity,
                                packageName + ".fileprovider",
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
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
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
        val storageDir: File = cacheDir
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
        if (resultCode == Activity.RESULT_OK && requestCode == 9835) {
            updateSituation()
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE1) {
            if (resultCode == Activity.RESULT_OK) {

                val file = File(currentPhotoPath)
                if (file.exists()) {
                    imagefile1 = ImageComprasser.compressImage(this@MyProfileActivity, file)

                    val bitmap = BitmapFactory.decodeFile(imagefile1!!.absolutePath)

                    iv_my_profile_img.setImageBitmap(bitmap)

                    makeUploadPhoto()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                CommonActivity.showToast(this, "User cancelled image capture")
            } else {
                // failed to capture image
                CommonActivity.showToast(this, "Sorry! Failed to capture image")
            }

        } else if (requestCode == GALLERY_REQUEST_CODE1) {
            if (data != null) {
                try {
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                    // Get the cursor
                    val cursor = contentResolver.query(
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
                        imagefile1 = ImageComprasser.compressImage(this@MyProfileActivity, file)

                        // Set the Image in ImageView after decoding the String
                        iv_my_profile_img.setImageBitmap(b)

                        makeUploadPhoto()
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateHeader()
    }

}
