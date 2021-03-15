package com.example.android.politicalpreparedness.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.election.ElectionsFragment
import com.example.android.politicalpreparedness.representative.DetailFragment

class LaunchFragment : Fragment() {

    private lateinit var viewModel: LaunchViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentLaunchBinding.inflate(inflater)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(LaunchViewModel::class.java)
        binding.viewModel = viewModel
        binding.representativeButton.setOnClickListener { navToRepresentatives() }
        binding.upcomingButton.setOnClickListener { navToElections() }

        viewModel.navigateToFragment.observe(viewLifecycleOwner) {
            if (it == ElectionsFragment::class.java.simpleName) {
                navToElections()
                viewModel.navigateToFragmentDone()
            } else if (it == DetailFragment::class.java.simpleName) {
                navToRepresentatives()
                viewModel.navigateToFragmentDone()
            }
        }

        return binding.root
    }

    private fun navToElections() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    private fun navToRepresentatives() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }
}
