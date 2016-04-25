package za.co.sanlam.account;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Panel extends ActionBarActivity{
    /*implements
} LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {*/
    /*private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;*/
//    private GoogleMap mMap;
    /*private double mLatitude = 0;
    private double mLongitude = 0;*/

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

   // private CharSequence mDrawerTitle;
   // private CharSequence mTitle;
    private String[] mItemTitles;
   // private List<String> data;
  // TextView txtView;
   TextView txtView, mHeader;
    private static String url = "http://109.123.112.186:8080/sancare/claim/api/get/henry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        int mId = 1;
        String mTitle = "test"; //where it is coming 4rm e.g tip
        String mText = "testing"; // Message
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(mTitle)
                .setContentText(mText);
        TaskStackBuilder mStackBuilder = TaskStackBuilder.create(this);
        mStackBuilder.addParentStack(Panel.class);
        mStackBuilder.addNextIntent(new Intent(this, Panel.class));
        PendingIntent mPending = mStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(mPending);
        NotificationManager mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(mId, mBuilder.build());


        //mTitle = mDrawerTitle = getTitle();
        mItemTitles = getResources().getStringArray(R.array.accounts_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        ImageView imageView1 = (ImageView) findViewById(R.id.list_image);
       // TextView textView1 = (TextView) findViewById(R.id.test);
        LayoutInflater inflater = this.getLayoutInflater();
        LinearLayout listHeaderView = (LinearLayout)inflater.inflate(
                R.layout.list_header, null);
        LinearLayout listFooterView = (LinearLayout)inflater.inflate(
                R.layout.list_footer, null);

        mDrawerList.addHeaderView(listHeaderView);
        mDrawerList.addFooterView(listFooterView);

        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.ic_launcher, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mItemTitles));

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
       // Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, null,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
         //   selectItem(0);
        }

        mHeader = (TextView)findViewById(R.id.header_text);

        /*String user = getIntent().getExtras().getString("username").toString();
        mHeader.setText(user);*/


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_panel, menu);
        MenuItem mItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(mItem);
        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_query:
                startActivity(new Intent(Panel.this, AddQuery.class));

                // create intent to perform web search for this planet
                /*Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_query).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onStart(){
        super.onStart();
//        mLocationClient.connect();
    }

    @Override
    public void onStop(){
//        mLocationClient.disconnect();
        super.onStop();
    }


/*
    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        try{
            connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException ex){
            ex.printStackTrace();
        }
    }*/

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            selectItem(position);
           txtView = (TextView)findViewById(R.id.textViewA);

         //   txtView.setText("book");

        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putInt(AccountFragment.ARG_ACCOUNT_ITEM_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        /*FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();*/

        // update selected item and title, then close the drawer
        if(position == 0 || position ==8) {
            setTitle("Sancare");
        } else{
            mDrawerList.setItemChecked(position, true);
            setTitle(mItemTitles[position - 1]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    /*@Override
    public void setTitle(CharSequence title) {
      //  mTitle = title;
      //  getActionBar().setTitle(mTitle);
    }*/

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class AccountFragment extends Fragment {
        public static final String ARG_ACCOUNT_ITEM_NUMBER = "account_number";
        private String[] mContacts;

        public AccountFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;

            int i = getArguments().getInt(ARG_ACCOUNT_ITEM_NUMBER);
            String account;
            if(i == 0 || i==8){
                account = "Sancare";
            }
            else{
                account = getResources().getStringArray(R.array.accounts_array)[i -1];
                switch (i){
                    case 1:
                        rootView = inflater.inflate(R.layout.sub_list_item, container, false);
                        ((TextView)rootView.findViewById(R.id.textViewA)).setText("My" +
                                " balance is less below what I expect");
                        break;
                    case 2:
                        rootView = inflater.inflate(R.layout.sub_list_item, container, false);
                        ((TextView)rootView.findViewById(R.id.textViewA)).setText("Medical" +
                                " bills worth Shs.3M");
                        try {

                        }
                        catch (Exception ex){

                        }
                        break;
                    case 3:
                        rootView = inflater.inflate(R.layout.sub_list_item, container, false);
                        ((TextView)rootView.findViewById(R.id.textViewA)).setText("Life Insurance");
                        break;
                    case 4:
                        rootView = inflater.inflate(R.layout.sub_list_item, container, false);
                        ((TextView)rootView.findViewById(R.id.textViewA)).setText("Drink water" +
                                " everyday for proper blood flow");
                        break;
                    case 5:
                        startActivity(new Intent(getActivity(), Station.class));
                        break;
                    case 6:
                        //Change rootView and display the list of contacts.
                        rootView = inflater.inflate(R.layout.contact_list, container, false);
                        mContacts = getResources().getStringArray(R.array.contacts_array);
                        ListView lV = ((ListView)rootView.findViewById(R.id.contact_list));
                                lV.setAdapter(
                                new ArrayAdapter<String>(getActivity(), R.layout.contact_item, mContacts));
                        lV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String contact = (String)parent.getItemAtPosition(position);
                                Log.v("contact", contact);
//                                contact = contact.substring(contact.indexOf(":")).replace(""+contact.charAt(0), "(+256)");
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+contact)));
                            }
                        });
                        break;
                    case 7:
                        rootView = inflater.inflate(R.layout.sub_list_item, container, false);
                        ((TextView)rootView.findViewById(R.id.textViewA)).setText("Sanlam" +
                                "is a life insurance company ......................");
                        break;
                    default:
                        break;
                }
            }


            /*int imageId = getResources().getIdentifier(account.toUpperCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
           ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);*/

            getActivity().setTitle(account);
            return rootView;
        }
    }

    /*private boolean servicesConnected(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(ConnectionResult.SUCCESS == resultCode)
            return true;
        else {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if(errorDialog != null){

            }
            return false;
        }
    }*/

    private static String dowloadUrl(String strUrl) throws IOException {
        String mData = "";
        InputStream mInput = null;
        HttpURLConnection mUrlConnection = null;
        try{
            URL mUrl = new URL(strUrl);
            mUrlConnection = (HttpURLConnection)mUrl.openConnection();
            mUrlConnection.connect();

            mInput = mUrlConnection.getInputStream();
            BufferedReader br =  new BufferedReader(new InputStreamReader(mInput));
            StringBuffer sb = new StringBuffer();
            String mLine = "";
            while ((mLine = br.readLine()) != null)
                sb.append(mLine);
            mData = sb.toString();
            br.close();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            mInput.close();
            mUrlConnection.disconnect();
        }
        return mData;
    }

    public class PostFetcher extends AsyncTask<String, Void, List<Object>> {

        List<Object> list = new ArrayList<Object>();
        @Override
        protected List<Object> doInBackground(String... urls) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            JSONArray jsonArray = null;
            try {
                HttpResponse response = httpClient.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream in = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String receipt = reader.readLine();
                    while(reader.readLine() !=null ){
                        receipt += reader.readLine();
                    }
                    String json = receipt.replace("\"[", "[").replace("]\"", "]").replace("\\\"", "\"");
                    if(!json.equalsIgnoreCase("null"))
                        jsonArray = new JSONArray(json);

                }

            } catch (Exception e) {

                e.printStackTrace();
            }
            try {
                return buildData(jsonArray);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
//				e.printStackTrace();
                return list;
            }
        }

        private List<Object> buildData(JSONArray jArray) throws JSONException{
            List<Object> list = new ArrayList<Object>();
            JSONObject jsonObj;
            for(int i= 0; i<jArray.length(); i++){
                jsonObj = jArray.getJSONObject(i);
                String str = (String) jsonObj.get("complaint");
                String str2 = ""+jsonObj.getInt("refNumber");
                /*Structure structure = new Structure();
                structure.setRefNumber("Reference: "+str2);
                structure.setComplaint("Complaint: "+str);
                list.add(structure);*/
            }
            return list;
        }

    }
}
