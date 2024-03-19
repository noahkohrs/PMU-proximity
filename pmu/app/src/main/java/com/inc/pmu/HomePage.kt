package com.inc.pmu

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.models.Player
import com.inc.pmu.viewmodels.ViewModelBeforeNetwork

class HomePage : Fragment(R.layout.home_page) {

    private lateinit var createButton: Button
    private lateinit var joinButton: Button

    private lateinit var vmUserData: ViewModelBeforeNetwork
    companion object {
        fun newInstance() = HomePage()
    }

    override fun onStart() {
        super.onStart()

        vmUserData = ViewModelProvider(requireActivity())[ViewModelBeforeNetwork::class.java]


        createButton = requireView().findViewById(R.id.createButton)
        joinButton = requireView().findViewById(R.id.joinButton)

        createButton.setOnClickListener {
            val fragment = WaitingForPlayer.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        joinButton.setOnClickListener {
            val fragment = JoinGame.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack("HomePage", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        //TODO create player
    }
}