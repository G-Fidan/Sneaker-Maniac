package com.example.sneaker_maniac

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
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

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        Properties.containerId = binding.mainContainer.id
        return super.onCreateView(name, context, attrs)
    }

    override fun onStart() {
        super.onStart()
        if (getSharedPreferences(
                App.appContext.getString(R.string.app_name),
                MODE_PRIVATE
            ).getString(
                "token",
                ""
            ) == ""
        ) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    Properties.containerId,
                    LoginFragment(),
                    LoginFragment::javaClass.name
                )
                .addToBackStack(LoginFragment::javaClass.name)
                .commitAllowingStateLoss()
        } else {
            supportFragmentManager
                .beginTransaction()
                .add(
                    Properties.containerId,
                    MainFragment(),
                    MainFragment::javaClass.name
                )
                .addToBackStack(MainFragment::javaClass.name)
                .commitAllowingStateLoss()
        }
    }
}