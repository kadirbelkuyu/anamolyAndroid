package Interfaces

import Models.CouponModel
import Models.TimeSlotModel

/**
 * Created on 07-02-2020.
 */
interface OnCouponSelected {
    fun onSelected(couponModel: CouponModel)
}