package kr.co.zetalux.androidgooglemap

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.zetalux.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapMarker(private val activity: Activity, private val map: GoogleMap) {

    // https://developers.google.com/android/reference/com/google/android/gms/maps/model/Marker?hl=ko
    fun setMarker(title: String, content: String, latLng: LatLng) {
        val marker = MarkerOptions()
            .position(latLng)
            .title(title)
            .snippet(content)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
        // 마커 추가
        map.addMarker(marker)
    }

    // 마커 스니펫 세팅
    // https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter
    fun setSnippet() =
        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            // 맵 상에 정보를 담은 윈도우를 호출 시...
            // 뷰를 생성하면 마커의 snippet 디자인 변경 가능
            // null 일 시에, 기본형태로 보여줌
            override fun getInfoWindow(marker: Marker): View? = null

            @SuppressLint("SetTextI18n")
            override fun getInfoContents(marker: Marker): View {
                val title = TextView(activity).run {
                    setTextColor(Color.BLACK)
                    gravity = Gravity.CENTER
                    setTypeface(null, Typeface.BOLD)
                    text = marker.title
                    return@run this
                }
                val snippet = TextView(activity).run {
                    setTextColor(Color.GRAY)
                    gravity = Gravity.START
                    text = "\n" + marker.snippet
                    return@run this
                }
                return LinearLayout(activity).run {
                    orientation = LinearLayout.VERTICAL
                    addView(title)
                    addView(snippet)
                    return@run this
                }
            }
        })

}