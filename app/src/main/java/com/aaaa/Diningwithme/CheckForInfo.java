package com.aaaa.Diningwithme;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;

import com.firebase.client.Firebase;

import java.util.Random;

public class CheckForInfo {
	 public static boolean checkEmail(String email){
		 if (email.matches("[a-zA-Z0-9._-]+@cornell+\\.+edu+"))
				 return true;
		 else
			 return false;
	 }
	 public static boolean checkPwd(String pwd, String cfpwd){
		 if (pwd.equals(cfpwd))
			 return true;
		 else
			 return false;
	 }
	 public static boolean checkCf(String cf, String scf){
		 if (cf.equals(scf))
			 return true;
		 else 
			 return false;
	 }
	 public static String generateCf(){
		 Random rand = new Random();
		 return String.valueOf(rand.nextInt(10000000));
	 }
}
