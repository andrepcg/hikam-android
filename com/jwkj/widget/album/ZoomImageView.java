package com.jwkj.widget.album;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import java.util.Observable;
import java.util.Observer;

public class ZoomImageView extends View implements Observer {
    private final AspectQuotient mAspectQuotient = new AspectQuotient();
    private Bitmap mBitmap;
    private final Paint mPaint = new Paint(2);
    private final Rect mRectDst = new Rect();
    private final Rect mRectSrc = new Rect();
    private ZoomState mState;
    private BasicZoomControl mZoomControl = new BasicZoomControl();
    private BasicZoomListener mZoomListener = new BasicZoomListener();

    private class AspectQuotient extends Observable {
        private float mAspectQuotient;

        private AspectQuotient() {
        }

        public float get() {
            return this.mAspectQuotient;
        }

        public void updateAspectQuotient(float viewWidth, float viewHeight, float contentWidth, float contentHeight) {
            float aspectQuotient = (contentWidth / contentHeight) / (viewWidth / viewHeight);
            if (aspectQuotient != this.mAspectQuotient) {
                this.mAspectQuotient = aspectQuotient;
                setChanged();
            }
        }
    }

    private class BasicZoomControl implements Observer {
        private static final float MAX_ZOOM = 14.0f;
        private static final float MIN_ZOOM = 1.0f;
        private AspectQuotient mAspectQuotient;
        private final ZoomState mState;

        private BasicZoomControl() {
            this.mState = new ZoomState();
        }

        public void setAspectQuotient(AspectQuotient aspectQuotient) {
            if (this.mAspectQuotient != null) {
                this.mAspectQuotient.deleteObserver(this);
            }
            this.mAspectQuotient = aspectQuotient;
            this.mAspectQuotient.addObserver(this);
        }

        public ZoomState getZoomState() {
            return this.mState;
        }

        public void zoom(float f, float x, float y) {
            float aspectQuotient = this.mAspectQuotient.get();
            float prevZoomX = this.mState.getZoomX(aspectQuotient);
            float prevZoomY = this.mState.getZoomY(aspectQuotient);
            this.mState.setZoom(this.mState.getZoom() * f);
            limitZoom();
            float newZoomX = this.mState.getZoomX(aspectQuotient);
            float newZoomY = this.mState.getZoomY(aspectQuotient);
            this.mState.setPanX(this.mState.getPanX() + ((x - 0.5f) * ((MIN_ZOOM / prevZoomX) - (MIN_ZOOM / newZoomX))));
            this.mState.setPanY(this.mState.getPanY() + ((y - 0.5f) * ((MIN_ZOOM / prevZoomY) - (MIN_ZOOM / newZoomY))));
            limitPan();
            this.mState.notifyObservers();
        }

        public void pan(float dx, float dy) {
            float aspectQuotient = this.mAspectQuotient.get();
            this.mState.setPanX(this.mState.getPanX() + (dx / this.mState.getZoomX(aspectQuotient)));
            this.mState.setPanY(this.mState.getPanY() + (dy / this.mState.getZoomY(aspectQuotient)));
            limitPan();
            this.mState.notifyObservers();
        }

        private float getMaxPanDelta(float zoom) {
            return Math.max(0.0f, 0.5f * ((zoom - MIN_ZOOM) / zoom));
        }

        private void limitZoom() {
            if (this.mState.getZoom() < MIN_ZOOM) {
                this.mState.setZoom(MIN_ZOOM);
            } else if (this.mState.getZoom() > MAX_ZOOM) {
                this.mState.setZoom(MAX_ZOOM);
            }
        }

        private void limitPan() {
            float aspectQuotient = this.mAspectQuotient.get();
            float zoomX = this.mState.getZoomX(aspectQuotient);
            float zoomY = this.mState.getZoomY(aspectQuotient);
            float panMinX = 0.5f - getMaxPanDelta(zoomX);
            float panMaxX = 0.5f + getMaxPanDelta(zoomX);
            float panMinY = 0.5f - getMaxPanDelta(zoomY);
            float panMaxY = 0.5f + getMaxPanDelta(zoomY);
            if (this.mState.getPanX() < panMinX) {
                this.mState.setPanX(panMinX);
            }
            if (this.mState.getPanX() > panMaxX) {
                this.mState.setPanX(panMaxX);
            }
            if (this.mState.getPanY() < panMinY) {
                this.mState.setPanY(panMinY);
            }
            if (this.mState.getPanY() > panMaxY) {
                this.mState.setPanY(panMaxY);
            }
        }

