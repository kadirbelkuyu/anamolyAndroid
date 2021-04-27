package Interfaces

import Models.TimeSlotModel

/**
 * Created on 07-02-2020.
 */
interface OnTimeSelected {
    fun onSelected(timeSlotModel: TimeSlotModel?)
}