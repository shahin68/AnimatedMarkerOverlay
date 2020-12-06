package com.marker.overlay.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.marker.overlay.R

class MainActivity: BaseActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}