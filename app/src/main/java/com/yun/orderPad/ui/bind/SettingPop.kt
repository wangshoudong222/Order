package com.yun.orderPad.ui.bind

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.yun.orderPad.R
import com.yun.orderPad.model.request.KitchenRequest
import com.yun.orderPad.model.result.Config
import com.yun.orderPad.model.result.KitchenInfo
import com.yun.orderPad.model.result.SchoolInfo
import com.yun.orderPad.model.result.WindowInfo
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.util.sp.SpUtil
import com.yun.orderPad.view.BaseDialog
import com.yun.orderPad.view.FullDialogHandler

class SettingPop(var viewModel: BindViewModel) : BaseDialog() {

    private var sn:TextView? = null
    private var school:TextView? = null
    private var kitchen:TextView? = null
    private var window:TextView? = null

    private var cancel: Button? = null
    private var confirm:Button? = null

    private var pvSchoolInfo: OptionsPickerView<SchoolInfo?>? = null
    private var pvKitchenInfo:OptionsPickerView<KitchenInfo?>? = null
    private var pvWindowInfo:OptionsPickerView<WindowInfo>? = null

    private var myHandler: FullDialogHandler? = null

    override fun getLayoutRes(): Int {
        return R.layout.layout_setting_choose_pop
    }


    override fun bindView(contentView: View?) {
        sn = contentView?.findViewById(R.id.sn_no)
        school = contentView?.findViewById(R.id.choose_school)
        kitchen = contentView?.findViewById(R.id.choose_kitchen)
        window = contentView?.findViewById(R.id.choose_window)

        cancel = contentView?.findViewById(R.id.btn_cancel)
        confirm = contentView?.findViewById(R.id.btn_confirm)

        initPv()

        cancel?.setOnClickListener {
            ToastUtil.show("取消绑定设置")
            dismiss()
        }

        confirm?.setOnClickListener {
            if (checkChoose()) {
                viewModel.saveConfig()
            }
        }

        school?.setOnClickListener {
            pvSchoolInfo?.show()
        }

        kitchen?.setOnClickListener {
            if (viewModel.school.value != null) {
                pvKitchenInfo?.show()
            } else {
                pvSchoolInfo?.show()
            }
        }

        window?.setOnClickListener {
            if (viewModel.school.value == null) {
                pvSchoolInfo?.show()
            } else if (viewModel.kitchen.value == null) {
                pvKitchenInfo?.show()
            } else {
                pvWindowInfo?.show()
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.requestSchools()

        setShow(viewModel.config.value)
        viewModel.config.observe(this) {
            setShow(it)
        }

        viewModel.unBindState.observe(this) {
            if(it == true) {
                setShow(null)
            }
        }

        viewModel.schools.observe(this) {
            if(it != null && it.isNotEmpty()) {
                pvSchoolInfo?.setPicker(viewModel.schools.value)
            } else {
                ToastUtil.show("暂无学校信息")
            }
        }

        viewModel.kitchens.observe(this) {
            if(it != null && it.isNotEmpty()) {
                pvKitchenInfo?.setPicker(viewModel.kitchens.value)
                pvKitchenInfo?.show()
            } else {
                ToastUtil.show("该学校暂无食堂信息，请重新选择")
            }
        }

        viewModel.windows.observe(this) {
            if(it != null && it.isNotEmpty()) {
                pvWindowInfo?.setPicker(viewModel.windows.value)
                pvWindowInfo?.show()
            } else {
                ToastUtil.show("该食堂暂无窗口信息，请重新选择")
            }
        }

        viewModel.school.observe(this) {
            viewModel.requestKitchen(it)
        }

        viewModel.kitchen.observe(this) {
            val request = KitchenRequest(viewModel.school.value?.schoolId,it.kitchenId)
            viewModel.requestWindows(request)
        }

        viewModel.saveState.observe(this) {
            if (it == true) {
                ToastUtil.show("设置窗口成功")
                dismiss()
            } else {
                ToastUtil.show("设置窗口失败")
            }
        }
    }

    private fun initPv() {
        pvSchoolInfo = OptionsPickerBuilder(context) { options1, options2, options3, v ->
            val schoolInfo = viewModel.schools.value?.get(options1)
            schoolInfo?.let {
                school?.text = schoolInfo.schoolName
                viewModel.chooseSchool(schoolInfo)
            }

        }.setLayoutRes(R.layout.pickerview_options) {
            val tvCancel = it.findViewById<TextView>(R.id.tv_cancel)
            val tvFinish = it.findViewById<TextView>(R.id.tv_finish)
            tvCancel.setOnClickListener {
                pvSchoolInfo?.dismiss()
            }
            tvFinish.setOnClickListener {
                pvSchoolInfo?.returnData()
                pvSchoolInfo?.dismiss()
            }

        }.isDialog(true)
            .setContentTextSize(25)
            .setOutSideCancelable(false)
            .build()

        pvKitchenInfo = OptionsPickerBuilder(context) { options1, options2, options3, v ->
            val kitchenInfo = viewModel.kitchens.value?.get(options1)
            kitchenInfo?.let {
                kitchen?.text = kitchenInfo.kitchenName
                viewModel.chooseKitchen(kitchenInfo)
            }

        }.setLayoutRes(R.layout.pickerview_options) {
            val tvCancel = it.findViewById<TextView>(R.id.tv_cancel)
            val tvFinish = it.findViewById<TextView>(R.id.tv_finish)
            tvCancel.setOnClickListener {
                pvKitchenInfo?.dismiss()
            }
            tvFinish.setOnClickListener {
                pvKitchenInfo?.returnData()
                pvKitchenInfo?.dismiss()
            }

        }.isDialog(true)
            .setContentTextSize(25)
            .setOutSideCancelable(false)
            .build()

        pvWindowInfo = OptionsPickerBuilder(context) { options1, options2, options3, v ->
            val windowInfo = viewModel.windows.value?.get(options1)
            windowInfo?.let {
                window?.text = windowInfo.windowName
                viewModel.chooseWindows(windowInfo)
            }

        }.setLayoutRes(R.layout.pickerview_options) {
            val tvCancel = it.findViewById<TextView>(R.id.tv_cancel)
            val tvFinish = it.findViewById<TextView>(R.id.tv_finish)
            tvCancel.setOnClickListener {
                pvWindowInfo?.dismiss()
            }
            tvFinish.setOnClickListener {
                pvWindowInfo?.returnData()
                pvWindowInfo?.dismiss()
            }

        }.isDialog(true)
            .setContentTextSize(25)
            .setOutSideCancelable(false)
            .build()
    }

    private fun checkChoose(): Boolean {
        if (viewModel.school.value == null) {
            ToastUtil.show("请选择学校或取消设置")
            return false
        }

        if (viewModel.kitchen.value == null) {
            ToastUtil.show("请选择食堂或取消设置")
            return false
        }

        if (viewModel.window.value == null) {
            ToastUtil.show("请选择窗口或取消设置")
            return false
        }

        return true
    }

    private fun setShow(config: Config?) {
        sn?.text = SpUtil.deviceId()
        school?.text = if (config == null) "" else config.schoolName
        kitchen?.text = if (config == null) "" else config.kitchenName
        window?.text = if (config == null) "" else config.windowName
    }

    override fun getHandler(): SimpleHandler {
        @Synchronized
        if (myHandler == null) {
            myHandler = FullDialogHandler()
        }
        return myHandler as FullDialogHandler
    }

    companion object {
        const val TAG = "SettingPop"
    }
}