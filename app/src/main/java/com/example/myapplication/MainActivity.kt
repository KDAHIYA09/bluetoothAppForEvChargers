package com.example.myapplication

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private lateinit var connected_device_adapter : connectedDeviceDetailsAdapter
    private var deviceList = mutableListOf<bluetoothDevicesListDataClass>()
    private var connectedDeviceData = mutableListOf<connectedDeviceDetailsDataClass>()
    private var selectedDevice : bluetoothDevicesListDataClass? = null
    private val bluetoothPermissionsRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        ckeckAndRequestPermission()

        setupAvailableDevicesRecyclerView()
        setupConnectedDevicesRecyclerView()
        handleSearchDeviceClick()

        mainBinding.disconnectDeviceButtonId.setOnClickListener{
            resetToInitialState()
            mainBinding.disconnectDeviceButtonId.visibility = View.GONE
        }





    }

    private fun setupAvailableDevicesRecyclerView() {
        available_bluetooth_devices_adapter = bluetoothDevicesListAdapter(deviceList) { device ->
            handleDeviceSelection(device)
        }
        mainBinding.availableDevicesListRv.adapter = available_bluetooth_devices_adapter
        mainBinding.availableDevicesListRv.layoutManager = LinearLayoutManager(this)
    }

    private fun setupConnectedDevicesRecyclerView() {
        connected_device_adapter = connectedDeviceDetailsAdapter(connectedDeviceData)
        mainBinding.deviceDetailsRvId.adapter = connected_device_adapter
        mainBinding.deviceDetailsRvId.layoutManager = LinearLayoutManager(this)
    }

    private fun handleDeviceSelection(device: bluetoothDevicesListDataClass) {
        selectedDevice = device

        mainBinding.availableDevicesListRv.visibility = View.GONE
        mainBinding.searchDeviceButtonId.isEnabled = false
        mainBinding.connectedDeviceDetailLlId.visibility = View.VISIBLE
        mainBinding.shimmerViewContainer.startShimmer()
        mainBinding.shimmerViewContainer.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            val dummyData = getDummyDeviceData()
            connectedDeviceData.clear()
            connectedDeviceData.addAll(dummyData)
            connected_device_adapter.notifyDataSetChanged()

            mainBinding.shimmerViewContainer.stopShimmer()
            mainBinding.shimmerViewContainer.visibility = View.GONE
            mainBinding.deviceDetailsRvId.visibility = View.VISIBLE
            mainBinding.disconnectDeviceButtonId.visibility = View.VISIBLE
        }, 1500)
    }

    private fun handleSearchDeviceClick() {
        mainBinding.searchDeviceButtonId.setOnClickListener {
            Toast.makeText(this, getString(R.string.search_device_toast_txt), Toast.LENGTH_SHORT).show()
            mainBinding.loadingProgressBar.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                val dummyDevices = getDummyBluetoothDevices()
                deviceList.clear()
                deviceList.addAll(dummyDevices)
                available_bluetooth_devices_adapter.notifyDataSetChanged()

                mainBinding.loadingProgressBar.visibility = View.GONE
                mainBinding.availableDevicesListRv.visibility = View.VISIBLE
            }, 1500)
        }
    }

    private fun getDummyBluetoothDevices(): List<bluetoothDevicesListDataClass> {
        return listOf(
            bluetoothDevicesListDataClass("EV Charger 1"),
            bluetoothDevicesListDataClass("EV Charger 2"),
            bluetoothDevicesListDataClass("EV Charger 3")
        )
    }

    private fun getDummyDeviceData() : List<connectedDeviceDetailsDataClass>{
        return  listOf(
            connectedDeviceDetailsDataClass("Device Name", "EV Charger X100"),
            connectedDeviceDetailsDataClass("MAC Address", "00:11:22:33:44:55"),
            connectedDeviceDetailsDataClass("Signal Strength", "-65 dBm"),
            connectedDeviceDetailsDataClass("Battery Level", "85%"),
            connectedDeviceDetailsDataClass("Charging Status", "Connected"),
            connectedDeviceDetailsDataClass("Firmware Version", "v1.2.3"),
            connectedDeviceDetailsDataClass("Last Synced", "10 Apr 2025, 11:00 PM")
    )}

    // BELOW other private functions
    private fun resetToInitialState() {
        selectedDevice = null
        connectedDeviceData.clear()
        connected_device_adapter.notifyDataSetChanged()
        mainBinding.deviceDetailsRvId.visibility = View.GONE
        mainBinding.connectedDeviceDetailLlId.visibility = View.GONE
        mainBinding.deviceDetailsRvId.visibility = View.GONE
        mainBinding.shimmerViewContainer.stopShimmer()
        mainBinding.shimmerViewContainer.visibility = View.GONE
        mainBinding.searchDeviceButtonId.isEnabled = true
    }

    //for permission check
    private fun ckeckAndRequestPermission(){
        val permissions = mutableListOf<String>()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            permissions.add(android.Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(android.Manifest.permission.BLUETOOTH_CONNECT)
        }else{
            permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            permissions.add(android.Manifest.permission.BLUETOOTH)
            permissions.add(android.Manifest.permission.BLUETOOTH_ADMIN)
        }

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if(missingPermissions.isNotEmpty()){
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), bluetoothPermissionsRequestCode)
        }else{
            initBluetoothFeatures()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == bluetoothPermissionsRequestCode){
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if(allGranted){
                initBluetoothFeatures()
            }else{
                Toast.makeText(this, "Bluetooth permissions are required to use this app", Toast.LENGTH_SHORT).show()
                showPermissionRequiredDialog()
            }
        }
    }

    private fun showPermissionRequiredDialog() {
        AlertDialog.Builder(this)
            .setTitle("Bluetooth Permissions Required")
            .setMessage("This app needs Bluetooth permissions to function. Please grant them to continue. Or exit application.")
            .setCancelable(false)
            .setPositiveButton("Grant Permissions"){
                _, _ -> ckeckAndRequestPermission()
            }
            .setNegativeButton("Exit App"){
                _,_ -> finishAffinity()
            }
            .show()
    }

    private fun initBluetoothFeatures() {
        Toast.makeText(this, "Permissions Granted Successfully", Toast.LENGTH_SHORT).show()
    }


}