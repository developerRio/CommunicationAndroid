package com.originalstocks.blueserverapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.originalstocks.blueserverapp.data.interfaces.DataCommInterface
import com.originalstocks.blueserverapp.data.service.BluetoothConnectionService
import com.originalstocks.blueserverapp.databinding.FragmentDeviceCommBinding
import com.originalstocks.blueserverapp.utils.REQUEST_PERMISSION_LOCATION
import com.originalstocks.blueserverapp.utils.checkMajorDeviceType
import com.originalstocks.blueserverapp.utils.showToast
import java.io.IOException
import java.util.*


class DeviceCommFragment : Fragment(), DataCommInterface {
    private val TAG = "DeviceCommFragment"
    private lateinit var binding: FragmentDeviceCommBinding
    private var mBluetoothDevice: BluetoothDevice? = null

    //Communication service
    private lateinit var mBluetoothCommService: BluetoothConnectionService
    private val MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

    // Location services
    private lateinit var locationManager: LocationManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDeviceCommBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Getting selected device*/
        mBluetoothDevice = arguments?.getParcelable("device_data")
        Log.i(TAG, "onViewCreated_device_data = ${mBluetoothDevice?.name}")

        mBluetoothCommService = BluetoothConnectionService(requireContext(), this)

        /*
        * if device type is phone, allow device to communicate with client, else if it is audio/video, will play some random music for that
        * */
        val deviceType = checkMajorDeviceType(mBluetoothDevice?.bluetoothClass?.majorDeviceClass!!)
        Log.i(TAG, "onViewCreated_device_deviceType = $deviceType")

        binding.sendMessageButton.setOnClickListener {
            val messageData = binding.messageEditText.text.toString()
            if (messageData.isNotEmpty()) {
                val byteArrayData: ByteArray = messageData.toByteArray()
                mBluetoothCommService.write(byteArrayData)
            } else {
                showToast(requireContext(), "Please type something...")
            }

        }

        if (deviceType == "PHONE") {
            startBluetoothConnection(mBluetoothDevice!!, MY_UUID_INSECURE)
        } else {
            // music stuff
            Log.i(TAG, "onViewCreated_not mobile, get the type !")
        }

        /*Location part*/

        binding.fetchMyCurrentLocation.setOnClickListener {
            /* checking location permissions*/
            checkPermission()
        }
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.sendMyLocation.setOnClickListener {
            val currentLocation = binding.fetchMyCurrentLocation.text.toString()
            Log.i(TAG, "onViewCreated_fetchedLocation = $currentLocation")
            val messageData = "LocationData $currentLocation"
            val byteArrayData: ByteArray = messageData.toByteArray()
            mBluetoothCommService.write(byteArrayData)
        }

    }

    private fun getAddress(longitude: Double, latitude: Double): String {
        val result = StringBuilder()
        try {
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                result.append(address.locality).append(", ")
                result.append(address.countryName)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result.toString()
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.e(TAG, "checkPermission_permission already granted")
                // Fetch the location
                val mLocationListener = LocationListener { location ->
                    Log.i(
                        TAG,
                        "onLocationChanged_location lat = ${location.latitude} long = ${location.longitude}"
                    )
                    val currentLocation = getAddress(location.longitude, location.latitude)
                    Log.i(TAG, "onViewCreated_fetchLocationAccordingToLatLong = $currentLocation")
                    binding.fetchMyCurrentLocation.text = currentLocation
                }

                /*Here we can use LocationManager.GPS_PROVIDER has it gives more Accurate location pointers but it consumes more battery hence using LocationManager.NETWORK_PROVIDER*/
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0F,
                    mLocationListener
                )
                //locationManager.removeUpdates(mLocationListener)

            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) -> {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_PERMISSION_LOCATION
                )
                // We've been denied once before. Explain why we need the permission, then ask again.
                Log.i(TAG, "checkPermission_permission once denied, asking permission again")
            }
            else -> {
                // We've never asked. Just do it.
                Log.i(TAG, "checkPermission_permission asking permission")
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_PERMISSION_LOCATION
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSION_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted now fetch the locations

                    // Fetch the location
                    val mLocationListener = LocationListener { location ->
                        Log.i(
                            TAG,
                            "onLocationChanged_location lat = ${location.latitude} long = ${location.longitude}"
                        )
                        val currentLocation = getAddress(location.longitude, location.latitude)
                        Log.i(TAG, "onViewCreated_fetchLocationAccordingToLatLong = $currentLocation")
                        binding.fetchMyCurrentLocation.text = currentLocation


                    }

                    /*Here we can use LocationManager.GPS_PROVIDER has it gives more Accurate location pointers but it consumes more battery hence using LocationManager.NETWORK_PROVIDER*/
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        0F,
                        mLocationListener
                    )
                    //locationManager.removeUpdates(mLocationListener)


                    Log.i(TAG, "checkPermission_permission onRequestPermissionsResult granted")
                } else {
                    Log.i(TAG, "checkPermission_permission onRequestPermissionsResult denied")
                }
                return
            }
        }
    }

    private fun startBluetoothConnection(mBluetoothDevice: BluetoothDevice, uuid: UUID) {
        Log.i(TAG, "startBluetoothConnection_init")
        mBluetoothCommService.startClient(mBluetoothDevice, uuid)
    }

    override fun dataFetched(data: String) {
        binding.messageEditText.setText("")
        Log.e(TAG, "dataFetched: $data")
        binding.receivedDataTextView.postDelayed(Runnable {
            if (data.contains("LocationData")) {
                // set it somewhere
                showToast(requireContext(), data)
                binding.locationTextView.text = data.replace("LocationData", "")
            } else {
                binding.receivedDataTextView.text = data
            }

        }, 500)

    }


}