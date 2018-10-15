package tw.edu.tut.mis.gps1001;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    FusedLocationProviderClient MLPC;
    final String TAG = "necro-wbj";
    private GoogleMap gMap;
    boolean isGPS_ON;
    @BindView(R.id.gpsswitch) ImageButton gpsSwtichButton;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        MLPC = LocationServices.getFusedLocationProviderClient(this);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        if (savedInstanceState != null){
            isGPS_ON = savedInstanceState.getBoolean("GPS_ON",true);
        }else {
            isGPS_ON = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d(TAG,"儲存GPS開關職狀態");
        outState.putBoolean("GPS_ON",isGPS_ON);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isGPS_ON = savedInstanceState.getBoolean("GPS_ON",true);
    }

    @OnClick(R.id.gpsswitch)
    void onGPSSwitch(View v){
        Log.d(TAG,"GPS開關處理");
        if(isGPS_ON){
            isGPS_ON = false;
            gpsSwtichButton.setImageResource(R.drawable.location1);
            StopLocationUpdate();
        }else{
            isGPS_ON = true;
            gpsSwtichButton.setImageResource(R.drawable.location2);
            StartLocationUpdat();
        }
    }

    LocationCallback LCB = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
            List<Location> locations = locationResult.getLocations();
            if (locations.size() > 0) {
                Location loc = locations.get(locations.size() - 1);
                double lat, lon;
                lat = loc.getLatitude();
                lon = loc.getLongitude();
                Log.d(TAG, "location:(" + lat + "," + lon + ")");
                ((TextView)findViewById(R.id.test)).setText("經緯度:(" + lat + "," + lon + ")");
                LatLng pos = new LatLng(lat,lon);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                gMap.addMarker(new MarkerOptions().position(pos).title("目前位置"));
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==9527){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                StartLocationUpdat();
            }else{
                Log.e(TAG,"沒有開啟定位權限");
                Toast.makeText(this,"必須要開啟定位權限方可使用",Toast.LENGTH_LONG).show();
            }
        }
    }

    void StartLocationUpdat() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this
                    ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}
                    ,9527);
            return;
        }
        LocationRequest req = new LocationRequest();
        req.setInterval(10000);
        req.setFastestInterval(5000);
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        MLPC.requestLocationUpdates(req, LCB, null);
    };
    void StopLocationUpdate(){
        MLPC.removeLocationUpdates(LCB);

    };

    @Override
    protected void onResume() {
        super.onResume();
        if(isGPS_ON) {
            StartLocationUpdat();
        }
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isGPS_ON) {
            StopLocationUpdate();
        }
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.038,120.237),17));
    }
}
