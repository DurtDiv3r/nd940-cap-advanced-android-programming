package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(application: Application) : AndroidViewModel(application) {

    val address = MutableLiveData<Address>()

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    fun getRepresentatives() {
        if (address.value != null) {
            viewModelScope.launch {
                try {
                    val (offices, officials) = CivicsApi.retrofitService.getRepresentativesDeferredAsync(address.value!!.toFormattedString()).await()
                    _representatives.postValue(offices.flatMap { office -> office.getRepresentatives(officials) })
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }
}
