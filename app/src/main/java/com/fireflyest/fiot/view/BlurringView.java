package com.fireflyest.fiot.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.fireflyest.fiot.R;

public class BlurringView extends View {
    public BlurringView(Context context) {
        super(context);
    }

    public BlurringView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        final int defaultBlurRadius = 11;
        final int defaultDownSampleFactor = 6;
        final int defaultOverlayColor = Color.parseColor("#50FFFFFF");

        initializeRenderScript(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlurringView);
        setBlurRadius(a.getInt(R.styleable.BlurringView_blurRadius, defaultBlurRadius));
        setDownsampleFactor(a.getInt(R.styleable.BlurringView_downSampleFactor,
                defaultDownSampleFactor));
        setOverlayColor(a.getColor(R.styleable.BlurringView_overlayColor, defaultOverlayColor));
        a.recycle();

    }

    private int mVagueHeight;

    /**
     * @param blurredView 屏幕跟布局
     * @param vagueHeight 距离底部的高度 dp  -1 全屏
     */
    public void setBlurredView(View blurredView, int vagueHeight) {
        mBlurredView = blurredView;
        mVagueHeight = vagueHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBlurredView != null) {
            if (prepare()) {
                // If the background of the blurred view is a color drawable, we use it to clear
                // the blurring canvas, which ensures that edges of the child views are blurred
                // as well; otherwise we clear the blurring canvas with a transparent color.
                if (mBlurredView.getBackground() != null && mBlurredView.getBackground() instanceof ColorDrawable) {
                    mBitmapToBlur.eraseColor(((ColorDrawable) mBlurredView.getBackground()).getColor());
                } else {
                    mBitmapToBlur.eraseColor(Color.TRANSPARENT);
                }

                mBlurredView.draw(mBlurringCanvas);
                if (mVagueHeight != -1) {
                    blur(dp2px(this.getContext(), mVagueHeight));
                } else
                    blur(mVagueHeight);

                canvas.save();
                canvas.translate(mBlurredView.getX() - getX(), mBlurredView.getY() - getY());
                canvas.scale(mDownsampleFactor, mDownsampleFactor);
                canvas.drawBitmap(mBlurredBitmap, 0, 0, null);
                canvas.restore();
            }
            canvas.drawColor(mOverlayColor);
        }
    }
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(1, dpVal, context.getResources().getDisplayMetrics());
    }
    public void setBlurRadius(int radius) {
        mBlurScript.setRadius(radius);
    }

    public void setDownsampleFactor(int factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Downsample factor must be greater than 0.");
        }

        if (mDownsampleFactor != factor) {
            mDownsampleFactor = factor;
            mDownsampleFactorChanged = true;
        }
    }

    public void setOverlayColor(int color) {
        mOverlayColor = color;
    }

    private void initializeRenderScript(Context context) {
        mRenderScript = RenderScript.create(context);
        mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
    }

    protected boolean prepare() {
        final int width = mBlurredView.getWidth();
        final int height = mBlurredView.getHeight();

        if (mBlurringCanvas == null || mDownsampleFactorChanged
                || mBlurredViewWidth != width || mBlurredViewHeight != height) {
            mDownsampleFactorChanged = false;

            mBlurredViewWidth = width;
            mBlurredViewHeight = height;

            int scaledWidth = width / mDownsampleFactor;
            int scaledHeight = height / mDownsampleFactor;

            // The following manipulation is to avoid some RenderScript artifacts at the edge.
            scaledWidth = scaledWidth - scaledWidth % 4 + 4;
            scaledHeight = scaledHeight - scaledHeight % 4 + 4;

            if (mBlurredBitmap == null
                    || mBlurredBitmap.getWidth() != scaledWidth
                    || mBlurredBitmap.getHeight() != scaledHeight) {
                mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight,
                        Bitmap.Config.ARGB_8888);
                if (mBitmapToBlur == null) {
                    return false;
                }
                mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight,
                        Bitmap.Config.ARGB_8888);

                if (mBlurredBitmap == null) {
                    return false;
                }
            }

            mBlurringCanvas = new Canvas(mBitmapToBlur);
            mBlurringCanvas.scale(1f / mDownsampleFactor, 1f / mDownsampleFactor);
            mBlurInput = Allocation.createFromBitmap(mRenderScript, mBitmapToBlur,
                    Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());
        }
        return true;
    }

    /**
     * 按长方形裁切图片
     *
     */
    private Bitmap imageCropWithRect(Bitmap bitmap, int imgHeight) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        /*int nw, nh, retX, retY;
        if (w > h) {
            nw = h / 2;
            nh = h;
            retX = (w - nw) / 2;
            retY = 0;
        } else {
            nw = w / 2;
            nh = w;
            retX = w / 4;
            retY = (h - w) / 2;
        }*/

        // 下面这句是关键
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, h - imgHeight, w, imgHeight, null, false);
        if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bmp;// Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
        // false);
    }

    /**
     * android.renderscript.RSIllegalArgumentException: Cannot update allocation from bitmap, sizes mismatch
     *
     */
    protected void blur(int h) {
        try {
            mBlurInput.copyFrom(mBitmapToBlur);
            mBlurScript.setInput(mBlurInput);
            mBlurScript.forEach(mBlurOutput);
            mBlurOutput.copyTo(mBlurredBitmap);
            if (h != -1) {
                mBlurredBitmap = imageCropWithRect(mBlurredBitmap, h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRenderScript != null) {
            mRenderScript.destroy();
        }
    }

    private int mDownsampleFactor;
    private int mOverlayColor;

    private View mBlurredView;
    private int mBlurredViewWidth, mBlurredViewHeight;

    private boolean mDownsampleFactorChanged;
    private Bitmap mBitmapToBlur, mBlurredBitmap;
    private Canvas mBlurringCanvas;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput, mBlurOutput;


}
