package com.bk.bkskup3.print.widgets;


import android.content.Context;
import com.bk.bands.paper.BitmapPaper;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/12/12
 * Time: 9:00 PM
 */
public class PaperView extends ScrollableImage
{
    public PaperView(Context context,BitmapPaper paper)
    {
        super(context,null);
        setImage(paper.getBitmap());
    }

}
