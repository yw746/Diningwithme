package com.aaaa.Diningwithme;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		GoogleApiClient.OnConnectionFailedListener,
		GoogleMap.OnInfoWindowClickListener,
		GoogleMap.OnMarkerClickListener {

	protected GoogleApiClient mGoogleApiClient;

	private PlaceAutocompleteAdapter mAdapter;

	private AutoCompleteTextView mAutocompleteView;

	private GoogleMap mMap;

	private LatLng location;
	private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
			new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
	
	static String user_name;
	static String user_email;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		// setup the title bar
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);


		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mMap = mapFragment.getMap();
		mMap.getUiSettings().setZoomControlsEnabled(true);
		mMap.getUiSettings().setRotateGesturesEnabled(true);
		mMap.getUiSettings().setScrollGesturesEnabled(true);

		mMap.setMyLocationEnabled(true);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



		// Getting the name of the best provider
		String provider = locationManager.GPS_PROVIDER;

		// Getting Current Location
		Location lastlocation = locationManager.getLastKnownLocation(provider);

		//initialize the current location
		if (lastlocation != null){
			drawMarker(lastlocation);
		}



		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, 0 /* clientId */, this)
				.addApi(Places.GEO_DATA_API)
				.build();

		// Retrieve the AutoCompleteTextView that will display Place suggestions.
		mAutocompleteView = (AutoCompleteTextView)
				findViewById(R.id.autocomplete_places);
		// Register a listener that receives callbacks when a suggestion has been selected
		mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
		// Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
		// the entire world.
		mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
				null);
		mAutocompleteView.setAdapter(mAdapter);

		// Set up the 'clear text' button that clears the text in the autocomplete view
		Button clearButton = (Button) findViewById(R.id.button_clear);
		clearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mAutocompleteView.setText("");
			}
		});
		
		// setup the static variable, to identify user
		Bundle bundle = this.getIntent().getExtras();
		user_email = bundle.getString("email");
		
		//search for the user name
		Firebase ref = new Firebase("https://diningwithme.firebaseio.com").child("KeyName");
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				HashMap user = snapshot.getValue(HashMap.class);
				Set set = user.entrySet();
				Iterator pp = set.iterator();
				while (pp.hasNext()) {
					Map.Entry entry = (Map.Entry) pp.next();
					if (entry.getValue().equals(user_email)) {
						user_name = entry.getKey().toString();
						break;
					}
				}
			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
				System.out.println("The read failed: " + firebaseError.getMessage());
			}
		});
		// Mark all the available activities on google map
		Firebase ref_user = new Firebase("https://diningwithme.firebaseio.com").child("Users");
		ref_user.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				Map<String, Objects> users = dataSnapshot.getValue(HashMap.class);
				//get all users in users
					HashMap<String, Object> user_info = dataSnapshot.getValue(HashMap.class);
					HashMap<String, Object> start_info = (HashMap<String, Object>)user_info.get("startInfo");
					HashMap<String, Object> dining_info = (HashMap<String, Object>)start_info.get("dining");

					// go through all dining information in dining_info
					Set set_d = dining_info.entrySet();
					Iterator pp_d = set_d.iterator();
					while (pp_d.hasNext()){
						Map.Entry entry_d = (Map.Entry)pp_d.next();
						Object dining_key = entry_d.getKey();
						if (!dining_key.equals("initial")){
							HashMap<String, Object> dining_activity = (HashMap<String, Object>)dining_info.get(dining_key);

							// check if the date is valid
							SimpleDateFormat time_trans= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							try {
								Date timeset = time_trans.parse(dining_key.toString());
								Calendar time_set = Calendar.getInstance();
								time_set.setTime(timeset);
								Calendar time_now = new GregorianCalendar();
								if (time_set.after(time_now)){
									LatLng latLng_dining = new LatLng(Double.parseDouble(dining_activity.get("locationX").toString()),Double.parseDouble(dining_activity.get("locationY").toString()));
									mMap.addMarker(new MarkerOptions().position(latLng_dining).title(dataSnapshot.getKey().toString()).snippet("Participants" + dining_activity.get("maximum_guests"))
											.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
								}
							}
							catch (Exception e){

							}
						}
					}
				}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {
				Map<String, Objects> users = dataSnapshot.getValue(HashMap.class);
				//get all users in users
				HashMap<String, Object> user_info = dataSnapshot.getValue(HashMap.class);
				HashMap<String, Object> start_info = (HashMap<String, Object>)user_info.get("startInfo");
				HashMap<String, Object> dining_info = (HashMap<String, Object>)start_info.get("dining");

				// go through all dining information in dining_info
				Set set_d = dining_info.entrySet();
				Iterator pp_d = set_d.iterator();
				while (pp_d.hasNext()){
					Map.Entry entry_d = (Map.Entry)pp_d.next();
					Object dining_key = entry_d.getKey();
					if (!dining_key.equals("initial")){
						Object dining = dining_info.get(dining_key);
						HashMap<String, Object> dining_activity = (HashMap<String, Object>)dining;

						// check if the date is valid
						SimpleDateFormat time_trans= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							Date timeset = time_trans.parse(dining_key.toString());
							Calendar time_set = Calendar.getInstance();
							time_set.setTime(timeset);
							Calendar time_now = new GregorianCalendar();
							if (time_set.after(time_now)){
								LatLng latLng_dining = new LatLng(Double.parseDouble(dining_activity.get("locationX").toString()),Double.parseDouble(dining_activity.get("locationY").toString()));
								mMap.addMarker(new MarkerOptions().position(latLng_dining).title(dataSnapshot.getKey().toString()).snippet("Participants" + dining_activity.get("maximum_guests"))
										.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
							}
						}
						catch (Exception e){

						}
					}
				}
			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {
				Map<String, Objects> users = dataSnapshot.getValue(HashMap.class);
				//get all users in users
				HashMap<String, Object> user_info = dataSnapshot.getValue(HashMap.class);
				HashMap<String, Object> start_info = (HashMap<String, Object>)user_info.get("startInfo");
				HashMap<String, Object> dining_info = (HashMap<String, Object>)start_info.get("dining");

				// go through all dining information in dining_info
				Set set_d = dining_info.entrySet();
				Iterator pp_d = set_d.iterator();
				while (pp_d.hasNext()){
					Map.Entry entry_d = (Map.Entry)pp_d.next();
					Object dining_key = entry_d.getKey();
					if (!dining_key.equals("initial")){
						Object dining = dining_info.get(dining_key);
						HashMap<String, Object> dining_activity = (HashMap<String, Object>)dining;

						// check if the date is valid
						SimpleDateFormat time_trans= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							Date timeset = time_trans.parse(dining_key.toString());
							Calendar time_set = Calendar.getInstance();
							time_set.setTime(timeset);
							Calendar time_now = new GregorianCalendar();
							if (time_set.after(time_now)){
								LatLng latLng_dining = new LatLng(Double.parseDouble(dining_activity.get("locationX").toString()),Double.parseDouble(dining_activity.get("locationY").toString()));
								mMap.addMarker(new MarkerOptions().position(latLng_dining).title(dataSnapshot.getKey().toString()).snippet("Participants" + dining_activity.get("maximum_guests"))
										.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
							}
						}
						catch (Exception e){

						}
					}
				}
			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) {
				Map<String, Objects> users = dataSnapshot.getValue(HashMap.class);
				//get all users in users
				HashMap<String, Object> user_info = dataSnapshot.getValue(HashMap.class);
				HashMap<String, Object> start_info = (HashMap<String, Object>)user_info.get("startInfo");
				HashMap<String, Object> dining_info = (HashMap<String, Object>)start_info.get("dining");

				// go through all dining information in dining_info
				Set set_d = dining_info.entrySet();
				Iterator pp_d = set_d.iterator();
				while (pp_d.hasNext()){
					Map.Entry entry_d = (Map.Entry)pp_d.next();
					Object dining_key = entry_d.getKey();
					if (!dining_key.equals("initial")){
						Object dining = dining_info.get(dining_key);
						HashMap<String, Object> dining_activity = (HashMap<String, Object>)dining;

						// check if the date is valid
						SimpleDateFormat time_trans= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							Date timeset = time_trans.parse(dining_key.toString());
							Calendar time_set = Calendar.getInstance();
							time_set.setTime(timeset);
							Calendar time_now = new GregorianCalendar();
							if (time_set.after(time_now)){
								LatLng latLng_dining = new LatLng(Double.parseDouble(dining_activity.get("locationX").toString()),Double.parseDouble(dining_activity.get("locationY").toString()));
								mMap.addMarker(new MarkerOptions().position(latLng_dining).title(dataSnapshot.getKey().toString()).snippet("Participants" + dining_activity.get("maximum_guests"))
										.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
							}
						}
						catch (Exception e){

						}
					}
				}
			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {

			}
		});
		Button button1 = (Button)this.findViewById(R.id.dining);
		ImageButton button2 = (ImageButton)this.findViewById(R.id.profile);
		Button button3 = (Button)this.findViewById(R.id.invitation);
		
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,DiningActivity.class));
			}
		});
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,ProfileActivity.class));
			}
		});
		button3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,StartActivity.class));
				
			}
		});
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
	 * Listener that handles selections from suggestions from the AutoCompleteTextView that
	 * displays Place suggestions.
	 * Gets the place id of the selected item and issues a request to the Places Geo Data API
	 * to retrieve more details about the place.
	 *
	 * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
	 * String...)
	 */
	private AdapterView.OnItemClickListener mAutocompleteClickListener
			= new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
			final AutocompletePrediction item = mAdapter.getItem(position);
			final String placeId = item.getPlaceId();
			final CharSequence primaryText = item.getPrimaryText(null);
             /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
			PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
					.getPlaceById(mGoogleApiClient, placeId);
			placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

		}
	};

	private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
			= new ResultCallback<PlaceBuffer>() {
		@Override
		public void onResult(PlaceBuffer places) {
			if (!places.getStatus().isSuccess()) {
				// Request did not complete successfully
				places.release();
				return;
			}
			// Get the Place object from the buffer.
			final Place place = places.get(0);

			location = place.getLatLng();

			if (!location.equals(null)) {
				//handleNewLocation(location)
				LatLng latLng = new LatLng(location.latitude, location.longitude);
				//mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location").snippet("Population: 2,074,200")
				//		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
			}


			places.release();
		}
	};

	private void drawMarker(Location location){
		// Remove any existing markers on the map
		mMap.clear();
		LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
		mMap.addMarker(new MarkerOptions()
				.position(currentPosition)
				.snippet("Lat:" + location.getLatitude() + "Lng:"+ location.getLongitude())
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.title("ME"));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {


		// TODO(Developer): Check error code and notify the user of error state and resolution.
		Toast.makeText(this,
				"Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		// This causes the marker at Perth to bounce into position when it is clicked.
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		final long duration = 1500;

		final Interpolator interpolator = new BounceInterpolator();

		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = Math.max(
						1 - interpolator.getInterpolation((float) elapsed / duration), 0);
				marker.setAnchor(0.5f, 1.0f + 2 * t);

				if (t > 0.0) {
					// Post again 16ms later.
					handler.postDelayed(this, 16);
				}
			}
		});

		return false;
	}

}
