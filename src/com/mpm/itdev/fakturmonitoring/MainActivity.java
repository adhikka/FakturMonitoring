package com.mpm.itdev.fakturmonitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {
	public static final String DEBUG_TAG = "FAKTUR_MONITORING";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends ListFragment {
		public static final String TAG_JUMLAH = "Jumlah";
		public static final String TAG_KODECABANG = "Kodecabang";
		public static final String TAG_TANGGALTX = "TanggalTx";
		public static final String TAG_TIPETX = "TipeTx";
		private String url="";

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			final Button submitButton = (Button) rootView.findViewById(R.id.submit_button);
			final RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroupTx);
			final EditText dateEdit = (EditText) rootView.findViewById(R.id.editTextdatetx);
			
			submitButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					ConnectivityManager connMgr = (ConnectivityManager) 
				            getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
				        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				        if (networkInfo != null && networkInfo.isConnected()) {
				        	String datetx = dateEdit.getText().toString();
							
							if (radioGroup.getCheckedRadioButtonId() == R.id.radioisinama) {
								url = "http://10.10.101.14/FakturMonitoring/MainService.svc/isinama/".concat(datetx);
							} else if (radioGroup.getCheckedRadioButtonId() == R.id.radiogabung) {
								url = "http://10.10.101.14/FakturMonitoring/MainService.svc/gabung/".concat(datetx);
							} else if (radioGroup.getCheckedRadioButtonId() == R.id.radiocetakfaktur) {
								url = "http://10.10.101.14/FakturMonitoring/MainService.svc/cetakfaktur/".concat(datetx);
							}
				        	
				        	AsyncTask<String, Void, String> task
				            = new AsyncTask<String, Void, String>() {
				        		ProgressDialog pDialog;
				            	InputStream is;
				            	StringBuilder builder = new StringBuilder();
				            	ArrayList<HashMap<String, String>> listvalues = new ArrayList<HashMap<String, String>>();
				            	
				            	protected void onPreExecute() {
				            		super.onPreExecute();
				            		pDialog = new ProgressDialog(getActivity());
				                    pDialog.setMessage("Please wait...");
				                    pDialog.setCancelable(false);
				                    pDialog.show();
				            	};
				            	
								@Override
								protected String doInBackground(
										String... params) {
									try {
										URL url = new URL(params[0]);
										HttpURLConnection conn = (HttpURLConnection) url.openConnection();
										conn.setReadTimeout(10000 /* milliseconds */);
								        conn.setConnectTimeout(15000 /* milliseconds */);
								        conn.setRequestMethod("GET");
								        conn.setDoInput(true);
								        // Starts the query
								        conn.connect();
								        int response = conn.getResponseCode();
								        
								        Log.d(DEBUG_TAG, "The response is: " + response);
								        
								        if (response == 200) {
								        	is = conn.getInputStream();
									        
									        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
									        
									        String line;
									        
									        while ((line = reader.readLine()) != null ) {
												builder.append(line);
											}
											
										} else {
											Toast.makeText(getActivity(), "Server connecting error.",Toast.LENGTH_SHORT).show();
										}
								        
									} catch (MalformedURLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return builder.toString();
								}
								
								@Override
								protected void onPostExecute(String result) {
									super.onPostExecute(result);
									try {
										JSONArray results = new JSONArray(result);
										for (int i = 0; i < results.length(); i++) {
											JSONObject obj = results.getJSONObject(i);
											
											int jumlah = obj.getInt(TAG_JUMLAH);
											String kodecabang = obj.getString(TAG_KODECABANG);
											String tanggaltx = obj.getString(TAG_TANGGALTX);
											String tipetx = obj.getString(TAG_TIPETX);
											
											HashMap<String, String> values = new HashMap<String, String>();
											values.put(TAG_JUMLAH, Integer.toString(jumlah));
											values.put(TAG_KODECABANG, kodecabang);
											values.put(TAG_TANGGALTX, tanggaltx);
											values.put(TAG_TIPETX, tipetx);
											
											listvalues.add(values);
											
										}
										
										if (pDialog.isShowing())
							                pDialog.dismiss();
										
										ListAdapter adapter = new SimpleAdapter(getActivity(), listvalues, R.layout.list_item, 
												new String[] {TAG_TIPETX, TAG_KODECABANG, TAG_TANGGALTX, TAG_JUMLAH},
												new int[] {R.id.textViewjenismonitoring,R.id.textView2, R.id.textView3, R.id.textView4}
												);
										
										setListAdapter(adapter);
									} catch (Exception e) {
										Log.e(DEBUG_TAG, e.getMessage());
									}
								};
							};
				        	task.execute(url);
				        } else {
				            Toast.makeText(getActivity(), "No network connection available.",Toast.LENGTH_SHORT).show();
				        }
					
				}
			});
			
			
			return rootView;
		}
	}
}