        public void update(Observable observable, Object data) {
            limitZoom();
            limitPan();
        }
    }

    private class BasicZoomListener implements OnTouchListener {
        private float mFirstX;
        private float mFirstY;
        private int mOldCounts;
        private float mSecondX;
        private float mSecondY;
        private BasicZoomControl mZoomControl;

        private BasicZoomListener() {
            this.mFirstX = -1.0f;
            this.mFirstY = -1.0f;
            this.mSecondX = -1.0f;
            this.mSecondY = -1.0f;
            this.mOldCounts = 0;
        }

        public void setZoomControl(BasicZoomControl control) {
            this.mZoomControl = control;
        }

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    if (ZoomImageView.this.mState.getZoom() > 1.0f) {
                        ZoomImageView.this.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    this.mOldCounts = 1;
                    this.mFirstX = event.getX();
                    this.mFirstY = event.getY();
                    break;
                case 1:
                    ZoomImageView.this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case 2:
                    if (ZoomImageView.this.mState.getZoom() > 1.0f) {
                        ZoomImageView.this.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    float fFirstX = event.getX();
                    float fFirstY = event.getY();
                    int nCounts = event.getPointerCount();
                    if (1 == nCounts) {
                        this.mOldCounts = 1;
                        this.mZoomControl.pan(-((fFirstX - this.mFirstX) / ((float) v.getWidth())), -((fFirstY - this.mFirstY) / ((float) v.getHeight())));
                    } else {
                        if (1 == this.mOldCounts) {
                            this.mSecondX = event.getX(event.getPointerId(nCounts - 1));
                            this.mSecondY = event.getY(event.getPointerId(nCounts - 1));
                            this.mOldCounts = nCounts;
                        } else {
                            float fSecondX = event.getX(event.getPointerId(nCounts - 1));
                            float fSecondY = event.getY(event.getPointerId(nCounts - 1));
                            this.mZoomControl.zoom((float) Math.pow(20.0d, (double) ((float) ((getLength(fFirstX, fFirstY, fSecondX, fSecondY) - getLength(this.mFirstX, this.mFirstY, this.mSecondX, this.mSecondY)) / ((double) v.getWidth())))), ((fFirstX + fSecondX) / 2.0f) / ((float) v.getWidth()), ((fFirstY + fSecondY) / 2.0f) / ((float) v.getHeight()));
                            this.mSecondX = fSecondX;
                            this.mSecondY = fSecondY;
                        }
                    }
                    this.mFirstX = fFirstX;
                    this.mFirstY = fFirstY;
                    break;
            }
            return true;
        }

        private double getLength(float x1, float y1, float x2, float y2) {
            return Math.sqrt(Math.pow((double) (x1 - x2), 2.0d) + Math.pow((double) (y1 - y2), 2.0d));
        }
    }

    private class ZoomState extends Observable {
        private float mPanX;
        private float mPanY;
        private float mZoom;

        private ZoomState() {
        }

        public float getPanX() {
            return this.mPanX;
        }

        public float getPanY() {
            return this.mPanY;
        }

        public float getZoom() {
            return this.mZoom;
        }

        public float getZoomX(float aspectQuotient) {
            return Math.min(this.mZoom, this.mZoom * aspectQuotient);
        }

        public float getZoomY(float aspectQuotient) {
            return Math.min(this.mZoom, this.mZoom / aspectQuotient);
        }

        public void setPanX(float panX) {
            if (panX != this.mPanX) {
                this.mPanX = panX;
                setChanged();
            }
        }

        public void setPanY(float panY) {
            if (panY != this.mPanY) {
                this.mPanY = panY;
                setChanged();
            }
        }

        public void setZoom(float zoom) {
            if (zoom != this.mZoom) {
                this.mZoom = zoom;
                setChanged();
            }
        }
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mZoomListener.setZoomControl(this.mZoomControl);
        setZoomState(this.mZoomControl.getZoomState());
        setOnTouchListener(this.mZoomListener);
        this.mZoomControl.setAspectQuotient(getAspectQuotient());
    }

    public void zoomImage(float f, float x, float y) {
        this.mZoomControl.zoom(f, x, y);
    }

    public void setImage(Bitmap bitmap) {
        this.mBitmap = bitmap;
        this.mAspectQuotient.updateAspectQuotient((float) getWidth(), (float) getHeight(), (float) this.mBitmap.getWidth(), (float) this.mBitmap.getHeight());
        this.mAspectQuotient.notifyObservers();
        invalidate();
    }

