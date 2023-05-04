package com.bk.bands.evaluate;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/27/2015
 * Time: 2:13 PM
 */
public interface BandEvaluator {
    Evaluator getHeaderValues();
    Evaluator getFooterValues();
    ValueIterator getBandValues();
}
