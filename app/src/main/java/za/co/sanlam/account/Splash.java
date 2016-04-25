package za.co.sanlam.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class Splash extends ActionBarActivity {
    private static long sleepTime = 5L;
    GoogleCloudMessaging gcm;
    private String regid;
    String PROJECT_NUMBER = "433563895236";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        getRegId();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000L * sleepTime);
                    //startActivity(new Intent(Splash.this, Login.class));
                    startActivity(new Intent(Splash.this, Panel.class));
                    finish();
                }catch (Exception ex){
                    while(true)
                        Log.e("splash ", ex.getLocalizedMessage());
                }

            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try{
                    if(gcm == null){
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID="+regid;
                    Log.v("regid",regid);
                } catch (Exception ex){
                    msg = "Error: "+ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg){
                Log.v("msg",msg);
            }
        }.execute(null, null, null);
    }

}
