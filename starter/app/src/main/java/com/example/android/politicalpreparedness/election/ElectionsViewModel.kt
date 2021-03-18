package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.Repository
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(dataSource: ElectionDao) : ViewModel() {

    private val repository = Repository(dataSource)

    //TODO: Create live data val for upcoming elections


    //unhappy with this, functional but reformat when done
    val upcomingElections = repository.allElections
    val savedElections = repository.savedElections
    //TODO: Create live data val for saved elections
//    private val _savedElections = MutableLiveData<List<Election>>()
//    val savedElections: LiveData<List<Election>>
//        get() = _savedElections

    init {
        viewModelScope.launch {
            try {
                repository.refreshElections()
            } catch (e: Exception) {
                println("Exception: $e")
            }
        }

    }


    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}