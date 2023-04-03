package com.example.foodorderscreenpoc.di.module

import com.example.foodorderscreenpoc.repository.AddressFinderRepository
import com.example.foodorderscreenpoc.ui.orderscreen.mapsreen.MapsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Test {
    fun modules() = repositoryModule + viewModelModule
}

val repositoryModule = module {
    viewModel{
        MapsViewModel(get())
    }
}

val viewModelModule = module {
    single {
        AddressFinderRepository(androidContext())
    }
}