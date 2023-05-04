package com.bk.bkskup3.library;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.bk.bands.DataSource;
import com.bk.bands.template.Template;
import com.bk.bands.xml.TemplateUnmarshaller;
import com.bk.bands.xml.UnmarshallException;
import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.DocumentOptionsStore;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.print.context.DocumentContext;
import com.bk.bkskup3.print.documents.DocumentArchetype;
import com.bk.bkskup3.print.documents.DocumentDeclaration;
import com.bk.bkskup3.print.documents.foodchain.FoodChainDataSource;
import com.bk.bkskup3.print.documents.invoice.ContractDataSource;
import com.bk.bkskup3.print.documents.invoice.DetailPieceInvoiceDataSource;
import com.bk.bkskup3.print.documents.invoice.DetailWeightInvoiceDataSource;
import com.bk.bkskup3.print.documents.invoice.GDPRDataSource;
import com.bk.bkskup3.print.documents.invoice.PayDueDaysContractDataSource;
import com.bk.bkskup3.print.documents.invoice.PieceCompactInvoiceDataSource;
import com.bk.bkskup3.print.documents.invoice.WeightCompactInvoiceDataSource;
import com.bk.bkskup3.print.documents.notification.MoveNotificationDataSource;
import com.bk.bkskup3.print.filters.LumpInvoiceFilter;
import com.bk.bkskup3.print.filters.NullFilter;
import com.bk.bkskup3.print.filters.RegularInvoiceFilter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DocumentLibraryService extends Service {


    public static interface Callback<T> {
        void callback(T result);
    }

    public static final DocumentDeclaration[] DOCUMENT_DECLARATIONS =
            {
                    new DocumentDeclaration("DETWEILUMP", R.string.detailWeightLumpInvoiceDocName, R.raw.faktura_rr, DetailWeightInvoiceDataSource.class, new LumpInvoiceFilter(), R.raw.detailweightinvoiceopts),
                    new DocumentDeclaration("DETWEIREG", R.string.detailWeightRegularInvoiceDocName, R.raw.faktura_vat, DetailWeightInvoiceDataSource.class, new RegularInvoiceFilter(), R.raw.detailweightinvoiceopts),
                    new DocumentDeclaration("DETPIELUMP", R.string.detailPieceLumpInvoiceDocName, R.raw.faktura_rr, DetailPieceInvoiceDataSource.class, new LumpInvoiceFilter(), R.raw.detailpieceinvoiceopts),
                    new DocumentDeclaration("DETPIEREG", R.string.detailPieceRegularInvoiceDocName, R.raw.faktura_vat, DetailPieceInvoiceDataSource.class, new RegularInvoiceFilter(), R.raw.detailweightinvoiceopts),
                    new DocumentDeclaration("COMPWEILUMP", R.string.weightCompactLumpInvoiceDocName, R.raw.faktura_rr, WeightCompactInvoiceDataSource.class, new LumpInvoiceFilter(), R.raw.weightcompactinvoiceopts),
                    new DocumentDeclaration("COMPWEIREG", R.string.weightCompactRegularInvoiceDocName, R.raw.faktura_vat, WeightCompactInvoiceDataSource.class, new RegularInvoiceFilter(), R.raw.weightcompactinvoiceopts),
                    new DocumentDeclaration("COMPIELUMP", R.string.pieceCompactLumpInvoiceDocName, R.raw.faktura_rr, PieceCompactInvoiceDataSource.class, new LumpInvoiceFilter(), R.raw.piececompactinvoiceopts),
                    new DocumentDeclaration("COMPPIEREG", R.string.pieceCompactRegularInvoiceDocName, R.raw.faktura_vat, PieceCompactInvoiceDataSource.class, new RegularInvoiceFilter(), R.raw.piececompactinvoiceopts),
                    new DocumentDeclaration("MOVNTFN", R.string.moveNotificationDocName, R.raw.zgloszenie_pocztowka, MoveNotificationDataSource.class, new NullFilter()),
                    new DocumentDeclaration("FOODCHAIN", R.string.foodChainDocName, R.raw.lancuch_zywieniowy, FoodChainDataSource.class, new NullFilter(), R.raw.foodchainopts),
                    new DocumentDeclaration("CONTRACTLUMP", R.string.contractDocName, R.raw.umowa_rr, ContractDataSource.class, new LumpInvoiceFilter()),
                    new DocumentDeclaration("CONTRACTREG", R.string.contractDocName, R.raw.umowa_vat, ContractDataSource.class, new RegularInvoiceFilter()),
                    new DocumentDeclaration("CONTRACTPAYDUEDAYS",R.string.contractPayDueDaysDocName, R.raw.umowa_termin_rr, PayDueDaysContractDataSource.class, new LumpInvoiceFilter()),
                    new DocumentDeclaration("CONTRACTPAYDUEDAYSBACK",R.string.contractPayDueDaysBackwardsDocName, R.raw.umowa_termin_rr_wstecz, PayDueDaysContractDataSource.class, new LumpInvoiceFilter(), R.raw.umowa_termin_rr_wstecz_opts),
                    new DocumentDeclaration("GDPR",R.string.gdprDocName, R.raw.rodo, GDPRDataSource.class, new NullFilter()),

            };


    private final IBinder mBinder = new LocalBinder();
    private DocumentOptionsStore mOptionsStore;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Map<String, DocumentPreference> mPreferences;
    private Map<String, DocumentDefinition> mDefinitions;
    private Map<String, DocumentProfileCount> mProfileCounts;

    public class LocalBinder extends Binder {
        public DocumentLibraryService getService() {
            return DocumentLibraryService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onCreate() {

        BkApplication bkApplication = (BkApplication) getApplication();
        BkStore bkStore = bkApplication.getStore();
        mOptionsStore = bkStore.getDocumentOptionsStore();

        Collection<DocumentPreference> documentPreferences = mOptionsStore.fetchPreferences();
        Collection<DocumentProfileCount> documentProfileCounts = mOptionsStore.fetchProfileCounts();

        mProfileCounts = new HashMap<>(Maps.uniqueIndex(documentProfileCounts, profileCount -> profileCount.getDocumentCode()));


        Map<Integer, Template> templateCache = new HashMap<Integer, Template>(DOCUMENT_DECLARATIONS.length);

        TemplateUnmarshaller unmarshaller = new TemplateUnmarshaller();

        mPreferences = new HashMap<>(documentPreferences.size());
        for (DocumentPreference preference : documentPreferences) {
            mPreferences.put(preference.getDocumentCode(), preference);
        }

        mDefinitions = new HashMap<>();
        for (DocumentDeclaration descriptor : DOCUMENT_DECLARATIONS) {
            String docCode = descriptor.getDocId();

            try {

                Collection<DocumentOptionDefinition> optionsDefs = parseOptionDefinitions(descriptor.getOptionsDefsResId());

                DocumentDefinition definition = new DocumentDefinition();
                definition.setDocId(docCode);
                definition.setDataSourceClass(descriptor.getDataSourceClass());
                definition.setDocumentFilter(descriptor.getDocumentFilter());
                definition.setDocumentName(getString(descriptor.getDocNameResId()));
                definition.setOptionDefinitions(optionsDefs);

                Template template = templateCache.get(descriptor.getTemplateResId());
                if (template == null) {
                    template = unmarshaller.unmarshal(getResources().openRawResource(descriptor.getTemplateResId()));
                    templateCache.put(descriptor.getTemplateResId(), template);
                }

                definition.setTemplate(template);

                DocumentPreference documentPreference = mPreferences.get(docCode);
                if (documentPreference == null) {
                    documentPreference = new DocumentPreference(docCode);
                    documentPreference.setVisible(true);
                    mPreferences.put(docCode, documentPreference);
                    insertPreference(documentPreference);
                }

                mDefinitions.put(docCode, definition);
            } catch (UnmarshallException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }


    private Collection<DocumentOptionDefinition> parseOptionDefinitions(int optionsDefsResId) throws ParseException {

        Collection<DocumentOptionDefinition> result = new ArrayList<>();

        if (optionsDefsResId != 0) {
            InputStream inputStream = getResources().openRawResource(optionsDefsResId);
            DocumentOptionXmlDescriptionParser parser = new DocumentOptionXmlDescriptionParser();
            result.addAll(parser.parse(inputStream));
        }
        return result;
    }

    private void insertPreference(final DocumentPreference preference) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mOptionsStore.insertPreference(preference);
            }
        });
    }

    public void onDestroy() {
        mExecutor.shutdown();
    }

    public void setVisiblePreference(String docCode, boolean visible) {
        DocumentPreference preference = mPreferences.get(docCode);
        if (preference != null) {
            preference.setVisible(visible);
            updatePreference(preference);
        }
    }

    private void updatePreference(final DocumentPreference preference) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mOptionsStore.updatePreference(preference);
            }
        });
    }

    public Collection<DocumentProfile> getDocumentProfiles(final String docCode) throws DocumentLibraryException {
        Future<Collection<DocumentProfile>> submit = mExecutor.submit(new Callable<Collection<DocumentProfile>>() {
            @Override
            public Collection<DocumentProfile> call() throws Exception {
                return mOptionsStore.fetchProfiles(docCode);
            }
        });

        try {
            return submit.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DocumentLibraryException(e);
        }
    }

    public void getDocumentProfiles(final String docCode, final Callback<Collection<DocumentProfile>> callback) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Collection<DocumentProfile> profiles = mOptionsStore.fetchProfiles(docCode);
                callback.callback(profiles);
            }
        });
    }

    public int getDocumentProfileCount(String docCode) {
        DocumentProfileCount profileCount = mProfileCounts.get(docCode);
        if (profileCount != null) {
            return profileCount.getProfileCount();
        }
        return 0;
    }

    public Collection<DocumentDescription> getDocumentsFor(Invoice invoice) {
        ArrayList<DocumentDescription> result = new ArrayList<>(mDefinitions.size());
        for (DocumentDefinition definition : mDefinitions.values()) {

            DocumentPreference documentPreference = mPreferences.get(definition.docId);
            if (documentPreference != null) {
                if (!documentPreference.isVisible()) {
                    continue;
                }
            }

            if (definition.documentFilter.apply(invoice)) {
                DocumentDescription description = new DocumentDescription(definition.docId);
                description.setDocName(definition.documentName);

                DocumentProfileCount profileCount = mProfileCounts.get(definition.docId);
                if (profileCount != null) {
                    description.setProfileCount(profileCount.getProfileCount());
                }

                result.add(description);
            }
        }
        return result;
    }

    public Collection<DocumentPreference> getDocumentPreferences() {
        return mPreferences.values();
    }

    public Collection<DocumentDefinition> getDocumentDefinitions() {
        return mDefinitions.values();
    }

    public void deleteProfile(final int profileId) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mOptionsStore.deleteProfile(profileId);
            }
        });
    }

    public DocumentProfile createNewProfile(String docCode, String profileName) throws DocumentLibraryException {
        DocumentDefinition definition = mDefinitions.get(docCode);
        if (definition == null) {
            throw new DocumentLibraryException("undefined document: " + docCode);
        }

        DocumentProfile newProfile = new DocumentProfile(0);
        newProfile.setProfileName(profileName);
        newProfile.setDocumentCode(docCode);

        Collection<DocumentOptionDefinition> optionDefinitions = definition.getOptionDefinitions();

        for (DocumentOptionDefinition optionDefinition : optionDefinitions) {
            String code = optionDefinition.getCode();
            String defaultValue = optionDefinition.getDefaultValue();
            newProfile.addOption(code, defaultValue);
        }

        DocumentProfile insertedProfile = mOptionsStore.insertProfile(newProfile);

        DocumentProfileCount profileCount = mProfileCounts.get(docCode);
        if (profileCount == null) {
            profileCount = new DocumentProfileCount(docCode);
            mProfileCounts.put(docCode, profileCount);
        }

        profileCount.setProfileCount(profileCount.getProfileCount() + 1);

        return insertedProfile;
    }


    public Iterable<DocumentOptionDefinition> getDocumentOptionDefinition(String docCode) {
        DocumentDefinition docDefinition = mDefinitions.get(docCode);
        return Iterables.unmodifiableIterable(docDefinition.getOptionDefinitions());
    }

    public void updateProfile(DocumentProfile profile) {
        mOptionsStore.updateProfile(profile);
    }

    public DocumentArchetype createArchetype(String docCode, DocumentContext context, DocumentProfile profile) throws DocumentLibraryException {

        DocumentDefinition definition = mDefinitions.get(docCode);
        DataSource dataSource;

        try {

            try {
                Constructor<? extends DataSource> withProfileConstructor = definition.dataSourceClass.getDeclaredConstructor(DocumentContext.class, DocumentProfile.class);
                dataSource = withProfileConstructor.newInstance(context, profile);
            } catch (NoSuchMethodException e) {

                try {
                    Constructor<? extends DataSource> onlyContextConstructor = definition.dataSourceClass.getDeclaredConstructor(DocumentContext.class);
                    dataSource = onlyContextConstructor.newInstance(context);
                } catch (NoSuchMethodException e2)
                {
                   throw  new DocumentLibraryException("data source class: " + definition.dataSourceClass.getName() + " does not have compatible constructors");
                }
            }

            DocumentArchetype archetype = new DocumentArchetype();
            archetype.setDocName(definition.documentName);
            archetype.setDataSource(dataSource);
            archetype.setDocTemplate(definition.template);
            return archetype;

        } catch (Exception e) {
            throw new DocumentLibraryException(e);
        }


    }
}
