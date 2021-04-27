package Config

import com.anamoly.BuildConfig

object BaseURL {

    const val BASE_URL = BuildConfig.BASE_URL
    const val IMG_BASE_URL = BuildConfig.IMG_BASE_URL

    const val IMG_PROFILE_URL = IMG_BASE_URL + "uploads/profile/"
    const val IMG_PRODUCT_URL = IMG_BASE_URL + "uploads/products/"
    const val IMG_CATEGORIES_URL = IMG_BASE_URL + "uploads/categories/crop/small/"
    const val IMG_BANNER_URL = IMG_BASE_URL + "uploads/banners/"
    const val IMG_HEADER_URL = IMG_BASE_URL + "uploads/app/"


    const val REGISTER_URL = BASE_URL + "index.php/rest/user/register"
    const val ADD_EDIT_FAMILY_URL = BASE_URL + "index.php/rest/user/setfamily"
    const val UPDATE_SETTING_URL = BASE_URL + "index.php/rest/user/updatesettings"
    const val UPDATE_NAME_URL = BASE_URL + "index.php/rest/user/update_name"
    const val VERIFY_EMAIL_URL = BASE_URL + "index.php/rest/user/verifyemail"
    const val GET_PRODUCT_DETAIL_URL = BASE_URL + "index.php/rest/products/details"
    const val EDIT_ADDRESS_URL = BASE_URL + "index.php/rest/address/update"
    const val SEND_ORDER_URL = BASE_URL + "index.php/rest/order/send"
    const val VALID_COUPON_URL = BASE_URL + "index.php/rest/coupon/validate"

    const val INTRO_URL = BASE_URL + "index.php/apppages/intro"
    const val TNC_URL = BASE_URL + "index.php/apppages/tnc"
    const val POLICY_URL = BASE_URL + "index.php/apppages/policy"
    const val DELIVERY_TNC_URL = BASE_URL + "index.php/apppages/deliverytnc"
    const val ABOUT_URL = BASE_URL + "index.php/apppages/about"


    const val ENCRYPTED_PASSWORD = BuildConfig.ENCRYPTED_PASSWORD

    const val HEADER_KEY = BuildConfig.HEADER_KEY
    var HEADER_LANG = "english"//dutch,english,arabic
    const val HEADER_DEVICE = BuildConfig.HEADER_DEVICE
    const val HEADER_VERSION = BuildConfig.HEADER_VERSION

    const val GET = "get"
    const val POST = "post"

    const val PROGRESSDIALOG = 2
    const val PROGRESSBAR = 1
    const val NONE = 0

    const val PREFS_NAME = "SeyyarLoginPrefs"
    const val PERMANENT_PREFS_NAME = "SeyyarPermanentPrefs"


    const val IS_LOGIN = "isLogin"
    const val KEY_ID = "user_id"
    const val KEY_TYPE_ID = "user_type_id"
    const val KEY_NAME = "user_fullname"
    const val KEY_EMAIL = "user_email"
    const val KEY_MOBILE = "user_phone"
    const val KEY_BDATE = "user_bdate"
    const val KEY_IMAGE = "user_image"
    const val KEY_NATIONAL_ID = "user_national_id"
    const val KEY_COUNTRY_CODE = "phone_country_code"
    const val KEY_CITY = "user_city"
    const val KEY_COUNTRY = "user_country"
    const val KEY_STATE = "user_state"
    const val KEY_GENDER = "user_gender"
    const val KEY_COUNTRY_NAME = "country_name"

    //FrontPage = 1,CategoryPage = 2,ProductPage = 3
    interface AD_TYPE {
        companion object {
            val FrontPage = 1
            val CategoryPage = 2
            val ProductPage = 3
        }
    }

}