package com.example.foodorderscreenpoc.ui.orderscreen.mapsreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderscreenpoc.repository.AddressFinderRepository
import kotlinx.coroutines.launch

class MapsViewModel(private val addressFinderRepository: AddressFinderRepository) : ViewModel() {

    private val currentLocationLD:MutableLiveData<String?> = MutableLiveData<String?>()
    val currentAddress:LiveData<String?> = currentLocationLD

    fun getCurrentLocation(latitude: Double, longitude: Double){
        viewModelScope.launch {
            val currentAddress = addressFinderRepository.find(latitude, longitude)
            currentLocationLD.value = currentAddress?.address
        }
    }
}