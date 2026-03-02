package com.practice.astra.ui.route

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.practice.astra.R
import com.practice.astra.databinding.FragmentRouteBinding

class RouteFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentRouteBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val tokyoStation = LatLng(35.6812, 139.7671)
        googleMap.addMarker(MarkerOptions().position(tokyoStation).title("東京駅のチケット"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tokyoStation, 15f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}