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
import java.math.BigDecimal


class ConfirmAdapter (val context: Context, val data: List<MealMenu>?): RecyclerView.Adapter<ConfirmAdapter.VH>(){

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val price: TextView
        val name: TextView
        val halal: TextView
        val countPrice: TextView
        val countMeal: TextView
        val iv: ImageView
        val bg: ConstraintLayout

        init {
            price = v.findViewById(R.id.price)
            name = v.findViewById(R.id.dish_name)
            countPrice = v.findViewById(R.id.count_price)
            countMeal = v.findViewById(R.id.count_meal)
            halal = v.findViewById(R.id.halal)
            iv = v.findViewById(R.id.image)
            bg = v.findViewById(R.id.bg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_confirm_meal, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val menu = data?.get(position)
        menu?.let {
            holder.halal.visibility = if (menu.isHalal == true) View.VISIBLE else View.INVISIBLE
            holder.price.text = menu.price.toString()
            holder.name.text = menu.dishSkuName
            holder.countPrice.text = menu.price?.multiply(BigDecimal(menu.quantity!!)).toString()
            holder.countMeal.text = menu.quantity.toString()
            holder.bg.setBackgroundResource(R.drawable.bg_gray_stroke_8)
            Glide.with(context).load(menu.dishPicUrl).apply(options).placeholder(R.drawable.dish).into(holder.iv)
        }
    }

    private val options = RequestOptions.bitmapTransform(RoundedCorners(
        CommonUtils.dp2px(BaseContext.instance.getContext(), 4f)))
}