package com.yun.orderPad.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.yun.orderPad.BaseContext
import com.yun.orderPad.R
import com.yun.orderPad.model.result.MealMenu
import com.yun.orderPad.util.CommonUtils


class OrderAdapter (val context: Context, val data: List<MealMenu>?): RecyclerView.Adapter<OrderAdapter.VH>(){

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val price: TextView
        val name: TextView
        val meal: TextView
        val halal: TextView
        val code: TextView
        val count: TextView
        val x: TextView
        val checked: ImageView
        val iv: ImageView
        val bg: ConstraintLayout

        init {
            price = v.findViewById(R.id.price)
            name = v.findViewById(R.id.dish_name)
            meal = v.findViewById(R.id.meal_name)
            code = v.findViewById(R.id.dish_code)
            halal = v.findViewById(R.id.halal)
            count = v.findViewById(R.id.count)
            x = v.findViewById(R.id.tv_x)
            iv = v.findViewById(R.id.image)
            checked = v.findViewById(R.id.check_im)
            bg = v.findViewById(R.id.bg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val menu = data?.get(position)
        menu?.let {
            holder.checked.setImageDrawable(context.getDrawable(if (menu.checked == true) R.drawable.checked else R.drawable.uncheck))
            holder.halal.visibility = if (menu.isHalal == true) View.VISIBLE else View.INVISIBLE
            holder.price.text = menu.price.toString()
            holder.name.text = menu.dishSkuName
            holder.meal.text = menu.mealTableName
            holder.code.text = menu.dishCode
            if (menu.quantity != 0L) {
                holder.count.text = menu.quantity?.toString()
                holder.x.visibility = View.VISIBLE
                holder.count.visibility = View.VISIBLE
            } else {
                holder.x.visibility = View.GONE
                holder.count.visibility = View.GONE
            }
            holder.bg.setBackgroundResource(if (menu.fouces == true) R.drawable.bg_gray_stroke_deep_8 else  R.drawable.bg_gray_stroke_8)
            Glide.with(context).load(menu.dishPicUrl).apply(options).placeholder(R.drawable.dish).into(holder.iv)
        }
    }

    private val options = RequestOptions.bitmapTransform(RoundedCorners(
        CommonUtils.dp2px(BaseContext.instance.getContext(), 4f)))
}