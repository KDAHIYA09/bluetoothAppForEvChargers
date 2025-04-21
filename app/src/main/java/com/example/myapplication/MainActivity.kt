package com.example.myapplication

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.helpers.PermissionHelper.getBluetoothPermissions
import com.example.myapplication.adapters.bluetoothDevicesListAdapter
import com.example.myapplication.adapters.connectedDeviceDetailsAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.dataclasses.bluetoothDevicesListDataClass
import com.example.myapplication.dataclasses.connectedDeviceDetailsDataClass
import com.example.myapplication.helpers.BluetoothHelper
import com.example.myapplication.helpers.PermissionHelper


class MainActivity : AppCompatActivity(), BluetoothHelper.BluetoothScanListener {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var available_bluetooth_devices_adapter : bluetoothDevicesListAdapter
    private lateinit var connected_device_adapter : connectedDeviceDetailsAdapter
    private var deviceList = mutableListOf<bluetoothDevicesListDataClass>()
    private var connectedDeviceData = mutableListOf<connectedDeviceDetailsDataClass>()
    private var selectedDevice : bluetoothDevicesListDataClass? = null
    private val bluetoothPermissions11 = arrayOf(
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.BLUETOOTH_ADMIN,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    private val bluetoothPermissions12 = arrayOf(
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.BLUETOOTH_ADMIN,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT
    )
    private lateinit var bluetoothHelper: BluetoothHelper


    private val bluetoothRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (PermissionHelper.requestPermissionsIfNeeded(this, bluetoothPermissions12, bluetoothRequestCode)) {
                Log.d("MainActivity", "Permissions Granted Successfully")
                initBluetoothFeatures()
            }
        }else{
            if (PermissionHelper.requestPermissionsIfNeeded(this, bluetoothPermissions11, bluetoothRequestCode)) {
                Log.d("MainActivity", "Permissions Granted Successfully")
                initBluetoothFeatures()
            }
        }

        setupAvailableDevicesRecyclerView()
        setupConnectedDevicesRecyclerView()

        mainBinding.searchDeviceButtonId.setOnClickListener{
            handleSearchDeviceClick()
        }

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

        bluetoothHelper.connectToDevice(device.mac_code)
    }

    private fun handleSearchDeviceClick() {
        Log.d("MainActivity", "Searching for Bluetooth devices...")

        // UI updates before starting scan
        mainBinding.loadingProgressBar.visibility = View.VISIBLE
        mainBinding.availableDevicesListRv.visibility = View.GONE
        deviceList.clear()
        available_bluetooth_devices_adapter.notifyDataSetChanged()

        // Start real Bluetooth scan

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Bluetooth permissions not granted", Toast.LENGTH_SHORT).show()
                return
            }
        }
        bluetoothHelper.startDiscovery()
    }

    private fun getDummyBluetoothDevices(): List<bluetoothDevicesListDataClass> {
        return listOf(
            bluetoothDevicesListDataClass("EV Charger 1", "111"),
            bluetoothDevicesListDataClass("EV Charger 2", "222"),
            bluetoothDevicesListDataClass("EV Charger 3", "333")
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



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initBluetoothFeatures()
            } else {
                // Re-run the permission check to show rationale or settings dialog
                PermissionHelper.requestPermissionsIfNeeded(this, getBluetoothPermissions(), 1001)
            }
        }
    }



    private fun initBluetoothFeatures() {
        bluetoothHelper = BluetoothHelper(this, this)

        if (bluetoothHelper.initBluetooth()) {
            Toast.makeText(this, "Bluetooth is ready", Toast.LENGTH_SHORT).show()
            // Proceed to scanning in Step 2
        }
    }

//    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDeviceFound(device: BluetoothDevice) {
//        if (!PermissionHelper.hasBluetoothPermissions(this)) {
//            Toast.makeText(this, "Bluetooth permissions not granted", Toast.LENGTH_SHORT).show()
//            return
//        }
    Log.d("MainActivity", "Device found: ${device.name} - ${device.address}")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Bluetooth permissions not granted", Toast.LENGTH_SHORT).show()
            return
        }
    }

    Log.d("MainActivity", "Device adding")
        val deviceData = bluetoothDevicesListDataClass(
            name = device.name ?: "Unknown",
            mac_code = device.address
        )
        // Avoid duplicates
        if (deviceList.none { it.mac_code == deviceData.mac_code }) {
            deviceList.add(deviceData)
            available_bluetooth_devices_adapter.notifyItemInserted(deviceList.size - 1)
        }

        mainBinding.loadingProgressBar.visibility = View.GONE
        mainBinding.availableDevicesListRv.visibility = View.VISIBLE

    }

    override fun onScanStarted() {
        Log.d("MainActivity", "Bluetooth scan started")
        mainBinding.loadingProgressBar.visibility = View.VISIBLE
        deviceList.clear()
        available_bluetooth_devices_adapter.notifyDataSetChanged()
    }

    override fun onScanFinished() {
        Log.d("MainActivity", "Bluetooth scan finished")
        mainBinding.loadingProgressBar.visibility = View.GONE
        if (deviceList.isEmpty()) {
            Toast.makeText(this, "No devices found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        runOnUiThread {
            mainBinding.shimmerViewContainer.stopShimmer()
            mainBinding.shimmerViewContainer.visibility = View.GONE
            mainBinding.deviceDetailsRvId.visibility = View.VISIBLE
            mainBinding.disconnectDeviceButtonId.visibility = View.VISIBLE

            connectedDeviceData.clear()
            connectedDeviceData.addAll(getDummyDeviceData())
            connected_device_adapter.notifyDataSetChanged()
        }
    }

    override fun onConnectionFailed(device: BluetoothDevice, reason: String) {
        runOnUiThread {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                return@runOnUiThread
            }
            Toast.makeText(this, "Failed to connect to ${device.name ?: "device"}: $reason", Toast.LENGTH_LONG).show()
            resetToInitialState()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        bluetoothHelper.cleanup()
    }

}