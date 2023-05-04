package com.bk.bkskup3.print.documents;

import com.bk.bkskup3.R;
import com.bk.bkskup3.print.documents.foodchain.FoodChainDataSource;
import com.bk.bkskup3.print.documents.invoice.ContractDataSource;
import com.bk.bkskup3.print.documents.invoice.DetailWeightInvoiceDataSource;
import com.bk.bkskup3.print.documents.invoice.PayDueDaysContractDataSource;
import com.bk.bkskup3.print.documents.notification.MoveNotificationDataSource;
import com.bk.bkskup3.print.filters.LumpInvoiceFilter;
import com.bk.bkskup3.print.filters.NullFilter;
import com.bk.bkskup3.print.filters.RegularInvoiceFilter;

public class DocumentDescriptors {
    private DocumentDescriptors() {
    }

    public static final DocumentDeclaration[] DOCUMENT_DESCRIPTORS =
            {
                    new DocumentDeclaration("DETWEILUMP", R.string.detailWeightLumpInvoiceDocName, R.raw.faktura_rr, DetailWeightInvoiceDataSource.class, new LumpInvoiceFilter(), R.raw.commoninvoiceopts),
                    new DocumentDeclaration("DETWEIREG", R.string.detailWeightRegularInvoiceDocName, R.raw.faktura_vat, DetailWeightInvoiceDataSource.class, new RegularInvoiceFilter(), R.raw.commoninvoiceopts),
//      new DocumentDeclaration(R.string.detailPieceLumpInvoiceDocName,       R.raw.faktura_rr,          DetailPieceInvoiceDataSource.class,       new LumpInvoiceFilter()),
//      new DocumentDeclaration(R.string.detailPieceRegularInvoiceDocName,    R.raw.faktura_vat,         DetailPieceInvoiceDataSource.class,       new RegularInvoiceFilter()),
                    new DocumentDeclaration("MOVNTFN", R.string.moveNotificationDocName, R.raw.zgloszenie_pocztowka, MoveNotificationDataSource.class, new NullFilter()),
//      new DocumentDeclaration(R.string.weightCompactLumpInvoiceDocName,     R.raw.faktura_rr,          WeightCompactInvoiceDataSource.class,     new LumpInvoiceFilter()),
//      new DocumentDeclaration(R.string.weightCompactRegularInvoiceDocName,  R.raw.faktura_vat,         WeightCompactInvoiceDataSource.class,     new RegularInvoiceFilter()),
//      new DocumentDeclaration(R.string.pieceCompactLumpInvoiceDocName,      R.raw.faktura_rr,          PieceCompactInvoiceDataSource.class,      new LumpInvoiceFilter()),
//      new DocumentDeclaration(R.string.pieceCompactRegularInvoiceDocName,   R.raw.faktura_vat,         PieceCompactInvoiceDataSource.class,      new RegularInvoiceFilter()),
                    new DocumentDeclaration("FOODCHAIN", R.string.foodChainDocName, R.raw.lancuch_zywieniowy, FoodChainDataSource.class, new NullFilter()),
                    new DocumentDeclaration("CONTRACTLUMP", R.string.contractDocName, R.raw.umowa_rr, ContractDataSource.class, new LumpInvoiceFilter()),
                    new DocumentDeclaration("CONTRACTREG", R.string.contractDocName, R.raw.umowa_vat, ContractDataSource.class, new RegularInvoiceFilter()),
                    new DocumentDeclaration("CONTRACTPAYDUEDAYS",R.string.contractPayDueDaysDocName, R.raw.umowa_termin_rr, PayDueDaysContractDataSource.class, new LumpInvoiceFilter()),
            };

}
