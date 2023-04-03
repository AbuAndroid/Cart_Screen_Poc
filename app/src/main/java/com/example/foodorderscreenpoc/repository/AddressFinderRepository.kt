package com.example.foodorderscreenpoc.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Parcelable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.util.Locale

class AddressFinderRepository(private val context:Context) {

    suspend fun find(latitude:Double,longitude:Double):CurrentPlaceModel?{
        return try {
            val geoCoder = Geocoder(context,Locale.getDefault())
            val result:MutableList<Address>? = withContext(Dispatchers.IO){
                geoCoder.getFromLocation(latitude,longitude,1)
            }
            val address = result?.get(0)
            CurrentPlaceModel(
                latitude = latitude,
                longitude = longitude,
                address = address?.getAddressLine(0),
                locality = address?.locality,
                subLocality = address?.subLocality,
                adminArea = address?.adminArea,
                city = address?.locality,
                state = address?.adminArea,
                country = address?.countryName,
                postalCode = address?.postalCode
            )
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }


    @Parcelize
    data class CurrentPlaceModel(
        val latitude:Double = 0.0,
        val longitude: Double = 0.0,
        val address:String?,
        val locality:String?,
        val subLocality:String?,
        val adminArea :String?,
        val city:String?,
        val state:String?,
        val country:String?,
        val postalCode:String?
    ):Parcelable
}