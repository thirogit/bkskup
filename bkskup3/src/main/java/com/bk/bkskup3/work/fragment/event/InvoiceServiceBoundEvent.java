package com.bk.bkskup3.work.fragment.event;

import com.bk.bkskup3.work.service.InvoiceService;

public class InvoiceServiceBoundEvent extends FragmentEvent {
    private InvoiceService mService;

    public InvoiceServiceBoundEvent(InvoiceService service) {
        this.mService = service;
    }

    public InvoiceService getService() {
        return mService;
    }
}
