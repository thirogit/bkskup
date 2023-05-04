package com.bk.bkskup3.print.documents.invoice;

import java.util.ArrayList;
import java.util.Collection;

import com.bk.bands.lookup.StrSubstitutor;
import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.model.Cow;
import com.bk.bkskup3.model.CowClass;
import com.bk.bkskup3.print.context.Definitions;
import com.bk.bkskup3.print.context.DocumentContext;
import com.bk.bkskup3.print.documents.util.TokenSubstitutorBuilder;
import com.bk.bkskup3.runtime.CowCalculator;
import com.bk.bkskup3.runtime.InvoiceCalculator;
import com.bk.bkskup3.utils.DoubleFormat;
import com.bk.bkskup3.print.context.Genders;
import com.bk.bkskup3.print.context.Units;
import com.bk.bkskup3.print.context.Unit;
import com.google.common.base.Strings;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/5/12
 * Time: 9:20 PM
 */
public class DetailPieceInvoiceDataSource extends AbstractInvoiceDataSource {
    public DetailPieceInvoiceDataSource(DocumentContext context, DocumentProfile profile) {
        super(context, profile);

        if (Strings.isNullOrEmpty(getInvoiceItemNameFmt())) {
            setInvoiceItemNameFmt("${COWNO}, ${SEX}, ${CLASS}");
        }
    }

    @Override
    protected Collection<InvoiceItem> createItems() {

        Collection<InvoiceItem> result = new ArrayList<InvoiceItem>(invoice.getCowCount());
        DocumentContext context = getContext();

        InvoiceCalculator invoiceCalc = new InvoiceCalculator(invoice);
        Genders genders = context.getGenders();

        Units units = context.getUnits();
        DoubleFormat priceFormat = context.getPriceFormat();
        DoubleFormat weightFormat = context.getWeightFormat();

        for (CowCalculator cowCalc : invoiceCalc.getCowCalculators()) {
            InvoiceItem item = new InvoiceItem();
            Cow cow = cowCalc.getCow();

            TokenSubstitutorBuilder builder = new TokenSubstitutorBuilder(mContext.getTokens());
            builder.with("COWNO",cow.getCowNo().toString());
            builder.with("SEX",genders.getGender(cow.getSex()));
            builder.with("WEIGHT",weightFormat.format(cow.getWeight()));
            builder.with("CLASS",cow.getClassCd());
            Definitions definitions = mContext.getDefinitions();
            CowClass cowClass = definitions.getCowClass(cow.getClassCd());
            builder.with("CLASSNAME", cowClass != null ? cowClass.getClassName() : cow.getClassCd());


            StrSubstitutor substitutor = builder.build();

            item.setGoodsQty(String.valueOf(1));
            item.setGoodsQtyUnit(units.getUnit(Unit.PIECE));
            item.setGoodsName(substitutor.replace(getInvoiceItemNameFmt()));
            item.setGoodsName(substitutor.replace(getInvoiceItemNameFmt()));
            item.setGoodsCategory(getGoodsCategory());

            item.setGrossPrice(priceFormat.format(cowCalc.getGrossPrice()));
            String cowNetPrice = priceFormat.format(cowCalc.getNetPrice());
            item.setNetPrice(cowNetPrice);
            item.setUnitPriceNet(cowNetPrice);
            item.setVatValue(priceFormat.format(cowCalc.getTaxValue()));
            result.add(item);

        }


        return result;
    }
}
