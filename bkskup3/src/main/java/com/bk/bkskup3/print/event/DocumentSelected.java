package com.bk.bkskup3.print.event;

import com.bk.bkskup3.work.fragment.event.FragmentEvent;

/**
 * Created by SG0891787 on 10/3/2017.
 */

public class DocumentSelected extends FragmentEvent {

    String documentCd;
    boolean longClick;

    public DocumentSelected(String documentCd) {
        this.documentCd = documentCd;
    }

    public String getDocumentCd() {
        return documentCd;
    }

    public boolean isLongClick() {
        return longClick;
    }

    public void setLongClick(boolean longClick) {
        this.longClick = longClick;
    }
}
