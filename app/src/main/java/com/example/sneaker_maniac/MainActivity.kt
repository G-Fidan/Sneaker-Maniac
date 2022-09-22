package com.example.sneaker_maniac

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sneaker_maniac.databinding.ActivityMainBinding
import com.example.sneaker_maniac.fragments.login.LoginFragment
import com.example.sneaker_maniac.fragments.main.MainFragment

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        if (getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getString(
                "token",
                ""
            ) == ""
        ) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    binding.mainContainer.id,
                    LoginFragment(),
                    LoginFragment::javaClass.name
                )
                .addToBackStack(LoginFragment::javaClass.name)
                .commitAllowingStateLoss()
        } else {
            supportFragmentManager
                .beginTransaction()
                .add(
                    binding.mainContainer.id,
                    MainFragment(),
                    MainFragment::javaClass.name
                )
                .addToBackStack(MainFragment::javaClass.name)
                .commitAllowingStateLoss()
        }
    }
}