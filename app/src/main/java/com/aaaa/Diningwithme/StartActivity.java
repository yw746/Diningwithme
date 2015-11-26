package com.aaaa.Diningwithme;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aaaa.Diningwithme.common.logger.Log;
import com.firebase.client.Firebase;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StartActivity extends Activity {

	/*private List< String> time = new ArrayList< String>();
	private List< String> number = new ArrayList< String>();
	private List< String> price = new ArrayList< String>();*/
	
	private Spinner spintime=null;
	private Spinner spinnumber=null;
	private Spinner spinprice=null;
	
	private ArrayAdapter<String> adapter1;
	private ArrayAdapter<String> adapter2;
	private ArrayAdapter<String> adapter3;

	private DatePicker dpToday=null;
	private TimePicker tpNow=null;
	
	
	private Button btnsubmit = null;

	private double longtitude = 0;
	private double langtitude = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);	
		//setup firebase
		Firebase.setAndroidContext(this);

		final String[] time=getResources().getStringArray(R.array.timearray);
		final String[] number=getResources().getStringArray(R.array.numberarray);
		final String[] price=getResources().getStringArray(R.array.pricearray);
		
		//spintime=(Spinner)super.findViewById(R.id.time);
		spinnumber=(Spinner)super.findViewById(R.id.number);
		spinprice=(Spinner)super.findViewById(R.id.price);
		btnsubmit=(Button)super.findViewById(R.id.submit);
		btnsubmit.setOnClickListener(new submitOnClickListener());
		dpToday=(DatePicker)super.findViewById(R.id.today);

		tpNow = (TimePicker)findViewById(R.id.now);
		tpNow.setIs24HourView(true);

		//set an adapter
		//adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,time);
		adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,number);
		adapter3 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,price);
		
		//set a form for the adapter
		//adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//set a topic
		//spintime.setPrompt("Choose a time");
		spinnumber.setPrompt("Choose a number");
		spinprice.setPrompt("Choose a price");
		//add the adapter to the spinner
		//spintime.setAdapter(adapter1);
		spinnumber.setAdapter(adapter2);
		spinprice.setAdapter(adapter3);

		//initialize langti and longti in LocationAcitvity, in order to make sure that user do select their location
		LocationActivity.langti = 0;
		LocationActivity.longti = 0;
		Button location = (Button)this.findViewById(R.id.location);

		location.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(StartActivity.this, LocationActivity.class));

			}
		});
        

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class submitOnClickListener implements OnClickListener{
		public void onClick(View v){

			//get price and number
			String snumber=spinnumber.getSelectedItem().toString();
			String sprice=spinprice.getSelectedItem().toString();

			//get date and time have been set
			int iYear=0;
			int iMonth=0;
			int iDay=0;
			String sDate="";
			iYear=dpToday.getYear();
			iMonth=dpToday.getMonth();
			iDay=dpToday.getDayOfMonth();
			int iHour = tpNow.getCurrentHour();
			int iMin = tpNow.getCurrentMinute();
			int iSec = 0;
			//compare time now and time user chosen
			Calendar time_now = new GregorianCalendar();
			Calendar time_set = new GregorianCalendar(iYear,iMonth,iDay,iHour,iMin,iSec);

			// Check if the content in each field valid or not
			if (sprice.length() > 3){
				Toast.makeText(getApplicationContext(), "Choose your price", Toast.LENGTH_SHORT).show();
			}
			else if (snumber.length() > 3){
				Toast.makeText(getApplicationContext(), "Choose your maximum participants", Toast.LENGTH_SHORT).show();
			}
			else if (LocationActivity.longti == 0 && LocationActivity.langti == 0){
				Toast.makeText(getApplicationContext(), "Choose your location", Toast.LENGTH_SHORT).show();
			}
			else if (time_now.after(time_set)){
				Toast.makeText(getApplicationContext(), "Shouldn't choose time before now !", Toast.LENGTH_SHORT).show();
			}
			else {
				// get user identity and setup firebase
				String user_identity = MainActivity.user_name;
				Firebase ref = new Firebase("https://diningwithme.firebaseio.com/Users");
				Firebase pInfo = ref.child(user_identity).child("startInfo").child("dining");

				//transfer date format
				SimpleDateFormat trans_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timeSet_str = trans_time.format(time_set.getTime());
				String timeNow_str = trans_time.format(time_now.getTime());

				//update data
				Map<String, Object> d_info_de = new HashMap<String, Object>();
				d_info_de.put("date", timeSet_str);
				d_info_de.put("maximum_guests", snumber);
				d_info_de.put("price", sprice);
				d_info_de.put("dishes", 0);
				d_info_de.put("locationX", LocationActivity.langti);
				d_info_de.put("locationY", LocationActivity.longti);
				Map<String, Object> d_info = new HashMap<String, Object>();
				d_info.put(timeSet_str,d_info_de);
				pInfo.updateChildren(d_info);
				Toast.makeText(getApplicationContext(), "Your activity has been setup successfully !", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
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
}
