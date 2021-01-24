package com.originalstocks.blueserverapp.data.adapter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.originalstocks.blueserverapp.R
import com.originalstocks.blueserverapp.data.interfaces.DeviceSelectionInterface
import com.originalstocks.blueserverapp.utils.checkMajorDeviceType
import com.originalstocks.blueserverapp.utils.showToast
import kotlinx.android.synthetic.main.row_paired_device_item.view.*

class PairedDevicesAdapter(val context: Context, var deviceSelectionInterface: DeviceSelectionInterface) :
    RecyclerView.Adapter<PairedDevicesAdapter.DeviceViewHolder>() {
    private val TAG = "PairedDevicesAdapter"
    private var devicesList: ArrayList<BluetoothDevice> = ArrayList()

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_paired_device_item, parent, false)
        return DeviceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return devicesList.size
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devicesList[position]
        holder.itemView.tvDeviceName.text = device.name
        holder.itemView.tvDeviceAddress.text = device.address

        Log.i(TAG, "onBindViewHolder_device_category = ${device.bluetoothClass.majorDeviceClass}")
        val deviceType = checkMajorDeviceType(device.bluetoothClass.majorDeviceClass)
        holder.itemView.tvDeviceType.text = deviceType
        holder.itemView.setOnClickListener {
            //connect(device)
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
            deviceSelectionInterface.selectedDeviceInfo(device)
        }
    }


    fun addItems(list: MutableSet<BluetoothDevice>) {
        devicesList.clear()
        devicesList.addAll(list)
        notifyDataSetChanged()
    }
}