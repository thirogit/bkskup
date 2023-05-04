package com.bk.bkskup3.print;

import android.app.Activity;
import android.content.Intent;
import android.widget.LinearLayout;
import com.bk.bands.paper.BitmapPaper;
import com.bk.bands.serializer.DocumentBean;
import com.bk.bkskup3.print.widgets.PaperView;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/5/12
 * Time: 8:45 PM
 */
public class PrintPreviewActivity extends Activity {
    public static final String EXTRA_DOCUMENT = "extra_document";

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Intent intent = getIntent();
            DocumentBean document = (DocumentBean) intent.getSerializableExtra(EXTRA_DOCUMENT);
            BitmapPaper paper = new BitmapPaper(200, document.getSize());

            document.print(paper);

            setContentView(new PaperView(this, paper), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
