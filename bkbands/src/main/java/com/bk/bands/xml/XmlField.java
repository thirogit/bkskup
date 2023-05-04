package com.bk.bands.xml;

import com.bk.bands.template.Field;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Transient;


@Default(DefaultType.PROPERTY)
public abstract class XmlField
{
//   @Transient
//   private XmlTemplate parentTemplate;

//   public XmlField() {} //just to satisfy jaxb

//   protected XmlField(XmlTemplate parentTemplate)
//   {
//      this.parentTemplate = parentTemplate;
//   }

//   public XmlTemplate getParentTemplate()
//   {
//      return parentTemplate;
//   }

   protected abstract Field delegate();

   @Attribute(required = true)
   public String getLabel()
   {
      return delegate().getLabel();
   }

    @Attribute(required = true)
   public void setLabel(String label)
   {
      delegate().setLabel(label);
   }

   public XmlPosition getPosition()
   {
      return new XmlPosition(delegate().getPosition(),delegate().getSize());
   }

   public void setPosition(XmlPosition value)
   {
      delegate().setPosition(value.getPosition());
      delegate().setSize(value.getSize());
   }

    @Transient
   public Field getField()
   {
      return delegate();
   }
}
