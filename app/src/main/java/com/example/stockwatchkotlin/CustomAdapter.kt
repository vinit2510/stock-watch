package com.example.stockwatchkotlin

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.list_item.view.*

class CustomAdapter(
    private val context: Activity,
    private var name: Array<String>,
    private var symbol: Array<String>,
    private var idArr: Array<Int>,
    private var type: Int,
    private var changeAl: ArrayList<String>

) : ArrayAdapter<String>(context, R.layout.list_item, name) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_item, null, true)


        rowView.tvName.text = name[position]
        rowView.tvSymbol.text = symbol[position]

        if (type == 1) {

            rowView.change_price_tv.visibility = View.GONE
            /*
            rowView.btnRemove.setOnClickListener {
                val dbHandler = DatabaseHandler(context)
                val isDeleted = dbHandler.deleteEntry(DataModel(idArr[position], name[position], symbol[position]))
                if (isDeleted > 0) {
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    (context as MainActivity).getListFromDB()
                    context.setChangePriceCard()
                }
            }
             */

        }

        if (type == 2) {
            rowView.change_price_tv.visibility = View.GONE
        }

        if (type == 3) {

            if (changeAl.size > 0) {
                rowView.change_price_tv.text = changeAl[position]
                if (changeAl[position].contains("-")) {
                    rowView.change_price_tv.setBackgroundColor(Color.parseColor("#DF5045"))
                    rowView.change_price_tv.text =
                        rowView.change_price_tv.text.toString().replace("(", "")
                } else {
                    rowView.change_price_tv.setBackgroundColor(Color.parseColor("#61C165"))
                    rowView.change_price_tv.text =
                        rowView.change_price_tv.text.toString().replace("(", "+")
                }
            }

        }

        //return super.getView(position, convertView, parent)
        return rowView
    }


}