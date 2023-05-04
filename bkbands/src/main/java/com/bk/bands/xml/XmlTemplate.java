package com.bk.bands.xml;

import com.bk.bands.template.Band;
import com.bk.bands.properties.TextStyle;
import com.bk.bands.template.Template;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Commit;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


@Root
@Default(DefaultType.FIELD)
public class XmlTemplate {
    @ElementList(entry = "style", inline = false)
    protected List<XmlStyle> styles;

    XmlFields header;

    @ElementList(entry = "band", inline = true, required = false)
    protected List<XmlBand> xmlBands;

    XmlFields footer;

    @Transient
    private Template template;

    public XmlTemplate() {

    }

    @Commit
    void commit()
    {
        template = new Template();

        Collection<TextStyle> pealedStyle = new LinkedList<TextStyle>();
        for(XmlStyle xmlStyle : styles) pealedStyle.add(xmlStyle.getStyle());
        template.setStyles(pealedStyle);

        List<Band> pealedBands = new LinkedList<Band>();
        if(xmlBands != null) {
            for (XmlBand xmlBand : xmlBands) pealedBands.add(xmlBand.getBand());
        }
        template.setBands(pealedBands);
        template.setFooter(footer.getFields());
        template.setHeader(header.getFields());

    }
    public Template getTemplate() {
        return template;
    }
}
