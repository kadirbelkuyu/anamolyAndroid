package com.anamoly.view.user_account

import Config.BaseURL
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.view.user_account.adapter.SituationAdapter
import com.thekhaeng.pushdownanim.PushDownAnim
import com.xiaofeng.flowlayoutmanager.Alignment
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import kotlinx.android.synthetic.main.activity_add_edit_situation.*
import org.json.JSONObject
import utils.*
import java.util.*
import kotlin.collections.ArrayList

class AddEditSituationActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var situationAdapterAdult: SituationAdapter
    lateinit var situationAdapterChild: SituationAdapter
    lateinit var situationAdapterDog: SituationAdapter
    lateinit var situationAdapterCat: SituationAdapter

    var adultCount: Int = 1
    var childCount: Int = 1
    var dogCount: Int = 1
    var catCount: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePrefs(this)
        CommonActivity.setStatusAndHeaderColor(this)
        setContentView(R.layout.activity_add_edit_situation)

        PushDownAnim.setPushDownAnimTo(btn_add_edit_situation_save)

        val family = SessionManagement.UserData.getSession(this, "family")
        if (family.isNotEmpty() && family != "null") {
            val jsonObject = JSONObject(family)
            adultCount = jsonObject.getString("no_of_adults").toInt()
            childCount = jsonObject.getString("no_of_child").toInt()
            dogCount = jsonObject.getString("no_of_dogs").toInt()
            catCount = jsonObject.getString("no_of_cats").toInt()
        }

        situationAdapterAdult = SituationAdapter(this, R.drawable.ic_adult, adultCount)
        situationAdapterChild = SituationAdapter(this, R.drawable.ic_child, childCount)
        situationAdapterDog = SituationAdapter(this, R.drawable.ic_dog, dogCount)
        situationAdapterCat = SituationAdapter(this, R.drawable.ic_cat, catCount)

        updateCounter()

        rv_add_edit_situation_adult.apply {
            val flowLayoutManager = FlowLayoutManager()
            flowLayoutManager.isAutoMeasureEnabled = true
            if (LanguagePrefs.getLang(this@AddEditSituationActivity).equals("ar")) {
                flowLayoutManager.setAlignment(Alignment.RIGHT)
            } else {
                flowLayoutManager.setAlignment(Alignment.LEFT)
            }
            layoutManager = flowLayoutManager
            adapter = situationAdapterAdult
        }

        rv_add_edit_situation_child.apply {
            val flowLayoutManager = FlowLayoutManager()
            flowLayoutManager.isAutoMeasureEnabled = true
            if (LanguagePrefs.getLang(this@AddEditSituationActivity).equals("ar")) {
                flowLayoutManager.setAlignment(Alignment.RIGHT)
            } else {
                flowLayoutManager.setAlignment(Alignment.LEFT)
            }
            layoutManager = flowLayoutManager
            adapter = situationAdapterChild
        }

        rv_add_edit_situation_dog.apply {
            val flowLayoutManager = FlowLayoutManager()
            flowLayoutManager.isAutoMeasureEnabled = true
            if (LanguagePrefs.getLang(this@AddEditSituationActivity).equals("ar")) {
                flowLayoutManager.setAlignment(Alignment.RIGHT)
            } else {
                flowLayoutManager.setAlignment(Alignment.LEFT)
            }
            layoutManager = flowLayoutManager
            adapter = situationAdapterDog
        }

        rv_add_edit_situation_cat.apply {
            val flowLayoutManager = FlowLayoutManager()
            flowLayoutManager.isAutoMeasureEnabled = true
            if (LanguagePrefs.getLang(this@AddEditSituationActivity).equals("ar")) {
                flowLayoutManager.setAlignment(Alignment.RIGHT)
            } else {
                flowLayoutManager.setAlignment(Alignment.LEFT)
            }
            layoutManager = flowLayoutManager
            adapter = situationAdapterCat
        }

        iv_add_edit_situation_adult_remove.setOnClickListener(this)
        iv_add_edit_situation_adult_add.setOnClickListener(this)
        iv_add_edit_situation_child_remove.setOnClickListener(this)
        iv_add_edit_situation_child_add.setOnClickListener(this)
        iv_add_edit_situation_dog_remove.setOnClickListener(this)
        iv_add_edit_situation_dog_add.setOnClickListener(this)
        iv_add_edit_situation_cat_remove.setOnClickListener(this)
        iv_add_edit_situation_cat_add.setOnClickListener(this)
        btn_add_edit_situation_save.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_add_edit_situation_adult_remove -> {
                if (adultCount > 1) {
                    adultCount--
                    situationAdapterAdult.total = adultCount
                    situationAdapterAdult.notifyDataSetChanged()
                }
            }
            R.id.iv_add_edit_situation_adult_add -> {
                adultCount++
                situationAdapterAdult.total = adultCount
                situationAdapterAdult.notifyDataSetChanged()
            }
            R.id.iv_add_edit_situation_child_remove -> {
                if (childCount > 0) {
                    childCount--
                    situationAdapterChild.total = childCount
                    situationAdapterChild.notifyDataSetChanged()
                }
            }
            R.id.iv_add_edit_situation_child_add -> {
                childCount++
                situationAdapterChild.total = childCount
                situationAdapterChild.notifyDataSetChanged()
            }
            R.id.iv_add_edit_situation_dog_remove -> {
                if (dogCount > 0) {
                    dogCount--
                    situationAdapterDog.total = dogCount
                    situationAdapterDog.notifyDataSetChanged()
                }
            }
            R.id.iv_add_edit_situation_dog_add -> {
                dogCount++
                situationAdapterDog.total = dogCount
                situationAdapterDog.notifyDataSetChanged()
            }
            R.id.iv_add_edit_situation_cat_remove -> {
                if (catCount > 0) {
                    catCount--
                    situationAdapterCat.total = catCount
                    situationAdapterCat.notifyDataSetChanged()
                }
            }
            R.id.iv_add_edit_situation_cat_add -> {
                catCount++
                situationAdapterCat.total = catCount
                situationAdapterCat.notifyDataSetChanged()
            }
            R.id.btn_add_edit_situation_save -> {
                /*Intent().apply {
                    putExtra("adultCount", adultCount)
                    putExtra("childCount", childCount)
                    putExtra("dogCount", dogCount)
                    putExtra("catCount", catCount)
                    setResult(Activity.RESULT_OK, this)
                    finish()
                }*/
                if (ConnectivityReceiver.isConnected) {
                    makeAddEditFamily(
                        adultCount.toString(),
                        childCount.toString(),
                        dogCount.toString(),
                        catCount.toString()
                    )
                } else {
                    ConnectivityReceiver.showSnackbar(this)
                }
            }
        }
        updateCounter()
    }

    private fun updateCounter() {
        tv_add_edit_situation_adult.text = "${resources.getString(R.string.adults)} - $adultCount"
        tv_add_edit_situation_child.text = "${resources.getString(R.string.child)} - $childCount"
        tv_add_edit_situation_dog.text = "${resources.getString(R.string.dogs)} - $dogCount"
        tv_add_edit_situation_cat.text = "${resources.getString(R.string.cat)} - $catCount"
    }

    private fun makeAddEditFamily(
        no_of_adults: String, no_of_child: String,
        no_of_dogs: String, no_of_cats: String
    ) {
        val params = ArrayList<NameValuePair>()
        params.add(
            NameValuePair(
                "user_id",
                SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
            )
        )
        params.add(NameValuePair("no_of_adults", no_of_adults))
        params.add(NameValuePair("no_of_child", no_of_child))
        params.add(NameValuePair("no_of_dogs", no_of_dogs))
        params.add(NameValuePair("no_of_cats", no_of_cats))

        val task = CommonAsyTask(
            BaseURL.POST, params,
            BaseURL.ADD_EDIT_FAMILY_URL, object : CommonAsyTask.VJsonResponce {
                override fun VResponce(responce: String, message: String) {

                    SessionManagement.UserData.setSession(
                        this@AddEditSituationActivity,
                        "family",
                        responce
                    )

                    Intent().apply {
                        setResult(Activity.RESULT_OK, this)
                        finish()
                    }
                }

                override fun VError(responce: String, code: String) {
                    CommonActivity.showToast(this@AddEditSituationActivity, responce)
                }
            }, BaseURL.PROGRESSDIALOG, this
        )
        task.execute()
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(LanguagePrefs.getLang(newBase!!)!!)
        // .. create or get your new Locale object here.
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

}
