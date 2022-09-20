package kr.co.zetalux.androidgooglemap

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

class DrawPoly(private val map: GoogleMap) :
    GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener {

    private val COLOR_WHITE_ARGB = -0x1
    private val COLOR_DARK_GREEN_ARGB = -0xc771c4
    private val COLOR_LIGHT_GREEN_ARGB = -0x7e387c
    private val COLOR_DARK_ORANGE_ARGB = -0xa80e9
    private val COLOR_LIGHT_ORANGE_ARGB = -0x657db
    private val COLOR_BLACK_ARGB = -0x1000000
    private val COLOR_BLACK = 0xff000000

    private val PATTERN_GAP_LENGTH_PX = 20F
    private val POLYLINE_STROKE_WIDTH_PX = 12F
    private val POLYGON_STROKE_WIDTH_PX = 8F
    private val PATTERN_DASH_LENGTH_PX = 20F

    // 획 패턴에 사용되는 점
    private val DOT: PatternItem = Dot()

    // 획 패턴에 사용되는 간격
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX)

    // 획 패턴에 사용되는 대시
    private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX)

    private val PATTERN_POLYGON: List<PatternItem> = listOf(DOT, GAP, DASH, GAP)

    private val PATTERN_POLYLINE_DOTTED = listOf(GAP, DOT)


    override fun onPolylineClick(polyline: Polyline) {
        if ((polyline.pattern == null) || (!polyline.pattern!!.contains(DOT))) {
            polyline.pattern = PATTERN_POLYLINE_DOTTED;
        } else {
            polyline.remove()
//            polyline.pattern = null;
        }
    }

    /*
    서울 위경도 범위
    37.715133
    37.413294

    126.734086
    127.269311

    서울역 : 37.5552782 , 126.9706760
    신구대학교 : 37.4487435 , 127.1680606
    노원역 : 37.6562678 , 127.0630304

     */


//    unpacking
//    리스트의 포장을 풀기

    fun drawPolyLine(vararg pointArray: LatLng) {
        val polyline: Polyline = map.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(*pointArray)
        )
        stylePolyline(polyline)
    }


    private fun stylePolyline(polyline: Polyline) {
        polyline.startCap = RoundCap()
        polyline.endCap = RoundCap()
        polyline.width = POLYLINE_STROKE_WIDTH_PX;
        polyline.color = COLOR_BLACK.toInt()
        polyline.jointType = JointType.ROUND
    }

    override fun onPolygonClick(polygon: Polygon) {
        var color: Int = polygon.strokeColor xor 0x00ffffff
        polygon.strokeColor = color
        color = polygon.fillColor xor 0x00ffffff
        polygon.fillColor = color
    }

    fun drawPolygon(vararg pointArray: LatLng) {
        val polygon: Polygon = map.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(*pointArray)
        )
        stylePolygon(polygon)
    }

    private fun stylePolygon(polygon: Polygon) {
        polygon.strokePattern = PATTERN_POLYGON
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = COLOR_DARK_ORANGE_ARGB
        polygon.fillColor = COLOR_LIGHT_ORANGE_ARGB
    }
}