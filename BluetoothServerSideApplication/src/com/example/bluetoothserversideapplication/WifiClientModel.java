package com.example.bluetoothserversideapplication;

public class WifiClientModel {

	
	/***** Declaring the variables with the datatypes *****/

    private String KEY_ID ;     //each user getting a unique id is stored
    private String USER_NUM ;  //The user number given by server is stored
    private String PORT_NUM;    //Port number of client is stored
    private String IP_ADDRESS; //IP address of client is stored
    private String TIME ;     //Time of connection is stored
    private String MESSAGE ;   //Message received from Client
    private String DEVICE ;   //Name of the Device Client used to Connect

    /***** All the setter and getter methods are defined here *****/
    
    
    
    public WifiClientModel() //Default Constructor
    {  
    	
    }


    public String getKEY_ID() {
        return KEY_ID;
    }

    public void setKEY_ID(String KEY_ID) {
        this.KEY_ID = KEY_ID;
    }

    public String getUSER_NUM() {
        return USER_NUM;
    }

    public void setUSER_NUM(String USER_NUM) {
        this.USER_NUM = USER_NUM;
    }

    public String getPORT_NUM() {
        return PORT_NUM;
    }

    public void setPORT_NUM(String PORT_NUM) {
        this.PORT_NUM = PORT_NUM;
    }

    public String getIP_ADDRESS() {
        return IP_ADDRESS;
    }

    public void setIP_ADDRESS(String IP_ADDRESS) {
        this.IP_ADDRESS = IP_ADDRESS;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public String getDEVICE() {
        return DEVICE;
    }

    public void setDEVICE(String DEVICE) {
        this.DEVICE = DEVICE;
    }






}




