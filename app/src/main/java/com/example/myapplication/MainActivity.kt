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
import com.example.myapplication.PermissionHelper.getBluetoothPermissions
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
    private val bluetoothPermissions = arrayOf(
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

        if (PermissionHelper.requestPermissionsIfNeeded(this, bluetoothPermissions, bluetoothRequestCode)) {
            Toast.makeText(this, "Permissions Granted Successfully", Toast.LENGTH_SHORT).show()
            initBluetoothFeatures()
        }

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
        bluetoothHelper = BluetoothHelper(this)

        if (bluetoothHelper.initBluetooth()) {
            Toast.makeText(this, "Bluetooth is ready", Toast.LENGTH_SHORT).show()
            // Proceed to scanning in Step 2
        }
    }


}