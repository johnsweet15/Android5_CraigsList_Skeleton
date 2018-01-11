package com.example.listview;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Activity_ListView extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ListView.OnItemClickListener {

	ListView my_listview;
	ConnectivityCheck myCheck;

	private String currentURL = "";
	private static final String SELECT_WEBSITE = "Select a website in settings";
	private static ArrayList<String> sortList;
	private String current = "Company";
	private static List<BikeData> bikes;
	public SharedPreferences preferences;
	private SharedPreferences.OnSharedPreferenceChangeListener listener;
	DownloadTask myTask;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Change title to indicate sort by
		setTitle("Sort by:");

		//listview that you will operate on
		my_listview = (ListView)findViewById(R.id.lv);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		sortList = new ArrayList<String>();
		sortList.add("Company");
		sortList.add("Location");
		sortList.add("Price");
		sortList.add("Model");

		//toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();

		myCheck = new ConnectivityCheck(this);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (myCheck.isNetworkReachableAlertUserIfNot(this)) {
			myTask = new DownloadTask(this);
			myTask.execute(preferences.getString("JSON_url", "-1"));
		}

		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences myPref, String key) {
				currentURL = myPref.getString(key, null);
				if(myCheck.isNetworkReachableAlertUserIfNot(Activity_ListView.this)) {
					myTask = new DownloadTask(Activity_ListView.this);
					myTask.execute(currentURL);
				}
			}
		};
		preferences.registerOnSharedPreferenceChangeListener(listener);

		setupSimpleSpinner();

		setupListViewOnClickListener();
	}

	public SharedPreferences getPreferences() {
		return preferences;
	}

	private void setupListViewOnClickListener() {
		//TODO you want to call my_listviews setOnItemClickListener with a new instance of android.widget.AdapterView.OnItemClickListener() {
		my_listview.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(bikes.get(position).toString());
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

	/**
	 * Takes the string of bikes, parses it using JSONHelper
	 * Sets the adapter with this list using a custom row layout and an instance of the CustomAdapter
	 * binds the adapter to the Listview using setAdapter
	 *
	 * @param JSONString  complete string of all bikes
	 */
	public void bindData(String JSONString) {
		if(JSONString != null) {
			bikes = JSONHelper.parseAll(JSONString);
			ListAdapter adapter = new BikeAdapter(this, bikes);
			my_listview.setAdapter(adapter);
			sort();
		}
		if(preferences.getString("JSON_url", "-1").equals("-1")){
			Toast.makeText(this, SELECT_WEBSITE, Toast.LENGTH_LONG).show();
		}
	}

	public void doNetworkCheck(View view) {
		String res = myCheck.isNetworkReachable(this)?"Network Reachable":"No Network";

		Toast.makeText(this, res,Toast.LENGTH_SHORT).show();
	}

	public void doWirelessCheck(View view) {
		String res = myCheck.isWifiReachable(this)?"WiFi Reachable":"No WiFi";
		Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
	}

	Spinner spinner;
	/**
	 * create a data adapter to fill above spinner with choices(Company,Location and Price),
	 * bind it to the spinner
	 * Also create a OnItemSelectedListener for this spinner so
	 * when a user clicks the spinner the list of bikes is resorted according to selection
	 * dontforget to bind the listener to the spinner with setOnItemSelectedListener!
	 */
	private void setupSimpleSpinner() {
		spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(this);
		spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, sortList));
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		current = sortList.get(position);
		sort();
	}

	public void sort() {
		if(bikes != null && bikes.size() > 0) {
			switch(current) {
				case "Company":
					Collections.sort(bikes, new Comparator<BikeData>() {
						@Override
						public int compare(BikeData bikeData, BikeData t1) {
							int check = bikeData.getModel().compareTo(t1.getModel());
							if(check == 0) {
								check = bikeData.getCompany().compareTo(t1.getCompany());
								if(check == 0) {
									check = bikeData.getLocation().compareTo(t1.getLocation());
									if(check == 0) {
										if (bikeData.getPrice() < t1.getPrice()) {
											return -1;
										}
										if (bikeData.getPrice() > t1.getPrice()) {
											return 1;
										}
										return 0;
									}
								}
							}
							return check;
						}
					});
					break;
				case "Model":
					Collections.sort(bikes, new Comparator<BikeData>() {
						@Override
						public int compare(BikeData bikeData, BikeData t1) {
							int check = bikeData.getCompany().compareTo(t1.getCompany());
							if(check == 0) {
								check = bikeData.getModel().compareTo(t1.getModel());
								if(check == 0) {
									check = bikeData.getLocation().compareTo(t1.getLocation());
									if(check == 0) {
										if (bikeData.getPrice() < t1.getPrice()) {
											return -1;
										}
										if (bikeData.getPrice() > t1.getPrice()) {
											return 1;
										}
										return 0;
									}
								}
							}
							return check;
						}
					});
					break;
				case "Location":
					Collections.sort(bikes, new Comparator<BikeData>() {
						@Override
						public int compare(BikeData bikeData, BikeData t1) {
							int check = bikeData.getLocation().compareTo(t1.getLocation());
							if(check == 0) {
								check = bikeData.getModel().compareTo(t1.getModel());
								if(check == 0) {
									if(bikeData.getPrice() < t1.getPrice()){
										return -1;}
									if(bikeData.getPrice() > t1.getPrice()){
										return 1;}
									return 0;
								}
							}
							return check;
						}
					});
					break;
				case "Price":
					Collections.sort(bikes, new Comparator<BikeData>() {
						@Override
						public int compare(BikeData bikeData, BikeData t1) {
							if(bikeData.getPrice() < t1.getPrice()){
								return -1;}
							if(bikeData.getPrice() > t1.getPrice()){
								return 1;}
							return 0;
						}
					});
					break;
			}
			BikeAdapter adapter = (BikeAdapter) my_listview.getAdapter();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_settings:
				Intent myIntent = new Intent(this, activityPreference.class);
				startActivity(myIntent);
				break;
			case R.id.action_refresh:
				myTask = new DownloadTask(this);
				myTask.execute(currentURL);
				spinner.setSelection(0);
				sort();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
