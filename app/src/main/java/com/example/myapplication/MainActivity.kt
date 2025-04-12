package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.bluetoothDevicesListAdapter
import com.example.myapplication.adapters.connectedDeviceDetailsAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.dataclasses.bluetoothDevicesListDataClass
import com.example.myapplication.dataclasses.connectedDeviceDetailsDataClass

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var available_bluetooth_devices_adapter : bluetoothDevicesListAdapter
    private var deviceList = mutableListOf<bluetoothDevicesListDataClass>()
    private var selectedDevice : bluetoothDevicesListDataClass? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

       //available devices adapter
        available_bluetooth_devices_adapter = bluetoothDevicesListAdapter(deviceList){
            device -> selectedDevice = device
            //Toast.makeText(this, "Conneting to ${device.name}", Toast.LENGTH_SHORT).show()

            mainBinding.availableDevicesListRv.visibility = View.GONE
            mainBinding.connectedDeviceDetailLlId.visibility = View.VISIBLE

        }
        mainBinding.availableDevicesListRv.adapter = available_bluetooth_devices_adapter
        mainBinding.availableDevicesListRv.layoutManager = LinearLayoutManager(this)

        //connected device adapter
        val connected_device_adapter = connectedDeviceDetailsAdapter(connectedDeviceDetails)
        mainBinding.deviceDetailsRvId.adapter = connected_device_adapter
        mainBinding.deviceDetailsRvId.layoutManager = LinearLayoutManager(this)

        mainBinding.searchDeviceButtonId.setOnClickListener{
            Toast.makeText(this, getString(R.string.search_device_toast_txt), Toast.LENGTH_SHORT).show()

            //search bluetooth fxn will be implemented here
            //dummy data for now
            val dummyDevices = getDummyBluetoothDevices()
            deviceList.clear()
            deviceList.addAll(dummyDevices)
            available_bluetooth_devices_adapter.notifyDataSetChanged()

            //after list is fetched display it in recycler view
            mainBinding.availableDevicesListRv.visibility = View.VISIBLE




        }





    }

    private fun getDummyBluetoothDevices(): List<bluetoothDevicesListDataClass> {
        return listOf(
            bluetoothDevicesListDataClass("EV Charger 1"),
            bluetoothDevicesListDataClass("EV Charger 2"),
            bluetoothDevicesListDataClass("EV Charger 3")
        )
    }

    val connectedDeviceDetails = listOf(
        connectedDeviceDetailsDataClass("Device Name", "EV Charger X100"),
        connectedDeviceDetailsDataClass("MAC Address", "00:11:22:33:44:55"),
        connectedDeviceDetailsDataClass("Signal Strength", "-65 dBm"),
        connectedDeviceDetailsDataClass("Battery Level", "85%"),
        connectedDeviceDetailsDataClass("Charging Status", "Connected"),
        connectedDeviceDetailsDataClass("Firmware Version", "v1.2.3"),
        connectedDeviceDetailsDataClass("Last Synced", "10 Apr 2025, 11:00 PM")
    )

}