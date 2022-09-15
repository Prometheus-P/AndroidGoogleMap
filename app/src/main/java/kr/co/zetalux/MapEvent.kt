package kr.co.zetalux

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.example.zetalux.R
import com.example.zetalux.databinding.DialogMarkerPopBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MapEvent(private val activity: Activity, private val map: GoogleMap) {

    // lateinit var : 늦게 initialize
    private lateinit var mapMarker: MapMarker

    // 다이얼로그 내용
    // https://developer.android.com/topic/libraries/view-binding?hl=ko
    private val dialogBinding by lazy { DialogMarkerPopBinding.inflate(activity.layoutInflater) }

    // 다이얼로그 틀
    // by lazy : 사용될 때, 한번 실행됨
    private val markerDialog by lazy {
        // https://developer.android.com/guide/topics/ui/dialogs?hl=ko
        val dialog = AlertDialog.Builder(activity).create()
        // 틀에 내용 얹기
        dialog.setView(dialogBinding.root);
        // 외부 터치 시, 다이얼로그 꺼지는 것 막기
        dialog.setCancelable(false)

        dialog
    }

    fun setMapLongClick() {
        mapMarker = MapMarker(activity, map)
        // 마커의 스니펫 설정
        mapMarker.setSnippet()
        // marker snippet longclick 시 삭제 여부 질문
        setMarkerDelete()
        // https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnMapLongClickListener?hl=ko
        map.setOnMapLongClickListener { latLng ->
            // 리셋
            markerCreateDialog(latLng = latLng)
        }
    }

    private fun markerCreateDialog(latLng: LatLng) {
        dialogBinding.txtMarkerTitle.text = null
        dialogBinding.txtMarkerDetail.text = null
        // btnConfirm 을 클릭 했을 때, 발생시킬 이벤트
        dialogBinding.btnConfirm.setOnClickListener {
            val title = dialogBinding.txtMarkerTitle.text.toString()
            val content = dialogBinding.txtMarkerDetail.text.toString()
            mapMarker.setMarker(title = title, content = content, latLng = latLng)
            // 다이얼로그 내리기
            markerDialog.dismiss()
        }
        // btnCancel 을 클릭 했을 때, 발생시킬 이벤트
        dialogBinding.btnCancel.setOnClickListener {
            // 다이얼로그 내리기
            markerDialog.dismiss()
        }
        // 다이얼로그 보여주기
        markerDialog.show()
    }

    private fun markerModDialog(marker: Marker) {
        dialogBinding.btnConfirm.setOnClickListener {
            marker.title = dialogBinding.txtMarkerTitle.text.toString()
            marker.snippet = dialogBinding.txtMarkerDetail.text.toString()

            // 다이얼로그 내리기
            markerDialog.dismiss()
            dialogBinding.txtMarkerTitle.text = null
            dialogBinding.txtMarkerDetail.text = null
        }
        // btnCancel 을 클릭 했을 때, 발생시킬 이벤트
        dialogBinding.btnCancel.setOnClickListener {
            val deleteDialog = AlertDialog.Builder(activity).run {
                setMessage("마커를 삭제 하시겠습니까?")
                setPositiveButton("삭제") { dialog, _ ->
                    marker.remove()
                    dialog.dismiss()
                    markerDialog.dismiss()
                }
                setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                    markerDialog.dismiss()
                }
            }
            deleteDialog.show()
        }
        // 다이얼로그 보여주기
        markerDialog.show()
    }

    fun setPoiClick() {
        // myMap 상의 poi 를 클릭 했을 때, 발생시킬 이벤트
        // https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnPoiClickListener?hl=ko
        map.setOnPoiClickListener { poi ->
            val latlng: String = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                poi.latLng.latitude,
                poi.latLng.longitude
            )
            // Snackbar
            // https://material.io/develop/android
            val snackbar =
                Snackbar.make(
                    activity.findViewById(R.id.content),
                    poi.name + "\n" + latlng,
                    Snackbar.LENGTH_INDEFINITE
                )
            // 최대 허용 줄 수
            snackbar.setTextMaxLines(2)
            // Snackbar 우측 버튼
            snackbar.setAction("확인") {
                // 지우기
                snackbar.dismiss()
            }
            // 보이기
            snackbar.show();
        }
    }

    // https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnInfoWindowClickListener?hl=ko
    private fun setMarkerDelete() = map.setOnInfoWindowLongClickListener { marker ->
        dialogBinding.txtMarkerTitle.setText(marker.title ?: "")
        dialogBinding.txtMarkerDetail.setText(marker.snippet ?: "")
        markerModDialog(marker)

    }

}