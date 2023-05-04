package com.bk.bkskup3.print.documents;

import com.bk.bands.DataSource;
import com.bk.bkskup3.print.context.DocumentContext;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/22/12
 * Time: 1:01 PM
 */
public abstract class DocumentDataSource implements DataSource
{
   protected DocumentContext mContext;

   protected DocumentDataSource(DocumentContext context)
   {
      this.mContext = context;
   }

   public DocumentContext getContext()
   {
      return mContext;
   }
}
