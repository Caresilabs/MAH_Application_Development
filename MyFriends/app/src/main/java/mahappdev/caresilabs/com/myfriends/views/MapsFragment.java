package mahappdev.caresilabs.com.myfriends.views;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.controllers.MainController;
import mahappdev.caresilabs.com.myfriends.models.DataModel;

public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0xff;

    private GoogleMap       map;
    private LocationManager locationManager;
    private MainController  controller;
    private Marker          myLocationMarker;

    private List<MarkerOptions> markersToAdd = new ArrayList<>();

    public MapsFragment() {
    }

    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        startLcationListening();

        return view;
    }

    private void startLcationListening() {
        // Requesting locationmanager for location updates
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1500, 1, this);

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1500, 1, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        for (MarkerOptions mrkOpt : markersToAdd) {
           map.addMarker(mrkOpt);
        }
        markersToAdd.clear();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        // Update marker
        if (myLocationMarker != null)
            myLocationMarker.setPosition(myLocation);

        // Move and zoom camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 9f));

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
        if (markersToAdd.size() != 0 && map == null)
            return;

        markersToAdd.clear();

        for (DataModel.MemberModel member : group.members.values()) {
            if (member.latitude == null) {
                continue;
            }

            LatLng memberPos = new LatLng(Double.parseDouble(member.latitude), Double.parseDouble(member.longitude));
            MarkerOptions marker = new MarkerOptions().position(memberPos).title(member.name);
            markersToAdd.add(marker);
        }

        if (map != null) {
            map.clear();
            for (MarkerOptions mrkOpt : markersToAdd) {
                Marker marker = map.addMarker(mrkOpt);
                if (mrkOpt.getTitle().equals(myName)) {
                    marker.setTitle(marker.getTitle() + " (me)");
                    myLocationMarker = marker;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLcationListening();
                }
                // TODO show denied message.
                return;
            }
            default:
                break;
        }
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }
}
