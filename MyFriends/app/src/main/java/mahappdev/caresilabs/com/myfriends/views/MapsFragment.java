package mahappdev.caresilabs.com.myfriends.views;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.controllers.MainController;
import mahappdev.caresilabs.com.myfriends.models.DataModel;

public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap       map;
    private LocationManager locationManager;
    private MainController  controller;
    private Marker          myLocationMarker;

    public MapsFragment() {
    }

    public static MapsFragment newInstance() { //String param1, String param2
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);

        mapFragment.getMapAsync(this);

        // setuping locatiomanager to perfrom location related operations
        locationManager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE);

        // Requesting locationmanager for location updates
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // return TODO;
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 200, 2, this);

         /*   Location lastLocation = locationManager.getLastKnownLocation
                    (LocationManager.PASSIVE_PROVIDER);

            LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            Marker marker = map.addMarker(new MarkerOptions().position(memberPos).title(member.name));*/
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        // Update marker
        if (myLocationMarker != null)
            myLocationMarker.setPosition(myLocation);

        // Move and zoom camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 8f));

        // Notify controller
        if (controller != null)
            controller.setMyLocation(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void updateMarkers(DataModel.GroupModel group, String myName) {
        map.clear();

        for (DataModel.MemberModel member : group.members.values()) {
            if (member.latitude == null) {
                continue;
            }

            LatLng memberPos = new LatLng(Double.parseDouble(member.latitude), Double.parseDouble(member.longitude));
            Marker marker = map.addMarker(new MarkerOptions().position(memberPos).title(member.name));
            if (member.name.equals(myName)) {
                myLocationMarker = marker;
            }
        }
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }
}
