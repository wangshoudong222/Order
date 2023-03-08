package com.yun.orderPad.ui.meal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yun.orderPad.R
import com.yun.orderPad.model.result.OrderInfo
import com.yun.orderPad.util.CommonUtils
import com.yun.orderPad.view.OrderAdapter
import com.yun.orderPad.view.SpaceItemDecoration

class PMealDataAdapter (val context: Context, var data: List<OrderInfo>?): RecyclerView.Adapter<PMealDataAdapter.VH>(){

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val order: TextView
        val time: TextView
        val meal: TextView
        val mealTye: TextView
        val rv: RecyclerView

        init {
            order = v.findViewById(R.id.order_id)
            time = v.findViewById(R.id.time)
            meal = v.findViewById(R.id.meal)
            mealTye = v.findViewById(R.id.meal_name)
            rv = v.findViewById(R.id.more_rv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.p_item_meal_more, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val orderInfo = data?.get(position)
        orderInfo?.let {
            holder.order.text = orderInfo.orderNo
            holder.time.text = orderInfo.mealDate
            holder.meal.text = orderInfo.mealTableName
            holder.mealTye.text = orderInfo.mealTypeName
            val adapter = PMealItemDataAdapter(context, orderInfo)
            holder.rv.layoutManager = GridLayoutManager(context, 2)
            val spaceItemDecoration = SpaceItemDecoration(CommonUtils.dp2px(context, 20f))
            holder.rv.addItemDecoration(spaceItemDecoration)
            holder.rv.adapter = adapter
        }
    }
}