package com.bk.bkskup3.work.fragment;

import com.bk.bkskup3.tasks.DependenciesForInvoice;
import com.squareup.otto.InheritSubscribers;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/14/2014
 * Time: 2:30 PM
 */
@InheritSubscribers
public abstract class InvoiceEditFragment extends InvoiceFragment {

    protected DependenciesForInvoice mDependencies;

    public void setDependencies(DependenciesForInvoice dependencies) {
        this.mDependencies = dependencies;
    }
}
