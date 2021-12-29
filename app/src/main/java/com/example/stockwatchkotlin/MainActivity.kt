package com.example.stockwatchkotlin

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    var nameArr = arrayOf("NULL")
    var symbolArr = arrayOf("NULL")
    var idArr = arrayOf<Int>()
    var changeAl = ArrayList<String>()

    lateinit var myAdapter: CustomAdapter
    val apiKeys = arrayOf("EQE52ud8DF269FPf8YYwbatUu8ad4JWu3vlswm4e", "vLUkYKFByH2MmjZEHu4jka1tQMe6SpJF7M26aJp6","OfchZQjCxV9lrwSbMKafo2ZlssYGw25FaAnuSPUS", "wKA5JQBRiP44RycLWxBw06Qqe1VfwO4dWpXpZAh0", "vWpNbQieOU5kO2Rse4RyU1Hjkj0Kkq206WUVB7vC", "tqigO4jvta5JzPnuBcr9gaM36ApV1JCWahfk9Z13", "sV3EiB4ygK214C8uCubYm4LD3T3r1I6G3g3zABgm", "7vypKmYsfr1MDqQW3JjGv124SG95hqgf1ZNfztwq", "T2CaE1EiO31lLpfvkKnBp17EvbCzMDLr3NEa83cO", "F1pKJl2fjH1bTwVvZgVA46G3RPVn52wW2GSZ37H8", "u7myyIPk0j8jA1KSjuhac1DVjTYWcXkq1ZGACjzY")
    var switchKey = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personPhoto = ""+acct.photoUrl
            user_name.text = personName

            if (personPhoto == "null"){
                Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/149/149071.png").into(user_img)
            }
            else{
                Glide.with(this).load(personPhoto).into(user_img)
            }
        }

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("sharedPref", MODE_PRIVATE)
        switchKey = sharedPreferences.getInt("key", 0)

        getListFromDB()
        setChangePriceCard()


        if (nameArr[0] != "NULL") {
            myAdapter = CustomAdapter(this, nameArr, symbolArr, idArr, 1, changeAl)
            listView.adapter = myAdapter
            listView.visibility = View.VISIBLE
            empty_list_massage.visibility = View.GONE

        } else {
            empty_list_massage.visibility = View.VISIBLE
            listView.visibility = View.INVISIBLE
        }

        listView.isLongClickable = true

        listView.setOnItemLongClickListener { adapterView, view, i, l ->
            val builder = AlertDialog.Builder(this)

            builder.setTitle("Delete")
            builder.setMessage("Do you want to delete ${symbolArr[i]}")
            builder.setIcon(R.drawable.ic_round_delete_24)

            builder.setPositiveButton("Yes") { dialogInterface, which ->

                val dbHandler = DatabaseHandler(this)
                val isDeleted = dbHandler.deleteEntry(DataModel(idArr[i], nameArr[i], symbolArr[i]))
                if (isDeleted > 0) {
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                    getListFromDB()
                    changeAl.removeAt(i)
                    setChangePriceCard()
                }
            }

            builder.setNegativeButton("No") { dialogInterface, which ->

            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()

            false
        }

        listView.setOnItemClickListener { adapterView, view, i, l ->

            val intent = Intent(this, DetailScreenActivity::class.java)
            intent.putExtra("stock_symbol", symbolArr[i])
            intent.putExtra("stock_name", nameArr[i])
            startActivity(intent)

        }

        add_new.setOnClickListener {
            val intent = Intent(this, AddStockActivity::class.java)
            startActivityForResult(intent, ADD_NEW_ENTRY_REQ_CODE)
        }
    }


    private fun getListFromDB() {
        val dbHandler = DatabaseHandler(this)
        val getList = dbHandler.viewEntry()
        val nameArrayList = arrayListOf<String>()
        val symbolArrayList = arrayListOf<String>()
        val idArrayList = arrayListOf<Int>()

        if (getList.size > 0) {
            for (i in getList) {
                nameArrayList.add(i.name)
                symbolArrayList.add(i.symbol)
                idArrayList.add(i.id)
            }
            nameArr = nameArrayList.toTypedArray()
            symbolArr = symbolArrayList.toTypedArray()
            idArr = idArrayList.toTypedArray()

            myAdapter = CustomAdapter(this, nameArr, symbolArr, idArr, 1, changeAl)
            listView.adapter = myAdapter
        } else {
            empty_list_massage.visibility = View.VISIBLE
            listView.visibility = View.INVISIBLE
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_NEW_ENTRY_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                changeAl.clear()
                getListFromDB()
                setChangePriceCard()

                if (nameArr[0] != "NULL") {
                    myAdapter = CustomAdapter(this, nameArr, symbolArr, idArr, 1, changeAl)
                    listView.adapter = myAdapter
                    listView.visibility = View.VISIBLE
                    empty_list_massage.visibility = View.GONE
                } else {
                    empty_list_massage.visibility = View.VISIBLE
                    listView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setChangePriceCard() {
        if (symbolArr.isNotEmpty()) {

            val requestQueue: RequestQueue = Volley.newRequestQueue(this)
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                "https://yfapi.net/v6/finance/quote?region=US&lang=en&symbols=${
                    symbolArr.toList().toString().replace("[", "").replace("]", "")
                        .replace(", ", ",")
                }", null,
                Response.Listener { response ->
                    //when we get Response

                    try {
                        val jsonObject = response.getJSONObject("quoteResponse")
                        val jsonArray = jsonObject.getJSONArray("result")
                        val length = jsonArray.length()

                        for (i in 0 until length) {
                            val stockDetails = jsonArray.getJSONObject(i)
                            changeAl.add(" ${String.format("%.1f", stockDetails.getDouble("regularMarketPrice"))} \n (${String.format("%.2f", stockDetails.getDouble("regularMarketChangePercent"))}% ")
                        }

                        myAdapter = CustomAdapter(this, nameArr, symbolArr, idArr, 3, changeAl)
                        listView.adapter = myAdapter

                    } catch (e: JSONException) {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                    }

                },

                Response.ErrorListener { error ->
                    //when we get Error

                    Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()

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

    }

    fun updateUserProfile(){
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personPhoto = ""+acct.photoUrl
            user_name.text = personName
            if (personPhoto == "null"){
                Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/149/149071.png").into(user_img)
            }
            else{
                Glide.with(this).load(personPhoto).into(user_img)
            }
        }
    }

    companion object {
        var ADD_NEW_ENTRY_REQ_CODE = 1

    }


}