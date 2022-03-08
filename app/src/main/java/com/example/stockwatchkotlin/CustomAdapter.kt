package com.example.stockwatchkotlin

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.list_item.view.*

/*
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

 */

class CustomAdapter(
    private val context: Activity,
    private var name: Array<String>,
    private var symbol: Array<String>,
    private var idArr: Array<Int>,
    private var type: Int,
    private var changeAl: ArrayList<String>

) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvName: TextView = view.tvName
        val tvSymbol: TextView = view.tvSymbol
        val tvChange: TextView = view.change_price_tv
        val cardView: CardView = view.card_view
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        if (type == 2){
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_list_item, parent, false)
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = name[position]
        holder.tvSymbol.text = symbol[position]

        if (type == 1) {
            holder.tvChange.visibility = View.GONE

        }

        if (type == 2) {
            holder.tvChange.visibility = View.GONE
            holder.cardView.setOnClickListener {
                (context as AddStockActivity).addStock(name[position], symbol[position])
            }
        }

        if (type == 3) {

            if (changeAl.size > 0) {
                holder.tvChange.text = changeAl[position]
                if (changeAl[position].contains("-")) {
                    holder.tvChange.setBackgroundColor(Color.parseColor("#DF5045"))
                    holder.tvChange.text = holder.tvChange.text.toString().replace("(", "")
                } else {
                    holder.tvChange.setBackgroundColor(Color.parseColor("#61C165"))
                    holder.tvChange.text = holder.tvChange.text.toString().replace("(", "+")
                }
            }

            holder.cardView.setOnClickListener {
                val intent = Intent(context, DetailScreenActivity::class.java)
                intent.putExtra("stock_symbol", symbol[position])
                intent.putExtra("stock_name", name[position])
                startActivity(context, intent, Bundle())
            }

            holder.cardView.setOnLongClickListener {
                (context as MainActivity).deleteStock(position)
                false
            }
        }
    }

    override fun getItemCount(): Int {
        return name.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

