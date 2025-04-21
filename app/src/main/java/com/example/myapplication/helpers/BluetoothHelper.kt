package com.example.myapplication.helpers

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.UUID
import kotlin.concurrent.thread

class BluetoothHelper(
    private val context: Context,
    private val listener: BluetoothScanListener,
) {
    //private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val bondReceiver = object : BroadcastReceiver() {
//        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
                val prevBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.BOND_NONE)

                if (bondState == BluetoothDevice.BOND_BONDED && prevBondState == BluetoothDevice.BOND_BONDING) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            Toast.makeText(context, "Bluetooth permissions not granted", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    Log.d("BluetoothHelper", "Bonded with ${device?.name}")
                    device?.address?.let { connectToDevice(it) }
                }
            }
        }
    }

    init {
        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        context.registerReceiver(bondReceiver, filter)
    }

    interface BluetoothScanListener {
        fun onDeviceFound(device: BluetoothDevice)
        fun onScanStarted()
        fun onScanFinished()
        fun onDeviceConnected(device: BluetoothDevice)
        fun onConnectionFailed(device: BluetoothDevice, reason: String)
    }

    private val bluetoothAdapter : BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private val foundDevices = mutableSetOf<String>()

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

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (!foundDevices.contains(it.address)) {
                            foundDevices.add(it.address)
                            listener.onDeviceFound(it)
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    listener.onScanFinished()
                    context?.unregisterReceiver(this)
                }
            }
        }
    }

    //@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startDiscovery() {
//        if (!PermissionHelper.hasBluetoothPermissions(context)) {
//            Toast.makeText(context, "Bluetooth permissions not granted", Toast.LENGTH_SHORT).show()
//            return
//        }
//        use this function to check permission for both categories of android devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context, "Bluetooth permissions not granted", Toast.LENGTH_SHORT).show()
                return
            }
        }
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter!!.cancelDiscovery()
        }

        foundDevices.clear()
        listener.onScanStarted()

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }

        context.registerReceiver(receiver, filter)
        bluetoothAdapter?.startDiscovery()
    }


    //add above check here
    //@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun cancelDiscovery() {
