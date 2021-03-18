package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel

    private lateinit var viewModel: ElectionsViewModel
    private lateinit var viewModelFactory: ElectionsViewModelFactory
    private lateinit var binding: FragmentElectionBinding
    private lateinit var savedElectionsAdapter: ElectionListAdapter
    private lateinit var upcomingElectionsAdapter: ElectionListAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel
        //TODO: Add binding values
        //TODO: Link elections to voter info
        //TODO: Initiate recycler adapters
        //TODO: Populate recycler adapters

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
        viewModel.savedElections.observe(viewLifecycleOwner) {
            savedElectionsAdapter.submitList(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    //TODO: Refresh adapters when fragment loads

}