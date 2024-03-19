package com.inc.testfragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class AddFragment : Fragment(R.layout.add_frag) {

    companion object {
        fun newInstance() = AddFragment()
    }

    private lateinit var viewModel: TestViewModel


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[TestViewModel::class.java]

        view?.findViewById<View>(R.id.add)?.setOnClickListener {
            viewModel.addOne()
            Log.d("AddFragment", "onActivityCreated: addOne" + viewModel.getNumber())
        }

        view?.findViewById<View>(R.id.next)?.setOnClickListener {
            val fragment = MinusFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }

}