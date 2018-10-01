package tw.edu.tut.mis.gps1001;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient MLPC;
    final String TAG = "necro-wbj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MLPC = LocationServices.getFusedLocationProviderClient(this);
    }

    LocationCallback LCB = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
            List<Location> locations =locationResult.getLocations();
            if (locations.size()>0) {
                Location loc = locations.get(locations.size() - 1);
                double lat,lon;
                lat = loc.getLatitude();
                lon = loc.getLongitude();
                Log.d(TAG,"location:("+lat+","+lon+")");
            }
        }
    };
    void StartLocationUpdat(){

    };
    void StopLocationUpdate(){

    };

    @Override
    protected void onResume() {
        super.onResume();
        StartLocationUpdat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StopLocationUpdate();
    }
}
