package com.bk.bkskup3.widgets;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.util.Predicate;
import com.bk.bkskup3.R;
import com.bk.countries.Countries;
import com.bk.countries.Country;
import com.bk.widgets.spinner.Spinner;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/9/11
 * Time: 2:14 PM
 */
public class CountrySpinner extends Spinner
{
   private boolean mShowFlag = true;

   public CountrySpinner(Context context)
   {
      super(context);
      create();

   }

   public CountrySpinner(Context context, int mode)
   {
      super(context, mode);
      create();
   }

   public CountrySpinner(Context context, AttributeSet attrs)
   {
      super(context, attrs);
      applyAttributes(attrs);
      create();
   }

   public CountrySpinner(Context context, AttributeSet attrs, int defStyle)
   {
      super(context, attrs, defStyle);
      applyAttributes(attrs);
      create();
   }

   public CountrySpinner(Context context, AttributeSet attrs, int defStyle, int mode)
   {
      super(context, attrs, defStyle, mode);
      applyAttributes(attrs);
      create();
   }

   protected void applyAttributes(AttributeSet attrs) {

      TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CountrySpinner);
      mShowFlag = typedArray.getBoolean(R.styleable.CountrySpinner_showFlag, true);
      typedArray.recycle();
   }


   protected void create()
   {
      Countries countries = Countries.getCountries();
      List<CountryItem> countryItems = new LinkedList<CountryItem>();
      Resources resources = getResources();

      for (Country country : countries.getAllCountries())
      {
         countryItems.add(new CountryItem(country, resources.getString(countries.getCountryName(country.getIsoNumber())),
                                                   resources.getDrawable(countries.getCountryFlag(country.getIsoNumber()))));
      }

      Collections.sort(countryItems, new Comparator<CountryItem>()
      {
         @Override
         public int compare(CountryItem item1, CountryItem item2)
         {
            return item1.getCountry().getCode2A().compareTo(item2.getCountry().getCode2A());
         }
      });
      setAdapter(new CountryItemAdapter(getContext(), countryItems));
   }

   public void setSelectedCountry(Country country)
   {
      if (country != null)
      {
         this.setSelection(((CountryItemAdapter)getAdapter()).getCountryPosition(country));
      }
   }

   public void setSelectedCountry(String countryCd2a)
   {
      if (countryCd2a != null)
      {
         this.setSelection(((CountryItemAdapter)getAdapter()).getCountryPosition(countryCd2a));
      }
   }

   public Country getSelectedCountry()
   {
      CountryItem countryItem = (CountryItem) getSelectedItem();
      if (countryItem != null)
      {
         return countryItem.getCountry();
      }

      return null;
   }

   public Country getCountryAtPosition(int position)
   {
      return ((CountryItemAdapter)getAdapter()).getCountryAtPosition(position);
   }


   private class CountryItem
   {
      private Country country;
      private Drawable itemImg;
      private String countryName;

      private CountryItem(Country country, String countryName,Drawable itemImg)
      {
         this.country = country;
         this.itemImg = itemImg;
         this.countryName = countryName;
      }

      public Drawable getItemImg()
      {
         return itemImg;
      }

      public Country getCountry()
      {
         return country;
      }

      public String getCountryName()
      {
         return countryName;
      }
   }

   protected class CountryItemAdapter extends ArrayAdapter<CountryItem>
   {
      public CountryItemAdapter(Context context, List<CountryItem> items)
      {
         super(context, R.layout.country_spinner, R.id.countrycodealpha2text, items);
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent)
      {
         View v = convertView;
         if (v == null)
         {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.country_spinner, null);
         }
         CountryItem item = getItem(position);
         if (item != null)
         {
            ImageView countryFlagImg = (ImageView) v.findViewById(R.id.countryflagicon);
            if(mShowFlag) {
               countryFlagImg.setVisibility(VISIBLE);
               countryFlagImg.setImageDrawable(item.getItemImg());
            }
            else
            {
               countryFlagImg.setVisibility(GONE);
            }

            TextView countryCodeAlpha2Text = (TextView) v.findViewById(R.id.countrycodealpha2text);
            countryCodeAlpha2Text.setText(item.getCountry().getCode2A());
         }
         return v;
      }

      public int getCountryPosition(final Country countryToFind)
      {
         return getCountryPosition(new Predicate<Country>()
         {
            public boolean apply(Country country)
            {
               return countryToFind.equals(country);
            }
         });
      }

      public int getCountryPosition(final String code2a)
      {
         return getCountryPosition(new Predicate<Country>()
         {
            public boolean apply(Country country)
            {
               return code2a.equals(country.getCode2A());
            }
         });
      }

      public Country getCountryAtPosition(int position)
      {
         CountryItem countryItem = ((CountryItemAdapter) getAdapter()).getItem(position);
         return countryItem.getCountry();
      }

      public int getCountryPosition(Predicate<Country> condition)
      {
            CountryItemAdapter adapter = (CountryItemAdapter) getAdapter();
            int count = adapter.getCount();
            for(int pos = 0; pos < count;pos++)
            {
               if(condition.apply(adapter.getItem(pos).getCountry()))
               {
                  return pos;
               }
            }

         return -1;
      }

      public View getDropDownView(int position, View convertView, ViewGroup parent)
      {
         View v = convertView;
         if (v == null)
         {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.country_spinner_item, null);
         }
         CountryItem item = getItem(position);
         if (item != null)
         {
            ImageView countryFlagImg = (ImageView) v.findViewById(R.id.countryflagicon);
            countryFlagImg.setImageDrawable(item.getItemImg());

            TextView countryCodeAlpha2Text = (TextView) v.findViewById(R.id.countrycodealpha2text);
            countryCodeAlpha2Text.setText(item.getCountry().getCode2A());

            TextView countryCodeAlpha3Text = (TextView) v.findViewById(R.id.countrycodealpha3text);
            countryCodeAlpha3Text.setText(item.getCountry().getCode3A());

            TextView countryNameText = (TextView) v.findViewById(R.id.countrynametext);
            countryNameText.setText(item.getCountryName());

         }
         return v;
      }
   }


}
