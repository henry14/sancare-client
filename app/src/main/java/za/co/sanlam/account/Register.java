package za.co.sanlam.account;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;


public class Register extends ActionBarActivity {

    private EditText mNumberView, mDateOfBirthView, mUsernameView, mPasswordView, mRetypeView;
    private Button mRegisterBtn;
//    private int mNumber;
    private String mDateOfBirth, mUsername, mPassword;
    private String url = "http://109.123.112.186:8080/sancare/client/api/add";
    private TextView mRegError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mRegError = (TextView)findViewById(R.id.register_error);
        mNumberView = (EditText)findViewById(R.id.member_number);
        mDateOfBirthView = (EditText)findViewById(R.id.date_of_birth);
        mUsernameView = (EditText)findViewById(R.id.username);
        mPasswordView = (EditText)findViewById(R.id.password);
        mRetypeView = (EditText)findViewById(R.id.password_repeat);
        mRegisterBtn = (Button)findViewById(R.id.register_button);
        mDateOfBirthView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDatePickerDialog();
                }

            }
        });
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((mPasswordView.getText().toString()).equals(mRetypeView.getText().toString()))
                    checkFeilds();

                else
                    return;

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void checkFeilds(){
        String mNumber = mNumberView.getText().toString();
        mDateOfBirth = mDateOfBirthView.getText().toString();
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        if(mNumber.isEmpty() || mDateOfBirth.isEmpty() || mUsername.isEmpty() || mPassword.isEmpty()) {
            mRegError.setVisibility(View.VISIBLE);
            mRegError.setText(R.string.register_error);
            return;
        }
        else {
            register();
        }

    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "date picker");
    }

    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        /*
         * (non-Javadoc)
         *
         * @see
         * android.app.DatePickerDialog.OnDateSetListener#onDateSet(android.
         * widget.DatePicker, int, int, int)
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, dd, mm, yy);

        }

        /*
         * (non-Javadoc)
         *
         * @see
         * android.app.DatePickerDialog.OnDateSetListener#onDateSet(android.
         * widget.DatePicker, int, int, int)
         */
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            mDateOfBirthView.setText(new StringBuilder().append(dayOfMonth).append("/")
                    .append(monthOfYear + 1).append("/").append(year));
        }
    }

    private void register(){
        try {
            JSONObject obj = new UserSignTask().execute(url).get();
            String result = obj.getString("decide").toString();
            if(result.equalsIgnoreCase("saved")){
                String username = mUsernameView.getText().toString();
                Bundle extras = new Bundle();
                extras.putString("username", username);
                startActivity(new Intent(this, Panel.class).putExtras(extras));
                mUsernameView.setText("");
                mPasswordView.setText("");
                mDateOfBirthView.setText("");
                mPasswordView.setText("");
                mRetypeView.setText("");
            } else{
                mUsernameView.setText("");
                mPasswordView.setText("");
                mDateOfBirthView.setText("");
                mPasswordView.setText("");
                mRetypeView.setText("");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mRegError.setText(R.string.number_error);
                    }
                }).start();

                return;
            }
        }
        catch(Exception ex){

        }

    }

    public JSONObject sendData(String url) {
        InputStream inputStream = null;
        String result = "";
        JSONObject jsonObj = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);
            JSONObject obj = new JSONObject();
            obj.put("username", mUsernameView.getText().toString());
            obj.put("password", mPasswordView.getText().toString());
            obj.put("customerNumber", mNumberView.getText().toString());
            obj.put("dateOfBirth", mDateOfBirthView.getText().toString());

            StringEntity se = new StringEntity(obj.toString());

            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("content-type", "application/json");

            HttpResponse httpResponse = httpClient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                inputStream = httpResponse.getEntity().getContent();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String receipt = reader.readLine();
                while (reader.readLine() != null) {
                    receipt += reader.readLine();
                }
                String json1 = receipt.replace("\"{", "{").replace("}\"", "}")
                        .replace("\\\"", "\"");
                if (!json1.equalsIgnoreCase("null")) {
                    jsonObj = new JSONObject(json1);
                }

            }

        } catch (Exception ex) {
            Log.d("InputStream", ex.getLocalizedMessage());

        }
        return jsonObj;
    }

    public class UserSignTask extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params){
            Data data = new Data();
            return sendData(params[0]);
        }

    }
}
