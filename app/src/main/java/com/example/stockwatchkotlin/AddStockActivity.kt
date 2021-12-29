package com.example.stockwatchkotlin

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_add_stock.*
import kotlinx.android.synthetic.main.activity_detail_screen.*
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddStockActivity : AppCompatActivity() {

    private val nameArr = arrayListOf<String>()
    private val symbolArr = arrayListOf<String>()
    private val idArr = arrayListOf<Int>()

    private lateinit var searchAdapter: CustomAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stock)

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("sharedPref", MODE_PRIVATE)
        //val editor = sharedPreferences.edit()

        switchKey = sharedPreferences.getInt("key", 0)

        searchAdapter = CustomAdapter(this, nameArr.toTypedArray(), symbolArr.toTypedArray(), idArr.toTypedArray(), 2, ArrayList(0))
        list_view.adapter = searchAdapter

        list_view.setOnItemClickListener { adapterView, view, i, l ->

            val dataModel = DataModel(0, nameArr[i], symbolArr[i])
            val dbHandler = DatabaseHandler(this)
            val addData = dbHandler.addEntry(dataModel)

            if (addData > 0) {
                Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)

                finish()
            }
        }

        search_view.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {


                if (s!!.length > 2) {
                    val requestQueue: RequestQueue = Volley.newRequestQueue(this@AddStockActivity)

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.GET,
                        "https://yfapi.net/v6/finance/autocomplete?region=US&lang=en&query=$s",
                        null,
                        Response.Listener { response ->

                            try {
                                nameArr.clear()
                                symbolArr.clear()
                                idArr.clear()

                                val jsonObject = response.getJSONObject("ResultSet")
                                val jsonArray = jsonObject.getJSONArray("Result")
                                val length = jsonArray.length()

                                for (i in 0 until length) {
                                    val searches = jsonArray.getJSONObject(i)
                                    nameArr.add(searches.getString("name"))
                                    symbolArr.add(searches.getString("symbol"))
                                    idArr.add(i)
                                }

                                searchAdapter = CustomAdapter(
                                    this@AddStockActivity,
                                    nameArr.toTypedArray(),
                                    symbolArr.toTypedArray(),
                                    idArr.toTypedArray(),
                                    2,
                                    ArrayList()
                                )

                                list_view.adapter = searchAdapter
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@AddStockActivity,
                                    e.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        },

                        Response.ErrorListener { error ->
                            Toast.makeText(this@AddStockActivity, "Please wait", Toast.LENGTH_SHORT)
                                .show()
                            switchKey++
                            if (switchKey == apiKeys.size)
                                switchKey = 0

                            val sharedPreferences: SharedPreferences =
                                this@AddStockActivity.getSharedPreferences(
                                    "sharedPref",
                                    MODE_PRIVATE
                                )
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
                return false
            }

        })

    }
}