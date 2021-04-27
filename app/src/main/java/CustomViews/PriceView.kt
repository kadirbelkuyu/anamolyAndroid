package CustomViews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.anamoly.R

/**
 * Created on 08-04-2020.
 */
class PriceView : LinearLayout {

    var isPrintLog: Boolean = false

    var tv_price: TextView? = null
    var tv_price_dot: TextView? = null

    var contexts: Context? = null
    var attributeSet: TypedArray? = null

    constructor(context: Context?) : super(context) {
        init(context!!, null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context!!, attrs!!)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.include_price, this)

        contexts = context
        attributeSet = context.theme.obtainStyledAttributes(attrs, R.styleable.PriceView, 0, 0)

        initComponents()
    }

    private fun initComponents() {
        val linearLayout = findViewById<View>(R.id.cl_price) as ConstraintLayout
        tv_price = findViewById<View>(R.id.tv_price) as TextView
        tv_price_dot = findViewById<View>(R.id.tv_price_dot) as TextView

        if (attributeSet != null) {
            try {
                setTextColor(
                    attributeSet!!.getColor(
                        R.styleable.PriceView_android_textColor,
                        ContextCompat.getColor(context, R.color.colorText)
                    )
                )
                setText(attributeSet!!.getString(R.styleable.PriceView_android_text)!!)
                setTextSize(attributeSet!!.getDimension(R.styleable.PriceView_priceSize, 14F))
                setTextSize2(attributeSet!!.getDimension(R.styleable.PriceView_priceSize2, 10F))
                setDotMargin(
                    attributeSet!!.getDimension(
                        R.styleable.PriceView_dotMargin,
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            -3F,
                            context.resources.displayMetrics
                        )
                    )
                )
                val fontFamiliy = attributeSet!!.getInt(R.styleable.PriceView_priceFontFamily, 2)
                when (fontFamiliy) {
                    1 -> setFontFamily(
                        Typeface.createFromAsset(
                            context.assets,
                            "fonts/Tajawal-Regular.ttf"
                        )
                    )
                    2 -> setFontFamily(
                        Typeface.createFromAsset(
                            context.assets,
                            "fonts/Tajawal-Medium.ttf"
                        )
                    )
                    3 -> setFontFamily(
                        Typeface.createFromAsset(
                            context.assets,
                            "fonts/Tajawal-Bold.ttf"
                        )
                    )
                }

                val visible =
                    attributeSet!!.getBoolean(R.styleable.PriceView_android_visibility, true)

                linearLayout.visibility = if (visible) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            } finally {
                attributeSet!!.recycle()
            }
        }

    }

    fun setText(text: String) {
        printLog("Original Text::$text")
        val discountPrice = text.split(".")
        if (discountPrice.size > 1) {
            printLog("First Text::${discountPrice[0]}")
            tv_price?.text = "${discountPrice[0]}."
            tv_price_dot?.text = ""
            if (discountPrice[1] != "00") {
                printLog("Second Text::${discountPrice[1]}")
                tv_price_dot?.text = discountPrice[1]
            }
        } else {
            tv_price?.text = discountPrice[0]
            tv_price_dot?.text = ""
            printLog("First Text::${discountPrice[0]}")
        }
    }

    fun setTextColor(color: Int) {
        tv_price?.setTextColor(color)
        tv_price_dot?.setTextColor(color)
    }

    fun setTextSize(size: Float) {
        tv_price?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    fun setTextSize2(size: Float) {
        tv_price_dot?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    fun setDotMargin(margin: Float) {
        tv_price_dot?.translationX = margin
    }

    fun setFontFamily(tf: Typeface?) {
        tv_price?.typeface = tf
        tv_price_dot?.typeface = tf
    }

    fun printLog(data: String) {
        if (isPrintLog) {
            Log.d("PriceView", data)
        }
    }

}