//        if (!PermissionHelper.hasBluetoothPermissions(context)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context, "Bluetooth permissions not granted", Toast.LENGTH_SHORT).show()
                return
            }
        }
        bluetoothAdapter?.cancelDiscovery()
        try {
            context.unregisterReceiver(receiver)
        } catch (_: Exception) { }
    }


    fun connectToDevice(macAddress: String) {
        val device = bluetoothAdapter?.getRemoteDevice(macAddress)
        device?.let {
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
//                != PackageManager.PERMISSION_GRANTED) {
//                Log.e("BluetoothConnection", "Bluetooth permissions not granted")
//                return
//            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(context, "Bluetooth permissions not granted", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            if (it.bondState != BluetoothDevice.BOND_BONDED) {
                Log.d("BluetoothConnection", "Device not bonded, initiating pairing...")
                it.createBond()
                return
            }

            thread {
                bluetoothAdapter?.cancelDiscovery()

                // Log device name and address
                Log.d("BluetoothConnection", "==========================================")
                Log.d("BluetoothConnection", "STARTING CONNECTION TRIALS")
                Log.d("BluetoothConnection", "Target device: ${it.name ?: "Unknown"} (${it.address})")

                // Try to get supported UUIDs
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    val uuids = it.uuids
                    if (uuids != null && uuids.isNotEmpty()) {
                        Log.d("BluetoothConnection", "Device advertises these UUIDs:")
                        for (uuid in uuids) {
                            Log.d("BluetoothConnection", "  - ${uuid.uuid}")
                        }
                    } else {
                        Log.d("BluetoothConnection", "No UUIDs advertised by device, will try common UUIDs")
                    }
                }

                // Try different UUIDs commonly used for Bluetooth communication
                val uuids = listOf(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"), // SPP
                    UUID.fromString("00001103-0000-1000-8000-00805F9B34FB"), // DIALER
                    UUID.fromString("0000110A-0000-1000-8000-00805F9B34FB"), // A2DP
                    UUID.fromString("00001115-0000-1000-8000-00805F9B34FB"), // PANU
                    UUID.fromString("00001118-0000-1000-8000-00805F9B34FB"), // Device ID
                    UUID.fromString("0000110C-0000-1000-8000-00805F9B34FB"), // AVDTP
                    UUID.fromString("00001112-0000-1000-8000-00805F9B34FB")  // HID
                )

                var connected = false

                // First try with secure socket
                Log.d("BluetoothConnection", "==========================================")
                Log.d("BluetoothConnection", "TRYING SECURE CONNECTIONS")

                for (uuid in uuids) {
                    Log.d("BluetoothConnection", "------------------------------------------")
                    Log.d("BluetoothConnection", "TRIAL: Secure socket with UUID: $uuid")

                    try {
                        val socket = it.createRfcommSocketToServiceRecord(uuid)
                        Log.d("BluetoothConnection", "Socket created, attempting connect()...")

                        try {
                            socket.connect()
                            Log.d("BluetoothConnection", "SUCCESS! Connected with UUID: $uuid")
                            connected = true

                            listener.onDeviceConnected(it)
                            Log.d("BluetoothConnection", "Notified UI of successful connection")
                            Log.d("BluetoothConnection", "Attempting to read data...")

                            // Successfully connected, now try to read data
                            val inputStream = socket.inputStream
                            val buffer = ByteArray(1024)

                            try {
                                if (inputStream.available() > 0) {
                                    Log.d("BluetoothConnection", "Data is immediately available!")
                                } else {
                                    Log.d("BluetoothConnection", "No data immediately available, waiting...")
                                }

                                // Try reading a few times
                                var readAttempts = 0
                                var dataReceived = false

                                while (readAttempts < 5 && !dataReceived) {
                                    readAttempts++
                                    Log.d("BluetoothConnection", "Read attempt $readAttempts...")

                                    val bytesRead = inputStream.read(buffer)
                                    if (bytesRead > 0) {
                                        val received = buffer.decodeToString(0, bytesRead)
                                        Log.d("BluetoothConnection", "SUCCESS! Data received: $received")
                                        dataReceived = true
                                    } else if (bytesRead == -1) {
                                        Log.d("BluetoothConnection", "End of stream reached")
                                        break
                                    } else {
                                        Log.d("BluetoothConnection", "No data read, waiting...")
                                        Thread.sleep(1000)  // Wait a second between reads
                                    }
                                }

                                if (!dataReceived) {
                                    Log.d("BluetoothConnection", "No data received after $readAttempts attempts")
                                }

                            } catch (e: IOException) {
                                Log.e("BluetoothConnection", "Error reading: ${e.message}")
                            } finally {
                                Log.d("BluetoothConnection", "Closing socket")
                                try { socket.close() } catch (e: IOException) { }
                            }

                            break  // Exit the loop if we successfully connected
                        } catch (e: IOException) {
                            Log.e("BluetoothConnection", "FAILED: Connection failed with UUID $uuid: ${e.message}")
                            try { socket.close() } catch (e2: IOException) { }
                        }
                    } catch (e: Exception) {
                        Log.e("BluetoothConnection", "FAILED: Error creating socket with UUID $uuid: ${e.message}")
                    }

                    Log.d("BluetoothConnection", "Moving to next UUID...")
                }

                // If secure socket failed, try with insecure socket
                if (!connected) {
                    Log.d("BluetoothConnection", "==========================================")
                    Log.d("BluetoothConnection", "TRYING INSECURE CONNECTIONS")

                    for (uuid in uuids) {
                        Log.d("BluetoothConnection", "------------------------------------------")
                        Log.d("BluetoothConnection", "TRIAL: Insecure socket with UUID: $uuid")

                        try {
                            val socket = it.createInsecureRfcommSocketToServiceRecord(uuid)
                            Log.d("BluetoothConnection", "Insecure socket created, attempting connect()...")

                            try {
                                socket.connect()
                                Log.d("BluetoothConnection", "SUCCESS! Connected with insecure socket and UUID: $uuid")
                                connected = true

                                listener.onDeviceConnected(it)
                                Log.d("BluetoothConnection", "Notified UI of successful connection")
                                Log.d("BluetoothConnection", "Attempting to read data...")

                                // Successfully connected, now try to read data
                                val inputStream = socket.inputStream
                                val buffer = ByteArray(1024)

                                try {
                                    if (inputStream.available() > 0) {
                                        Log.d("BluetoothConnection", "Data is immediately available!")
                                    } else {
                                        Log.d("BluetoothConnection", "No data immediately available, waiting...")
                                    }

                                    // Try reading a few times
                                    var readAttempts = 0
                                    var dataReceived = false

                                    while (readAttempts < 5 && !dataReceived) {
                                        readAttempts++
                                        Log.d("BluetoothConnection", "Read attempt $readAttempts...")

                                        val bytesRead = inputStream.read(buffer)
                                        if (bytesRead > 0) {
                                            val received = buffer.decodeToString(0, bytesRead)
                                            Log.d("BluetoothConnection", "SUCCESS! Data received: $received")
                                            dataReceived = true
                                        } else if (bytesRead == -1) {
                                            Log.d("BluetoothConnection", "End of stream reached")
                                            break
                                        } else {
                                            Log.d("BluetoothConnection", "No data read, waiting...")
                                            Thread.sleep(1000)  // Wait a second between reads
                                        }
                                    }

                                    if (!dataReceived) {
                                        Log.d("BluetoothConnection", "No data received after $readAttempts attempts")
                                    }

                                } catch (e: IOException) {
                                    Log.e("BluetoothConnection", "Error reading: ${e.message}")
                                } finally {
                                    Log.d("BluetoothConnection", "Closing socket")
                                    try { socket.close() } catch (e: IOException) { }
                                }

                                break  // Exit the loop if we successfully connected
                            } catch (e: IOException) {
                                Log.e("BluetoothConnection", "FAILED: Insecure connection failed with UUID $uuid: ${e.message}")
                                try { socket.close() } catch (e2: IOException) { }
                            }
                        } catch (e: Exception) {
                            Log.e("BluetoothConnection", "FAILED: Error creating insecure socket with UUID $uuid: ${e.message}")
                        }

                        Log.d("BluetoothConnection", "Moving to next UUID...")
                    }
                }

                Log.d("BluetoothConnection", "==========================================")
                if (connected) {
                    Log.d("BluetoothConnection", "CONNECTION PROCESS COMPLETED: Successfully connected to device")
                } else {
                    Log.e("BluetoothConnection", "CONNECTION PROCESS COMPLETED: Failed to connect with any UUID")
                    listener.onConnectionFailed(it, "Could not connect to device with any known profile")
                }
            }
        }
    }


    fun cleanup() {
        try {
            context.unregisterReceiver(bondReceiver)
        } catch (e: Exception) {
            Log.e("BluetoothHelper", "Receiver already unregistered: ${e.message}")
        }
    }


}


