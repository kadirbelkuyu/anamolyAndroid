package CustomViews

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.anamoly.AppController
import com.anamoly.R

class TextViewMedium : TextView {

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
        val tf = Typeface.createFromAsset(context.assets, "fonts/Tajawal-Medium.ttf")
        typeface = tf

        when (currentTextColor) {
            ContextCompat.getColor(context, R.color.colorPrimary) -> {
                setTextColor(
                    AppController.decorativeTextOne
                )
            }
            ContextCompat.getColor(context, R.color.colorOrange) -> {
                setTextColor(
                    AppController.decorativeTextTwo
                )
            }
            ContextCompat.getColor(context, R.color.colorText) -> {
                setTextColor(
                    AppController.defaultTextColor
                )
            }
        }

        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.TextViewMedium, 0, 0)

            if (a.getBoolean(R.styleable.TextViewMedium_allowBG, false)) {
                val gradientDrawable = GradientDrawable()
                gradientDrawable.cornerRadius =
                    a.getDimension(R.styleable.TextViewMedium_cornerRadius, 5F)
                gradientDrawable.setColor(AppController.secondButtonColor)
                background = gradientDrawable
            }
        }
    }

}
