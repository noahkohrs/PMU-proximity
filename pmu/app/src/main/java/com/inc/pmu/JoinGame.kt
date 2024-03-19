package com.inc.pmu

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class JoinGame : Fragment(R.layout.join_page) {

    lateinit var homePage: Button

    companion object {
        fun newInstance() = HomePage()
    }

    override fun onStart() {
        super.onStart()

        homePage = requireView().findViewById(R.id.homePage)

        homePage.setOnClickListener {
            val fragment = HomePage.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }
}