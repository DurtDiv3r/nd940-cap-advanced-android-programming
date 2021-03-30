package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.repository.Repository
import kotlinx.coroutines.launch

class ElectionsViewModel(dataSource: ElectionDao) : ViewModel() {

    private val repository = Repository(dataSource)

    val upcomingElections = repository.allElections
    val savedElections = repository.savedElections

    init {
        viewModelScope.launch {
            try {
                repository.refreshElections()
            } catch (e: Exception) {
                Log.e(TAG, "Exception: $e")
            }
        }
    }

    companion object {
        val TAG: String = ElectionsViewModel::class.java.simpleName
    }
}