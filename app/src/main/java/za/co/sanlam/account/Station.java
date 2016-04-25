package za.co.sanlam.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
/**
 * Created by henry14 on 11/12/14.
 */

public class Station extends FragmentActivity  implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    LocationClient mLocationClient;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private double mLatitude = 0;
    private double mLongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        mLocationRequest = LocationRequest.create();

        mLocationRequest.setInterval(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(1000);

        if(servicesConnected() == true)
            mLocationClient = new LocationClient(this, this, this);

        if(servicesConnected()) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            if(location != null){
                onLocationChanged(location);
            }

            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            String where = "";
            sb.append("location="+mLatitude+","+mLongitude);
            sb.append("&radius=5000");
            sb.append("&name=%22Sanlam%22");//%20Life%20Insurance%22");
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyBKPImjEgRV4V6P3RKzSNI-Yf6bgtwJUUc");

            PlacesTask placesTask = new PlacesTask();
            placesTask.execute(sb.toString());

        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop(){
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "disconnected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //if(connectonResult.hasResolution())
        try{
            connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch(IntentSender.SendIntentException ex){
            ex.printStackTrace();
        }
        //  else showErrorDialog(connectionResult.getErrorCode());


    }

    @Override
    public void onLocationChanged(Location location) {

        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    public static class ErrorDialogFragment extends DialogFragment {

        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog=null;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch(resultCode){
                    case Activity.RESULT_OK:
                        //Try request again
                        break;
                }
        }

    }

    private boolean servicesConnected(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(ConnectionResult.SUCCESS == resultCode)
            return true;

        else{
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,  CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if(errorDialog != null){
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(),"Location Updates");
            }
            return false;
        }
    }

    private String downloadUrl(String strUrl) throws IOException {

        String data = "";
        InputStream input = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection)url.openConnection();

            urlConnection.connect();
            input = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch(Exception ex){
            ex.printStackTrace();

        } finally{
            input.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class PlacesTask extends AsyncTask<String, Integer, String>{
        String data = null;

        @Override
        protected String doInBackground(String... urls) {
            try{
                data = downloadUrl(urls[0]);
            } catch(Exception ex){
                ex.getMessage();
                ex.printStackTrace();

            }
            return data;
        }

        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>>{
        JSONObject jObject;


        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);
                places = placeJsonParser.parse(jObject);
            } catch(Exception ex){

            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list){
            mMap.clear();

            for(int i=0; i<list.size(); i++){
                MarkerOptions markerOptions = new MarkerOptions();

                HashMap<String, String> hmPlace = list.get(i);
                double lat = Double.parseDouble(hmPlace.get("lat"));
                double lng = Double.parseDouble(hmPlace.get("lng"));

                String name = hmPlace.get("place_name");

                String vicinity = hmPlace.get("vicinity");

                LatLng  latLng = new LatLng(lat, lng);

                markerOptions.position(latLng);

                markerOptions.title(name +" : "+vicinity);

                mMap.addMarker(markerOptions);
            }
        }
    }
}
