package com.marker.overlay.di

import com.marker.overlay.data.MapsRepository
import com.marker.overlay.data.MapsRepositoryImpl
import com.marker.overlay.ui.fragments.main.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    viewModel { HomeViewModel(get()) }
    factory<MapsRepository> { MapsRepositoryImpl() }
}