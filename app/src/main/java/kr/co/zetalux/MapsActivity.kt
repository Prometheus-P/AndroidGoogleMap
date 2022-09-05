package kr.co.zetalux

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.zetalux.R
import com.example.zetalux.databinding.DialogMarkerPopBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var myMap: GoogleMap

    // 제일 처음 호출
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        // 맵 조작 ui
        val factory = MapFactory(this, myMap)
        with(factory){
            setGoogleMapUi()
            setMapVisibility(minZoom = 10.0f, maxZoom =22.0f)
            setMapStyle()
            setSnippet()
        }
        // 맵 길게 클릭 시 이벤트 설정
        setMapLongClick(myMap)

        setPoiClick(myMap)

        // 권한 확인
        if (isPermissionGranted()) {
            // 내 위치 정보 접근 권한 있으면 실행
            enableMyLocation(myMap)
        } else {
            // 내 위치 정보 접근 권한 없으면 실행
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    // 우측 상단 메뉴 버튼
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            myMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            myMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            myMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            myMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setMapLongClick(map: GoogleMap) {
        // 다이얼로그 틀
        val markerDialog = AlertDialog.Builder(this).create()
        // 다이얼로그 내용
        val dialogBinding = DialogMarkerPopBinding.inflate(layoutInflater)
        // 틀에 내용 얹기
        markerDialog.setView(dialogBinding.root);
        // 외부 터치 시, 다이얼로그 꺼지는 것 막기
        markerDialog.setCancelable(false)

        map.setOnMapLongClickListener { latLng ->
            // btnConfirm 을 클릭 했을 때, 발생시킬 이벤트
            dialogBinding.btnConfirm.setOnClickListener {
                val latlng: String = String.format(
                    Locale.getDefault(),
                    "Lat: %1$.5f, Long: %2$.5f",
                    latLng.latitude,
                    latLng.longitude
                )
                val detail: String = dialogBinding.txtMarkerDetail.text.toString()
                // 마커 추가
                map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(dialogBinding.txtMarkerTitle.text.toString())
                        .snippet(latlng + "\n" + detail)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_ic))
                )
                // 다이얼로그 내리기
                markerDialog.dismiss()
                // 리셋
                dialogBinding.txtMarkerTitle.text = null
                dialogBinding.txtMarkerDetail.text = null
            }
            // btnCancel 을 클릭 했을 때, 발생시킬 이벤트
            dialogBinding.btnCancel.setOnClickListener {
                markerDialog.dismiss()
                dialogBinding.txtMarkerTitle.text = null
                dialogBinding.txtMarkerDetail.text = null
            }
            // 다이얼로그 보여주기
            markerDialog.show()
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        // myMap 상의 poi 를 클릭 했을 때, 발생시킬 이벤트
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
                    findViewById(R.id.content),
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

    /*
    private fun isPermissionGranted(): Boolean =
        (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
                &&
                (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    */

    // 권한이 허용 되었는지 확인
    private fun isPermissionGranted(): Boolean {
        val checker = fun(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        val isFineGranted = checker(Manifest.permission.ACCESS_FINE_LOCATION)

//        val isFineGranted = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED

        val isCoarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return isFineGranted && isCoarseGranted
    }


    @SuppressLint("MissingPermission")
    private fun enableMyLocation(map: GoogleMap?) {
        // 파라미터가 널 값일 경우
        val myMap = map ?: this.myMap
        val mLocMan = getSystemService(LOCATION_SERVICE) as LocationManager
        if (mLocMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            myMap.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            // 마지막 위치를 가져오는 걸 성공 하면 실행
            fusedLocationClient.lastLocation.addOnSuccessListener { it ->
                val latlng = LatLng(it.latitude, it.longitude)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15f)
                myMap.moveCamera(cameraUpdate)
            }
        } else {
            Toast.makeText(this, "내 위치 확인을 위해 GPS를 켜주시기 바랍니다.", Toast.LENGTH_SHORT).show()
        }
    }


    // ActivityCompat.requestPermissions 으로 권한을 질문 하고,
    // 대답을 받는 곳
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation(null)
            }
        }
    }
}
