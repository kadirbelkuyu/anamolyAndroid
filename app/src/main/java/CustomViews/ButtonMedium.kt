package CustomViews

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.Button
import androidx.core.content.ContextCompat
import com.anamoly.AppController
import com.anamoly.R
import utils.SessionManagement

class ButtonMedium : Button {

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

        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ButtonMedium, 0, 0)

            val textColor = if (a.getInt(R.styleable.ButtonMedium_buttonType, 1) == 1)
                AppController.buttonTextColor
            else
                AppController.secondButtonTextColor

            val bgColor = if (a.getInt(R.styleable.ButtonMedium_buttonType, 1) == 1)
                AppController.buttonColor
            else
                AppController.secondButtonColor

            setTextColor(textColor)

            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius =
                a.getDimension(R.styleable.ButtonBold_cornerRadius, 5F)

            if (a.getBoolean(R.styleable.ButtonBold_allowStock, false)) {
                gradientDrawable.setStroke(
                    a.getDimension(R.styleable.ButtonBold_strokeWidth, 1F).toInt(),
                    bgColor
                )
                gradientDrawable.setColor(
                    a.getColor(
                        R.styleable.ButtonBold_android_solidColor,
                        ContextCompat.getColor(context, R.color.colorWhite)
                    )
                )
            } else {
                gradientDrawable.setColor(bgColor)
            }
            if (!a.getBoolean(R.styleable.ButtonMedium_disableOnline, false)) {
                background = gradientDrawable
            }

        }
    }

}
