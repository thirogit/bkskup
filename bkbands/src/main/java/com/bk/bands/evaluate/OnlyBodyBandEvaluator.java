package com.bk.bands.evaluate;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/27/2015
 * Time: 10:46 PM
 */
public class OnlyBodyBandEvaluator implements BandEvaluator {

    private ValueIterator valueIterator;

    public OnlyBodyBandEvaluator(ValueIterator valueIterator) {
        this.valueIterator = valueIterator;
    }

    @Override
    public Evaluator getHeaderValues() {
        return null;
    }

    @Override
    public Evaluator getFooterValues() {
        return null;
    }

    @Override
    public ValueIterator getBandValues() {
        return valueIterator;
    }
}
