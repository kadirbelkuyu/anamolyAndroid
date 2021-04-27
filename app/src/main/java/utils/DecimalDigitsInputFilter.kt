package utils

import android.text.InputFilter
import android.text.Spanned

import java.util.regex.Matcher
import java.util.regex.Pattern

class DecimalDigitsInputFilter(val digitsBeforeZero: Int, val digitsAfterZero: Int) : InputFilter {
    internal var mPattern: Pattern

    init {
        mPattern =
            Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {


        if (source.equals(".")) { // for dot
            val keep = digitsBeforeZero - (dest.length - (dend - dstart))
            var digitsBefore = digitsBeforeZero
            if (keep <= 0) {
                digitsBefore = digitsBeforeZero + 1
            }
            mPattern =
                Pattern.compile("[0-9]{0," + (digitsBefore - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
        } else if (source.isEmpty()) {
            mPattern =
                Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
        }

        val matcher = mPattern.matcher(dest)
        return if (!matcher.matches()) "" else null

    }
}
