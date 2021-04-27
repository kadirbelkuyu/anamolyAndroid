package utils

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan

/**
 * Created on 14-02-2020.
 */
object StringsHelper {
    fun highlightLCS(
        text1: String, text2: String,
        highlightColor: Int
    ): SpannableStringBuilder {
        var lcs = lcs(text1.toLowerCase(), text2.toLowerCase())
        val tLower = text1.toLowerCase()
        val builder = SpannableStringBuilder()
        builder.append(text1)
        var i = 0
        while (i < tLower.length && lcs.length > 0) {
            if (tLower[i] == lcs[0]) {
                builder.setSpan(ForegroundColorSpan(highlightColor), i, i + 1, 0)
                lcs = lcs.substring(1)
            }
            i++
        }
        return builder
    }

    fun lcs(text1: String, text2: String): String {
        val lengths =
            Array(text1.length + 1) { IntArray(text2.length + 1) }
        for (i in 0 until text1.length) {
            for (j in 0 until text2.length) {
                if (text1[i] == text2[j]) {
                    lengths[i + 1][j + 1] = lengths[i][j] + 1
                } else {
                    lengths[i + 1][j + 1] =
                        Math.max(lengths[i + 1][j], lengths[i][j + 1])
                }
            }
        }
        val sb = StringBuffer()
        var x = text1.length
        var y = text2.length
        while (x != 0 && y != 0) {
            if (lengths[x][y] == lengths[x - 1][y]) {
                x--
            } else if (lengths[x][y] == lengths[x][y - 1]) {
                y--
            } else {
                assert(text1[x - 1] == text2[y - 1])
                sb.append(text1[x - 1])
                x--
                y--
            }
        }
        return sb.reverse().toString()
    }
}