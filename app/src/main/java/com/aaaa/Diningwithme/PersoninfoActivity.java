package com.aaaa.Diningwithme;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.BitmapFactory;  


public class PersoninfoActivity extends Activity {

	private static final int PHOTO_REQUEST_GALLERY = 1;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 2;
	private static final int PHOTO_REQUEST_CUT = 3;
	private static Bitmap personalPhoto = null;
	File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personinfo);
		Firebase.setAndroidContext(this);
		
		// get the basic element in the interface
		final TextView emailaddress = (TextView)this.findViewById(R.id.email_usr);
		final TextView usrname_usr = (TextView)this.findViewById(R.id.username_usr);
		final EditText editfirst = (EditText)this.findViewById(R.id.editFirst);
		editfirst.requestFocus(); // clear focus
		final EditText editlast = (EditText)this.findViewById(R.id.editLast);
		final EditText phonenumber = (EditText)this.findViewById(R.id.edit_phone);
		final ImageView picView = (ImageView)findViewById(R.id.photo);
		Button save = (Button)this.findViewById(R.id.save);
		Button change_photo = (Button)this.findViewById(R.id.change_photo);
		
		// get user identity
		String user_identity = MainActivity.user_name;
		Firebase ref = new Firebase("https://diningwithme.firebaseio.com/Users");
		final Firebase pInfo = ref.child(user_identity).child("personalInfo");
		
		// get user information
		pInfo.addChildEventListener(new ChildEventListener() {
		    // Retrieve new posts as they are added to the database
		    @Override
		    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
		        String field = snapshot.getKey();
		        
		        //judge which field it correspond to
		        if (field.equals("firstName")){
		        	editfirst.setText(snapshot.getValue().toString());
		        }
		        if (field.equals("lastName")){
		        	editlast.setText(snapshot.getValue().toString());
		        }
		        if (field.equals("emailAddress")){
		        	emailaddress.setText(snapshot.getValue().toString());
		        }
		        if (field.equals("phoneNum")){
		        	phonenumber.setText(snapshot.getValue().toString());
		        }
		        if (field.equals("userName")){
		        	usrname_usr.setText(snapshot.getValue().toString());
		        	usrname_usr.setGravity(Gravity.LEFT);
		        }
		        if (field.equals("photo")){
		        	byte[] imageAsBytes = Base64.decode(snapshot.getValue().toString(), Base64.DEFAULT);
		        	Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
		        	picView.setImageBitmap(bmp);
		        }
		    }
		    
		    @Override
		    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
		    	String field = snapshot.getKey();
		        
		        //judge which field it correspond to
		        if (field.equals("firstName")){
		        	editfirst.setText(snapshot.getValue().toString());
		        }
		        if (field.equals("lastName")){
		        	editlast.setText(snapshot.getValue().toString());
		        }
		        if (field.equals("emailAddress")){
		        	emailaddress.setText(snapshot.getValue().toString());
		        }
		        if (field.equals("phoneNum")){
		        	phonenumber.setText(snapshot.getValue().toString());
		        }
		        if (field.equals("userName")){
		        	usrname_usr.setText(snapshot.getValue().toString());
		        }
		        if (field.equals("photo")){
		        	byte[] imageAsBytes = Base64.decode(snapshot.getValue().toString(), Base64.DEFAULT);
		        	Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
		        	picView.setImageBitmap(bmp);
		        }
		    }
		    
		    public void onChildRemoved(DataSnapshot snapshot) {
		        
		    }
		    
		    @Override
		    public void onCancelled(FirebaseError firebaseError) {
		    	
		    }

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

		});
		
		// setup button for upload picture
		change_photo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				new AlertDialog.Builder(PersoninfoActivity.this)
                .setTitle("change photo")
                .setPositiveButton("camera", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        // 调用系统的拍照功能
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                    }
                })
                .setNegativeButton("gallery", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                }).show();
				
			}
		});
		
		// setup button for save
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// upload person information
				Map<String, Object> change = new HashMap<String, Object>();
				change.put("firstName", editfirst.getText().toString());
				change.put("lastName", editlast.getText().toString());
				change.put("phoneNum", phonenumber.getText().toString());
				pInfo.updateChildren(change);
				//upload personalPhoto if it is changed
				if (personalPhoto != null){
					sentPicToFirebase(personalPhoto);
				}
				Toast.makeText(getApplicationContext(), "update successfully", Toast.LENGTH_SHORT).show();
				
			}
		});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   
        switch (requestCode) {
        case PHOTO_REQUEST_TAKEPHOTO:
            startPhotoZoom(Uri.fromFile(tempFile), 150);
            break;

        case PHOTO_REQUEST_GALLERY:
            if (data != null)
                startPhotoZoom(data.getData(), 150);
            break;

        case PHOTO_REQUEST_CUT:
            if (data != null) 
                setPicToView(data);
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	// get the name of picture named by date
	private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }
	
	private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    //将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            ImageView picView = (ImageView)findViewById(R.id.photo);
            picView.setImageBitmap(photo);
            personalPhoto = photo;
        }
    }
	
    // 将图像转码成为base64然后上传到firebaase
    private void sentPicToFirebase(Bitmap bitmap){
    	//将图像转码成为base64格式，最后成为string变量
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
    	bitmap.recycle();
    	byte[] byteArray = stream.toByteArray();
    	String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
    	// 上传到firebase
    	Firebase ref = new Firebase("https://diningwithme.firebaseio.com/Users");
		final Firebase personInfo = ref.child(MainActivity.user_name).child("personalInfo");
    	Map<String, Object> imageupload = new HashMap<String, Object>();
    	imageupload.put("photo", imageFile);
    	personInfo.updateChildren(imageupload);
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
