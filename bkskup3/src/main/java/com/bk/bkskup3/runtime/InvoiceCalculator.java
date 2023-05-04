package com.bk.bkskup3.runtime;

import com.bk.bkskup3.model.Cow;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.InvoiceDeduction;
import com.bk.bkskup3.utils.MoneyRounding;
import com.bk.bkskup3.utils.NullUtils;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/1/12
 * Time: 11:27 PM
 */
public class InvoiceCalculator
{
   class CowCalculatorImpl implements CowCalculator
   {
      private Cow cow;

      CowCalculatorImpl(Cow cow)
      {
         this.cow = cow;
      }

      @Override
      public double getNetPrice()
      {
         return cow.getPrice();
      }

      @Override
      public double getGrossPrice()
      {
         return getNetPrice()+getTaxValue();
      }

      @Override
      public double getTaxValue()
      {
          Double vatRate = invoice.getVatRate();
          return MoneyRounding.round(cow.getPrice() * NullUtils.<Double>valueForNull(vatRate,0.0));
      }

      @Override
      public double getNetPricePerKg()
      {
         return cow.getPrice()/cow.getWeight();
      }

       @Override
       public double getGrossPricePerKg() {
           return getGrossPrice()/cow.getWeight();
       }

       @Override
      public Cow getCow()
      {
         return cow;
      }
   }

    class DeductionCalculatorImpl implements DeductionCalculator
    {
        private InvoiceDeduction deduction;

        DeductionCalculatorImpl(InvoiceDeduction deduction) {
            this.deduction = deduction;
        }

        @Override
        public InvoiceDeduction getDeduction() {
            return deduction;
        }

        @Override
        public double getDeductedAmount() {
            double totalNet = getNet();
            double preciseAmount = totalNet * deduction.getFraction();
            return MoneyRounding.roundToInteger(preciseAmount);
        }
    }

   private Invoice invoice;
   private Collection<CowCalculator> cowCalculators;
   private Collection<DeductionCalculator> deductionCalculators;

   public InvoiceCalculator(Invoice invoice)
   {
      this.invoice = invoice;
      this.cowCalculators = new ArrayList<CowCalculator>(invoice.getCowCount());
       this.deductionCalculators = new ArrayList<DeductionCalculator>(invoice.getDeductionsCount());

      for(Cow cow : invoice.getCows())
      {
         cowCalculators.add(new CowCalculatorImpl(cow));
      }

       for(InvoiceDeduction deduction : invoice.getDeductions())
       {
           if(deduction.isEnabled()) {
               deductionCalculators.add(new DeductionCalculatorImpl(deduction));
           }
       }
   }

   public double getNetAvgPriceForCow()
   {
      return getNet()/invoice.getCowCount();
   }

   public double getNet()
   {
      double totalNet = 0.0;
      for(CowCalculator cowCalc : cowCalculators)
      {
         totalNet += cowCalc.getNetPrice();
      }
      return totalNet;
   }

    public double getDeductedAmount()
    {
        double deductedTotal = 0.0;
        double netTotal = getNet();
        for(InvoiceDeduction deduction : invoice.getDeductions())
        {
            if(deduction.isEnabled()) {
                deductedTotal += MoneyRounding.roundToInteger(netTotal * deduction.getFraction());
            }
        }
        return deductedTotal;
    }

    public double getGrossAfterDeductions()
    {
        return getGross()-getDeductedAmount();
    }

   public double getGross()
   {
      return getNet()+getTax();
   }

   public double getTax()
   {
      double totalVat = 0.0;
      for(CowCalculator cowCalc : cowCalculators)
      {
         totalVat += cowCalc.getTaxValue();
      }
      return totalVat;
   }

   public Invoice getInvoice()
   {
      return invoice;
   }

   public double getNetAvrPricePerKg()
   {
      return getNet()/getTotalWeight();
   }

   public double getTotalWeight()
   {
      double totalWeight = 0.0;
      for(Cow cow : invoice.getCows())
      {
         totalWeight += cow.getWeight();
      }
      return totalWeight;
   }

   public Collection<CowCalculator> getCowCalculators()
   {
      return ImmutableList.copyOf(cowCalculators);
   }

    public Collection<DeductionCalculator> getDeductionCalculators() { return ImmutableList.copyOf(deductionCalculators); }

}
