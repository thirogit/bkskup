package com.bk.barcode.service;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/13/13
 * Time: 9:32 AM
 */
public interface ServiceObserver
{
   void notifyConnecting();
   void notifyConnected();
   void notifyConnectionFailed();
   void notifyDisconnected();
   void notifyBarcode(String barcode);
   void notifyConnectionLost();
}
