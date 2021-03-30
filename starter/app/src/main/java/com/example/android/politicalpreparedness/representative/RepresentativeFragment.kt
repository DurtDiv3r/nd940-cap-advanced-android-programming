package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.BuildConfig.APPLICATION_ID
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var viewModelFactory: RepresentativeViewModelFactory
    private lateinit var representativeAdapter: RepresentativeListAdapter

    private lateinit var _address: Address


    companion object {
        //Added Constant for Location request
        private const val REQUEST_FOREGROUND_PERMISSION_RESULT_CODE = 1
        private const val ADDRESS = "ADDRESS_KEY"
        private val TAG = DetailFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)

        val application = requireNotNull(this.activity).application
        binding.lifecycleOwner = viewLifecycleOwner
        viewModelFactory = RepresentativeViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RepresentativeViewModel::class.java)

        binding.viewModel = viewModel
        if (savedInstanceState != null) {
            binding.address = savedInstanceState.getParcelable(ADDRESS)
        } else {
            binding.address = Address("", "", "", "", "")
        }

        val spinner = binding.state
        ArrayAdapter.createFromResource(requireContext(), R.array.states, android.R.layout.simple_spinner_dropdown_item).also { adapter ->
                    spinner.adapter = adapter
        }

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
            hideKeyboard()
        }

        binding.buttonSearch.setOnClickListener {
            if (validateAddress(binding)) {
                viewModel.address.value = binding.address
                hideKeyboard()
            } else {
                displaySnackbar(requireView(), getString(R.string.complete_fields))
            }
            viewModel.getRepresentatives()
        }

        representativeAdapter = RepresentativeListAdapter()
        binding.recyclerView.adapter = representativeAdapter


        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            representativeAdapter.submitList(it)
        })

        return binding.root
    }



    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putParcelable(ADDRESS, binding.address)
    }

    private fun validateAddress(binding: FragmentRepresentativeBinding): Boolean {
        return binding.address?.line1?.isNotEmpty() == true && binding.address?.city?.isNotEmpty() == true && binding.address?.state?.isNotEmpty() == true && binding.address?.zip?.isNotEmpty() == true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || (requestCode == REQUEST_FOREGROUND_PERMISSION_RESULT_CODE && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            Snackbar.make(binding.motionLayout, R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.settings) {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
        } else {
            checkLocationPermissions()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        if (isPermissionGranted()) {
            getLocation()
            return true
        } else {
            val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(
                    permissionsArray,
                    REQUEST_FOREGROUND_PERMISSION_RESULT_CODE)
            return false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION))
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val address = geoCodeLocation(location)
                        viewModel.address.value = address
                        binding.address = address
                        val states = resources.getStringArray(R.array.states)
                        val selectedStateIndex = states.indexOf(address?.state)
                        binding.state.setSelection(selectedStateIndex)
                        viewModel.getRepresentatives()
                    } else
                        displaySnackbar(requireView(), getString(R.string.location_error))
                }.addOnFailureListener { e -> e.printStackTrace() }

    }

    private fun geoCodeLocation(location: Location): Address? {
        val geoCoder = Geocoder(context, Locale.getDefault())
        return geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    if (address.countryCode != "US") {
                        displaySnackbar(requireView(), getString(R.string.valid_address))
                        null
                    } else {
                        Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                    }
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun displaySnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }
}