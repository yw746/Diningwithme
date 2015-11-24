package com.aaaa.Diningwithme;


import java.util.HashMap;
import java.util.Map;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateUserActivity extends Activity {
	EditText edit_username = null;
	EditText edit_password = null;
	EditText edit_cfpwd = null;
	EditText edit_email = null;
	EditText cc = null;
	String confirm_code = null;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_createuser);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		Firebase.setAndroidContext(this);
		TextView username = (TextView) super.findViewById(R.id.username);
		edit_username = (EditText) super.findViewById(R.id.edit_username);
		TextView password = (TextView) super.findViewById(R.id.password);
		edit_password = (EditText) super.findViewById(R.id.edit_password);
		TextView cfpwd = (TextView) super.findViewById(R.id.confirm_password);
		edit_cfpwd = (EditText) super.findViewById(R.id.edit_cfpwd);
		TextView email = (TextView) super.findViewById(R.id.emailadrress);
		edit_email = (EditText) super.findViewById(R.id.email);

//		Button getcc = (Button)super.findViewById(R.id.gcc);

//		getcc.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Firebase ref = new Firebase("https://diningwithme.firebaseio.com");
//				if (CheckForInfo.checkEmail(edit_email.getText().toString())){
//						
//						ref.createUser(edit_email.getText().toString(),"TemporaryPassword",new Firebase.ResultHandler() {
//						
//						@Override
//						public void onSuccess() {
//							// TODO Auto-generated method stub
//							Firebase ref = new Firebase("https://diningwithme.firebaseio.com");
//							ref.resetPassword(edit_email.getText().toString(), new Firebase.ResultHandler() {
//							    @Override
//							    public void onSuccess() {
//							        // password reset email sent
//									Toast.makeText(getApplicationContext(), "We have sent your confirm code", Toast.LENGTH_SHORT).show();
//
//							    }
//							    @Override
//							    public void onError(FirebaseError firebaseError) {
//							        // error encountered
//									Toast.makeText(getApplicationContext(), "Error, Try again !", Toast.LENGTH_SHORT).show();
//
//							    }
//							});
//						}
//						
//						@Override
//						public void onError(FirebaseError arg0) {
//							// TODO Auto-generated method stub
//							Toast.makeText(getApplicationContext(), "Error, Try again !", Toast.LENGTH_SHORT).show();
//							//finish();
//						}
//					});	
//					
//					Toast.makeText(getApplicationContext(), "confirm code has been sent", Toast.LENGTH_SHORT).show();
//				}
//				else {
//					Toast.makeText(getApplicationContext(), "You should use your cornell email adress", Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
//		
		Button submit = (Button) super.findViewById(R.id.ssubmit);
		submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String info1 = "you should use cornell mail";
				String info2 = "your password should be the same";
				if (!CheckForInfo.checkEmail(edit_email.getText().toString())) {
					Toast.makeText(getApplicationContext(), info1, Toast.LENGTH_SHORT).show();
				} else if (!CheckForInfo.checkPwd(edit_password.getText().toString(), edit_cfpwd.getText().toString())) {
					Toast.makeText(getApplicationContext(), info2, Toast.LENGTH_SHORT).show();
					edit_password.setText("");
					edit_cfpwd.setText("");
				} else {
					Firebase ref = new Firebase("https://diningwithme.firebaseio.com");
					ref.createUser(edit_email.getText().toString(), edit_password.getText().toString(), new Firebase.ResultHandler() {
						@Override
						public void onSuccess() {
							// account created
							Toast.makeText(getApplicationContext(), "your account has been created successfully", Toast.LENGTH_SHORT).show();
							Firebase ref = new Firebase("https://diningwithme.firebaseio.com");
							Firebase userRef = ref.child("Users");
							Firebase keyRef = ref.child("KeyName");

							// setup personal information
							Map<String, String> personalInfo = new HashMap<String, String>();
							personalInfo.put("lastName", "");
							personalInfo.put("firstName", "");
							personalInfo.put("phoneNum", "");
							personalInfo.put("emailAddress", edit_email.getText().toString());
							personalInfo.put("userName", edit_username.getText().toString());

							//setup start information
							Map<String, Object> startdiningInfo = new HashMap<String, Object>();
							startdiningInfo.put("date", 0);
							startdiningInfo.put("maximum_guests", 0);
							startdiningInfo.put("price", 0);
							startdiningInfo.put("dishes", 0);
							startdiningInfo.put("locationX", 0.0);
							startdiningInfo.put("locationY", 0.0);

							Map<String, Object> startpartyInfo = new HashMap<String, Object>();
							startpartyInfo.put("date", 0);
							startpartyInfo.put("maximum_guests", 0);
							startpartyInfo.put("price", 0);
							startpartyInfo.put("dishes", 0);
							startpartyInfo.put("locationX", 0.0);
							startpartyInfo.put("locationY", 0.0);

							Map<String, Map<String, Object>> diningHolder = new HashMap<String, Map<String, Object>>();
							diningHolder.put("initial", startdiningInfo);

							Map<String, Map<String, Object>> partyHolder = new HashMap<String, Map<String, Object>>();
							partyHolder.put("initial", startpartyInfo);

							Map<String, Object> startInfo = new HashMap<String, Object>();
							startInfo.put("dining", diningHolder);
							startInfo.put("party", partyHolder);

							// setup dining information
							Map<String, Object> diningInfo = new HashMap<String, Object>();
							diningInfo.put("initial", 0);

							// setup party information
							Map<String, Object> partyInfo = new HashMap<String, Object>();
							partyInfo.put("initial", 0);

							// hold all information
							Map<String, Object> infoHolder = new HashMap<String, Object>();
							infoHolder.put("personalInfo", personalInfo);
							infoHolder.put("startInfo", startInfo);
							infoHolder.put("diningInfo", diningInfo);
							infoHolder.put("partyInfo", partyInfo);
							Map<String, Object> allInfo = new HashMap<String, Object>();
							allInfo.put(edit_username.getText().toString(), infoHolder);

							userRef.updateChildren(allInfo);

							// save email address and username pair
							Map<String, Object> email_username = new HashMap<String, Object>();
							email_username.put(edit_username.getText().toString(), edit_email.getText().toString());
							keyRef.updateChildren(email_username);

							// pass the user email address
							Intent intent = new Intent();
							intent.setClass(CreateUserActivity.this, MainActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("email", edit_email.getText().toString());
							intent.putExtras(bundle);
							// transfer to another interface
							startActivity(intent);

							// clear all the information to ""
							edit_username.setText("");
							edit_password.setText("");
							edit_cfpwd.setText("");
							edit_email.setText("");
						}

						@Override
						public void onError(FirebaseError firebaseError) {
							// error encountered
							Toast.makeText(getApplicationContext(), "Error, please try again", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"CreateUser Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.aaaa.Diningwithme/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"CreateUser Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.aaaa.Diningwithme/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}
}
