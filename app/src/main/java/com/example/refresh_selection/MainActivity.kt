package com.example.refresh_selection

import android.Manifest
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
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
private val TAG = BluetoothLeService::class.java.simpleName

class MainActivity : AppCompatActivity(){
    private var mConnectionState: TextView? = null
    private val REQUEST_ENABLE_BT = 3
    private var leDeviceListAdapter: LeDeviceListAdapter? = null
    private var mScanning: Boolean = false
    private var handler: Handler? = null
    private var mibandDevice: BluetoothDevice? = null;
    var bluetoothLeService : BluetoothLeService? = null
    private var mGattCharacteristics: ArrayList<ArrayList<BluetoothGattCharacteristic>>? = ArrayList()
    private var mConnected = false
    private var mNotifyCharacteristic: BluetoothGattCharacteristic? = null


    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    private var mBtn: Button? = null

    override fun onResume() {
        super.onResume()
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
        if (bluetoothLeService != null) {
            val result = bluetoothLeService!!.connect(mibandDevice?.address)
            Log.d(TAG, "Connect request result=" + result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler()

        mBtn = findViewById<Button>(R.id.pairBt)

        leDeviceListAdapter = LeDeviceListAdapter()
        //권한 설정
        val permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
        val permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
        val permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permission4 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission1 != PackageManager.PERMISSION_GRANTED
                || permission2 != PackageManager.PERMISSION_GRANTED
                || permission3 != PackageManager.PERMISSION_GRANTED
                || permission4 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION),
                    642)
        } else {
            Log.d("DISCOVERING-PERMISSIONS", "Permissions Granted")
        }

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
        }

        val button = findViewById<Button>(R.id.pairBt)
        button.setOnClickListener {
            mibandDevice?.name?.let { it1 -> Log.d("mibandDevice", it1) }
            val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
            bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    // Code to manage Service lifecycle.
    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            bluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            if (!bluetoothLeService!!.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth")
                finish()
            }
            bluetoothLeService!!.connect(mibandDevice?.address)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothLeService = null
        }
    }

    private val mGattUpdateReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action){
                ACTION_GATT_CONNECTED -> {
                    mConnected = true
                    invalidateOptionsMenu()
                }
                ACTION_GATT_DISCONNECTED -> {
                    mConnected = false
                    invalidateOptionsMenu()
                }
                ACTION_DATA_AVAILABLE -> {

                }
                ACTION_GATT_SERVICES_DISCOVERED -> {
                    bluetoothLeService!!.setCharacteristicNotification(ACTIVITY_UUID,true)
                    bluetoothLeService!!.setCharacteristicNotification(REAL_TIME_STEP_UUID,true)
                    bluetoothLeService!!.setCharacteristicNotification(CONTROL_POINT_UUID,true)
                }
            }
        }
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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            TODO("Not yet implemented")
        }

        override fun getItem(i: Int): Any {
            return mLeDevices[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }


    }
    internal class ViewHolder {
        var deviceName: TextView? = null
        var deviceAddress: TextView? = null
    }


    override fun onPause() {
        super.onPause()
        unregisterReceiver(mGattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
        bluetoothLeService = null
    }

    companion object {

        @JvmField var EXTRAS_DEVICE_NAME = "DEVICE_NAME"
        @JvmField var EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"

        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ACTION_GATT_CONNECTED)
            intentFilter.addAction(ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED)
            intentFilter.addAction(ACTION_DATA_AVAILABLE)
            return intentFilter
        }
    }
}