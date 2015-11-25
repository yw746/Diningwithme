package com.aaaa.Diningwithme;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.List;



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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);	
		
		final String[] time=getResources().getStringArray(R.array.timearray);
		final String[] number=getResources().getStringArray(R.array.numberarray);
		final String[] price=getResources().getStringArray(R.array.pricearray);
		
		//spintime=(Spinner)super.findViewById(R.id.time);
		spinnumber=(Spinner)super.findViewById(R.id.number);
		spinprice=(Spinner)super.findViewById(R.id.price);
		btnsubmit=(Button)super.findViewById(R.id.submit);
		btnsubmit.setOnClickListener(new submitOnClickListener());
		dpToday=(DatePicker)super.findViewById(R.id.today);
		tpNow=(TimePicker)super.findViewById(R.id.now);


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
			String stime="";
			String snumber="";
			String sprice="";
			String sinfo="";
			
			//stime=spintime.getSelectedItem().toString();
			snumber=spinnumber.getSelectedItem().toString();
			sprice=spinprice.getSelectedItem().toString();
			sinfo="**"+"number:"+snumber+"**"+"price:"+sprice;

			int iYear=0;
			int iMonth=0;
			int iDay=0;
			String sDate="";
			iYear=dpToday.getYear();
			iMonth=dpToday.getMonth()+1;
			iDay=dpToday.getDayOfMonth();
			sDate="date："+String.valueOf(iYear)+"year"+String.valueOf(iMonth)+"month"+String.valueOf(iDay)+"day";
			
			Toast.makeText(getApplicationContext(), sinfo+sDate, Toast.LENGTH_SHORT).show();
			//finish();
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