    private void setZoomState(ZoomState state) {
        if (this.mState != null) {
            this.mState.deleteObserver(this);
        }
        this.mState = state;
        this.mState.addObserver(this);
        invalidate();
    }

    private AspectQuotient getAspectQuotient() {
        return this.mAspectQuotient;
    }

    protected void onDraw(Canvas canvas) {
        if (this.mBitmap != null && this.mState != null) {
            Rect rect;
            Log.d("ZoomImageView", "OnDraw");
            float aspectQuotient = this.mAspectQuotient.get();
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int bitmapWidth = this.mBitmap.getWidth();
            int bitmapHeight = this.mBitmap.getHeight();
            Log.d("ZoomImageView", "viewWidth = " + viewWidth);
            Log.d("ZoomImageView", "viewHeight = " + viewHeight);
            Log.d("ZoomImageView", "bitmapWidth = " + bitmapWidth);
            Log.d("ZoomImageView", "bitmapHeight = " + bitmapHeight);
            float panX = this.mState.getPanX();
            float panY = this.mState.getPanY();
            float zoomX = (this.mState.getZoomX(aspectQuotient) * ((float) viewWidth)) / ((float) bitmapWidth);
            float zoomY = (this.mState.getZoomY(aspectQuotient) * ((float) viewHeight)) / ((float) bitmapHeight);
            this.mRectSrc.left = (int) ((((float) bitmapWidth) * panX) - (((float) viewWidth) / (2.0f * zoomX)));
            this.mRectSrc.top = (int) ((((float) bitmapHeight) * panY) - (((float) viewHeight) / (2.0f * zoomY)));
            this.mRectSrc.right = (int) (((float) this.mRectSrc.left) + (((float) viewWidth) / zoomX));
            this.mRectSrc.bottom = (int) (((float) this.mRectSrc.top) + (((float) viewHeight) / zoomY));
            this.mRectDst.left = 0;
            this.mRectDst.top = 0;
            this.mRectDst.right = getWidth();
            this.mRectDst.bottom = getHeight();
            if (this.mRectSrc.left < 0) {
                rect = this.mRectDst;
                rect.left = (int) (((float) rect.left) + (((float) (-this.mRectSrc.left)) * zoomX));
                this.mRectSrc.left = 0;
            }
            if (this.mRectSrc.right > bitmapWidth) {
                rect = this.mRectDst;
                rect.right = (int) (((float) rect.right) - (((float) (this.mRectSrc.right - bitmapWidth)) * zoomX));
                this.mRectSrc.right = bitmapWidth;
            }
            if (this.mRectSrc.top < 0) {
                rect = this.mRectDst;
                rect.top = (int) (((float) rect.top) + (((float) (-this.mRectSrc.top)) * zoomY));
                this.mRectSrc.top = 0;
            }
            if (this.mRectSrc.bottom > bitmapHeight) {
                rect = this.mRectDst;
                rect.bottom = (int) (((float) rect.bottom) - (((float) (this.mRectSrc.bottom - bitmapHeight)) * zoomY));
                this.mRectSrc.bottom = bitmapHeight;
            }
            this.mRectDst.left = 0;
            this.mRectDst.top = 0;
            this.mRectDst.right = viewWidth;
            this.mRectDst.bottom = viewHeight;
            Log.d("ZoomImageView", "mRectSrc.top" + this.mRectSrc.top);
            Log.d("ZoomImageView", "mRectSrc.bottom" + this.mRectSrc.bottom);
            Log.d("ZoomImageView", "mRectSrc.left" + this.mRectSrc.left);
            Log.d("ZoomImageView", "mRectSrc.right" + this.mRectSrc.right);
            Log.d("ZoomImageView", "mRectDst.top" + this.mRectDst.top);
            Log.d("ZoomImageView", "mRectDst.bottom" + this.mRectDst.bottom);
            Log.d("ZoomImageView", "mRectDst.left" + this.mRectDst.left);
            Log.d("ZoomImageView", "mRectDst.right" + this.mRectDst.right);
            canvas.drawBitmap(this.mBitmap, this.mRectSrc, this.mRectDst, this.mPaint);
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mAspectQuotient.updateAspectQuotient((float) (right - left), (float) (bottom - top), (float) this.mBitmap.getWidth(), (float) this.mBitmap.getHeight());
        this.mAspectQuotient.notifyObservers();
    }

    public void update(Observable observable, Object data) {
        invalidate();
    }
}
