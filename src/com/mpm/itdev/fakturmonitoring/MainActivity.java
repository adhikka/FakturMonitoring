package com.mpm.itdev.fakturmonitoring;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.content.res.Configuration;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

import android.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;

public class MainActivity extends Activity implements ActivityListener
{
	public static final String DEBUG_TAG = "FAKTUR_MONITORING";
	public static final String ROOT_URL = "http://10.10.101.14/FakturMonitoring/MainService.svc/";
	public static final String ISINAMA_URL = "isinama/";
	public static final String GABUNG_URL = "gabung/";
	public static final String CETAKFAKTUR_URL = "cetakfaktur/";
	public static final String ISINAMAHOURLY_URL = "monitorisinama/";
	public static final String SELECTED_ITEM = "SELECTED_ITEM";


	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
	private String[] mMonitoringArray;	
	private int selectedItem;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMonitoringArray = getResources().getStringArray(R.array.monitoring_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item , mMonitoringArray));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(
			this,
			mDrawerLayout,
			R.drawable.ic_drawer , R.string.drawer_open,  /* "open drawer" description for accessibility */
			R.string.drawer_close
		) {
			public void onDrawerClosed(View view)
			{
                getActionBar().setTitle(getTitle());
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView)
			{
                getActionBar().setTitle(R.string.fragment_main_jenis_monitoring);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null)
		{
			//getFragmentManager().beginTransaction()
			//	.add(R.id.content_frame, new MonitoringFragment()).commit();
			selectItem(0);
		}
		else
		{
			if (savedInstanceState.containsKey(SELECTED_ITEM))
			{
				int position = savedInstanceState.getInt(SELECTED_ITEM);
				selectItem(position);
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item))
		{
            return true;
        }

		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
	{
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
	{
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	/* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener
	{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
            selectItem(position);
        }
    }

    private void selectItem(int position)
	{
		String url = "";
		String tag = "";

		selectedItem = position;
        // update the main content by replacing fragments
		if (position == 0)
		{
			url = ROOT_URL.concat(ISINAMA_URL);
			tag = MonitoringFragment.ISI_NAMA_FRAGMENT_TAG;
		}
		else if (position == 1)
		{
			url = ROOT_URL.concat(GABUNG_URL);
			tag = MonitoringFragment.GABUNG_FRAGMENT_TAG;
		}
		else if (position == 2)
		{
			url = ROOT_URL.concat(CETAKFAKTUR_URL);
			tag = MonitoringFragment.CETAK_FAKTUR_FRAGMENT_TAG;
		}
		else if (position == 3)
		{
			url = ROOT_URL.concat(ISINAMAHOURLY_URL);
			tag = MonitoringFragment.ISI_NAMA_HOURLY_FRAGMENT_TAG;
		}

        Fragment fragment ;//= new MonitoringFragment();
		fragment = getFragmentManager().findFragmentByTag(tag);

		if (fragment == null)
		{
			fragment = new MonitoringFragment();

			Bundle args = new Bundle();
			args.putString(MonitoringFragment.ARG_MONITORING_URL, url);
			args.putInt(MonitoringFragment.ARG_MONITORING_TYPE, position);
			fragment.setArguments(args);	
		}

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, tag).addToBackStack(tag).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);        
        mDrawerLayout.closeDrawer(mDrawerList);
    }

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{	
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_ITEM, selectedItem);
	}

	@Override
	public void showDatePickerDialog(View v)
	{

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class MonitoringFragment extends ListFragment implements DatePickerDialog.OnDateSetListener
	{	
		public static final String ISI_NAMA_FRAGMENT_TAG = "isi_nama_fragment";
		public static final String GABUNG_FRAGMENT_TAG = "gabung_fragment";
		public static final String CETAK_FAKTUR_FRAGMENT_TAG = "cetak_faktur_fragment";
		public static final String ISI_NAMA_HOURLY_FRAGMENT_TAG = "isi_nama_hourly_fragment";
		public static final String TAG_JUMLAH = "Jumlah";
		public static final String TAG_KODECABANG = "Kodecabang";
		public static final String TAG_TANGGALTX = "TanggalTx";
		public static final String TAG_TIPETX = "TipeTx";
		public static final String ARG_MONITORING_URL = "monitoring_url";
		public static final String ARG_MONITORING_TYPE = "monitoring_type";
		public static final String SELECTED_DATE = "selected_date";
		public static final String DATA_FROM_SERVER = "data_from_server";

		private ActivityListener activityListener;
		private View rootView;
		private String[] mMonitoringArray;		
		private String url="";
		private int mType=0;
		private String selectedDate;
		private String dataFromServer;
		ProgressDialog pDialog;

		public MonitoringFragment()
		{
		}

		@Override
		public void onAttach(Activity activity)
		{
			super.onAttach(activity);
			if (activity instanceof MainActivity)
			{
				this.activityListener = (ActivityListener) activity;
			}
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day)
		{
			updateDate(Integer.toString(year) + "-" + Integer.toString(month + 1) + "-" + Integer.toString(day));
		}

		private void updateDate(String date)
		{
			EditText editText = (EditText) rootView.findViewById(R.id.editTextdatetx);
			editText.setText(date);
			selectedDate = date;
		}

		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
		}



		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState)
		{
			rootView = inflater.inflate(R.layout.fragment_main, container,
										false);
			if (savedInstanceState != null)
			{
				if (savedInstanceState.containsKey(SELECTED_DATE))
				{
					String date = savedInstanceState.getString(SELECTED_DATE);
					updateDate(date);
				}
			}

			final Button submitButton = (Button) rootView.findViewById(R.id.submit_button);
			//final RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroupTx);
			final EditText dateEdit = (EditText) rootView.findViewById(R.id.editTextdatetx);
			final TextView textView = (TextView) rootView.findViewById(R.id.textViewjenismonitoring);

			Bundle args = getArguments();
			url = args.getString(ARG_MONITORING_URL);

			mType = args.getInt(ARG_MONITORING_TYPE);
			mMonitoringArray = getActivity().getResources().getStringArray(R.array.monitoring_array);
			textView.setText("Monitoring " + mMonitoringArray[mType]);
			
			dateEdit.setKeyListener(null);
			dateEdit.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View view)
					{
						//activityListener.showDatePickerDialog(view);
						DialogFragment dialog = new DialogFragment() {
							@Override
							public Dialog onCreateDialog(Bundle savedInstanceState)
							{
								// Use the current date as the default date in the picker
								final Calendar c = Calendar.getInstance();
								int year = c.get(Calendar.YEAR);
								int month = c.get(Calendar.MONTH);
								int day = c.get(Calendar.DAY_OF_MONTH);

								// Create a new instance of DatePickerDialog and return it
								return new DatePickerDialog(MonitoringFragment.this.getActivity(), MonitoringFragment.this, year, month, day);
							}
						};
						dialog.show(getFragmentManager(), "datePicker");
					}
				});

			submitButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0)
					{

						ConnectivityManager connMgr = (ConnectivityManager) 
				            getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
				        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				        if (networkInfo != null && networkInfo.isConnected())
						{
				        	String datetx = dateEdit.getText().toString();

							if (datetx == null ? true : datetx.equals("") ? true : false)
							{
								Toast.makeText(getActivity(), "Tanggal Tx can not be empty", Toast.LENGTH_LONG).show();
								return;
							}

							String tempUrl = url;

							tempUrl = tempUrl.concat(datetx);

				        	AsyncTask<String, Void, String> task
								= new AsyncTask<String, Void, String>() {

				            	InputStream is;
				            	StringBuilder builder = new StringBuilder();
				            	ArrayList<HashMap<String, String>> listvalues = new ArrayList<HashMap<String, String>>();

				            	protected void onPreExecute()
								{
				            		super.onPreExecute();
				            		pDialog = new ProgressDialog(getActivity());
				                    pDialog.setMessage("Please wait...");
				                    pDialog.setCancelable(false);
				                    pDialog.show();
				            	};

								@Override
								protected String doInBackground(
									String... params)
								{
									try
									{
										URL url = new URL(params[0]);
										HttpURLConnection conn = (HttpURLConnection) url.openConnection();
										conn.setReadTimeout(10000);
								        conn.setConnectTimeout(15000);
								        conn.setRequestMethod("GET");
								        conn.setDoInput(true);
								        // Starts the query
								        conn.connect();
								        int response = conn.getResponseCode();

								        Log.d(DEBUG_TAG, "The response is: " + response);

								        if (response == 200)
										{
								        	is = conn.getInputStream();

									        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

									        String line;

									        while ((line = reader.readLine()) != null)
											{
												builder.append(line);
											}

										}
										else
										{
											getActivity().runOnUiThread(new Runnable() {

													@Override
													public void run()
													{
														Toast.makeText(getActivity(), "Server connecting error.", Toast.LENGTH_LONG).show();
													}

												});											
										}

									}
									catch (MalformedURLException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									catch (IOException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
										getActivity().runOnUiThread(new Runnable() {

												@Override
												public void run()
												{
													Toast.makeText(getActivity(), "No network connection available.", Toast.LENGTH_LONG).show();
													if (pDialog.isShowing())
														pDialog.dismiss();
												}

											});	

									}
									return builder.toString();
								}

								@Override
								protected void onPostExecute(String result)
								{
									super.onPostExecute(result);
									dataFromServer = result;
									try
									{
										if (mType == 0 || mType == 1 || mType == 2)
										{
											listvalues = processDailyMonitoring(result);

											if (pDialog.isShowing())
												pDialog.dismiss();

											ListAdapter adapter = new SimpleAdapter(getActivity(), listvalues, R.layout.list_item, 
																					new String[] {TAG_TIPETX, TAG_KODECABANG, TAG_TANGGALTX, TAG_JUMLAH},
																					new int[] {R.id.textViewjenismonitoring,R.id.textView2, R.id.textView3, R.id.textView4}
																					);

											setListAdapter(adapter);
										}
										else if (mType == 3)
										{
											listvalues = processHourlyMonitoring(result);

											if (pDialog.isShowing())
												pDialog.dismiss();

											ListAdapter adapter = new SimpleAdapter(getActivity(), listvalues, R.layout.list_item_hourly, 
																					new String[] {TAG_TIPETX, /*TAG_KODECABANG, TAG_TANGGALTX,*/ TAG_JUMLAH},
																					new int[] {R.id.textViewjenismonitoringhourly /*,R.id.textView2, R.id.textView3*/,R.id.textCountHourly}
																					);

											setListAdapter(adapter);
										}

									}
									catch (Exception e)
									{
										Log.e(DEBUG_TAG, e.getMessage());
									}
								}


							};
							task.execute(tempUrl);
				        }
						else
						{
				            Toast.makeText(getActivity(), "No network connection available.", Toast.LENGTH_SHORT).show();						
				        }

					}
				});


			return rootView;
		}

		private ArrayList<HashMap<String, String>> processHourlyMonitoring(String result)
		{
			String[] resultArray = result.replace("\"", "").split(";");
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			int j = 1;
			int total = 0;

			for (int i=7; i <= 23; i++)
			{
				HashMap<String, String> values = new HashMap<String, String>();
				values.put(TAG_JUMLAH, resultArray[j]);											
				values.put(TAG_TIPETX, Integer.toString(i) + " - " + Integer.toString(i + 1));
				total += Integer.parseInt(resultArray[j]);
				j += 1;

				list.add(values);
			}
			HashMap<String, String> totalValues = new HashMap<String, String>();
			totalValues.put(TAG_JUMLAH, Integer.toString(total));											
			totalValues.put(TAG_TIPETX, "Total");
			list.add(totalValues);

			return list;
		}

		private ArrayList<HashMap<String, String>> processDailyMonitoring(String result) throws JSONException
		{
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			JSONArray results = new JSONArray(result);
			for (int i = 0; i < results.length(); i++)
			{
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

				list.add(values);

			}


			return list;
		}
		
		private ArrayList<HashMap<String, String>> processHourlyMonitoringDetail(String result) throws JSONException
		{
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			JSONArray results = new JSONArray(result);
			for (int i = 0; i < results.length(); i++)
			{
				JSONObject obj = results.getJSONObject(i);

				int jumlah = obj.getInt(TAG_JUMLAH);
				String kodecabang = obj.getString("kodecabang");
				String tgltransaksi = obj.getString("tgltransaksi");
				int h07_08 = obj.getInt("h07_08");
				int h08_09 = obj.getInt("h08_09");
				int h09_10 = obj.getInt("h09_10");
				int h10_11 = obj.getInt("h10_11");
				int h11_12 = obj.getInt("h11_12");
				int h12_13 = obj.getInt("h12_13");
				int h13_14 = obj.getInt("h13_14");
				int h14_15 = obj.getInt("h14_15");
				int h15_16 = obj.getInt("h15_16");
				int h16_17 = obj.getInt("h16_17");
				int h17_18 = obj.getInt("h17_18");
				int h18_19 = obj.getInt("h18_19");
				int h19_20 = obj.getInt("h19_20");
				int h20_21 = obj.getInt("h20_21");
				int h21_22 = obj.getInt("h21_22");
				int h22_23 = obj.getInt("h22_23");
				int h23_24 = obj.getInt("h23_24");

				HashMap<String, String> values = new HashMap<String, String>();
				values.put("kodecabang", kodecabang);
				values.put("tgltransaksi", tgltransaksi);
				values.put(TAG_TANGGALTX, tanggaltx);
				values.put("h07_08",Integer.toString(h07_08));
				values.put("h08_09",Integer.toString(h08_09));
				values.put("h09_10" , Integer.toString(h09_10));
				values.put("h10_11", Integer.toString(h10_11));
				values.put("h11_12", Integer.toString(h11_12));
				values.put("h12_13", Integer.toString(h12_13));
				values.put("h13_14", Integer.toString(h13_14));
				values.put("h14_15", Integer.toString(h14_15));
				values.put("h15_16 = obj.getInt("h15_16");
				values.put("h16_17 = obj.getInt("h16_17");
				values.put("h17_18 = obj.getInt("h17_18");
				values.put("h18_19 = obj.getInt("h18_19");
				values.put("h19_20 = obj.getInt("h19_20");
				values.put("h20_21 = obj.getInt("h20_21");
				values.put("h21_22 = obj.getInt("h21_22");
				values.put("h22_23 = obj.getInt("h22_23");
				values.put("h23_24 = obj.getInt("h23_24");

				list.add(values);

			}


			return list;
		}

		@Override
		public void onSaveInstanceState(Bundle outState)
		{
			super.onSaveInstanceState(outState);
			outState.putString(SELECTED_DATE, selectedDate);
			outState.putString(DATA_FROM_SERVER, dataFromServer);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);
			if (savedInstanceState != null)
			{
				ArrayList<HashMap<String, String>> listvalues = new ArrayList<HashMap<String, String>>();
				dataFromServer = savedInstanceState.getString(DATA_FROM_SERVER);
				if (mType == 0 || mType == 1 || mType == 2)
				{
					try
					{
						listvalues = processDailyMonitoring(dataFromServer);
						ListAdapter adapter = new SimpleAdapter(getActivity(), listvalues, R.layout.list_item, 
																new String[] {TAG_TIPETX, TAG_KODECABANG, TAG_TANGGALTX, TAG_JUMLAH},
																new int[] {R.id.textViewjenismonitoring,R.id.textView2, R.id.textView3, R.id.textView4}
																);

						setListAdapter(adapter);
					}
					catch (JSONException e)
					{

					}

				}
				else if (mType == 4)
				{
					try
					{
						listvalues = processHourlyMonitoring(dataFromServer);
						ListAdapter adapter = new SimpleAdapter(getActivity(), listvalues, R.layout.list_item_hourly, 
																new String[] {TAG_TIPETX, /*TAG_KODECABANG, TAG_TANGGALTX,*/ TAG_JUMLAH},
																new int[] {R.id.textViewjenismonitoringhourly /*,R.id.textView2, R.id.textView3*/,R.id.textCountHourly}
																);

						setListAdapter(adapter);
					}
					catch (Exception e)
					{

					}

				}
			}
		}


	}
}

interface ActivityListener
{
	void showDatePickerDialog(View v);
}
