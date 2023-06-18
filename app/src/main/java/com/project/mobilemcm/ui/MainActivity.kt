package com.project.mobilemcm.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.project.mobilemcm.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHost =
        supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        handleBackPressed(navHost)
    }

    private fun handleBackPressed(navHost: NavHostFragment) {
        onBackPressedDispatcher.addCallback(this) {
            val backStack = navHost.childFragmentManager.backStackEntryCount
            if (backStack == 0) {
                finish()
            } else {
               // if(navHost.navController.currentDestination==navHost.navController.findDestination(""))
                navHost.navController.navigateUp()
            }
        }
    }
}