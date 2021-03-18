package com.example.android.politicalpreparedness.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception


class Repository(private val db: ElectionDao) {
    val allElections: LiveData<List<Election>> = db.getAllElections()
    val savedElections: LiveData<List<Election>> = db.getSavedElections()
    val voterInfo = MutableLiveData<VoterInfoResponse>()

    suspend fun refreshElections() {

        withContext(Dispatchers.IO) {
            val elections: List<Election>
            val electionNetworkResult: ElectionResponse = CivicsApi.retrofitService.getElections().await()
            elections = electionNetworkResult.elections

                elections.forEach{
                    val dbElection = db.getElectionById(it.id)
                    if (dbElection != null) {
                        it.isElectionSaved = dbElection.value!!.isElectionSaved!!
                    }
                }
            db.insertElections(elections)
        }
    }

    fun getElectionById(id: Int) = db.getElectionById(id)

    suspend fun getVoterInfo(electionId: Int, address: String) {
        try {
            withContext(Dispatchers.IO) {
                val voterInfoResponse: VoterInfoResponse = CivicsApi.retrofitService.getVoterInfo(electionId, address).await()
                voterInfo.postValue(voterInfoResponse)
            }
        } catch (e: Exception) {
            Log.e("ERROR:","Error getting voter info: $e")
        }
    }

    suspend fun followElection(election: Election) {
        withContext(Dispatchers.IO) {
            db.insertElection(election)
        }
    }
}
