package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.Repository
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val dataSource: ElectionDao) : ViewModel() {

    private val repository = Repository(dataSource)

    val voterInfo = repository.voterInfo

    private val electionId = MutableLiveData<Int>()
    val election = electionId.switchMap {
        liveData { emitSource(repository.getElectionById(it)) }
    }

    private val _voterUrl = SingleLiveEvent<String>()
    val voterUrl: SingleLiveEvent<String>
        get() = _voterUrl

    fun getElectionById(id: Int) {
        electionId.postValue(id)
    }

    fun getVoterInfo(args: VoterInfoFragmentArgs) {

        var address: String
        if (args.argElection.division.state.isNotEmpty()) {
            address = args.argElection.division.state
        } else {
            address = args.argElection.division.country
        }

        viewModelScope.launch {
            repository.getVoterInfo(args.argElection.id, address)

        }
    }

    fun getVoterUrl(voterUrl: String) {
        _voterUrl.postValue(voterUrl)
    }

    fun isElectionFollowed(election: Election) {
        election.isElectionSaved = !election.isElectionSaved
        viewModelScope.launch {
            repository.followElection(election)
        }
    }
    //TODO: Add live data to hold voter info

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}