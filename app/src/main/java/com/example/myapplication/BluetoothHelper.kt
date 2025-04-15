package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.widget.Toast

class BluetoothHelper(private val context: Context) {

    private val bluetoothAdapter : BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    fun isBluetoothSupported(): Boolean {
        return bluetoothAdapter != null
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun initBluetooth(): Boolean {
        return when {
            !isBluetoothSupported() -> {
                Toast.makeText(context, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show()
                false
            }
            !isBluetoothEnabled() -> {
                Toast.makeText(context, "Bluetooth is disabled", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

}