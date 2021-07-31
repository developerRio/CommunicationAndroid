package com.originalstocks.blueserverapp.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.originalstocks.blueserverapp.IBluetoothA2dp
import com.originalstocks.blueserverapp.R
import com.originalstocks.blueserverapp.data.adapter.PairedDevicesAdapter
import com.originalstocks.blueserverapp.data.interfaces.DeviceSelectionInterface
import com.originalstocks.blueserverapp.databinding.FragmentSelectDeviceBinding
import com.originalstocks.blueserverapp.utils.REQUEST_DISCOVERABLE_BLUETOOTH
import com.originalstocks.blueserverapp.utils.REQUEST_ENABLE_BLUETOOTH
import com.originalstocks.blueserverapp.utils.showToast


class SelectDeviceFragment : Fragment(), DeviceSelectionInterface {
    private val TAG = "SelectDeviceFragment"

    //class to connect to an A2dp device
    private lateinit var a2dp: BluetoothA2dp
    private lateinit var ia2dp: IBluetoothA2dp

    private lateinit var binding: FragmentSelectDeviceBinding
    private var isEnabled: Boolean = false
    private lateinit var devicesAdapter: PairedDevicesAdapter
    var mBTDevicesList: ArrayList<BluetoothDevice> = ArrayList()
    private lateinit var mBluetoothAdapter: BluetoothAdapter

    private val mNewDeviceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    Log.i(
                        TAG,
                        "onReceive_mNewDeviceReceiver name = $deviceName address = $deviceHardwareAddress"
                    )
                    showToast(context, "Paired with $deviceName successfully.")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelectDeviceBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBTDevicesList = ArrayList()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        /*toggle bluetooth switch*/
        if (mBluetoothAdapter.isEnabled) {
            binding.toggleBluetoothButton.isChecked = true
            isEnabled = true
        }
        binding.toggleBluetoothButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!mBluetoothAdapter.isEnabled) {
                    //turn bluetooth on
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
                    isEnabled = true
                }
            } else {
                if (isEnabled) {
                    //turn bluetooth off
                    mBluetoothAdapter.disable()
                    isEnabled = false
                }
            }
        }


        /** pairing a new device */
        binding.pairNewDeviceButton.setOnClickListener {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_BLUETOOTH)
            /* also registering a receiver to listen when a new device is found*/
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            requireActivity().registerReceiver(mNewDeviceReceiver, filter)
        }

        /* advertisements check support */
        if (mBluetoothAdapter.isMultipleAdvertisementSupported) {
            Log.i(TAG, "onViewCreated_isMultipleAdvertisementSupported TRUE ")
        } else {
            Log.e(TAG, "onViewCreated_isMultipleAdvertisementSupported False ")
        }
        /* unfortunately advertisements part isn't supported in my both devices as Nokia 7.2, Motorola G9 power :( */
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(mNewDeviceReceiver)
    }

    override fun onResume() {
        super.onResume()
        if (isEnabled) {
            Log.i(TAG, "onResume_bluetooth_isEnabled $isEnabled ")
            setRecyclerview(fetchingPairedDeviceList())
        } else {
            Log.e(TAG, "onResume_bluetooth_isEnabled $isEnabled ")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Log.i(TAG, "onActivityResult_connection permission granted")
                // Permission granted, fetch the device list
                setRecyclerview(fetchingPairedDeviceList())
            } else {
                Log.e(TAG, "onActivityResult_connection permission denied")
                // user denied bluetooth discoverable permission, ask again if required
            }
        } else if (requestCode == REQUEST_DISCOVERABLE_BLUETOOTH) {
            Log.i(TAG, "onActivityResult_discoverable enabled")

        }
    }

    @SuppressLint("HardwareIds")
    private fun fetchingPairedDeviceList(): MutableSet<BluetoothDevice> {
        /* getting this data only when permissions are granted else don't*/
        val pairedDevice: MutableSet<BluetoothDevice> = mBluetoothAdapter.bondedDevices
        Log.i(TAG, "enablingBluetoothConnection_pairedDevice = $pairedDevice")
        return pairedDevice
    }

    private fun setRecyclerview(
        devices: MutableSet<BluetoothDevice>
    ) {
        binding.pairedDevicesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        devicesAdapter = PairedDevicesAdapter(requireContext(), this)
        devicesAdapter.addItems(devices)
        binding.pairedDevicesRecyclerView.adapter = devicesAdapter
    }

    override fun selectedDeviceInfo(device: BluetoothDevice) {
        val bundle = Bundle()
        bundle.putParcelable("device_data", device)
        findNavController().navigate(R.id.action_selectDeviceFragment_to_deviceCommFragment, bundle)
        showToast(requireContext(), "${device.name} selected...")
    }

}