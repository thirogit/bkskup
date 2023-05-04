package com.bk.bkskup3.work.input;

import com.bk.bkskup3.model.DeductionDetails;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/18/2014
 * Time: 11:37 AM
 */
public class DeductionInput implements Serializable {
    private DeductionDetails mDetails = new DeductionDetails();
    private boolean mEnabled;
    private UUID mInputId;

    public DeductionInput(UUID inputId) {
        this.mInputId = inputId;
    }

    public DeductionInput() {
    }

    public UUID getInputId() {
        return mInputId;
    }

    public String getCode() {
        return mDetails.getCode();
    }

    public void setCode(String code) {
        this.mDetails.setCode(code);
    }

    public String getReason() {
        return mDetails.getReason();
    }

    public void setReason(String reason) {
        this.mDetails.setReason(reason);
    }

    public Double getFraction() {
        return mDetails.getFraction();
    }

    public void setFraction(Double fraction) {
        this.mDetails.setFraction(fraction);
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public void copyFrom(DeductionInput src) {

        this.mDetails.setCode(src.getCode());
        this.mDetails.setReason(src.getReason());
        this.mDetails.setFraction(src.getFraction());
        this.mEnabled = src.isEnabled();
    }
}
