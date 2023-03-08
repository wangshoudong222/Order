package com.yun.orderPad.ui.meal.presentation

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yun.orderPad.databinding.ActivityPMealWaitBinding
import com.yun.orderPad.databinding.ActivityPSingleBinding
import com.yun.orderPad.event.ConfirmEvent
import com.yun.orderPad.model.COMMIT_STATE
import com.yun.orderPad.ui.meal.SetMealActivity
import com.yun.orderPad.ui.order.ErrorPop1
import com.yun.orderPad.ui.order.SingleActivity
import com.yun.orderPad.ui.order.SingleViewModel
import com.yun.orderPad.util.CommonUtils
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.MainThreadHandler
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.view.OrderAdapter
import com.yun.orderPad.view.SpaceItemDecoration
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal

class WaitPresentation(outerContext: Context?, display: Display?, ) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPMealWaitBinding
    private lateinit var viewModel: SingleViewModel
    private lateinit var activity: SetMealActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPMealWaitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
    }

    private fun initViewModel() {
        activity = ownerActivity as SetMealActivity
        viewModel = ViewModelProvider(activity).get(SingleViewModel::class.java)
        viewModel.config.observe(activity) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.pMealTitle.titleName.text = it.schoolName + " " + it.windowName
            }
        }
    }

    companion object {
        const val TAG = "WaitPresentation"
    }
}