package com.bk.bkskup3.work;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.bk.bkskup3.R;
import com.bk.bkskup3.model.IBAN;
import com.bk.bkskup3.utils.Dates;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.utils.check.IBANCheck;
import com.bk.countries.Countries;
import com.bk.widgets.actionbar.ActionBar;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/17/13
 * Time: 6:28 PM
 */
public class OcrNumberActivity extends Activity {


    static class CaptureHandler extends Handler
    {
        private OcrNumberActivity parent;

        public CaptureHandler(OcrNumberActivity parent) {
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            if(msg.what == CAPTURE_PICTURE_WHAT)
            {
                parent.mCameraView.captureImage(parent.mOnImageCaptured);
            }

            super.handleMessage(msg);
        }
    }

    static class ResultHandler extends Handler
    {
        private OcrNumberActivity parent;

        public ResultHandler(OcrNumberActivity parent) {
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            if(msg.what == CAPTURE_PICTURE_WHAT)
            {
                parent.mCameraView.captureImage(parent.mOnImageCaptured);
            }

            super.handleMessage(msg);
        }

//        public void deliverResult(String )
    }

    private static final int CAPTURE_PICTURE_WHAT = 1;

    private CameraView mCameraView;
    private int cameraMethod = CameraKit.Constants.METHOD_STANDARD;
    private boolean cropOutput = false;
//    private FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();


    private CameraKitEventCallback<CameraKitImage> mOnImageCaptured = cameraKitImage -> {


//        byte[] jpeg = cameraKitImage.getJpeg();
//        ByteBuffer buffer = ByteBuffer.wrap(jpeg);
//
//        Size captureSize = mCameraView.getCaptureSize();
//        Size previewSize = mCameraView.getPreviewSize();
//
//
//        FirebaseVisionImageMetadata metadata =
//                new FirebaseVisionImageMetadata.Builder()
//                        .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
//                        .setWidth(previewSize.getWidth())
//                        .setHeight(previewSize.getHeight())
//                        .setRotation(FirebaseVisionImageMetadata.ROTATION_0)
//                        .build();
//
//        FirebaseVisionImage image = FirebaseVisionImage.fromByteBuffer(buffer, metadata);

//        saveToExternalStorage(image.getBitmapForDebugging());

        Bitmap bitmap = cameraKitImage.getBitmap();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        saveToExternalStorage(bitmap);

//        Task<FirebaseVisionText> task = detector.detectInImage(image);
//        task.addOnSuccessListener(firebaseVisionText -> {
//            List<FirebaseVisionText.Block> blocks = firebaseVisionText.getBlocks();
//            for (FirebaseVisionText.Block block : blocks) {
//                List<FirebaseVisionText.Line> lines = block.getLines();
//                for (FirebaseVisionText.Line line : lines) {
//                    String concatedElements = "";
//                    for(FirebaseVisionText.Element element : line.getElements())
//                    {
//                        concatedElements += element.getText();
//                    }
//
//                    if(doesSatisfy(concatedElements))
//                    {
//
//                    }
//                }
//            }
//        });

//        task.addOnCompleteListener(task1 -> scheduleCapture());

    };

    private CaptureHandler mCaptureHandler;


    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_input);

        final ActionBar bar = findViewById(R.id.actionBar);
        bar.addAction(new ActionBar.TextAction(R.string.cancel, () -> finish()));
        bar.setTitle(getTitle());

        mCameraView = findViewById(R.id.camera);
        mCameraView.setMethod(cameraMethod);
        mCameraView.setCropOutput(cropOutput);

        mCaptureHandler = new CaptureHandler(this);

        TextView ocrOkBtn = findViewById(R.id.ocrOkBtn);
        ocrOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraView.captureImage(mOnImageCaptured);
            }
        });

    }


    private void saveToExternalStorage(Bitmap bitmapImage){
        File directory = Environment.getExternalStorageDirectory();
        File mypath=new File(directory,"ocr_" + Dates.now().getTime() + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String p = mypath.getAbsolutePath();

        System.out.println(p);
    }

    private void scheduleCapture() {

        cancelCapture();
        mCaptureHandler.sendEmptyMessageDelayed(CAPTURE_PICTURE_WHAT, 500);

    }

    private void cancelCapture()
    {
        mCaptureHandler.removeMessages(CAPTURE_PICTURE_WHAT);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
        mCameraView.setFocus(CameraKit.Constants.FOCUS_CONTINUOUS);

        //scheduleCapture();
    }

    @Override
    protected void onPause() {
        cancelCapture();
        mCameraView.stop();
        super.onPause();
    }

    private boolean doesSatisfy(String recognizedText)
    {
        String noSpaces = recognizedText.replace(" ", "");
        if(Numbers.isAllDigits(noSpaces)) {
            String countryCd = getResources().getString(R.string.country);
            IBAN iban = new IBAN(Countries.getCountries().getCountryIsoCode2(countryCd), noSpaces);
            return IBANCheck.validate(iban);
        }
        return false;
    }


}
