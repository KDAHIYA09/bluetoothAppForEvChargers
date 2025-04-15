package com.example.myapplication


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionHelper {

    private const val PREFS_NAME = "permission_prefs"
    private const val FIRST_REQUEST_KEY = "first_request_done"

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getBluetoothPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    fun requestPermissionsIfNeeded(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ): Boolean {
        val sharedPrefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val isFirstRequest = !sharedPrefs.getBoolean(FIRST_REQUEST_KEY, false)

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        return if (missingPermissions.isNotEmpty()) {

            if (isFirstRequest) {
                editor.putBoolean(FIRST_REQUEST_KEY, true).apply()
                ActivityCompat.requestPermissions(activity, missingPermissions.toTypedArray(), requestCode)
            } else {
                val permanentlyDenied = missingPermissions.any {
                    !ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
                }

                if (permanentlyDenied) {
                    showSettingsDialog(activity)
                } else {
                    ActivityCompat.requestPermissions(activity, missingPermissions.toTypedArray(), requestCode)
                }
            }

            false
        } else {
            true
        }
    }

    private fun showSettingsDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("Permissions Required")
            .setMessage("Please grant the required permissions in app settings.")
            .setCancelable(false)
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", activity.packageName, null)
                activity.startActivity(intent)
            }
            .setNegativeButton("Exit") { _, _ ->
                activity.finishAffinity()
            }
            .show()
    }
}

