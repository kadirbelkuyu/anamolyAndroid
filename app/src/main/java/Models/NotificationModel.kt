package Models

import java.io.Serializable

/**
 * Created on 22-02-2020.
 */
class NotificationModel : Serializable {

    var noti_id: Int = 0
    var noti_play_id: String? = null
    var noti_title: String? = null
    var noti_message: String? = null
    var noti_type: String? = null
    var noti_ref_id: String? = null

}