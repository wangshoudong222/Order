package com.yun.orderPad.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.yun.orderPad.BuildConfig
import com.yun.orderPad.databinding.ActivityLoginBinding
import com.yun.orderPad.ui.BaseActivity
import com.yun.orderPad.ui.BindActivity
import com.yun.orderPad.util.sp.SpUtil

class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val btnLogin = binding.btnLogin
        val btnOffline = binding.btnOffline

        binding.version.text = BuildConfig.VERSION_NAME

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.loginResult.observe(this@LoginActivity) {
            SpUtil.token(it?.token)
            updateUiWithUser()
            loginSuccess()
        }

        loginViewModel.loginError.observe(this@LoginActivity) {
            showLoginFailed(it)
        }

        btnLogin.setOnClickListener {
            if (TextUtils.isEmpty(username.text.toString())) {
                showLoginFailed("请输入用户名")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password.text.toString())) {
                showLoginFailed("请输入密码")
                return@setOnClickListener
            }

            loginViewModel.login(username.text.toString(), password.text.toString())
        }
    }

    private fun loginSuccess() {
        startActivity(Intent(this, BindActivity::class.java))
        finish()
    }

    private fun updateUiWithUser() {
        Toast.makeText(
            applicationContext,
            "登录成功",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(errorString: String?) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}