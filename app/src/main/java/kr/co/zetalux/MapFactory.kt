package kr.co.zetalux

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.zetalux.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker

class MapFactory(private val context: Context, private val map: GoogleMap) {
    private val latLngBounds = LatLngBounds(
        LatLng(33.10000000, 125.06666667),  // SW
        LatLng(38.45000000, 131.87222222)   // NE
    )

    fun setGoogleMapUi() {
        // 빌딩 활성화
        map.isBuildingsEnabled = true
        // myMap.uiSettings. 으로 시작하는 세팅
        map.uiSettings.run {
            isZoomControlsEnabled = false
            isZoomGesturesEnabled = true
            isMyLocationButtonEnabled = true
            isScrollGesturesEnabled = true
            // 맵 메뉴
            isMapToolbarEnabled = false
            // 기울이기 제스쳐
            isTiltGesturesEnabled = true
        }
    }

    fun setMapVisibility(minZoom: Float, maxZoom: Float) {
        // 줌 범위 지정
        map.setMinZoomPreference(minZoom)
        map.setMaxZoomPreference(maxZoom)
        // 카메라 제한
        map.setLatLngBoundsForCameraTarget(latLngBounds)
    }

    // 맵 스타일
    // R.raw.map_style 에 json 으로 정의 됨
    fun setMapStyle() {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e("setMapStyle_ERROR", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("setMapStyle_Resources.NotFoundException_ERROR", "Can't find style. Error: ", e)
        }
    }

    fun setSnippet() =
        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            // 맵 상에 정보를 담은 윈도우를 호출 시...
            // 뷰를 생성하면 마커의 snippet 디자인 변경 가능
            // null 일 시에, 기본형태로 보여줌
            override fun getInfoWindow(marker: Marker): View? = null

            @SuppressLint("SetTextI18n")
            override fun getInfoContents(marker: Marker): View {
                val title = TextView(context).run {
                    setTextColor(Color.BLACK)
                    gravity = Gravity.CENTER
                    setTypeface(null, Typeface.BOLD)
                    text = marker.title
                    return@run this
                }
                val snippet = TextView(context).run {
                    setTextColor(Color.GRAY)
                    gravity = Gravity.START
                    text = "\n" + marker.snippet
                    return@run this
                }
                return LinearLayout(context).run {
                    orientation = LinearLayout.VERTICAL
                    addView(title)
                    addView(snippet)
                    return@run this
                }
            }
        })
}