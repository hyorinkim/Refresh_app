package com.example.refresh_selection

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
            val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
            bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }

        val button2 = findViewById<Button>(R.id.test)
        button2.setOnClickListener {
            //val characteristic = mGattCharacteristics!![6][0]
            //bluetoothLeService!!.setCharacteristicNotification(characteristic,true)
            bluetoothLeService!!.readCharacteristic()
            //Log.d("charateristic",characteristic.value.toString())
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
            // Automatically connects to the device upon successful start-up initialization.
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
                    //updateConnectionState(R.string.connected)
                    invalidateOptionsMenu()
                }
                ACTION_GATT_DISCONNECTED -> {
                    mConnected = false
                    //updateConnectionState(R.string.disconnected)
                    invalidateOptionsMenu()
                    //clearUI()
                }
                ACTION_GATT_SERVICES_DISCOVERED -> {
                    // Show all the supported services and characteristics on the user interface.
                    displayGattServices(bluetoothLeService!!.supportedGattServices)
                }
                ACTION_DATA_AVAILABLE -> {
                    Log.d("actionDataAvailiabe","s")
                    //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
                }
            }
        }
    }
    private fun updateConnectionState(resourceId: Int) {
        runOnUiThread { mConnectionState!!.setText(resourceId) }
    }

    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return
        var uuid: String?
        //val unknownServiceString: String = resources.getString(R.string.unknown_service)
        //val unknownCharaString: String = resources.getString(R.string.unknown_characteristic)
        val gattServiceData: MutableList<HashMap<String, String>> = mutableListOf()
        val gattCharacteristicData: MutableList<ArrayList<HashMap<String, String>>> =
                mutableListOf()
        mGattCharacteristics =  ArrayList<ArrayList<BluetoothGattCharacteristic>>()

        // Loops through available GATT Services.
        gattServices.forEach { gattService ->
            val currentServiceData = HashMap<String, String>()
            uuid = gattService.uuid.toString()
            Log.d("Gatt_Servide UUID", uuid!!)
            //currentServiceData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownServiceString)
            //currentServiceData[LIST_UUID] = uuid
            gattServiceData += currentServiceData

            val gattCharacteristicGroupData: ArrayList<HashMap<String, String>> = arrayListOf()
            val gattCharacteristics = gattService.characteristics
            val charas = ArrayList<BluetoothGattCharacteristic>()

            // Loops through available Characteristics.
            gattCharacteristics.forEach { gattCharacteristic ->
                charas.add(gattCharacteristic)
                val currentCharaData: HashMap<String, String> = hashMapOf()
                uuid = gattCharacteristic.uuid.toString()
                Log.d("Gatt_charar UUID", uuid!!)
                //currentCharaData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownCharaString)
                //currentCharaData[LIST_UUID] = uuid
                gattCharacteristicGroupData += currentCharaData
            }
            mGattCharacteristics!!.add(charas)
            gattCharacteristicData += gattCharacteristicGroupData
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