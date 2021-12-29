package com.example.stockwatchkotlin

import android.content.Context
import android.view.View
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import kotlinx.android.synthetic.main.custom_marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*


class CustomMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    override fun refreshContent(e: Entry, highlight: Highlight?) {

        val mFormat = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)

        val millis = e.x.toLong() * 1000L

        val xData = mFormat.format(Date(millis))
        tvContent.visibility = View.VISIBLE
        tvContent.text = "$xData\n${e.y}"
    }

    fun getXOffset(): Int {
        return -width
    }

    fun getYOffset(): Int {
        return -height
    }
}