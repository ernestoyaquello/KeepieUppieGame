package com.ernestoyaquello.keepieuppie.views.game.framework.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class Painter {
    private final Paint paint;
    private final Rect srcRect;
    private final Rect dstRect;
    private Canvas canvas;

    public Painter() {
        this.paint = new Paint();
        this.srcRect = new Rect();
        this.dstRect = new Rect();
    }

    public void drawText(String text, float x, float y, Typeface typeface, float textSize, int textColor, Paint.Align align) {
        drawText(text, (int)x, (int)y, typeface, textSize, textColor, align);
    }

    public void drawText(String text, int x, int y, Typeface typeface, float textSize, int textColor, Paint.Align align) {
        paint.setTypeface(typeface);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(align);

        canvas.drawText(text, x, y, paint);
    }

    public void drawTextCenteredAndRotated(String text, int x, int y, Typeface typeface, float textSize, int textColor, float rotationAngle) {
        paint.setTypeface(typeface);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.CENTER);

        if (rotationAngle != 0) {
            canvas.save();
            canvas.rotate(rotationAngle, x, y - (textSize / 2));
            canvas.drawText(text, x, y, paint);
            canvas.restore();
        } else {
            canvas.drawText(text, x, y, paint);
        }
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        paint.setColor(color);
        dstRect.set(x, y, x + width, y + height);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(dstRect, paint);
    }

    public void fillRect(int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(canvas.getClipBounds(), paint);
    }

    public void drawImage(Bitmap bitmap, int x, int y) {
        drawImage(bitmap, x, y, bitmap.getWidth(), bitmap.getHeight(), 0);
    }

    public void drawImage(Bitmap bitmap, int x, int y, float rotationAngle) {
        drawImage(bitmap, x, y, bitmap.getWidth(), bitmap.getHeight(), rotationAngle);
    }

    public void drawImage(Bitmap bitmap, int x, int y, int width, int height, float rotationAngle) {
        boolean scale = false;
        if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
            srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            dstRect.set(x, y, x + width, y + height);

            scale = true;
        }

        if (rotationAngle != 0) {
            canvas.save();
            canvas.rotate(rotationAngle, x + (width / 2), y + (height / 2));

            if (scale) {
                canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
            } else {
                canvas.drawBitmap(bitmap, x, y, paint);
            }

            canvas.restore();
        } else {
            if (scale) {
                canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
            } else {
                canvas.drawBitmap(bitmap, x, y, paint);
            }
        }
    }

    public Paint getPaint() {
        return paint;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
}
