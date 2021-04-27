package com.anamoly.view.order_tracking

import Config.BaseURL
import Dialogs.LoaderDialog
import Models.OrderModel
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.OrderDetailResponse
import kotlinx.android.synthetic.main.activity_track_order.*
import org.json.JSONObject
import utils.ConnectivityReceiver
import utils.DrawRoute
import utils.SessionManagement

class TrackOrderActivity : CommonActivity(), OnMapReadyCallback {

    companion object {
        val TAG = TrackOrderActivity::class.java.simpleName
    }

    private val mMarkers: HashMap<String, Marker> = HashMap()
    private var lastLatLng: LatLng? = null
    private lateinit var mMap: GoogleMap

    lateinit var orderModel: OrderModel

    lateinit var trackOrderViewModel: TrackOrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackOrderViewModel = ViewModelProviders.of(this).get(TrackOrderViewModel::class.java)
        setContentView(R.layout.activity_track_order)
        setHeaderTitle(resources.getString(R.string.order_tracking))

        if (intent.hasExtra("orderData")) {
            orderModel = intent.getSerializableExtra("orderData") as OrderModel
            bindView()
        } else if (intent.hasExtra("order_id")) {
            if (ConnectivityReceiver.isConnected) {
                makeOrderDetail(intent.getStringExtra("order_id")!!)
            }
        }

    }

    private fun bindView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_tracking) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setDetail(orderModel.boy_name, "")
    }

    private fun makeOrderDetail(order_id: String) {
        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)
        params["order_id"] = order_id

        val loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        trackOrderViewModel.makeGetOrderDetail(params).observe(
            this,
            Observer { response: OrderDetailResponse? ->
                loaderDialog.dismiss()
                if (response != null) {
                    if (response.responce!!) {
                        orderModel = response.orderModel!!
                        bindView()
                    } else {
                        showToast(this@TrackOrderActivity, response.message!!)
                    }
                }
            })

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_truck)

        if (!orderModel.latitude.isNullOrEmpty() && !orderModel.longitude.isNullOrEmpty()) {
            val latLngStart =
                LatLng(orderModel.latitude!!.toDouble(), orderModel.longitude!!.toDouble())
            mMap.addMarker(
                MarkerOptions().position(latLngStart)
                    .title(resources.getString(R.string.delivery_location))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_user))
            ).showInfoWindow()
        }

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney").icon(icon))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        subscribeToUpdates()

    }

    private fun setDetail(driverName: String?, time: String) {
        if (time.contains("Minute", true)) {
            time.replace("Minute", resources.getString(R.string.minute))
        }
        if (time.contains("Minutes", true)) {
            time.replace("Minutes", resources.getString(R.string.minutes))
        }
        if (time.contains("Hour", true)
            || time.contains("Hours", true)
        ) {
            time.replace("Hour", resources.getString(R.string.hour))
            time.replace("Hours", resources.getString(R.string.hour))
        }

        val spannableStringBuilder = SpannableStringBuilder()
        if (!driverName.isNullOrEmpty()) {
            spannableStringBuilder.append(driverName)
            spannableStringBuilder.setSpan(
                ForegroundColorSpan(Color.RED),
                0,
                spannableStringBuilder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        spannableStringBuilder.append(" ${resources.getString(R.string.will_be_there_in)} ")
        if (time.isNotEmpty()) {
            spannableStringBuilder.append(time)
            spannableStringBuilder.setSpan(
                ForegroundColorSpan(Color.RED),
                (spannableStringBuilder.length - time.length),
                spannableStringBuilder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        tv_track_distance.text = spannableStringBuilder
    }

    private fun drawRoute(latLngStart: LatLng, latLngDestination: LatLng) {
        val loaderDialog = LoaderDialog(this)
        loaderDialog.show()

        val drawRoute = DrawRoute(this)
        val routeUrl = drawRoute.getDirectionsUrl(latLngStart, latLngDestination)
        drawRoute.DownloadTask(DrawRoute.OnRouteListener { response: String, pathList: List<HashMap<String, String>>, lineOptions ->
            //Log.d(TAG, "RouteLatLngListDATA::$pathList")

            loaderDialog.dismiss()

            if (lineOptions != null) {
                mMap.addPolyline(lineOptions)
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngDestination, 21F))

            var totalDistance = ""
            var totalDuration = ""

            val jsonObject = JSONObject(response)
            if (jsonObject.has("routes")) {
                val jsonArrayRoutes = jsonObject.getJSONArray("routes")
                for (route in 0 until jsonArrayRoutes.length()) {
                    if (jsonArrayRoutes.length() > 0 && jsonArrayRoutes.getJSONObject(route)
                            .has("legs")
                    ) {
                        val jsonArrayLegs =
                            jsonArrayRoutes.getJSONObject(route).getJSONArray("legs")
                        for (legs in 0 until jsonArrayLegs.length()) {
                            val jsonObjectLegs = jsonArrayLegs.getJSONObject(legs)
                            if (totalDistance.isEmpty()) {
                                val jsonObjectDistance = jsonObjectLegs.getJSONObject("distance")
                                val jsonObjectDuration = jsonObjectLegs.getJSONObject("duration")

                                totalDistance = jsonObjectDistance.getString("text")
                                totalDuration = jsonObjectDuration.getString("text")

                                setDetail(orderModel.boy_name, totalDuration)
                                break
                            }
                        }
                    }
                }
            }
        }).execute(routeUrl)
    }

    private fun subscribeToUpdates() {
        val path = "locations/${orderModel.order_id}"
        val ref: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(path)
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                dataSnapshot: DataSnapshot,
                previousChildName: String?
            ) {
                setMarker(dataSnapshot)
            }

            override fun onChildChanged(
                dataSnapshot: DataSnapshot,
                previousChildName: String?
            ) {
                setMarker(dataSnapshot)
            }

            override fun onChildMoved(
                dataSnapshot: DataSnapshot,
                previousChildName: String?
            ) {
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {
                Log.d(
                    TAG,
                    "Failed to read value.",
                    error.toException()
                )
            }
        })
    }

    val value = HashMap<String?, Any?>()

    private fun setMarker(dataSnapshot: DataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once

        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        val key = dataSnapshot.key
        //val value = dataSnapshot.value as HashMap<String, Any>
        value[key] = dataSnapshot.value
        if (value["latitude"] != null && value["longitude"] != null) {
            val lat = value["latitude"].toString().toDouble()
            val lng = value["longitude"].toString().toDouble()
            val location = LatLng(lat, lng)
            if (!mMarkers.containsKey("123")) {
                mMarkers["123"] = mMap.addMarker(
                    MarkerOptions().title(key).position(location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_truck))
                )
                if (value["bearing"] != null)
                    mMarkers["123"]!!.rotation = value["bearing"].toString().toFloat()
            } else {
                mMarkers["123"]!!.position = location
                if (value["bearing"] != null)
                    mMarkers["123"]!!.rotation = value["bearing"].toString().toFloat()
            }
            val builder: LatLngBounds.Builder = LatLngBounds.Builder()
            for (marker in mMarkers.values) {
                builder.include(marker.position)
            }
            if (lastLatLng == null) {
                lastLatLng = location
                if (!orderModel.latitude.isNullOrEmpty() && !orderModel.longitude.isNullOrEmpty()) {
                    val destinationLatLng =
                        LatLng(orderModel.latitude!!.toDouble(), orderModel.longitude!!.toDouble())
                    drawRoute(destinationLatLng, lastLatLng!!)
                }
            }
            //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300))
        }
    }

}
