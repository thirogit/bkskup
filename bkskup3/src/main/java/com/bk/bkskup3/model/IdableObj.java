package com.bk.bkskup3.model;


import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 28.06.11
 * Time: 20:03
 */
public class IdableObj implements Idable,Serializable
{
   private int id;

   public IdableObj(int id)
   {
      this.id = id;
   }

   public int getId()
   {
      return id;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        IdableObj idable = (IdableObj) o;

        return id == idable.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
