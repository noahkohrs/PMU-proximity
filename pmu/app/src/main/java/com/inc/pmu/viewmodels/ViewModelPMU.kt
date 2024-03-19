package com.inc.pmu.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.inc.pmu.Global
import com.inc.pmu.models.Game
import java.util.UUID

abstract class ViewModelPMU : ViewModel() {
    val localId : String = UUID.randomUUID().toString()
    var localUsername: String = localId
    lateinit var connectionsClient : ConnectionsClient
    public lateinit var game : Game

    private companion object {
        const val TAG = Global.TAG
        val STRATEGY = Strategy.P2P_STAR
    }

    /**
     * Set the connectionClient to connectionClient Start discovering for other devices
     * @param connectionsClient the connectionClient to use
     */
    abstract fun startDiscovering(connectionsClient: ConnectionsClient)


    /**
     * Set the connectionClient to connectionClient Start hosting for other devices
     * @param connectionsClient the connectionClient to use
     */
    abstract fun startHosting(connectionsClient: ConnectionsClient)

    protected val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "onEndpointFound")
            Log.d(TAG, "Requesting connection...")
            connectionsClient.requestConnection(
                localId,
                endpointId,
                connectionLifecycleCallback
            ).addOnSuccessListener {
                Log.d(TAG, "Successfully requested a connection")
            }.addOnFailureListener {
                Log.d(TAG, "Failed to request the connection")
            }
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "onEndpointLost")
        }
    }

    abstract fun onConnectionResultOK(endpointId: String)

    protected val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d(TAG, "onConnectionInitiated")

            Log.d(TAG, "Accepting connection...")
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, resolution: ConnectionResolution) {
            Log.d(Global.TAG, "onConnectionResult")

            when (resolution.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(Global.TAG, "ConnectionsStatusCodes.STATUS_OK")

                    connectionsClient.stopDiscovery()
                    onConnectionResultOK(endpointId)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.d(Global.TAG, "ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.d(Global.TAG, "ConnectionsStatusCodes.STATUS_ERROR")
                }
                else -> {
                    Log.d(Global.TAG, "Unknown status code ${resolution.status.statusCode}")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "onDisconnected")
        }
    }

    abstract fun onPayloadReceived(endpointId: String, paquet: String)
    // au lieu d'un string pour le paquet, il faudrait lui donner un JSON qu'il va parser pour traiter la requête

    protected val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                payload.asBytes()?.let {
                    val message = String(it)
                    onPayloadReceived(endpointId,message)
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    abstract fun broadcast(payload: Payload)
}