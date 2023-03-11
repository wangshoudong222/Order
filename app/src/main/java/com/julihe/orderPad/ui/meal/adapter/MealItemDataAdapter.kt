package com.julihe.orderPad.ui.meal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.julihe.orderPad.R
import com.julihe.orderPad.model.result.OrderInfo
import java.math.BigDecimal

class MealItemDataAdapter (val context: Context, val data: OrderInfo?): RecyclerView.Adapter<MealItemDataAdapter.VH>(){

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val price: TextView
        val dishName: TextView
        val mealName: TextView
        val halal: TextView
        val time: TextView
        val window: TextView
        val state: TextView
        val confirmTime: TextView

        init {
            price = v.findViewById(R.id.price)
            dishName = v.findViewById(R.id.dish_name)
            mealName = v.findViewById(R.id.meal_name)
            time = v.findViewById(R.id.time)
            window = v.findViewById(R.id.window)
            halal = v.findViewById(R.id.halal)
            state = v.findViewById(R.id.state)
            confirmTime = v.findViewById(R.id.confirm_time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
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
            holder.window.text = if (info.isSelfWindow) "本窗口" else info.windowName
            holder.state.text = if (info.isWaitingPickUp) info.stateName else "取餐完成"
            holder.state.setTextColor(context.resources.getColor(if (info.isWaitingPickUp) R.color.color_333333 else R.color.color_00B578))
            holder.price.text = info.price?.multiply(BigDecimal(info.quantity!!)).toString()
            holder.confirmTime.text = info.confirmTime
            holder.time.text = data?.mealDate
        }
    }
}