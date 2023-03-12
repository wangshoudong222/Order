package com.julihe.order.ui.meal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.julihe.order.R
import com.julihe.order.model.result.OrderInfo

class PMealItemDataAdapter (val context: Context, val data: OrderInfo?): RecyclerView.Adapter<PMealItemDataAdapter.VH>(){

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val price: TextView
        val count: TextView
        val dishName: TextView
        val mealName: TextView
        val halal: TextView

        init {
            price = v.findViewById(R.id.price)
            dishName = v.findViewById(R.id.dish_name)
            mealName = v.findViewById(R.id.meal_name)
            count = v.findViewById(R.id.count)
            halal = v.findViewById(R.id.halal)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.p_item_meal_detail, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int {
        return data?.orderDetailInfoList!!.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val info = data?.orderDetailInfoList?.get(position)
        info?.let {
            holder.dishName.text = info.dishSkuName
            holder.mealName.text = info.mealTableName
            holder.halal.visibility = if (info.isHalalFlag) View.VISIBLE else View.INVISIBLE
            holder.price.text = info.price.toString()
            holder.count.text = info.quantity.toString()
        }
    }
}