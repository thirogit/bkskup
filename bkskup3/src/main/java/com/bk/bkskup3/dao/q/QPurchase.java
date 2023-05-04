package com.bk.bkskup3.dao.q;

import com.bk.bkskup3.model.PurchaseState;
import com.mysema.query.types.path.DatePath;
import com.mysema.query.types.path.EnumPath;
import com.mysema.query.types.path.NumberPath;

import java.util.Date;

import static com.mysema.query.types.PathMetadataFactory.forProperty;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/15/2014
 * Time: 6:13 PM
 */
public class QPurchase {
    private QPurchase() {
    }

    public static final NumberPath<Integer> id = new NumberPath<Integer>(Integer.class, forProperty(null,"purchaseid"));
    public static final NumberPath<Integer> herdNo = new NumberPath<Integer>(Integer.class, forProperty(null,"herdno"));
    public static final EnumPath state = new EnumPath<PurchaseState>(PurchaseState.class, forProperty(null,"state"));
    public static final DatePath<Date> startDt = new DatePath<Date>(Date.class, forProperty(null,"start"));

}
