package com.bk.bands;

import com.bk.bands.evaluate.BandEvaluator;
import com.bk.bands.evaluate.Evaluator;
import com.bk.bands.evaluate.ValueIterator;

/**
 * Created by IntelliJ IDEA.
 * User: sg0891787
 * Date: 3/5/12
 * Time: 9:38 AM
 */
public interface DataSource
{
   Evaluator getHeaderValues();
   Evaluator getFooterValues();
   BandEvaluator getBandValues(String bandName);

}
