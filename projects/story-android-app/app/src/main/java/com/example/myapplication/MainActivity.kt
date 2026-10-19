package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            //點擊後按鈕立刻消失
            binding.btnStart.visibility = View.GONE

            //睜眼轉場動畫
            binding.vFadeBlack.visibility = View.VISIBLE
            binding.vFadeBlack.animate()
                .alpha(1f)
                .setDuration(3000)
                .withEndAction {
                    val intent = Intent(this, SetupActivity::class.java)
                    startActivity(intent)

                    finish()
                }
                .start()
        }
    }
}