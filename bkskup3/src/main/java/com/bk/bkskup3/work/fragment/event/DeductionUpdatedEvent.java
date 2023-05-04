package com.bk.bkskup3.work.fragment.event;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/20/2014
 * Time: 11:03 PM
 */
public class DeductionUpdatedEvent extends FragmentEvent {

    private UUID mDeductionId;

    public DeductionUpdatedEvent(UUID deductionId) {
        this.mDeductionId = deductionId;
    }

    public UUID getDeductionId() {
        return mDeductionId;
    }
}
