package com.inc.pmu

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.inc.pmu.models.Player

class HomePage : Fragment(R.layout.home_page) {

    private lateinit var createButton: Button
    private lateinit var joinButton: Button
    companion object {
        fun newInstance() = HomePage()
    }

    override fun onStart() {
        super.onStart()

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