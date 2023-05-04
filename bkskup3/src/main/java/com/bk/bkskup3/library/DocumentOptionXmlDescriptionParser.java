package com.bk.bkskup3.library;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by SG0891787 on 2/15/2017.
 */

public class DocumentOptionXmlDescriptionParser {

    private static final String ROOT_ELEMENT_NAME = "options";
    private static final String OPTION_ELEMENT_NAME = "option";


    public Collection<DocumentOptionDefinition> parse(InputStream is) throws ParseException {
        Collection<DocumentOptionDefinition> result = new ArrayList<DocumentOptionDefinition>();
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "UTF-8");

            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, ROOT_ELEMENT_NAME);

            while (parser.nextTag() == XmlPullParser.START_TAG) {


                DocumentOptionDefinition definition = new DocumentOptionDefinition();

                parser.require(XmlPullParser.START_TAG, null, OPTION_ELEMENT_NAME);

                for (int attrib = 0, count = parser.getAttributeCount(); attrib < count; attrib++) {
                    String attributeName = parser.getAttributeName(attrib);
                    String attributeValue = parser.getAttributeValue(attrib);

                    if ("title".equals(attributeName)) {
                        definition.setTitle(attributeValue);
                    }

                    if ("code".equals(attributeName)) {
                        definition.setCode(attributeValue);
                    }

                    if ("type".equals(attributeName)) {
                        definition.setType(DocumentOptionType.valueOf(attributeValue));
                    }

                    if ("default".equals(attributeName)) {
                        definition.setDefaultValue(attributeValue);
                    }
                }

                result.add(definition);

                parser.nextTag();
                parser.require(XmlPullParser.END_TAG, null, OPTION_ELEMENT_NAME);
            }
            parser.require(XmlPullParser.END_TAG, null, ROOT_ELEMENT_NAME);
            return result;

        } catch (XmlPullParserException e) {
            throw new ParseException(e);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }
}
