package com.julihe.order.ui.simple.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.julihe.order.R
import com.julihe.order.databinding.FragmentPayErrorBinding
import com.julihe.order.databinding.FragmentSimplePayErrorBinding
import com.julihe.order.model.COMMIT_STATE
import com.julihe.order.ui.order.SingleViewModel
import com.julihe.order.ui.simple.SimpleViewModel
import com.julihe.order.util.MainThreadHandler

/**
 * 支付失败
 */
class SimplePayErrorFragment : Fragment() {

    private lateinit var binding : FragmentSimplePayErrorBinding
    private lateinit var viewModel: SimpleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSimplePayErrorBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initViewModel() {
        MainThreadHandler.postDelayed(REORDER, {
            viewModel.checkState(COMMIT_STATE.REORDER)
        },5000)

        viewModel = ViewModelProvider(activity!!).get(SimpleViewModel::class.java)
        viewModel.sum.observe(activity!!){
            if (it != null) {
                binding.tvSum.text = it
            }
        }

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
        binding.errorMsg.text = viewModel.errorMsg.value

        binding.btnBack.setOnClickListener {
            MainThreadHandler.removeCallbacks(REORDER)
            viewModel.checkState(COMMIT_STATE.REORDER)
        }
    }

    companion object {
        fun newInstance() = SimplePayErrorFragment()

        const val TAG = "SimplePayErrorFragment"
        const val REORDER = "SimplePayErrorFragment_REORDER"

    }

}