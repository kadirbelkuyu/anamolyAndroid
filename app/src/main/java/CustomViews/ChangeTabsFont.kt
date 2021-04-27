package CustomViews

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

class ChangeTabsFont(context: Context, tabLayout: TabLayout, font: String) {

    init {
        val vg = tabLayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (i in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.setTypeface(
                        Typeface.createFromAsset(
                            context.assets,
                            font
                        )
                    )
                    //tabViewChild.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8F)
                }
            }
        }
    }

}