package kr.co.zetalux

import android.content.Context
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import java.util.*

class DrawPoly(private val context: Context, private val map: GoogleMap):  GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener  {

    private val PATTERN_GAP_LENGTH_PX = 20
    private val DOT: PatternItem = Dot()
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())
    private val COLOR_WHITE_ARGB = -0x1
    private val COLOR_DARK_GREEN_ARGB = -0xc771c4
    private val COLOR_LIGHT_GREEN_ARGB = -0x7e387c
    private val COLOR_DARK_ORANGE_ARGB = -0xa80e9
    private val COLOR_LIGHT_ORANGE_ARGB = -0x657db
    private val COLOR_BLACK_ARGB = -0x1000000
    private val POLYLINE_STROKE_WIDTH_PX = 12
    private val POLYGON_STROKE_WIDTH_PX = 8
    private val PATTERN_DASH_LENGTH_PX = 20
    private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
    // Create a stroke pattern of a gap followed by a dash.
    private val PATTERN_POLYGON_ALPHA = listOf(GAP, DASH)
    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private val PATTERN_POLYGON_BETA: List<PatternItem> = listOf(DOT, GAP, DASH, GAP)
    // Create a stroke pattern of a gap followed by a dot.
    private val PATTERN_POLYLINE_DOTTED = listOf(GAP, DOT)

    override fun onPolylineClick(polyline: Polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.pattern == null) || (!polyline.pattern!!.contains(DOT))) {
            polyline.pattern = PATTERN_POLYLINE_DOTTED;
        } else {
            // The default pattern is a solid stroke.
            polyline.pattern = null;
        }

        Toast.makeText(context, "Route type " + polyline.tag.toString(), Toast.LENGTH_SHORT).show();
    }

    override fun onPolygonClick(polygon: Polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        var color: Int = polygon.strokeColor xor 0x00ffffff
        polygon.strokeColor = color
        color = polygon.fillColor xor 0x00ffffff
        polygon.fillColor = color
        Toast.makeText(context, "Area type " + polygon.tag.toString(), Toast.LENGTH_SHORT).show()
    }
    /*
    서울 위경도 범위
    37.715133
    37.413294

    126.734086
    127.269311
     */

    fun draw() {
        val polyline1: Polyline = map.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(37.715133, 126.734086),
                    LatLng(37.513294, 127.469311),
                    LatLng(37.413294, 127.269311),
                )
        )
        polyline1.tag = "B"
        stylePolyline(polyline1)

        val polygon1: Polygon = map.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(  37.515133, 126.534086),
                    LatLng(37.513294, 127.369311),
                    LatLng( 37.413294,  127.269311)
                )
        )
        polygon1.tag = "beta"
        stylePolygon(polygon1)

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

        map.setOnPolylineClickListener(this)
        map.setOnPolygonClickListener(this)
    }

    private fun stylePolyline(polyline: Polyline) {
        var type = ""
        // Get the data object stored with the polyline.
        if (polyline.tag != null) {
            type = polyline.tag.toString()
        }
        when (type) {
            "A" ->                 // Use a custom bitmap as the cap at the start of the line.
                polyline.startCap = ButtCap()
            "B" ->                 // Use a round cap at the start of the line.
                polyline.startCap = RoundCap()
        }
        polyline.endCap = RoundCap()
        polyline.width = 12F
        polyline.color = 0xff000000.toInt()
        polyline.jointType = JointType.ROUND
    }

    private fun stylePolygon(polygon: Polygon) {
        var type = ""
        // Get the data object stored with the polygon.
        if (polygon.tag != null) {
            type = polygon.tag.toString()
        }
        var pattern: List<PatternItem?>? = null
        var strokeColor: Int = COLOR_BLACK_ARGB
        var fillColor: Int = COLOR_WHITE_ARGB
        when (type) {
            "alpha" -> {
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA
                strokeColor = COLOR_DARK_GREEN_ARGB
                fillColor = COLOR_LIGHT_GREEN_ARGB
            }
            "beta" -> {
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA
                strokeColor = COLOR_DARK_ORANGE_ARGB
                fillColor = COLOR_LIGHT_ORANGE_ARGB
            }
        }
        polygon.strokePattern = pattern
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
    }
}