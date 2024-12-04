package com.example.pdietetyczny

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pdietetyczny.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainVm by viewModels<MainViewModel> ()
    //private var click = 0
    //private var textsize = 20
    private val CLICK_KEY = "CLICK_KEY"
    private val home = "Rozpocznij odliczanie"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        if (savedInstanceState != null){
            click = savedInstanceState.getInt(CLICK_KEY)
            textsize += savedInstanceState.getInt(CLICK_KEY)*5
            binding.myText.text = click.toString()
            binding.myText.textSize = textsize.toFloat()
        }
        */
        binding.myText.text = mainVm.click.toString()
        binding.myText.textSize = mainVm.textsize.toFloat()

        binding.myButton.setOnClickListener{
            mainVm.click += 1
            if (mainVm.textsize<200) {
                mainVm.textsize += 5
            }
            binding.myText.textSize = mainVm.textsize.toFloat()
            binding.myText.text = mainVm.click.toString()
        }


    }

        //override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
        //outState.putInt(CLICK_KEY, click)
        //}

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