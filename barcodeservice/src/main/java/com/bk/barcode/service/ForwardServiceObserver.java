package com.bk.barcode.service;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/13/13
 * Time: 5:16 PM
 */
public class ForwardServiceObserver implements ServiceObserver
{
   @Override
   public void notifyConnecting() {}

   @Override
   public void notifyConnected() {}

   @Override
   public void notifyConnectionFailed() {}

   @Override
   public void notifyDisconnected() {}

   @Override
   public void notifyBarcode(String barcode) {}

   @Override
   public void notifyConnectionLost() {}

}
