package com.example.foodorderscreenpoc.ui.orderscreen.orderscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodorderscreenpoc.databinding.ActivityMainBinding
import com.example.foodorderscreenpoc.ui.orderscreen.mapsreen.MapsActivity

class OrderScreenActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val getLocationDetainsFromMapActivity by lazy {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode==100){
                val intent = result.data
                if(intent!=null){
                   val deliveryLocation =  intent.getStringExtra("selectedLocation")
                    binding.uiTvDeliveryAddress.text = deliveryLocation
                }
            }
            else if(result.resultCode == 200){
                val intent = result.data
                if(intent!=null){
                    val pickUpLocation = intent.getStringExtra("selectedLocation")
                    binding.uiTvPickupAddress.text = pickUpLocation
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getLocationDetainsFromMapActivity
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.uiBtSelectDeliveryLocation.setOnClickListener {
            val intent = Intent(this,MapsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("Title","SelectDeliveryUpLocation")
            intent.putExtras(bundle)
            getLocationDetainsFromMapActivity.launch(intent)
        }

        binding.uiBtSelectPickupLocation.setOnClickListener {
            val intent = Intent(this,MapsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("Title","SelectPickUpLocation")
            intent.putExtras(bundle)
            getLocationDetainsFromMapActivity.launch(intent)
        }

        binding.uiIvEditDeliveryAddress.setOnClickListener {
            Log.e("clicked","edit Clicked")
        }
    }
}