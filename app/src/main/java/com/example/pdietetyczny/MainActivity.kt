package com.example.pdietetyczny

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pdietetyczny.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var click = 0
    private var textsize = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myButton.setOnClickListener{
            click += 1
            if (textsize<200) {
                textsize += 5
            }
            binding.myText.textSize = textsize.toFloat()
            binding.myText.text = click.toString()
        }


        //binding.myButton.setOnClickListener{
        //    binding.myText.text = "zmiana!"
        //}
    }

    override fun onStart() {
        super.onStart()
        Log.d("Stan aplikacji", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Stan aplikacji", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Stan aplikacji", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Stan aplikacji", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Stan aplikacji", "onDestroy")
    }
}