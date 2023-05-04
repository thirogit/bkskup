package com.bk.countries;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/25/12
 * Time: 1:07 PM
 */
public interface Country
{
   boolean isInEU();

   String getCode2A();

   String getCode3A();

   int getIsoNumber();

   int getCowNoLength();

   int getFarmNoLength();

   int getHentNoLength();
}
