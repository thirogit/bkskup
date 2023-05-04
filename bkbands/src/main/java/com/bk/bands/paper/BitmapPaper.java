package com.bk.bands.paper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.bk.bands.properties.*;
import com.bk.bands.runtime.BandsException;

public class BitmapPaper implements Paper {
    private int paperDPI;
    private Dimension paperSizeMM10;
    private Bitmap paperImage;
    private Canvas paperCanvas;

    class TypefaceCacheKey {
        private String faceName;
        private int typeFaceStyle;

        TypefaceCacheKey(String faceName, int typeFaceStyle) {
            this.faceName = faceName;
            this.typeFaceStyle = typeFaceStyle;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            TypefaceCacheKey key = (TypefaceCacheKey) o;
            return typeFaceStyle == key.typeFaceStyle && faceName.equals(key.faceName);
        }

        @Override
        public int hashCode() {
            return 31 * faceName.hashCode() + typeFaceStyle;
        }
    }

    private Map<TypefaceCacheKey, Typeface> typefaceCache;


    public BitmapPaper(int paperDPI, Dimension paperSizeMM10) {
        this.paperDPI = paperDPI;
        this.paperSizeMM10 = paperSizeMM10;
        this.typefaceCache = new LinkedHashMap<TypefaceCacheKey, Typeface>();
    }

    private Bitmap createPaperImage(Dimension pixelSize) {
        return Bitmap.createBitmap(pixelSize.width, pixelSize.height, Bitmap.Config.RGB_565);
    }

    private int toPaperDots(int mm10) {
        return toDots(mm10, paperDPI);
    }

    private int toDots(int mm10, int DPI) {
        final double inchTimes100 = 2.54 * 100;
        double inches = mm10 / inchTimes100;
        Double pixels = inches * DPI;
        long rounded = Math.round(pixels);
//      int iii =  pixels.intValue();
        return (int) rounded;//(mm10*DPI)/254;
    }

    public Dimension getPaperSizeInPixels() {
        return new Dimension(toPaperDots(paperSizeMM10.width) + 1, toPaperDots(paperSizeMM10.height) + 1);
    }

    private Canvas getPaperCanvas() throws BandsException {

        if (paperImage == null) {
            if (paperDPI < 100) {
                throw new BandsException("Min. DPI is 100");
            }

            if (paperSizeMM10.height <= 0 || paperSizeMM10.width <= 0) {
                throw new BandsException("Invalid paper size");
            }

            paperImage = createPaperImage(getPaperSizeInPixels());

        }
        if (paperCanvas == null) {
            paperCanvas = new Canvas(paperImage);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            // make the entire canvas white
            paint.setColor(Color.WHITE.getRGB());
            paperCanvas.drawPaint(paint);
        }

        return paperCanvas;
    }

    public void drawRectangle(Rectangle rect, Border border) throws BandsException {
        Canvas canvas = getPaperCanvas();
        Paint pen = new Paint();
        pen.setStyle(Paint.Style.STROKE);
        pen.setColor(border.getLineColor().getRGB());
        pen.setStrokeWidth(border.getLineWidth());

        int l = toPaperDots(rect.x);
        int t = toPaperDots(rect.y);
        int r = toPaperDots(rect.x + rect.width);
        int b = toPaperDots(rect.y + rect.height);

        canvas.drawRect(new Rect(l, t, r, b), pen);

    }

    private int getTypefaceStyleForFont(Font font) {
        if (font.isBold() && font.isItalic()) {
            return Typeface.BOLD_ITALIC;
        }

        if (font.isBold()) {
            return Typeface.BOLD;
        }

        if (font.isItalic()) {
            return Typeface.ITALIC;
        }

        return Typeface.NORMAL;

    }

