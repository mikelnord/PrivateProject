package com.project.mobilemcm.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.mobilemcm.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

//    override fun onStart() {
//        super.onStart()
//        if (requestedOrientation !=
//            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        ) {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        }
//
//    }
}
