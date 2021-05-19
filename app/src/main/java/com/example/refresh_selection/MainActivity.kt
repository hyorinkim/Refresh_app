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
    private val REQUEST_ENABLE_BT = 3
    private var leDeviceListAdapter: LeDeviceListAdapter? = null
    private var mScanning: Boolean = false
    private var handler: Handler? = null
    private var mibandDevice: BluetoothDevice? = null;
    var bluetoothLeService : BluetoothLeService? = null
    private var mConnected = false
    private var isBindedService = false
    private var setServiceNotification: List<UUID>? = null;

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    private var mBtn: Button? = null
    private var scanBtn: Button? = null

    override fun onResume() {
        super.onResume()
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
        if (bluetoothLeService != null) {
            val result = bluetoothLeService!!.connect(mibandDevice?.address)
            Log.d(TAG, "Connect request result=" + result)
        }
    }

    /**
     * 앱 시작 시 실행
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        getBluetoothPermissions()

        //변수 초기화
        handler = Handler()
        mBtn = findViewById<Button>(R.id.pairBt)
        scanBtn = findViewById<Button>(R.id.scan)
        leDeviceListAdapter = LeDeviceListAdapter()

        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
            finish()
        }

        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        //페어링된 디바이스가 있는지 확인
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
        }
        if (pairedDevices != null) {
            for (device in pairedDevices) {
                if (device.name.contains("Mi band")) {
                    mibandDevice = device;
                    setMibandDeviceInfoView()
                    break
                }
            }
        }

        //페어링된 디바이스가 없으면 블루투스 스캔 시작
        if(mibandDevice == null){
            scanLeDevice(true)
        }

        //paring 버튼 이벤트. 페어링을 시작함.
        mBtn!!.setOnClickListener {
            mBtn?.visibility = View.GONE
            findViewById<View>(R.id.scrollView).visibility = View.GONE
            mibandDevice?.name?.let { it1 -> Log.d("mibandDevice", it1) }
            val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
            isBindedService = bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }

        //scan button event. 디바이스를 못 찾을 경우 나타나는 버튼으로 클릭 시 다시 스캔을 시작
        scanBtn!!.setOnClickListener {
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            scanBtn?.visibility = View.INVISIBLE
            scanLeDevice(true)
        }
    }

    /**
     * 미밴드 디바이스 정보를 뷰에 나타냄
     */
    fun setMibandDeviceInfoView() {
        val textView: TextView = findViewById<TextView>(R.id.deviceName)
        val textView2: TextView = findViewById<TextView>(R.id.address)
        findViewById<View>(R.id.scrollView).visibility = View.VISIBLE
        scanBtn?.visibility = View.INVISIBLE
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        mBtn?.visibility = View.VISIBLE
        textView.text = mibandDevice!!.name
        textView2.text = mibandDevice!!.address
    }

    /**
     * 앱의 권한을 설정
     */
    fun getBluetoothPermissions() {
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
    }

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

    /**
     * BluetoothLeService 에서 날린 브로드캐스트를 여기서 받음.
     */
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

                }
            }
        }
    }

    /**
     * 디바이스 스캔시 콜백
     */
    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
            if( mScanning ) {
                leDeviceListAdapter!!.addDevice(device)
                leDeviceListAdapter!!.notifyDataSetChanged()
            } else {
                if( mibandDevice != null ) { //미밴드 디바이스를 찾을 시
                    setMibandDeviceInfoView()
                }
            }
        }
    }

    /**
     * 블루투스 디바이스 스캔
     */
    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                handler?.postDelayed({
                    mScanning = false
                    bluetoothAdapter?.stopLeScan(leScanCallback)
                    if(mibandDevice==null) {
                        scanBtn?.visibility = View.VISIBLE
                        findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                    }
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

    /**
     * 블루투스 스캔을 통해 찾은 디바이스를 관리하기 위한 어댑터 클래스
     */
    private inner class LeDeviceListAdapter : BaseAdapter() {
        private val mLeDevices: ArrayList<BluetoothDevice> = ArrayList<BluetoothDevice>()

        /**
         * 디바이스 이름이 Mi band인 디바이스를 찾아 mLeDevices 리스트에 집어넣음.
         */
        fun addDevice(device: BluetoothDevice) {
            if (!mLeDevices.contains(device)) {
                if(device!=null){
                    if(device.name!=null){
                        if(device.name.contains("Mi Band")){
                            mLeDevices.add(device)
                            mibandDevice = device;
                            setMibandDeviceInfoView()
                            scanLeDevice(false)
                        }
                    }
                }
            }
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

    override fun onDestroy() {
        super.onDestroy()
        if( isBindedService  ) unbindService(mServiceConnection)
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
            intentFilter.addAction(ACTIVITY_DATA_FETCH)
            return intentFilter
        }
    }
}