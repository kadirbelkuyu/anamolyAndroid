package configFCM

import android.app.Activity
import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging

import java.util.ArrayList

import utils.NameValuePair
import com.google.firebase.iid.InstanceIdResult
import com.google.android.gms.tasks.OnSuccessListener


class MyFirebaseRegister(internal var _context: Activity) {

    companion object {
        private val TAG = "MyFirebaseRegister"
    }

    fun RegisterUser(user_id: String, isRegister: Boolean) {
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("android_seyyar")
            .addOnCompleteListener { task ->
                var msg = "success"
                if (!task.isSuccessful) {
                    msg = "failed"
                }
                Log.d(TAG, msg)
            }
        // [END subscribe_topics]

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(_context,
            OnSuccessListener<InstanceIdResult> { instanceIdResult ->
                val token = instanceIdResult.token
                Log.e(TAG, token)
                if (isRegister) {
                    makeRegisterFCM(user_id, token)
                }
            })
    }

    private fun makeRegisterFCM(user_id: String, token: String?) {

        val params = ArrayList<NameValuePair>()
        params.add(NameValuePair("userId", user_id))
        params.add(NameValuePair("token", token!!))

        /*val task = CommonAsyTask(
            BaseURL.POST, params,
            BaseURL.SAVE_FCM_TOKEN_URL, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String) {
                    Log.i(_context.toString(), responce)
                }

                override fun VError(responce: String) {
                    Log.e(_context.toString(), responce)
                }
            }, BaseURL.NONE, _context
        )
        task.execute()*/
    }

}
