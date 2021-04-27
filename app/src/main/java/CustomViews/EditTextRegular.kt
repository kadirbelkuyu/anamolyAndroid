package CustomViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.EditText

class EditTextRegular : EditText {

    internal var mOriginalLeftPadding = -1f

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val tf = Typeface.createFromAsset(context.assets, "fonts/Tajawal-Regular.ttf")
        typeface = tf
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (tag != null) {
            calculatePrefix()
        }
    }

    private fun calculatePrefix() {
        if (mOriginalLeftPadding == -1f) {
            val prefix = tag as String
            val widths = FloatArray(prefix.length)
            paint.getTextWidths(prefix, widths)
            var textWidth = 0f
            for (w in widths) {
                textWidth += w
            }
            mOriginalLeftPadding = compoundPaddingLeft.toFloat()
            setPadding(
                (textWidth + mOriginalLeftPadding).toInt(),
                paddingRight, paddingTop,
                paddingBottom
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (tag != null) {
            val prefix = tag as String
            canvas.drawText(
                prefix, mOriginalLeftPadding,
                getLineBounds(0, null).toFloat(), paint
            )
        }
    }

}
