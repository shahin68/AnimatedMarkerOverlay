package com.shahin.overlay.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.shahin.overlay.R

class MainActivity: BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}