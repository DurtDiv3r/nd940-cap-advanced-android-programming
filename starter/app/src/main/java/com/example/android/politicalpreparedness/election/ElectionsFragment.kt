package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment : Fragment() {

    private lateinit var viewModel: ElectionsViewModel
    private lateinit var viewModelFactory: ElectionsViewModelFactory
    private lateinit var binding: FragmentElectionBinding

    private lateinit var savedElectionsAdapter: ElectionListAdapter
    private lateinit var upcomingElectionsAdapter: ElectionListAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)
        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao

        viewModelFactory = ElectionsViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)

        upcomingElectionsAdapter = ElectionListAdapter(ElectionListener {
            findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it))
        })
        binding.upcomingElectionsRecycler.adapter = upcomingElectionsAdapter
        viewModel.upcomingElections.observe(viewLifecycleOwner) { elections ->
            upcomingElectionsAdapter.submitList(elections)
        }

        savedElectionsAdapter = ElectionListAdapter(ElectionListener {
            findNavController()
                    .navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it))
        })
        binding.savedElectionsRecycler.adapter = savedElectionsAdapter
        viewModel.savedElections.observe(viewLifecycleOwner) { savedElections ->
            savedElectionsAdapter.submitList(savedElections)
        }

        return binding.root
    }
}