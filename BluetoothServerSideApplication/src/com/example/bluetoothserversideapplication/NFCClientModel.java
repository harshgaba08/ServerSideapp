package com.example.bluetoothserversideapplication;

public class NFCClientModel {

	
	/***** Declaring the variables with the datatypes *****/

    private String KEY_ID ;     //each user getting a unique id is stored
    private String CLIENT_NAME ;  //The client name given by server is stored
   
    private String TIME ;     //Time of connection is stored

    /***** All the setter and getter methods are defined here *****/
	
	public NFCClientModel() {
		// TODO Auto-generated constructor stub
	}
	
    
    public String getKEY_ID() {
        return KEY_ID;
    }

    public void setKEY_ID(String KEY_ID) {
        this.KEY_ID = KEY_ID;
    }

    public String getCLIENT_NAME() {
        return CLIENT_NAME;
    }

    public void setCLIENT_NAME(String CLIENT_NAME) {
        this.CLIENT_NAME = CLIENT_NAME;
    }

    
    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }
}
