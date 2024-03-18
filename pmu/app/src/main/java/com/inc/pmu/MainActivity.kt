package com.inc.pmu

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.inc.pmu.viewmodels.ViewModelClient
import com.inc.pmu.viewmodels.ViewModelHost

class MainActivity : AppCompatActivity() {
    private lateinit var connectionsClient : ConnectionsClient
    private lateinit var viewModelClient : ViewModelClient
    private lateinit var viewModelHost : ViewModelHost
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pseudo_choice)
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.isEmpty() || permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.any { !it.value }) {
            Toast.makeText(this, "Required permissions needed", Toast.LENGTH_LONG).show()
            finish()
        } else {
            recreate()
        }
    }
    override fun onStart() {
        super.onStart()
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestMultiplePermissions.launch(
                REQUIRED_PERMISSIONS
            )
        }
        connectionsClient = Nearby.getConnectionsClient(applicationContext)
        viewModelClient = ViewModelClient(connectionsClient)
        viewModelHost = ViewModelHost(connectionsClient)
    }
    private companion object {
        val REQUIRED_PERMISSIONS =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
    }

    fun onClickPlayButton(view: View) {
        val intent = Intent(this,HomePage::class.java)
        intent.putExtra("Pseudo", this.findViewById<EditText>(R.id.pseudoInput).text.toString())
        startActivities(arrayOf(intent))
    }
}