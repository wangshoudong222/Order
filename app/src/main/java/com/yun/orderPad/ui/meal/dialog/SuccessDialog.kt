package com.yun.orderPad.ui.meal.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.yun.orderPad.R
import com.yun.orderPad.model.result.OrderInfo
import com.yun.orderPad.ui.meal.SetMealActivity
import com.yun.orderPad.ui.meal.SetMealViewModel
import com.yun.orderPad.view.BaseDialog
import com.yun.orderPad.ui.meal.adapter.MealDataAdapter

class SuccessDialog(
    val activity: SetMealActivity, val viewModel: SetMealViewModel,
    val listener: DialogInterface.OnKeyListener
) : BaseDialog() {

    private var myHandler: SuccessCenterDialogHandler? = null
    private var icon: ImageView? = null
    private var school: TextView? = null
    private var info: TextView? = null
    private var name: TextView? = null
    private var success_ll: LinearLayout? = null
    private var waiting_ll: LinearLayout? = null
    private var ll_back: LinearLayout? = null
    private var rv: RecyclerView? = null
    private var btn: Button? = null

    override fun getLayoutRes(): Int {
        return R.layout.layout_dialog_success
    }

    override fun bindView(contentView: View?) {
        icon = contentView?.findViewById(R.id.icon)
        school = contentView?.findViewById(R.id.school)
        info = contentView?.findViewById(R.id.info)
        name = contentView?.findViewById(R.id.name)

        success_ll = contentView?.findViewById(R.id.success_ll)
        waiting_ll = contentView?.findViewById(R.id.wait_layout)
        ll_back = contentView?.findViewById(R.id.ll_back)
        rv = contentView?.findViewById(R.id.rv)
        btn = contentView?.findViewById(R.id.btn_back)

        btn?.setOnClickListener {
            dismiss()
            viewModel.cleanState()
        }

        val manager = LinearLayoutManager(context)
        manager.orientation = RecyclerView.VERTICAL
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayout.VERTICAL)
        dividerItemDecoration.setDrawable(context?.getDrawable(R.drawable.item_white)!!)
        rv?.layoutManager = manager
        rv?.addItemDecoration(dividerItemDecoration)

        initViewShow()

        dialog?.setOnKeyListener(listener)
    }

    private fun initViewShow() {
        viewModel.student.observe(activity) {
            it?.let {
                val infoText = it.numberOfClassName + " " + it.gradeName + " " + it.className + " "  +it.studentNo
                Glide.with(activity).load(it.avatar).apply(RequestOptions.bitmapTransform(CircleCrop())).placeholder(R.drawable.head_normal).into(icon!!)
                name?.text = it.studentName
                school?.text = it.schoolName
                info?.text = infoText
            }
        }

        viewModel.confirmState.observe(activity) {
            if (it == true)  {
                waiting_ll?.visibility = View.GONE
                success_ll?.visibility = View.VISIBLE
                ll_back?.visibility = View.VISIBLE
            } else {
                waiting_ll?.visibility = View.VISIBLE
                success_ll?.visibility = View.GONE
                ll_back?.visibility = View.GONE
            }
        }

        viewModel.orders.observe(activity) {
            if (!it.isNullOrEmpty()) {
                setAdapter(it)
            }
        }
    }



    private fun setAdapter(list: List<OrderInfo>?) {
        val adapter = MealDataAdapter(activity, list)
        rv?.adapter = adapter
    }

    override fun getHandler(): SimpleHandler {
        @Synchronized
        if (myHandler == null) {
            myHandler = SuccessCenterDialogHandler()
        }
        return myHandler as SuccessCenterDialogHandler
    }

    interface OnDialogKey: DialogInterface.OnKeyListener {
        fun onKey()
    }

    companion object {
        const val TAG = "ErrorDialog"
    }
}