package tw.edu.tut.mis.gps1001;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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
                Toast.makeText(this,"必須要開啟定位權限方可使用",Toast.LENGTH_LONG);
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
        StartLocationUpdat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StopLocationUpdate();
    }
}
