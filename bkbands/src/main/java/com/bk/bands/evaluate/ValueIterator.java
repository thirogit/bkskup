package com.bk.bands.evaluate;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/8/12
 * Time: 11:04 AM
 */
public interface ValueIterator extends Evaluator {
    boolean hasNext();

    boolean next();
}
