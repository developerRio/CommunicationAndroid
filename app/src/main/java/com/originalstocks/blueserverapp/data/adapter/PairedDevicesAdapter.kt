package com.originalstocks.blueserverapp.data.adapter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.originalstocks.blueserverapp.data.interfaces.DeviceSelectionInterface
import com.originalstocks.blueserverapp.databinding.RowPairedDeviceItemBinding
import com.originalstocks.blueserverapp.utils.checkMajorDeviceType

private const val TAG = "PairedDevicesAdapter"

class PairedDevicesAdapter(
    val context: Context,
    private var deviceSelectionInterface: DeviceSelectionInterface
) :
    RecyclerView.Adapter<PairedDevicesAdapter.DeviceViewHolder>() {
    private var devicesList: ArrayList<BluetoothDevice> = ArrayList()

    class DeviceViewHolder(val binding: RowPairedDeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(
            RowPairedDeviceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return devicesList.size
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devicesList[position]

        val deviceType = checkMajorDeviceType(device.bluetoothClass.majorDeviceClass)

        holder.binding.apply {
            tvDeviceName.text = device.name
            tvDeviceType.text = deviceType
            tvDeviceAddress.text = device.address
        }

        Log.i(TAG, "onBindViewHolder_device_category = ${device.bluetoothClass.majorDeviceClass}")
        holder.itemView.setOnClickListener {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
            deviceSelectionInterface.selectedDeviceInfo(device)
        }
    }


    fun addItems(list: MutableSet<BluetoothDevice>) {
        devicesList.clear()
        devicesList.addAll(list)
    }
}