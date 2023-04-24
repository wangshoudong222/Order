package com.julihe.order.ui.simple.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.julihe.order.R
import com.julihe.order.databinding.FragmentSimpleConfirmBinding
import com.julihe.order.databinding.FragmentSimplePaySucessBinding
import com.julihe.order.model.COMMIT_STATE
import com.julihe.order.ui.simple.SimpleViewModel
import com.julihe.order.util.CommonUtils
import com.julihe.order.util.MainThreadHandler

/**
 * 支付成功
 */
class SimplePaySuccessFragment : Fragment() {

    private lateinit var binding : FragmentSimplePaySucessBinding
    private lateinit var viewModel: SimpleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSimplePaySucessBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(activity!!).get(SimpleViewModel::class.java)
        MainThreadHandler.postDelayed(REORDER,{
            viewModel.checkState(COMMIT_STATE.REORDER)
        },5000)

        viewModel.state.observe(this) {
            if (it == COMMIT_STATE.REORDER) {
                MainThreadHandler.removeCallbacks(REORDER)
            }
        }
    }

    private fun initView() {
        viewModel.student.value?.let {
            val info = it.numberOfClassName + " " + it.gradeName + " " + it.className + " "  +it.studentNo
            binding.name.text = it.studentName
            binding.school.text = it.schoolName
            binding.info.text = info
            Glide.with(activity!!).load(it.avatar).apply(RequestOptions.bitmapTransform(CircleCrop())).placeholder(R.drawable.head_normal).into(binding.icon)
        }
        binding.tvNum.text = viewModel.count.value
        binding.time.text = CommonUtils.formatToDate(System.currentTimeMillis())
    }


    companion object {
        fun newInstance() = SimplePaySuccessFragment()
        const val TAG = "FragmentSimplePaySucessBinding"
        const val REORDER = "FragmentSimplePaySucessBinding_REORDER"
    }

}