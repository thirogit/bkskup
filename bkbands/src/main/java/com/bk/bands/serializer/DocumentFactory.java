package com.bk.bands.serializer;


import java.util.LinkedList;
import java.util.List;

import com.bk.bands.DataSource;
import com.bk.bands.evaluate.BandEvaluator;
import com.bk.bands.properties.Dimension;
import com.bk.bands.serializer.DocumentBean;
import com.bk.bands.evaluate.Evaluable;
import com.bk.bands.evaluate.Evaluator;
import com.bk.bands.evaluate.ValueIterator;
import com.bk.bands.print.FieldVisitor;
import com.bk.bands.serializer.DocumentSerializer;
import com.bk.bands.template.Band;
import com.bk.bands.template.CompositeField;
import com.bk.bands.template.Field;
import com.bk.bands.template.StackField;
import com.bk.bands.template.Template;
import com.bk.bands.runtime.BandsException;

/**
 * Created by IntelliJ IDEA.
 * User: sg0891787
 * Date: 3/5/12
 * Time: 9:45 AM
 */
public class DocumentFactory {
    public static DocumentBean create(String name,Template template, DataSource source) {
        StackField stack = new StackField();

        List<Field> headerFields = template.getHeader();
        if (headerFields != null) {
            CompositeField footerField = evaluateFields(headerFields, source.getHeaderValues());
            stack.pushField(footerField);
        }

        List<Band> bands = template.getBands();
        for (Band band : bands) {

            BandEvaluator bandEvaluator = source.getBandValues(band.getName());
            if(bandEvaluator != null) {
                ValueIterator bandValues = bandEvaluator.getBandValues();
                if(bandValues.hasNext()) {
                    List<Field> bandHeaderFields = band.getHeader();
                    if (bandHeaderFields != null) {
                        CompositeField bandHeaderField = evaluateFields(bandHeaderFields, bandEvaluator.getHeaderValues());
                        stack.pushField(bandHeaderField);
                    }
                    stack.pushField(band.expand(bandValues));

                    List<Field> bandFooterFields = band.getFooter();
                    if (bandFooterFields != null) {
                        CompositeField bandFooterField = evaluateFields(bandFooterFields, bandEvaluator.getFooterValues());
                        stack.pushField(bandFooterField);
                    }
                }
            }
        }

        List<Field> footerFields = template.getFooter();
        if (footerFields != null) {
            CompositeField headerField = evaluateFields(footerFields, source.getFooterValues());
            stack.pushField(headerField);
        }

        DocumentSerializer serializer = new DocumentSerializer();
        stack.visit(serializer);

        DocumentBean documentBean = new DocumentBean();
        documentBean.name = name;
        documentBean.fields = serializer.getFields();
        documentBean.size = new Dimension(calculateWidth(documentBean.fields),calculateHeight(documentBean.fields));

        return documentBean;
    }



    private static int calculateHeight(List<FieldBean> fields)
    {
        int maxH = 0;
        for(FieldBean field : fields)
        {
            int fieldBottom = field.getPosition().y+field.getSize().height;
            if(fieldBottom > maxH)
            {
                maxH = fieldBottom;
            }
        }
        return maxH;
    }

    private static int calculateWidth(List<FieldBean> fields)
    {
        int maxW = 0;
        for(FieldBean field : fields)
        {
            int fieldRight = field.getPosition().x+field.getSize().width;
            if(fieldRight > maxW)
            {
                maxW = fieldRight;
            }
        }
        return maxW;
    }

    private static CompositeField evaluateFields(List<Field> fields, Evaluator evaluator) {
        List<Field> evaluatedFields = new LinkedList<Field>();
        for (Field field : fields) {
            if (field instanceof Evaluable && evaluator != null) {
                evaluatedFields.add((((Evaluable) field).evaluate(evaluator)));
            } else {
                evaluatedFields.add(field);
            }
        }
        return new CompositeField(evaluatedFields);
    }


}
