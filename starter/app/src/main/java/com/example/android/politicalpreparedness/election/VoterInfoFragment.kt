package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding
    private lateinit var viewModel: VoterInfoViewModel
    private lateinit var viewModelFactory: VoterInfoViewModelFactory

    private val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentVoterInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao

        viewModelFactory = VoterInfoViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val election = args.argElection
        viewModel.getElectionById(election.id)

        try {
            viewModel.getVoterInfo(args)
        } catch (e: Exception) {
            Log.e(VoterInfoFragment::class.java.simpleName, "Error getting voter info $e")
        }

        viewModel.voterUrl.observe(viewLifecycleOwner, Observer {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            startActivity(intent)
        })

        return binding.root

        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */
    }
}