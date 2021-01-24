package com.originalstocks.blueserverapp.data.interfaces

import android.bluetooth.BluetoothDevice

interface DeviceSelectionInterface {
    fun selectedDeviceInfo(device: BluetoothDevice)
}