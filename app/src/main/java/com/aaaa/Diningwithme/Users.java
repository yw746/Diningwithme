package com.aaaa.Diningwithme;

public class Users {
	private String lastName;
	private String firstName;
	private String phoneNum;
	private String emailAddress;
	private String userName;
	public Users(){}
	public Users(String lastname, String firstname, String phonenum, String email, String username){
		lastName = lastname;
		firstName = firstname;
		phoneNum = phonenum;
		emailAddress = email;
		userName = username;
	}
	protected void chLast(String a){
		lastName = a;
	}
	protected void chFirst(String a){
		firstName = a;
	}
	protected void chphone(String a){
		phoneNum = a;
	}
	protected void chEmail(String a){
		emailAddress = a;
	}
	protected void chUser(String a){
		userName = a;
	}
	public String getLast(){return lastName;}
	public String getFirst(){return firstName;}
	public String getPhone(){return phoneNum;}
	public String getEmail(){return emailAddress;}
	public String getUser(){return userName;}
}
