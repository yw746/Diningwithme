package com.aaaa.Diningwithme;

import android.os.Bundle;
import android.app.Activity;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaaa.Diningwithme.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;

public class ManageInfo extends Activity {
    // create a HashMap to hold all the layout we added
    static HashMap<String, LinearLayout> layoutHolder = new HashMap<String, LinearLayout>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setup firebase
        Firebase.setAndroidContext(this);
        //setup Scrollview
        final ScrollView layout = new ScrollView(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        //setup the linearLayout in Scrollview
        final LinearLayout holder = new LinearLayout(this);
        holder.setOrientation(LinearLayout.VERTICAL);
        layout.addView(holder);
        // set layout as our view
        setContentView(layout);

        String user_identity = MainActivity.user_name;
        Firebase ref = new Firebase("https://diningwithme.firebaseio.com/Users");
        final Firebase dInfo = ref.child(user_identity).child("startInfo").child("dining");
        //search dining infomation
        dInfo.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String dining_name = dataSnapshot.getKey();
                HashMap<String, Object> diningInfo = dataSnapshot.getValue(HashMap.class);
                // if there is some activity
                if (!dining_name.equals("initial")){
                    //acquire dining infomation
                    String diningDate = (String)diningInfo.get("date");
                    String diningPrice = (String)diningInfo.get("price");
                    String diningPar = (String)diningInfo.get("maximum_guests");
                    // add content to scrollview
                    addInfo(holder, diningDate, diningPrice, diningPar);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String dining_name = dataSnapshot.getKey();
                if (layoutHolder.containsKey(dining_name)){
                    deleteInfo(holder, layoutHolder.get(dining_name));
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    protected void addInfo(LinearLayout holder, String time, String price, String participants){
        LinearLayout diningview = new LinearLayout(this);
        diningview.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams ltp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView dateInfo = new TextView(this);
        TextView priceInfo = new TextView(this);
        TextView partiInfo = new TextView(this);
        Button cancel = new Button(this);
        dateInfo.setText("time: " + time);
        dateInfo.setGravity(Gravity.CENTER);
        priceInfo.setText("price: " + price);
        priceInfo.setGravity(Gravity.CENTER);
        partiInfo.setText("maximum number: " + participants);
        partiInfo.setGravity(Gravity.CENTER);
        cancel.setText("cancel");
        cancel.setGravity(Gravity.CENTER);
        final String name_of_dining = time;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase ref = new Firebase("https://diningwithme.firebaseio.com/Users");
                Firebase dInfo = ref.child(MainActivity.user_name).child("startInfo").child("dining");
                dInfo.child(name_of_dining).setValue(null);
            }
        });
        diningview.addView(dateInfo,ltp);
        diningview.addView(priceInfo, ltp);
        diningview.addView(partiInfo,ltp);
        diningview.addView(cancel,ltp);
        holder.addView(diningview, ltp);
        layoutHolder.put(time, diningview);
    }

    protected void deleteInfo (LinearLayout holder, LinearLayout layout_to_delete){
        // reomove information in activity manage page
        holder.removeView(layout_to_delete);
    }
}
