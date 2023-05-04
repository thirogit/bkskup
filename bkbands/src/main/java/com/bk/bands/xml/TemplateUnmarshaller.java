package com.bk.bands.xml;

import com.bk.bands.properties.Color;
import com.bk.bands.template.Template;
import com.bk.bands.xml.xmladapter.ColorTransformer;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.transform.RegistryMatcher;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/27/12
 * Time: 11:21 AM
 */
public class TemplateUnmarshaller
{

   public Template unmarshal(InputStream templateXml) throws UnmarshallException {
       try {
           Strategy strategy = new CycleStrategy("id", "ref");
           RegistryMatcher matcher = new RegistryMatcher();
           matcher.bind(Color.class, ColorTransformer.class);
           Serializer serializer = new Persister(strategy, matcher);
           XmlTemplate xmlTemplate = serializer.read(XmlTemplate.class, templateXml);
           return xmlTemplate.getTemplate();
       } catch (Exception e) {
           throw new UnmarshallException(e);
       }
   }
}
