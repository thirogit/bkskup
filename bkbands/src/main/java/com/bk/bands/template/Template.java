package com.bk.bands.template;

import com.bk.bands.properties.TextStyle;

import java.util.Collection;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/6/12
 * Time: 8:32 PM
 */
public class Template
{
    private Collection<TextStyle> styles;

    private List<Field>  header;

    private List<Band> bands;
   private List<Field> footer;

   public List<Field> getHeader()
   {
      return header;
   }

   public void setHeader(List<Field> header)
   {
      this.header = header;
   }

   public List<Band> getBands()
   {
      return bands;
   }

   public void setBands(List<Band> bands)
   {
      this.bands = bands;
   }

   public List<Field> getFooter()
   {
      return footer;
   }

   public void setFooter(List<Field> footer)
   {
      this.footer = footer;
   }

   public Collection<TextStyle> getStyles()
   {
      return styles;
   }

   public void setStyles(Collection<TextStyle> styles)
   {
      this.styles = styles;
   }
}
