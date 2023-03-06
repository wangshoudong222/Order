package com.yun.orderPad.ui.order.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.yun.orderPad.R
import com.yun.orderPad.databinding.FragmentPayErrorBinding
import com.yun.orderPad.model.COMMIT_STATE
import com.yun.orderPad.ui.order.SingleViewModel
import com.yun.orderPad.util.MainThreadHandler

/**
 * 支付失败
 */
class SinglePayErrorFragment : Fragment() {

    private lateinit var binding : FragmentPayErrorBinding
    private lateinit var viewModel: SingleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPayErrorBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initViewModel() {
        MainThreadHandler.postDelayed(TAG, {
            viewModel.checkState(COMMIT_STATE.REORDER)
        },5000)

        viewModel = ViewModelProvider(activity!!).get(SingleViewModel::class.java)
        viewModel.sum.observe(activity!!){
            binding.tvSum.text = it
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
        binding.errorMsg.text = viewModel.errorMsg.value

        binding.btnBack.setOnClickListener {
            MainThreadHandler.removeCallbacks(TAG)
            viewModel.checkState(COMMIT_STATE.REORDER)
        }
    }

    companion object {
        fun newInstance() = SinglePayErrorFragment()

        const val TAG = "SinglePayErrorFragment"
    }

}