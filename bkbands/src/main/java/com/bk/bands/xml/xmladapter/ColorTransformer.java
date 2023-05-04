package com.bk.bands.xml.xmladapter;


import com.bk.bands.properties.Color;
import com.bk.bands.xml.xmladapter.util.ColorUtil;
import org.simpleframework.xml.transform.Transform;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/7/12
 * Time: 12:12 AM
 */
public class ColorTransformer implements Transform<Color>
{
    public Color unmarshal(String val) throws Exception
    {
      return ColorUtil.decodeHtmlColorString(val);
    }

    public String marshal(Color val) throws Exception
    {
      return '#' + Integer.toHexString(val.getRGB());
    }

    @Override
    public Color read(String s) throws Exception {
        return unmarshal(s);
    }

    @Override
    public String write(Color color) throws Exception {
        return marshal(color);
    }
}
