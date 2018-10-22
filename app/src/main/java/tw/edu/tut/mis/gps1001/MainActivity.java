package tw.edu.tut.mis.gps1001;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
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
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    FusedLocationProviderClient MLPC;
    final String TAG = "necro-wbj";
    private GoogleMap gMap = null;
    boolean isGPS_ON;
    String mUserID;
    String mTempUserID = UUID.randomUUID().toString();
    double mlat=23.038, mlon=120.237;
    Handler timerHandler = new Handler();

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
            mUserID = mTempUserID;
        }else {
            isGPS_ON = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d(TAG,"儲存GPS開關職狀態");
        outState.putBoolean("GPS_ON",isGPS_ON);
        outState.putString("USER_ID",mTempUserID);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isGPS_ON = savedInstanceState.getBoolean("GPS_ON",true);
        mUserID = savedInstanceState.getString("USER_ID",mTempUserID);
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
                mlat = loc.getLatitude();
                mlon = loc.getLongitude();
                Log.d(TAG, "location:(" + mlat + "," + mlon + ")");
                ((TextView)findViewById(R.id.test)).setText("經緯度:(" + mlat + "," + mlon + ")");
                LatLng pos = new LatLng(mlat,mlon);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                gMap.clear();
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
                Log.e(TAG,"沒有開啟允許定位的權限");
                Toast.makeText(this,"必須要開啟允許定位的權限才能正常使用",Toast.LENGTH_LONG).show();
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
    }
    void StopLocationUpdate(){
        MLPC.removeLocationUpdates(LCB);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isGPS_ON) {
            StartLocationUpdat();
        }
        mapView.onResume();
        timerHandler.postDelayed(timerTask,0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isGPS_ON) {
            StopLocationUpdate();
        }
        mapView.onPause();
        timerHandler.removeCallbacks(timerTask);
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

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {

            Log.d(TAG, "啟動:" + mlat + "," + mlon);
            if( gMap != null ){
                LatLng pos = new LatLng(mlat, mlon);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                gMap.clear();
                gMap.addMarker(new MarkerOptions().position(pos).title("目前位置"));
            }
            timerHandler.postDelayed(this,5000);

        }
    };
}
