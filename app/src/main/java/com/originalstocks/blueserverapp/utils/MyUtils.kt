package com.originalstocks.blueserverapp.utils

import android.bluetooth.BluetoothClass
import android.content.Context
import android.widget.Toast

const val REQUEST_ENABLE_BLUETOOTH = 101
const val REQUEST_DISCOVERABLE_BLUETOOTH = 102
const val REQUEST_PERMISSION_LOCATION = 103

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun checkMajorDeviceType(deviceClassMajor: Int): String {
    var deviceType = "NA"

    when (deviceClassMajor) {
        BluetoothClass.Device.Major.AUDIO_VIDEO -> {
            deviceType = "AUDIO_VIDEO"
        }
        BluetoothClass.Device.Major.COMPUTER -> {
            deviceType = "COMPUTER"
        }
        BluetoothClass.Device.Major.HEALTH -> {
            deviceType = "HEALTH"
        }
        BluetoothClass.Device.Major.IMAGING -> {
            deviceType = "IMAGING"
        }
        BluetoothClass.Device.Major.MISC -> {
            deviceType = "MISC"
        }
        BluetoothClass.Device.Major.NETWORKING -> {
            deviceType = "NETWORKING"
        }
        BluetoothClass.Device.Major.PERIPHERAL -> {
            deviceType = "PERIPHERAL"
        }
        BluetoothClass.Device.Major.PHONE -> {
            deviceType = "PHONE"
        }
        BluetoothClass.Device.Major.TOY -> {
            deviceType = "TOY"
        }
        BluetoothClass.Device.Major.WEARABLE -> {
            deviceType = "WEARABLE"
        }
        BluetoothClass.Device.Major.UNCATEGORIZED -> {
            deviceType = "UNCATEGORIZED"
        }

    }

    return deviceType
}
