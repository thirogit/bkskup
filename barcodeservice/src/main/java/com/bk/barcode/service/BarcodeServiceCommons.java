package com.bk.barcode.service;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/9/13
 * Time: 12:01 PM
 */
public class BarcodeServiceCommons
{
   public static final int MSG_REGISTER_LISTENER = 1;
   public static final int MSG_UNREGISTER_LISTENER = 2;
   public static final int MSG_GET_CONFIGURATION = 5;
   public static final int MSG_SET_CONFIGURATION = 6;
   public static final int MSG_GET_STATE = 7;
   public static final int MSG_GET_BATTERY_STATUS = 8;

   public static final int MSG_NOTIFY_CONNECTED = 7;
   public static final int MSG_NOTIFY_CONNECTING = 8;
   public static final int MSG_NOTIFY_READ = 9;
   public static final int MSG_NOTIFY_CONFIGURATION = 11;
   public static final int MSG_NOTIFY_DISCONNECTED = 12;
   public static final int MSG_NOTIFY_BATTERY = 13;

   public static final String MSG_READ_BARCODE_DATA_KEY = "barcode";
   public static final String MSG_BT_ADDRESS_DATA_KEY = "bt_address";

}
