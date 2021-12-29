package com.example.stockwatchkotlin

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.activity_detail_screen.*
import org.json.JSONException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DetailScreenActivity : AppCompatActivity() {

    val apiKeys = arrayOf(
        "EQE52ud8DF269FPf8YYwbatUu8ad4JWu3vlswm4e",
        "vLUkYKFByH2MmjZEHu4jka1tQMe6SpJF7M26aJp6",
        "OfchZQjCxV9lrwSbMKafo2ZlssYGw25FaAnuSPUS",
        "wKA5JQBRiP44RycLWxBw06Qqe1VfwO4dWpXpZAh0",
        "vWpNbQieOU5kO2Rse4RyU1Hjkj0Kkq206WUVB7vC",
        "tqigO4jvta5JzPnuBcr9gaM36ApV1JCWahfk9Z13",
        "sV3EiB4ygK214C8uCubYm4LD3T3r1I6G3g3zABgm",
        "7vypKmYsfr1MDqQW3JjGv124SG95hqgf1ZNfztwq",
        "T2CaE1EiO31lLpfvkKnBp17EvbCzMDLr3NEa83cO",
        "F1pKJl2fjH1bTwVvZgVA46G3RPVn52wW2GSZ37H8",
        "u7myyIPk0j8jA1KSjuhac1DVjTYWcXkq1ZGACjzY"
    )


    var switchKey = 0

    //private var yValue: ArrayList<Entry> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_screen)

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("sharedPref", MODE_PRIVATE)
        //val editor = sharedPreferences.edit()

        switchKey = sharedPreferences.getInt("key", 0)


        val stockSymbol = intent.getStringExtra("stock_symbol")
        val stockName = intent.getStringExtra("stock_name")


        btn_1d.setTextColor(Color.GRAY)

        name_tv.text = stockName
        symbol_tv.text = stockSymbol


        //range:    1d 5d 1mo 3mo 6mo 1y 5y max
        //interval: 1m 5m 15m 1d 1wk 1mo

        btn_1d.setOnClickListener {
            resetBgColor()
            btn_1d.setTextColor(Color.GRAY)
            getDataForGraph(stockSymbol!!, "1d", "15m")
        }

        btn_1m.setOnClickListener {

            resetBgColor()
            btn_1m.setTextColor(Color.GRAY)
            getDataForGraph(stockSymbol!!, "1mo", "1d")

        }
        btn_6m.setOnClickListener {
            resetBgColor()
            btn_6m.setTextColor(Color.GRAY)
            getDataForGraph(stockSymbol!!, "6mo", "1d")
        }
        btn_1y.setOnClickListener {

            resetBgColor()
            btn_1y.setTextColor(Color.GRAY)
            getDataForGraph(stockSymbol!!, "1y", "1d")
        }
        btn_5y.setOnClickListener {

            resetBgColor()
            btn_5y.setTextColor(Color.GRAY)
            getDataForGraph(stockSymbol!!, "5y", "1d")
        }
        btn_max.setOnClickListener {

            resetBgColor()
            btn_max.setTextColor(Color.GRAY)
            getDataForGraph(stockSymbol!!, "max", "1d")
        }



        if (stockSymbol != null) {
            getAndSetDataInScreen(stockSymbol)
            getDataForGraph(stockSymbol, "1d", "15m")

        }


        refresh_layout.setOnRefreshListener {
            if (stockSymbol != null) {
                getAndSetDataInScreen(stockSymbol)
                getDataForGraph(stockSymbol, "1d", "15m")
                refresh_layout.isRefreshing = false
            }
        }

    }

    private fun resetBgColor() {
        val btnArr = arrayOf(btn_1d, btn_1m, btn_6m, btn_1y, btn_5y, btn_max)

        for (i in btnArr) {
            //i.setBackgroundColor(parseColor("#00FFFFFF"))
            i.setTextColor(Color.WHITE)

        }
    }


    private fun getAndSetDataInScreen(stockSymbol: String) {

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,
            "https://yfapi.net/v6/finance/quote?region=US&lang=en&symbols=$stockSymbol", null,
            Response.Listener { response ->
                //when we get Response

                try {
                    val jsonObject = response.getJSONObject("quoteResponse")
                    val jsonArray = jsonObject.getJSONArray("result")
                    val stockDetails = jsonArray.getJSONObject(0)
                    val sdf = SimpleDateFormat("dd/MM hh:mm", Locale.ENGLISH)
                    val changePrice = stockDetails.getDouble("regularMarketChange")

                    if (changePrice < 0) {
                        change_tv.setTextColor(-0xb8687)
                        change_tv.text = "${
                            String.format(
                                "%.2f",
                                stockDetails.getDouble("regularMarketChange")
                            )
                        } (${
                            String.format(
                                "%.2f",
                                stockDetails.getDouble("regularMarketChangePercent")
                            )
                        }%)"
                    } else {
                        change_tv.setTextColor(-0xb350b0)
                        change_tv.text = "${
                            String.format(
                                "%.2f",
                                stockDetails.getDouble("regularMarketChange")
                            )
                        } (${
                            String.format(
                                "%.2f",
                                stockDetails.getDouble("regularMarketChangePercent")
                            )
                        }%)"
                        change_tv.text = "+" + change_tv.text.toString().replace("(", "(+")
                    }



                    date_tv.text = sdf.format(stockDetails.getLong("regularMarketTime") * 1000L)
                    price_tv.text = stockDetails.getDouble("regularMarketPrice").toString()
                    //change_tv.text = "${String.format("%.2f", stockDetails.getDouble("regularMarketChange"))} (${String.format("%.2f", stockDetails.getDouble("regularMarketChangePercent"))}%)"
                    volume_size_tv.text =
                        stockDetails.getLong("regularMarketVolume").toString()
                    open_price_tv.text =
                        stockDetails.getDouble("regularMarketOpen").toFloat().toString()
                    high_price_tv.text =
                        stockDetails.getDouble("regularMarketDayHigh").toFloat().toString()
                    low_price_tv.text =
                        stockDetails.getDouble("regularMarketDayLow").toFloat().toString()
                    prevclose_price_tv.text =
                        stockDetails.getDouble("regularMarketPreviousClose").toFloat().toString()
                    ftwl_price_tv.text = stockDetails.getDouble("fiftyTwoWeekLow").toString()
                    ftwh_price_tv.text = stockDetails.getDouble("fiftyTwoWeekHigh").toString()
                    ask_price_tv.text = stockDetails.getDouble("ask").toString()
                    bid_price_tv.text = stockDetails.getDouble("bid").toString()
                    currency_tv.text = stockDetails.getString("currency")


                } catch (e: JSONException) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                }

            },

            Response.ErrorListener { error ->
                //when we get Error

                Toast.makeText(this, "Please Refresh", Toast.LENGTH_SHORT).show()
                switchKey++

                if (switchKey == apiKeys.size)
                    switchKey = 0

                val sharedPreferences: SharedPreferences =
                    this.getSharedPreferences("sharedPref", MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                editor.putInt("key", switchKey)
                editor.apply()
            }
        ) {
            // Providing Request Headers

            override fun getHeaders(): Map<String, String> {

                val headers = HashMap<String, String>()
                headers["accept:"] = "application/json"
                headers["X-API-KEY"] = apiKeys[switchKey]

                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)
    }


    private fun setGraph(values: ArrayList<Entry>, interval: String, rangeType: String) {

        line_chart.setNoDataText("Graph Loading...")
        line_chart.setNoDataTextColor(Color.WHITE)
        //line_chart.setBackgroundColor(Color.WHITE)
        line_chart.setDrawGridBackground(false)
        line_chart.setDrawBorders(true)
        line_chart.setBorderWidth(1.5f)
        line_chart.setBorderColor(Color.WHITE)
        line_chart.description.isEnabled = false
        line_chart.setPinchZoom(false)


        val legend = line_chart.legend
        legend.isEnabled = false

        val leftAxis: YAxis = line_chart.axisLeft
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawZeroLine(false)
        leftAxis.setDrawGridLines(false)


        val set = LineDataSet(values, "Data Set")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = Color.WHITE
        set.fillColor = Color.parseColor("#6b6b6b")

        set.setDrawCircles(false)
        set.lineWidth = 3f
        set.fillAlpha = 150
        set.setDrawFilled(true)

        line_chart.axisRight.setDrawGridLines(true)
        line_chart.axisLeft.setDrawGridLines(true)
        line_chart.xAxis.setDrawGridLines(false)
        line_chart.axisRight.isEnabled = false
        line_chart.xAxis.labelCount = 5
        line_chart.axisLeft.labelCount = 4
        line_chart.xAxis.textColor = Color.WHITE
        line_chart.axisLeft.textColor = Color.WHITE

        if (interval == "1d") {
            line_chart.xAxis.valueFormatter = object : ValueFormatter() {

                private val mFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
                override fun getFormattedValue(value: Float): String {
                    val millis = value.toLong() * 1000L
                    return mFormat.format(Date(millis))
                }
            }
        }
        if (interval == "15m") {
            line_chart.xAxis.valueFormatter = object : ValueFormatter() {

                private val mFormat = SimpleDateFormat("HH:MM", Locale.ENGLISH)
                override fun getFormattedValue(value: Float): String {
                    val millis = value.toLong() * 1000L
                    return mFormat.format(Date(millis))
                }
            }
        }
        if (rangeType == "5y" || rangeType == "max") {
            line_chart.xAxis.valueFormatter = object : ValueFormatter() {

                private val mFormat = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)
                override fun getFormattedValue(value: Float): String {
                    val millis = value.toLong() * 1000L
                    return mFormat.format(Date(millis))
                }
            }
        }


        val customMarkerView = CustomMarkerView(this, R.layout.custom_marker_view)
        line_chart.markerView = customMarkerView


        val data = LineData(set)
        data.setDrawValues(false)

        line_chart.animateX(1000)
        line_chart.data = data


    }

    private fun getDataForGraph(stockSymbol: String, rangeType: String, interval: String) {

        line_chart.setNoDataText("Graph Loading...")
        line_chart.setNoDataTextColor(Color.WHITE)

        val dataValue: ArrayList<Entry> = ArrayList()

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,
            "https://yfapi.net/v8/finance/spark?interval=$interval&range=$rangeType&symbols=$stockSymbol",
            null,
            Response.Listener { response ->
                //when we get Response

                try {
                    val jsonObject = response.getJSONObject(stockSymbol)
                    val jsonArrayTimeStamp = jsonObject.getJSONArray("timestamp")
                    val jsonArrayClose = jsonObject.getJSONArray("close")


                    for (i in 0 until jsonArrayClose.length()) {
                        dataValue.add(
                            Entry(
                                jsonArrayTimeStamp.getLong(i).toFloat(),
                                jsonArrayClose.getDouble(i).toFloat()
                            )
                        )
                    }
                    dataValue.reversed()
                    setGraph(dataValue, interval, rangeType)

                } catch (e: JSONException) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                }

            },

            Response.ErrorListener { error ->
                //when we get Error

                Toast.makeText(this, "Please Refresh", Toast.LENGTH_SHORT).show()
                switchKey++
                if (switchKey == apiKeys.size)
                    switchKey = 0

                val sharedPreferences: SharedPreferences =
                    this.getSharedPreferences("sharedPref", MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                editor.putInt("key", switchKey)
                editor.apply()

            }) {
            // Providing Request Headers

            override fun getHeaders(): Map<String, String> {

                val headers = HashMap<String, String>()
                headers["accept:"] = "application/json"
                headers["X-API-KEY"] = apiKeys[switchKey]

                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)

    }

}
