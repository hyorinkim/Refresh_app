package com.example.refresh_selection

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.InputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity(){

    private val REQUEST_ENABLE_BT = 3
    var mBluetoothAdapter: BluetoothAdapter? = null
    var mDevices: Set<BluetoothDevice>? = null
    var mPairedDeviceCount = 0
    var mRemoteDevice: BluetoothDevice? = null
    var mSocket: BluetoothSocket? = null
    var mInputStream: InputStream? = null
    var mOutputStream: OutputStream? = null
    var mWorkerThread: Thread? = null
    var mDelimiter: Byte = 10
    private var bluetoothAdapter: BluetoothAdapter? = null

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    private var mBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtn = findViewById(R.id.pairBt) as Button

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

        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        //페어링된 디바이스 set
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
        }

        val button = findViewById<Button>(R.id.pairBt)
        button.setOnClickListener {
            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter?.cancelDiscovery()
            }
            var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            this.registerReceiver(receiver, filter)
            filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            this.registerReceiver(receiver, filter)
            filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            this.registerReceiver(receiver, filter)
            bluetoothAdapter?.startDiscovery()
        }

    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED ->{
                    Log.d("Discovery_started","ok")
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address

                    if (deviceName != null && deviceHardwareAddress != null) {
                        Log.d("BluetoothName: ",deviceName)
                        Log.d("Bluetooth Mac Address:", deviceHardwareAddress)
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }

}