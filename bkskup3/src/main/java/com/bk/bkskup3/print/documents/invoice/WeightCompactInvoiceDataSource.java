package com.bk.bkskup3.print.documents.invoice;

import java.util.Arrays;
import java.util.Collection;

import com.bk.bands.lookup.StrSubstitutor;
import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.print.context.DocumentContext;
import com.bk.bkskup3.print.context.NumeralDeclination;
import com.bk.bkskup3.print.documents.util.TokenSubstitutorBuilder;
import com.bk.bkskup3.runtime.InvoiceCalculator;
import com.bk.bkskup3.utils.DoubleFormat;
import com.bk.bkskup3.print.context.Unit;
import com.google.common.base.Strings;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/23/12
 * Time: 3:26 PM
 */
public class WeightCompactInvoiceDataSource extends AbstractInvoiceDataSource
{
   public WeightCompactInvoiceDataSource(DocumentContext context, DocumentProfile profile)
   {
      super(context,profile);
      if(Strings.isNullOrEmpty(getInvoiceItemNameFmt())) {
         setInvoiceItemNameFmt("#${QTY}, ${WEIGHT}");
      }
   }

   @Override
   protected Collection<InvoiceItem> createItems()
   {
      DocumentContext context = getContext();
      InvoiceItem item = new InvoiceItem();
      DoubleFormat priceFormat = context.getPriceFormat();
      DoubleFormat pricePegKgFormat = context.getPricePerKgFormat();
      DoubleFormat weightFormat = context.getWeightFormat();
      InvoiceCalculator invoiceCalc = new InvoiceCalculator(invoice);
      NumeralDeclination goodsDeclination = getGoodsNameDeclination();

      item.setGoodsQty(context.getWeightFormat().format(invoiceCalc.getTotalWeight()));
      item.setGoodsQtyUnit(context.getUnits().getUnit(Unit.KG));
      item.setUnitPriceNet(pricePegKgFormat.format(invoiceCalc.getNetAvrPricePerKg()));

      TokenSubstitutorBuilder builder = new TokenSubstitutorBuilder(mContext.getTokens());
      builder.with("QTY", String.valueOf(invoice.getCowCount()));
      builder.with("WEIGHT", weightFormat.format(invoiceCalc.getTotalWeight()));
      builder.with("GOODS", goodsDeclination.declinate(invoice.getCowCount()));

      StrSubstitutor substitutor = builder.build();

      item.setGoodsName(substitutor.replace(getInvoiceItemNameFmt()));

      item.setGoodsCategory(getGoodsCategory());
      item.setGrossPrice(priceFormat.format(invoiceCalc.getGross()));
      item.setNetPrice(priceFormat.format(invoiceCalc.getNet()));
      item.setVatValue(priceFormat.format(invoiceCalc.getTax()));

      return Arrays.asList(item);
   }


}
