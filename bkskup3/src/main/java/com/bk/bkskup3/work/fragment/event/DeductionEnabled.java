package com.bk.bkskup3.work.fragment.event;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/24/2014
 * Time: 1:46 PM
 */
public class DeductionEnabled extends FragmentEvent {
   private UUID deductionId;

    public DeductionEnabled(UUID deductionId) {
        this.deductionId = deductionId;
    }

    public UUID getDeductionId() {
        return deductionId;
    }
}
