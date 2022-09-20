package kr.co.zetalux.androidgooglemap

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*


class MapSetting(private val context: Context, private val map: GoogleMap) {

    // 구글 맵에서 제공하는 UI 세팅
    // https://developers.google.com/maps/documentation/android-sdk/controls?hl=ko
    fun setGoogleMapUi() {
//        map.isBuildingsEnabled = true
//        map.isIndoorEnabled = true
//        map.isTrafficEnabled = true

        // myMap.uiSettings. 으로 시작하는 세팅
        // https://developers.google.com/android/reference/com/google/android/gms/maps/UiSettings?hl=ko
        map.uiSettings.run {
            isZoomControlsEnabled = false
            isCompassEnabled = true
            isIndoorLevelPickerEnabled = true
            isZoomGesturesEnabled = true
            isMyLocationButtonEnabled = true
            isScrollGesturesEnabled = true
            isRotateGesturesEnabled = true
            // 맵 메뉴
            isMapToolbarEnabled = true
            // 기울이기 제스쳐
            isTiltGesturesEnabled = true
        }
    }

    // 카메라 줌 및 범위 지정
    // https://developers.google.com/maps/documentation/android-sdk/views?hl=ko
    fun setMapVisibility(minZoom: Float, maxZoom: Float) {
        val latLngBounds = LatLngBounds(
            LatLng(33.10000000, 125.06666667),  // SW
            LatLng(38.45000000, 131.87222222)   // NE
        )
        // 줌 범위 지정
        map.setMinZoomPreference(minZoom)
        map.setMaxZoomPreference(maxZoom)
        // 카메라 제한
        map.setLatLngBoundsForCameraTarget(latLngBounds)
    }

    // 맵 스타일
    // https://developers.google.com/maps/documentation/android-sdk/styling?hl=ko
    // R.raw.map_style 에 json 으로 정의 됨
    fun setMapStyle() {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    com.example.zetalux.R.raw.map_style
                )
            )
            if (!success) {
                Log.e("setMapStyle_ERROR", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("setMapStyle_Resources.NotFoundException_ERROR", "Can't find style. Error: ", e)
        }
    }

}