package com.example.refresh_selection

import android.Manifest
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

private const val SCAN_PERIOD: Long = 10000
//private val TAG = BluetoothLeService::class.java.simpleName
private const val STATE_DISCONNECTED = 0
private const val STATE_CONNECTING = 1
private const val STATE_CONNECTED = 2
const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
const val ACTION_GATT_SERVICES_DISCOVERED =
        "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
const val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"
//val UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT)

class MainActivity : AppCompatActivity(){
    private var bluetoothGatt: BluetoothGatt? = null
    private val REQUEST_ENABLE_BT = 3
    private var leDeviceListAdapter: LeDeviceListAdapter? = null
    private var mScanning: Boolean = false
    private var handler: Handler? = null
    private var mibandDevice: BluetoothDevice? = null;
    private var connectionState = STATE_DISCONNECTED
    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    private var mBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler()

        mBtn = findViewById<Button>(R.id.pairBt)

        leDeviceListAdapter = LeDeviceListAdapter()

        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
            finish()
        }

        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

//        페어링된 디바이스 set
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
        }

        findViewById<View>(R.id.scrollView).visibility = View.VISIBLE
        if (pairedDevices != null) {
            for (device in pairedDevices) {
                if (device.name.contains("Mi band")) {
                    mibandDevice = device;
                    val textView: TextView = findViewById<TextView>(R.id.deviceName)
                    val textView2: TextView = findViewById<TextView>(R.id.address)
                    textView.text = device.name
                    textView2.text = device.address
                    break
                }
            }
        }

        if(mibandDevice == null){
            scanLeDevice(true)
        }else {
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            mBtn?.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }

        val button = findViewById<Button>(R.id.pairBt)
        button.setOnClickListener {
            mibandDevice?.name?.let { it1 -> Log.d("mibandDevice", it1) }
            bluetoothGatt = mibandDevice?.connectGatt(this, false, gattCallback)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            val intentAction: String
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    intentAction = ACTION_GATT_CONNECTED
                    connectionState = STATE_CONNECTED
                    broadcastUpdate(intentAction)
                    Log.i("", "Connected to GATT server.")
                    Log.i("", "Attempting to start service discovery: " +
                            bluetoothGatt?.discoverServices())
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    intentAction = ACTION_GATT_DISCONNECTED
                    connectionState = STATE_DISCONNECTED
                    Log.i("", "Disconnected from GATT server.")
                    broadcastUpdate(intentAction)
                }
            }
        }
        // New services discovered
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                else -> Log.w("", "onServicesDiscovered received: $status")
            }
        }

        // Result of a characteristic read operation
        override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
        ) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
                }
            }
        }
    }
    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
        when (characteristic.uuid) {
//            UUID_HEART_RATE_MEASUREMENT -> {
//                val flag = characteristic.properties
//                val format = when (flag and 0x01) {
//                    0x01 -> {
//                        Log.d(TAG, "Heart rate format UINT16.")
//                        BluetoothGattCharacteristic.FORMAT_UINT16
//                    }
//                    else -> {
//                        Log.d(TAG, "Heart rate format UINT8.")
//                        BluetoothGattCharacteristic.FORMAT_UINT8
//                    }
//                }
//                val heartRate = characteristic.getIntValue(format, 1)
//                Log.d(TAG, String.format("Received heart rate: %d", heartRate))
//                intent.putExtra(EXTRA_DATA, (heartRate).toString())
//            }
//            else -> {
//                // For all other profiles, writes the data formatted in HEX.
//                val data: ByteArray? = characteristic.value
//                if (data?.isNotEmpty() == true) {
//                    val hexString: String = data.joinToString(separator = " ") {
//                        String.format("%02X", it)
//                    }
//                    intent.putExtra(EXTRA_DATA, "$data\n$hexString")
//                }
//            }

        }
        sendBroadcast(intent)
    }

    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
            leDeviceListAdapter!!.addDevice(device)
            leDeviceListAdapter!!.notifyDataSetChanged()

        }
    }

    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                handler?.postDelayed({
                    mScanning = false
                    bluetoothAdapter?.stopLeScan(leScanCallback)
                }, SCAN_PERIOD)
                mScanning = true
                bluetoothAdapter?.startLeScan(leScanCallback)
            }
            else -> {
                mScanning = false
                bluetoothAdapter?.stopLeScan(leScanCallback)
            }
        }
    }


    // Adapter for holding devices found through scanning.
    private inner class LeDeviceListAdapter : BaseAdapter() {
        private val mLeDevices: ArrayList<BluetoothDevice>
        private val mInflater: LayoutInflater

        init {
            mLeDevices = ArrayList<BluetoothDevice>()
            mInflater = this@MainActivity.layoutInflater
        }

        fun addDevice(device: BluetoothDevice) {
            if (!mLeDevices.contains(device)) {
                if(device!=null){
                    if(device.name!=null){
                        if(device.name.contains("Mi Band")){
                            mLeDevices.add(device)
                            bluetoothAdapter?.stopLeScan(leScanCallback)
                            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                            mBtn?.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            val textView: TextView = findViewById<TextView>(R.id.deviceName)
                            val textView2: TextView = findViewById<TextView>(R.id.address)
                            textView.text = device.name
                            textView2.text = device.address
                            mibandDevice = device;
                        }
                    }
                }
            }
        }

        fun getDevice(position: Int): BluetoothDevice? {
            return mLeDevices[position]
        }

        fun clear() {
            mLeDevices.clear()
        }

        override fun getCount(): Int {
            return mLeDevices.size
        }

        override fun getItem(i: Int): Any {
            return mLeDevices[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View? {
            var view = view
            val viewHolder: ViewHolder
            // General ListView optimization code.
            if (view == null) {
                view = mInflater.inflate(R.layout.activity_main, null)
                viewHolder = ViewHolder()
//                viewHolder.deviceAddress = view.findViewById<View>(R.id.multiLine) as TextView
//                viewHolder.deviceName = view.findViewById<View>(R.id.multiLIne2) as TextView
                view.tag = viewHolder
            } else {
                viewHolder = view.tag as ViewHolder
            }
            val device = mLeDevices[i]
            val deviceName = device.name
//            if (deviceName != null && deviceName.length > 0) viewHolder.deviceName!!.text = deviceName else viewHolder.deviceName.setText(R.string.unknown_device)
            viewHolder.deviceAddress!!.text = device.address
            return view
        }
    }
    internal class ViewHolder {
        var deviceName: TextView? = null
        var deviceAddress: TextView? = null
    }
}