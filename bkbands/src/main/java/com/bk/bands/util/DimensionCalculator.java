package com.bk.bands.util;

import java.util.List;

import com.bk.bands.template.Field;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/9/12
 * Time: 10:43 PM
 */
public class DimensionCalculator
{
   private List<Field> fields;

   private int north = 0;
   private int south = 0;
   private int west = 0;
   private int east = 0;

   public DimensionCalculator(List<Field> fields)
   {
      this.fields = fields;
   }

   public void calculate()
   {
      north = calculateNorthParallel();
      south = calculateSouthParallel();
      west = calculateWestMeridian();
      east = calculateEastMeridian();
   }

   private int calculateSouthParallel()
   {
      int maxSouth = 0;
      for(Field field : fields)
      {
         int fieldBottom = field.getPosition().y+field.getSize().height;
         if(fieldBottom > maxSouth)
         {
            maxSouth = fieldBottom;
         }
      }
      return maxSouth;
   }

   private int calculateNorthParallel()
   {
//      int maxNorth = -1;
//      for(Field field : fields)
//      {
//         int fieldTop = field.getPosition().y;
//         if(maxNorth < 0 || fieldTop < maxNorth)
//         {
//            maxNorth = fieldTop;
//         }
//      }
//      return maxNorth;
      return 0;
   }

   private int calculateWestMeridian()
   {
//      int maxWest = -1;
//      for(Field field : fields)
//      {
//         int fieldLeft = field.getPosition().x;
//         if(maxWest < 0 || fieldLeft < maxWest)
//         {
//            maxWest = fieldLeft;
//         }
//      }
//      return maxWest;
      return 0;
   }

   private int calculateEastMeridian()
   {
      int maxEast = 0;
      for(Field field : fields)
      {
         int fieldRight = field.getPosition().x+field.getSize().width;
         if(fieldRight > maxEast)
         {
            maxEast = fieldRight;
         }
      }
      return maxEast;
   }


   public int getHeight()
   {
      return getSouth() - getNorth();
   }

   public int getWidth()
   {
      return getEast() - getWest();
   }

   public int getNorth()
   {
      return north;
   }

   public int getSouth()
   {
      return south;
   }

   public int getWest()
   {
      return west;
   }

   public int getEast()
   {
      return east;
   }
}
