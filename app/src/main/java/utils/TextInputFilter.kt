package utils

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.Log
import java.util.regex.Pattern


class TextInputFilter : InputFilter {

    private var blockCharacterSet = "[a-zA-Z ]+"
    private var TextLength = -1
    private var selectedFilter: Filter? = null

    enum class Filter {
        ONLY_ALPHABETS,
        ALPHA_NUMERIC,
        ALPHA_NUMERIC_WITHOUT_SPACE,
        ALPHA_NUMERIC_FOR_ID,
        ATM_NUMERICS,
        ONLY_NUMERIC,
        ONLY_NUMERIC_WITH_DOT,
        ALPHA_NUMERIC_WITH_SPECIAL_CHAR,
        NOT_ALLOW_SPECIAL_CHARS,
        NOT_ALLOW_SPACE,
        NOT_ALLOW_ARABIC_NUMBER,
        ALPHA_NUMERIC_WITH_ARABIC,
        ALLOW_EMAIL_ONLY,
        NOT_ALLOW_SPACE_AND_SPECIAL_CHARS
    }

    constructor(filter: Filter) {
        setFilter(filter)
    }

    fun setFilter(filter: Filter) {
        selectedFilter = filter
        if (filter === Filter.ALPHA_NUMERIC) {
            blockCharacterSet = "[a-zA-Z 0-9 ]+"
        } else if (filter == Filter.ALPHA_NUMERIC_WITHOUT_SPACE) {
            blockCharacterSet = "[a-zA-Z0-9]+"
        } else if (filter == Filter.ALPHA_NUMERIC_FOR_ID) {
            blockCharacterSet = "[a-zA-Z0-9.@_-]+"
        } else if (filter == Filter.NOT_ALLOW_SPECIAL_CHARS) {
            blockCharacterSet = "[()/|?,;:'~<>\\\\+=.[]{}]+"
        } else if (filter == Filter.NOT_ALLOW_SPACE) {
            blockCharacterSet = "[ ]+"
        } else if (filter == Filter.ATM_NUMERICS) {
            blockCharacterSet = "[0-9-]+"
        } else if (filter == Filter.ONLY_ALPHABETS) {
            blockCharacterSet = "[a-zA-Z ]+"
        } else if (filter == Filter.ONLY_NUMERIC) {
            blockCharacterSet = "[0-9]+"
        } else if (filter == Filter.ONLY_NUMERIC_WITH_DOT) {
            blockCharacterSet = "[0-9.]+"
        } else if (filter == Filter.ALPHA_NUMERIC_WITH_SPECIAL_CHAR) {
            blockCharacterSet = "[a-zA-Z 0-9!#Â¤%&/()=+?@Â£\${}\\,.;:-_|<>]+"
        } else if (filter == Filter.NOT_ALLOW_ARABIC_NUMBER) {
            blockCharacterSet = "[٠١٢٣٤٥٦٧٨٩]+"
        } else if (filter == Filter.ALPHA_NUMERIC_WITH_ARABIC) {
            blockCharacterSet = "[\\u0621-\\u064A\\u0660-\\u0669a-zA-Z 0-9 ]+"
        } else if (filter == Filter.ALLOW_EMAIL_ONLY) {
            blockCharacterSet = "[a-zA-Z0-9!#$%&'*+-/=?^_.@`{|}~|<>]+"
        } else if (filter == Filter.NOT_ALLOW_SPACE_AND_SPECIAL_CHARS) {
            blockCharacterSet = "[ ()/|?,;:'~<>\\\\+=.[]{}]+"
        }

    }

    constructor(filter: Filter, textLength: Int) {
        TextLength = textLength
        setFilter(filter)
    }

    override fun filter(
        source: CharSequence, start: Int, end: Int,
        dest: Spanned?, dstart: Int, dend: Int
    ): CharSequence? {

        if (source == "") { // for backspace
            return source
        }

        if (TextLength != -1 && dest != null) {
            val keep = TextLength - (dest.length - (dend - dstart))
            if (keep <= 0) {
                Log.e("filter", "limitOver")
                return ""
            }
        }

        return if (source is SpannableStringBuilder) {
            for (i in end - 1 downTo start) {
                val currentChar = source[i]

                if (selectedFilter === Filter.NOT_ALLOW_SPECIAL_CHARS) {
                    if (!Character.isLetterOrDigit(currentChar)) {
                        source.delete(i, i + 1)
                    }
                } else if (selectedFilter == Filter.NOT_ALLOW_SPACE) {
                    if (Character.isWhitespace(currentChar)) {
                        source.delete(i, i + 1)
                    }
                } else if (selectedFilter == Filter.NOT_ALLOW_ARABIC_NUMBER) {
                    if (currentChar.toString().matches(blockCharacterSet.toRegex())) {
                        source.delete(i, i + 1)
                    }
                } else if (selectedFilter === Filter.NOT_ALLOW_SPACE_AND_SPECIAL_CHARS) {
                    if (!Character.isLetterOrDigit(currentChar)) {
                        source.delete(i, i + 1)
                    }
                } else {
                    if (!currentChar.toString().matches(blockCharacterSet.toRegex())) {
                        source.delete(i, i + 1)
                    }
                }
            }
            source
        } else {
            val filteredStringBuilder = StringBuilder()
            for (i in start until end) {
                val currentChar = source[i]

                if (selectedFilter == Filter.NOT_ALLOW_SPECIAL_CHARS) {
                    if (Character.isLetterOrDigit(currentChar)) {
                        filteredStringBuilder.append(currentChar)
                    }
                } else if (selectedFilter == Filter.NOT_ALLOW_SPACE) {
                    if (!Character.isWhitespace(currentChar)) {
                        filteredStringBuilder.append(currentChar)
                    }
                } else if (selectedFilter == Filter.NOT_ALLOW_ARABIC_NUMBER) {
                    if (!currentChar.toString().matches(blockCharacterSet.toRegex())) {
                        filteredStringBuilder.append(currentChar)
                    }
                } else if (selectedFilter == Filter.NOT_ALLOW_SPACE_AND_SPECIAL_CHARS) {
                    if (Character.isLetterOrDigit(currentChar)) {
                        filteredStringBuilder.append(currentChar)
                    }
                } else {
                    if (currentChar.toString().matches(blockCharacterSet.toRegex())) {
                        filteredStringBuilder.append(currentChar)
                    }
                }
            }

            filteredStringBuilder.toString()
        }

    }

}
