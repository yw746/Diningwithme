package com.aaaa.Diningwithme;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText tvUserName=null;
	private EditText tvPassword=null;
	private Button btnLogin=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Firebase.setAndroidContext(this);
		tvUserName = (EditText)super.findViewById(R.id.userName);
		tvPassword = (EditText)super.findViewById(R.id.passWord);
		Button newuser = (Button)findViewById(R.id.newuser);
		
		newuser.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoginActivity.this,CreateUserActivity.class));
			}
		});
		tvUserName.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvUserName.setHint(null);
			}
		});
		
		tvPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvPassword.setHint(null);
			}
		});
		
		btnLogin = (Button)super.findViewById(R.id.login);
		btnLogin.setOnClickListener(new LoginOnClickListener());
	}

	private class LoginOnClickListener implements OnClickListener{
		public void onClick(View v){
			Firebase ref = new Firebase("https://diningwithme.firebaseio.com");
			String username=tvUserName.getText().toString();
			String password=tvPassword.getText().toString();
			//This is our superuser !
			if ((username.equals("jm2522") && password.equals("1234567890"))){
				startActivity(new Intent(LoginActivity.this,MainActivity.class));
				tvPassword.setText("");
			}
			else{
				ref.authWithPassword(username, password, new Firebase.AuthResultHandler() {
				    @Override
				    public void onAuthenticated(AuthData authData) {
				    	//if successfully, pass the email address to MainActivity
				    	Intent intent = new Intent();
				    	intent.setClass(LoginActivity.this,MainActivity.class);
				    	Bundle bundle = new Bundle();
				    	bundle.putString("email",tvUserName.getText().toString());
				    	intent.putExtras(bundle);
				    	startActivity(intent);
				    	tvPassword.setText("");
				    }
				    @Override
				    public void onAuthenticationError(FirebaseError firebaseError) {
				    	tvPassword.setText("");
						Toast.makeText(getApplicationContext(), "username or password is invalid", Toast.LENGTH_SHORT).show();
				    }
				});
			}
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
}