    private Typeface getTypefaceForFont(Font font) {
        int typeFaceStyle = getTypefaceStyleForFont(font);
        final TypefaceCacheKey typeFaceKey = new TypefaceCacheKey(font.getFaceName(), typeFaceStyle);
        Typeface typeFace = typefaceCache.get(typeFaceKey);
        if (typeFace == null) {
            typeFace = Typeface.create(font.getFaceName(), typeFaceStyle);
            if (typeFace != null) {
                typefaceCache.put(typeFaceKey, typeFace);
            } else {
                typeFace = Typeface.SANS_SERIF;
            }
        }
        return typeFace;
    }

    private TextPaint getPaintForStyle(TextStyle style) {
        TextPaint textPaint = new TextPaint();
        Font styleFont = style.getFont();
        Typeface typeFace = getTypefaceForFont(styleFont);
        textPaint.setTypeface(typeFace);
        textPaint.setUnderlineText(styleFont.isUnderline());
        textPaint.setStrikeThruText(styleFont.isStrikeout());
        textPaint.setTextSize(Math.abs(styleFont.getHeight()));
        textPaint.setColor(style.getTextColor().getRGB());
        textPaint.setTextAlign(Paint.Align.LEFT);
        return textPaint;
    }

    private Layout.Alignment getLayoutAlignForHorizontalAlign(HorizontalAlign horAlign) {
        if (horAlign == null) {
            return Layout.Alignment.ALIGN_NORMAL;
        }

        switch (horAlign) {
            case CENTER:
                return Layout.Alignment.ALIGN_CENTER;
            case LEFT:
                return Layout.Alignment.ALIGN_NORMAL;
            case RIGHT:
                return Layout.Alignment.ALIGN_OPPOSITE;
        }
        throw new IllegalArgumentException("Unsupported horizontal align: " + horAlign.value());
    }


    public void drawText(String text, TextStyle style, Rectangle rect, Border border) throws BandsException {
        if (border != null) {
            drawRectangle(rect, border);
        }
        if (text.length() > 0) {
            Canvas canvas = getPaperCanvas();
            TextPaint textPaint = getPaintForStyle(style);
            Rectangle textRect = new Rectangle(toPaperDots(rect.x), toPaperDots(rect.y), toPaperDots(rect.width), toPaperDots(rect.height));
            StaticLayout layout = new StaticLayout(text, textPaint, textRect.width, getLayoutAlignForHorizontalAlign(style.getHorAlign()), 1.0f, 1.0f, false);

            int textHeight = layout.getHeight();
            int yOffset = 0;
            VerticalAlign verAlign = style.getVerAlign();
            if (verAlign != null) {
                switch (verAlign) {
                    case BOTTOM:
                        yOffset = textRect.height - textHeight;
                        break;
                    case CENTER:
                        yOffset = (textRect.height - textHeight) / 2;

                }
            }

            canvas.save();

            canvas.translate(textRect.x, textRect.y);

            canvas.clipRect(0, 0, textRect.width, textRect.height);


            Color bgColor = style.getBackColor();
            if (bgColor != null) {
                Paint brush = new Paint();
                brush.setStyle(Paint.Style.FILL);
                brush.setColor(bgColor.getRGB());
                canvas.drawRect(new Rect(0, 0, textRect.width, textRect.height), brush);
            }

            canvas.translate(0, yOffset);

            layout.draw(canvas);
            canvas.restore();


        }

    }


    @Override
    public void drawOval(Rectangle rect, Border border) throws BandsException {
        if (border != null && border.getLineColor() != null && border.getLineWidth() > 0) {
            Canvas canvas = getPaperCanvas();

            Paint pen = new Paint();
            pen.setStyle(Paint.Style.STROKE);
            pen.setColor(border.getLineColor().getRGB());
            pen.setStrokeWidth(border.getLineWidth());

            int l = toPaperDots(rect.x);
            int t = toPaperDots(rect.y);
            int r = toPaperDots(rect.x + rect.width);
            int b = toPaperDots(rect.y + rect.height);

            canvas.drawOval(new RectF(l, t, r, b), pen);
        }

    }

    public Bitmap getBitmap() {
        return paperImage;
    }

}