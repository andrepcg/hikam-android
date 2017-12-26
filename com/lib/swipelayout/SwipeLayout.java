package com.lib.swipelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import com.hikam.C0291R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SwipeLayout extends FrameLayout {
    private static final int DRAG_BOTTOM = 8;
    private static final int DRAG_LEFT = 1;
    private static final int DRAG_RIGHT = 2;
    private static final int DRAG_TOP = 4;
    public static final int EMPTY_LAYOUT = -1;
    private GestureDetector gestureDetector;
    private float mBottomEdgeSwipeOffset;
    private int mBottomIndex;
    private boolean mBottomSwipeEnabled;
    private Map<DragEdge, Integer> mBottomViewIdMap;
    private boolean mBottomViewIdsSet;
    private int mCurrentDirectionIndex;
    private DoubleClickListener mDoubleClickListener;
    private int mDragDistance;
    private List<DragEdge> mDragEdges;
    private ViewDragHelper mDragHelper;
    private Callback mDragHelperCallback;
    private int mEventCounter;
    private float mLeftEdgeSwipeOffset;
    private int mLeftIndex;
    private boolean mLeftSwipeEnabled;
    private List<OnLayout> mOnLayoutListeners;
    private Map<View, ArrayList<OnRevealListener>> mRevealListeners;
    private float mRightEdgeSwipeOffset;
    private int mRightIndex;
    private boolean mRightSwipeEnabled;
    private Map<View, Boolean> mShowEntirely;
    private ShowMode mShowMode;
    private List<SwipeDenier> mSwipeDeniers;
    private boolean mSwipeEnabled;
    private List<SwipeListener> mSwipeListeners;
    private float mTopEdgeSwipeOffset;
    private int mTopIndex;
    private boolean mTopSwipeEnabled;
    private boolean mTouchConsumedByChild;
    private int mTouchSlop;
    private float sX;
    private float sY;

    public interface DoubleClickListener {
        void onDoubleClick(SwipeLayout swipeLayout, boolean z);
    }

    public enum DragEdge {
        Left,
        Right,
        Top,
        Bottom
    }

    public interface OnLayout {
        void onLayout(SwipeLayout swipeLayout);
    }

    public interface OnRevealListener {
        void onReveal(View view, DragEdge dragEdge, float f, int i);
    }

    public enum ShowMode {
        LayDown,
        PullOut
    }

    public enum Status {
        Middle,
        Open,
        Close
    }

    public interface SwipeDenier {
        boolean shouldDenySwipe(MotionEvent motionEvent);
    }

    class SwipeDetector extends SimpleOnGestureListener {
        SwipeDetector() {
        }

        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            if (SwipeLayout.this.mDoubleClickListener == null) {
                SwipeLayout.this.performAdapterViewItemClick(e);
            }
            return true;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (SwipeLayout.this.mDoubleClickListener != null) {
                SwipeLayout.this.performAdapterViewItemClick(e);
            }
            return true;
        }

        public void onLongPress(MotionEvent e) {
            SwipeLayout.this.performLongClick();
        }

        public boolean onDoubleTap(MotionEvent e) {
            if (SwipeLayout.this.mDoubleClickListener != null) {
                View target;
                View bottom = (ViewGroup) SwipeLayout.this.getBottomViews().get(SwipeLayout.this.mCurrentDirectionIndex);
                View surface = SwipeLayout.this.getSurfaceView();
                if (e.getX() <= ((float) bottom.getLeft()) || e.getX() >= ((float) bottom.getRight()) || e.getY() <= ((float) bottom.getTop()) || e.getY() >= ((float) bottom.getBottom())) {
                    target = surface;
                } else {
                    target = bottom;
                }
                SwipeLayout.this.mDoubleClickListener.onDoubleClick(SwipeLayout.this, target == surface);
            }
            return true;
        }
    }

    public interface SwipeListener {
        void onClose(SwipeLayout swipeLayout);

        void onHandRelease(SwipeLayout swipeLayout, float f, float f2);

        void onOpen(SwipeLayout swipeLayout);

        void onStartClose(SwipeLayout swipeLayout);

        void onStartOpen(SwipeLayout swipeLayout);

        void onUpdate(SwipeLayout swipeLayout, int i, int i2);
    }

    class C11451 extends Callback {
        C11451() {
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == SwipeLayout.this.getSurfaceView()) {
                switch ((DragEdge) SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex)) {
                    case Top:
                    case Bottom:
                        return SwipeLayout.this.getPaddingLeft();
                    case Left:
                        if (left < SwipeLayout.this.getPaddingLeft()) {
                            return SwipeLayout.this.getPaddingLeft();
                        }
                        if (left > SwipeLayout.this.getPaddingLeft() + SwipeLayout.this.mDragDistance) {
                            return SwipeLayout.this.getPaddingLeft() + SwipeLayout.this.mDragDistance;
                        }
                        return left;
                    case Right:
                        if (left > SwipeLayout.this.getPaddingLeft()) {
                            return SwipeLayout.this.getPaddingLeft();
                        }
                        if (left < SwipeLayout.this.getPaddingLeft() - SwipeLayout.this.mDragDistance) {
                            return SwipeLayout.this.getPaddingLeft() - SwipeLayout.this.mDragDistance;
                        }
                        return left;
                    default:
                        return left;
                }
            } else if (SwipeLayout.this.getBottomViews().get(SwipeLayout.this.mCurrentDirectionIndex) != child) {
                return left;
            } else {
                switch ((DragEdge) SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex)) {
                    case Top:
                    case Bottom:
                        return SwipeLayout.this.getPaddingLeft();
                    case Left:
                        if (SwipeLayout.this.mShowMode != ShowMode.PullOut || left <= SwipeLayout.this.getPaddingLeft()) {
                            return left;
                        }
                        return SwipeLayout.this.getPaddingLeft();
                    case Right:
                        if (SwipeLayout.this.mShowMode != ShowMode.PullOut || left >= SwipeLayout.this.getMeasuredWidth() - SwipeLayout.this.mDragDistance) {
                            return left;
                        }
                        return SwipeLayout.this.getMeasuredWidth() - SwipeLayout.this.mDragDistance;
                    default:
                        return left;
                }
            }
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            if (child == SwipeLayout.this.getSurfaceView()) {
                switch ((DragEdge) SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex)) {
                    case Top:
                        if (top < SwipeLayout.this.getPaddingTop()) {
                            return SwipeLayout.this.getPaddingTop();
                        }
                        if (top > SwipeLayout.this.getPaddingTop() + SwipeLayout.this.mDragDistance) {
                            return SwipeLayout.this.getPaddingTop() + SwipeLayout.this.mDragDistance;
                        }
                        return top;
                    case Bottom:
                        if (top < SwipeLayout.this.getPaddingTop() - SwipeLayout.this.mDragDistance) {
                            return SwipeLayout.this.getPaddingTop() - SwipeLayout.this.mDragDistance;
                        }
                        if (top > SwipeLayout.this.getPaddingTop()) {
                            return SwipeLayout.this.getPaddingTop();
                        }
                        return top;
                    case Left:
                    case Right:
                        return SwipeLayout.this.getPaddingTop();
                    default:
                        return top;
                }
            }
            switch ((DragEdge) SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex)) {
                case Top:
                    if (SwipeLayout.this.mShowMode == ShowMode.PullOut) {
                        if (top > SwipeLayout.this.getPaddingTop()) {
                            return SwipeLayout.this.getPaddingTop();
                        }
                        return top;
                    } else if (SwipeLayout.this.getSurfaceView().getTop() + dy < SwipeLayout.this.getPaddingTop()) {
                        return SwipeLayout.this.getPaddingTop();
                    } else {
                        if (SwipeLayout.this.getSurfaceView().getTop() + dy > SwipeLayout.this.getPaddingTop() + SwipeLayout.this.mDragDistance) {
                            return SwipeLayout.this.getPaddingTop() + SwipeLayout.this.mDragDistance;
                        }
                        return top;
                    }
                case Bottom:
                    if (SwipeLayout.this.mShowMode == ShowMode.PullOut) {
                        if (top < SwipeLayout.this.getMeasuredHeight() - SwipeLayout.this.mDragDistance) {
                            return SwipeLayout.this.getMeasuredHeight() - SwipeLayout.this.mDragDistance;
                        }
                        return top;
                    } else if (SwipeLayout.this.getSurfaceView().getTop() + dy >= SwipeLayout.this.getPaddingTop()) {
                        return SwipeLayout.this.getPaddingTop();
                    } else {
                        if (SwipeLayout.this.getSurfaceView().getTop() + dy <= SwipeLayout.this.getPaddingTop() - SwipeLayout.this.mDragDistance) {
                            return SwipeLayout.this.getPaddingTop() - SwipeLayout.this.mDragDistance;
                        }
                        return top;
                    }
                case Left:
                case Right:
                    return SwipeLayout.this.getPaddingTop();
                default:
                    return top;
            }
        }

        public boolean tryCaptureView(View child, int pointerId) {
            return child == SwipeLayout.this.getSurfaceView() || SwipeLayout.this.getBottomViews().contains(child);
        }

        public int getViewHorizontalDragRange(View child) {
            return SwipeLayout.this.mDragDistance;
        }

        public int getViewVerticalDragRange(View child) {
            return SwipeLayout.this.mDragDistance;
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            for (SwipeListener l : SwipeLayout.this.mSwipeListeners) {
                l.onHandRelease(SwipeLayout.this, xvel, yvel);
            }
            if (releasedChild == SwipeLayout.this.getSurfaceView()) {
                SwipeLayout.this.processSurfaceRelease(xvel, yvel);
            } else if (SwipeLayout.this.getBottomViews().contains(releasedChild)) {
                if (SwipeLayout.this.getShowMode() == ShowMode.PullOut) {
                    SwipeLayout.this.processBottomPullOutRelease(xvel, yvel);
                } else if (SwipeLayout.this.getShowMode() == ShowMode.LayDown) {
                    SwipeLayout.this.processBottomLayDownMode(xvel, yvel);
                }
            }
            SwipeLayout.this.invalidate();
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            int evLeft = SwipeLayout.this.getSurfaceView().getLeft();
            int evRight = SwipeLayout.this.getSurfaceView().getRight();
            int evTop = SwipeLayout.this.getSurfaceView().getTop();
            int evBottom = SwipeLayout.this.getSurfaceView().getBottom();
            if (changedView == SwipeLayout.this.getSurfaceView()) {
                if (SwipeLayout.this.mShowMode == ShowMode.PullOut) {
                    if (SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex) == DragEdge.Left || SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex) == DragEdge.Right) {
                        ((ViewGroup) SwipeLayout.this.getBottomViews().get(SwipeLayout.this.mCurrentDirectionIndex)).offsetLeftAndRight(dx);
                    } else {
                        ((ViewGroup) SwipeLayout.this.getBottomViews().get(SwipeLayout.this.mCurrentDirectionIndex)).offsetTopAndBottom(dy);
                    }
                }
            } else if (SwipeLayout.this.getBottomViews().contains(changedView)) {
                if (SwipeLayout.this.mShowMode == ShowMode.PullOut) {
                    SwipeLayout.this.getSurfaceView().offsetLeftAndRight(dx);
                    SwipeLayout.this.getSurfaceView().offsetTopAndBottom(dy);
                } else {
                    Rect rect = SwipeLayout.this.computeBottomLayDown((DragEdge) SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex));
                    ((ViewGroup) SwipeLayout.this.getBottomViews().get(SwipeLayout.this.mCurrentDirectionIndex)).layout(rect.left, rect.top, rect.right, rect.bottom);
                    int newLeft = SwipeLayout.this.getSurfaceView().getLeft() + dx;
                    int newTop = SwipeLayout.this.getSurfaceView().getTop() + dy;
                    if (SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex) == DragEdge.Left && newLeft < SwipeLayout.this.getPaddingLeft()) {
                        newLeft = SwipeLayout.this.getPaddingLeft();
                    } else if (SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex) == DragEdge.Right && newLeft > SwipeLayout.this.getPaddingLeft()) {
                        newLeft = SwipeLayout.this.getPaddingLeft();
                    } else if (SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex) == DragEdge.Top && newTop < SwipeLayout.this.getPaddingTop()) {
                        newTop = SwipeLayout.this.getPaddingTop();
                    } else if (SwipeLayout.this.mDragEdges.get(SwipeLayout.this.mCurrentDirectionIndex) == DragEdge.Bottom && newTop > SwipeLayout.this.getPaddingTop()) {
                        newTop = SwipeLayout.this.getPaddingTop();
                    }
                    SwipeLayout.this.getSurfaceView().layout(newLeft, newTop, SwipeLayout.this.getMeasuredWidth() + newLeft, SwipeLayout.this.getMeasuredHeight() + newTop);
                }
            }
            SwipeLayout.this.dispatchRevealEvent(evLeft, evTop, evRight, evBottom);
            SwipeLayout.this.dispatchSwipeEvent(evLeft, evTop, dx, dy);
            SwipeLayout.this.invalidate();
        }
    }

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCurrentDirectionIndex = 0;
        this.mDragDistance = 0;
        this.mBottomViewIdMap = new HashMap();
        this.mBottomViewIdsSet = false;
        this.mSwipeListeners = new ArrayList();
        this.mSwipeDeniers = new ArrayList();
        this.mRevealListeners = new HashMap();
        this.mShowEntirely = new HashMap();
        this.mSwipeEnabled = true;
        this.mLeftSwipeEnabled = true;
        this.mRightSwipeEnabled = true;
        this.mTopSwipeEnabled = true;
        this.mBottomSwipeEnabled = true;
        this.mDragHelperCallback = new C11451();
        this.mEventCounter = 0;
        this.mTouchConsumedByChild = false;
        this.sX = -1.0f;
        this.sY = -1.0f;
        this.gestureDetector = new GestureDetector(getContext(), new SwipeDetector());
        this.mDragHelper = ViewDragHelper.create(this, this.mDragHelperCallback);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray a = context.obtainStyledAttributes(attrs, C0291R.styleable.SwipeLayout);
        int dragEdgeChoices = a.getInt(1, 2);
        this.mLeftEdgeSwipeOffset = a.getDimension(2, 0.0f);
        this.mRightEdgeSwipeOffset = a.getDimension(3, 0.0f);
        this.mTopEdgeSwipeOffset = a.getDimension(5, 0.0f);
        this.mBottomEdgeSwipeOffset = a.getDimension(0, 0.0f);
        this.mDragEdges = new ArrayList();
        if ((dragEdgeChoices & 1) == 1) {
            this.mDragEdges.add(DragEdge.Left);
        }
        if ((dragEdgeChoices & 2) == 2) {
            this.mDragEdges.add(DragEdge.Right);
        }
        if ((dragEdgeChoices & 4) == 4) {
            this.mDragEdges.add(DragEdge.Top);
        }
        if ((dragEdgeChoices & 8) == 8) {
            this.mDragEdges.add(DragEdge.Bottom);
        }
        populateIndexes();
        this.mShowMode = ShowMode.values()[a.getInt(4, ShowMode.PullOut.ordinal())];
        a.recycle();
    }

    public void addSwipeListener(SwipeListener l) {
        this.mSwipeListeners.add(l);
    }

    public void removeSwipeListener(SwipeListener l) {
        this.mSwipeListeners.remove(l);
    }

    public void addSwipeDenier(SwipeDenier denier) {
        this.mSwipeDeniers.add(denier);
    }

    public void removeSwipeDenier(SwipeDenier denier) {
        this.mSwipeDeniers.remove(denier);
    }

    public void removeAllSwipeDeniers() {
        this.mSwipeDeniers.clear();
    }

    public void addRevealListener(int childId, OnRevealListener l) {
        View child = findViewById(childId);
        if (child == null) {
            throw new IllegalArgumentException("Child does not belong to SwipeListener.");
        }
        if (!this.mShowEntirely.containsKey(child)) {
            this.mShowEntirely.put(child, Boolean.valueOf(false));
        }
        if (this.mRevealListeners.get(child) == null) {
            this.mRevealListeners.put(child, new ArrayList());
        }
        ((ArrayList) this.mRevealListeners.get(child)).add(l);
    }

    public void addRevealListener(int[] childIds, OnRevealListener l) {
        for (int i : childIds) {
            addRevealListener(i, l);
        }
    }

    public void removeRevealListener(int childId, OnRevealListener l) {
        View child = findViewById(childId);
        if (child != null) {
            this.mShowEntirely.remove(child);
            if (this.mRevealListeners.containsKey(child)) {
                ((ArrayList) this.mRevealListeners.get(child)).remove(l);
            }
        }
    }

    public void removeAllRevealListeners(int childId) {
        View child = findViewById(childId);
        if (child != null) {
            this.mRevealListeners.remove(child);
            this.mShowEntirely.remove(child);
        }
    }

    protected boolean isViewTotallyFirstShowed(View child, Rect relativePosition, DragEdge edge, int surfaceLeft, int surfaceTop, int surfaceRight, int surfaceBottom) {
        if (((Boolean) this.mShowEntirely.get(child)).booleanValue()) {
            return false;
        }
        int childLeft = relativePosition.left;
        int childRight = relativePosition.right;
        int childTop = relativePosition.top;
        int childBottom = relativePosition.bottom;
        if (getShowMode() == ShowMode.LayDown) {
            if ((edge != DragEdge.Right || surfaceRight > childLeft) && ((edge != DragEdge.Left || surfaceLeft < childRight) && ((edge != DragEdge.Top || surfaceTop < childBottom) && (edge != DragEdge.Bottom || surfaceBottom > childTop)))) {
                return false;
            }
            return true;
        } else if (getShowMode() != ShowMode.PullOut) {
            return false;
        } else {
            if ((edge != DragEdge.Right || childRight > getWidth()) && ((edge != DragEdge.Left || childLeft < getPaddingLeft()) && ((edge != DragEdge.Top || childTop < getPaddingTop()) && (edge != DragEdge.Bottom || childBottom > getHeight())))) {
                return false;
            }
            return true;
        }
    }

    protected boolean isViewShowing(View child, Rect relativePosition, DragEdge availableEdge, int surfaceLeft, int surfaceTop, int surfaceRight, int surfaceBottom) {
        int childLeft = relativePosition.left;
        int childRight = relativePosition.right;
        int childTop = relativePosition.top;
        int childBottom = relativePosition.bottom;
        if (getShowMode() != ShowMode.LayDown) {
            if (getShowMode() == ShowMode.PullOut) {
                switch (availableEdge) {
                    case Top:
                        if (childTop < getPaddingTop() && childBottom >= getPaddingTop()) {
                            return true;
                        }
                    case Bottom:
                        if (childTop < getHeight() && childTop >= getPaddingTop()) {
                            return true;
                        }
                    case Left:
                        if (childRight >= getPaddingLeft() && childLeft < getPaddingLeft()) {
                            return true;
                        }
                    case Right:
                        if (childLeft <= getWidth() && childRight > getWidth()) {
                            return true;
                        }
                    default:
                        break;
                }
            }
        }
        switch (availableEdge) {
            case Top:
                if (surfaceTop >= childTop && surfaceTop < childBottom) {
                    return true;
                }
            case Bottom:
                if (surfaceBottom > childTop && surfaceBottom <= childBottom) {
                    return true;
                }
            case Left:
                if (surfaceLeft < childRight && surfaceLeft >= childLeft) {
                    return true;
                }
            case Right:
                if (surfaceRight > childLeft && surfaceRight <= childRight) {
                    return true;
                }
        }
        return false;
    }

    protected Rect getRelativePosition(View child) {
        View t = child;
        Rect r = new Rect(t.getLeft(), t.getTop(), 0, 0);
        while (t.getParent() != null && t != getRootView()) {
            t = (View) t.getParent();
            if (t == this) {
                break;
            }
            r.left += t.getLeft();
            r.top += t.getTop();
        }
        r.right = r.left + child.getMeasuredWidth();
        r.bottom = r.top + child.getMeasuredHeight();
        return r;
    }

    protected void dispatchSwipeEvent(int surfaceLeft, int surfaceTop, int dx, int dy) {
        DragEdge edge = getDragEdge();
        boolean open = true;
        if (edge == DragEdge.Left) {
            if (dx < 0) {
                open = false;
            }
        } else if (edge == DragEdge.Right) {
            if (dx > 0) {
                open = false;
            }
        } else if (edge == DragEdge.Top) {
            if (dy < 0) {
                open = false;
            }
        } else if (edge == DragEdge.Bottom && dy > 0) {
            open = false;
        }
        dispatchSwipeEvent(surfaceLeft, surfaceTop, open);
    }

    protected void dispatchSwipeEvent(int surfaceLeft, int surfaceTop, boolean open) {
        safeBottomView();
        Status status = getOpenStatus();
        if (!this.mSwipeListeners.isEmpty()) {
            this.mEventCounter++;
            for (SwipeListener l : this.mSwipeListeners) {
                if (this.mEventCounter == 1) {
                    if (open) {
                        l.onStartOpen(this);
                    } else {
                        l.onStartClose(this);
                    }
                }
                l.onUpdate(this, surfaceLeft - getPaddingLeft(), surfaceTop - getPaddingTop());
            }
            if (status == Status.Close) {
                for (SwipeListener l2 : this.mSwipeListeners) {
                    l2.onClose(this);
                }
                this.mEventCounter = 0;
            }
            if (status == Status.Open) {
                ((ViewGroup) getBottomViews().get(this.mCurrentDirectionIndex)).setEnabled(true);
                for (SwipeListener l22 : this.mSwipeListeners) {
                    l22.onOpen(this);
                }
                this.mEventCounter = 0;
            }
        }
    }

    private void safeBottomView() {
        Status status = getOpenStatus();
        List<ViewGroup> bottoms = getBottomViews();
        if (status == Status.Close) {
            for (ViewGroup bottom : bottoms) {
                if (bottom.getVisibility() != 4) {
                    bottom.setVisibility(4);
                }
            }
        } else if (((ViewGroup) bottoms.get(this.mCurrentDirectionIndex)).getVisibility() != 0) {
            ((ViewGroup) bottoms.get(this.mCurrentDirectionIndex)).setVisibility(0);
        }
    }

    protected void dispatchRevealEvent(int surfaceLeft, int surfaceTop, int surfaceRight, int surfaceBottom) {
        if (!this.mRevealListeners.isEmpty()) {
            for (Entry<View, ArrayList<OnRevealListener>> entry : this.mRevealListeners.entrySet()) {
                Iterator it;
                View child = (View) entry.getKey();
                Rect rect = getRelativePosition(child);
                if (isViewShowing(child, rect, (DragEdge) this.mDragEdges.get(this.mCurrentDirectionIndex), surfaceLeft, surfaceTop, surfaceRight, surfaceBottom)) {
                    this.mShowEntirely.put(child, Boolean.valueOf(false));
                    int distance = 0;
                    float fraction = 0.0f;
                    if (getShowMode() != ShowMode.LayDown) {
                        if (getShowMode() == ShowMode.PullOut) {
                            switch ((DragEdge) this.mDragEdges.get(this.mCurrentDirectionIndex)) {
                                case Top:
                                    distance = rect.bottom - getPaddingTop();
                                    fraction = ((float) distance) / ((float) child.getHeight());
                                    break;
                                case Bottom:
                                    distance = rect.top - getHeight();
                                    fraction = ((float) distance) / ((float) child.getHeight());
                                    break;
                                case Left:
                                    distance = rect.right - getPaddingLeft();
                                    fraction = ((float) distance) / ((float) child.getWidth());
                                    break;
                                case Right:
                                    distance = rect.left - getWidth();
                                    fraction = ((float) distance) / ((float) child.getWidth());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    switch ((DragEdge) this.mDragEdges.get(this.mCurrentDirectionIndex)) {
                        case Top:
                            distance = rect.top - surfaceTop;
                            fraction = ((float) distance) / ((float) child.getHeight());
                            break;
                        case Bottom:
                            distance = rect.bottom - surfaceBottom;
                            fraction = ((float) distance) / ((float) child.getHeight());
                            break;
                        case Left:
                            distance = rect.left - surfaceLeft;
                            fraction = ((float) distance) / ((float) child.getWidth());
                            break;
                        case Right:
                            distance = rect.right - surfaceRight;
                            fraction = ((float) distance) / ((float) child.getWidth());
                            break;
                    }
                    it = ((ArrayList) entry.getValue()).iterator();
                    while (it.hasNext()) {
                        ((OnRevealListener) it.next()).onReveal(child, (DragEdge) this.mDragEdges.get(this.mCurrentDirectionIndex), Math.abs(fraction), distance);
                        if (Math.abs(fraction) == 1.0f) {
                            this.mShowEntirely.put(child, Boolean.valueOf(true));
                        }
                    }
                }
                if (isViewTotallyFirstShowed(child, rect, (DragEdge) this.mDragEdges.get(this.mCurrentDirectionIndex), surfaceLeft, surfaceTop, surfaceRight, surfaceBottom)) {
                    this.mShowEntirely.put(child, Boolean.valueOf(true));
                    it = ((ArrayList) entry.getValue()).iterator();
                    while (it.hasNext()) {
                        OnRevealListener l = (OnRevealListener) it.next();
                        if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left || this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
                            l.onReveal(child, (DragEdge) this.mDragEdges.get(this.mCurrentDirectionIndex), 1.0f, child.getWidth());
                        } else {
                            l.onReveal(child, (DragEdge) this.mDragEdges.get(this.mCurrentDirectionIndex), 1.0f, child.getHeight());
                        }
                    }
                }
            }
        }
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void addOnLayoutListener(OnLayout l) {
        if (this.mOnLayoutListeners == null) {
            this.mOnLayoutListeners = new ArrayList();
        }
        this.mOnLayoutListeners.add(l);
    }

    public void removeOnLayoutListener(OnLayout l) {
        if (this.mOnLayoutListeners != null) {
            this.mOnLayoutListeners.remove(l);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount != this.mDragEdges.size() + 1) {
            throw new IllegalStateException("You need to have one surface view plus one view for each of your drag edges");
        }
        int i = 0;
        while (i < childCount) {
            if (getChildAt(i) instanceof ViewGroup) {
                i++;
            } else {
                throw new IllegalArgumentException("All the children in SwipeLayout must be an instance of ViewGroup");
            }
        }
        if (this.mShowMode == ShowMode.PullOut) {
            layoutPullOut();
        } else if (this.mShowMode == ShowMode.LayDown) {
            layoutLayDown();
        }
        safeBottomView();
        if (this.mOnLayoutListeners != null) {
            for (i = 0; i < this.mOnLayoutListeners.size(); i++) {
                ((OnLayout) this.mOnLayoutListeners.get(i)).onLayout(this);
            }
        }
    }

    void layoutPullOut() {
        Rect rect = computeSurfaceLayoutArea(false);
        getSurfaceView().layout(rect.left, rect.top, rect.right, rect.bottom);
        rect = computeBottomLayoutAreaViaSurface(ShowMode.PullOut, rect);
        ((ViewGroup) getBottomViews().get(this.mCurrentDirectionIndex)).layout(rect.left, rect.top, rect.right, rect.bottom);
        bringChildToFront(getSurfaceView());
    }

    void layoutLayDown() {
        Rect rect = computeSurfaceLayoutArea(false);
        getSurfaceView().layout(rect.left, rect.top, rect.right, rect.bottom);
        rect = computeBottomLayoutAreaViaSurface(ShowMode.LayDown, rect);
        ((ViewGroup) getBottomViews().get(this.mCurrentDirectionIndex)).layout(rect.left, rect.top, rect.right, rect.bottom);
        bringChildToFront(getSurfaceView());
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left || this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
            this.mDragDistance = ((ViewGroup) getBottomViews().get(this.mCurrentDirectionIndex)).getMeasuredWidth() - dp2px(getCurrentOffset());
        } else {
            this.mDragDistance = ((ViewGroup) getBottomViews().get(this.mCurrentDirectionIndex)).getMeasuredHeight() - dp2px(getCurrentOffset());
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean z = true;
        if (!isEnabled() || !isEnabledInAdapterView()) {
            return true;
        }
        if (!isSwipeEnabled()) {
            return false;
        }
        for (SwipeDenier denier : this.mSwipeDeniers) {
            if (denier != null && denier.shouldDenySwipe(ev)) {
                return false;
            }
        }
        switch (ev.getActionMasked()) {
            case 0:
                Status status = getOpenStatus();
                if (status != Status.Close) {
                    if (status == Status.Open) {
                        if (childNeedHandleTouchEvent((ViewGroup) getBottomViews().get(this.mCurrentDirectionIndex), ev) == null) {
                            z = false;
                        }
                        this.mTouchConsumedByChild = z;
                        break;
                    }
                }
                this.mTouchConsumedByChild = childNeedHandleTouchEvent(getSurfaceView(), ev) != null;
                break;
                break;
            case 1:
            case 3:
                this.mTouchConsumedByChild = false;
                break;
        }
        if (this.mTouchConsumedByChild) {
            return false;
        }
        return this.mDragHelper.shouldInterceptTouchEvent(ev);
    }

    private View childNeedHandleTouchEvent(ViewGroup v, MotionEvent event) {
        if (v == null) {
            return null;
        }
        if (v.onTouchEvent(event)) {
            return v;
        }
        for (int i = v.getChildCount() - 1; i >= 0; i--) {
            View child = v.getChildAt(i);
            if (child instanceof ViewGroup) {
                View grandChild = childNeedHandleTouchEvent((ViewGroup) child, event);
                if (grandChild != null) {
                    return grandChild;
                }
            } else if (childNeedHandleTouchEvent(v.getChildAt(i), event)) {
                return v.getChildAt(i);
            }
        }
        return null;
    }

    private boolean childNeedHandleTouchEvent(View v, MotionEvent event) {
        if (v == null) {
            return false;
        }
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);
        int left = loc[0];
        int top = loc[1];
        if (event.getRawX() <= ((float) left) || event.getRawX() >= ((float) (v.getWidth() + left)) || event.getRawY() <= ((float) top) || event.getRawY() >= ((float) (v.getHeight() + top))) {
            return false;
        }
        return v.onTouchEvent(event);
    }

    private boolean shouldAllowSwipe() {
        if (this.mCurrentDirectionIndex == this.mLeftIndex && !this.mLeftSwipeEnabled) {
            return false;
        }
        if (this.mCurrentDirectionIndex == this.mRightIndex && !this.mRightSwipeEnabled) {
            return false;
        }
        if (this.mCurrentDirectionIndex == this.mTopIndex && !this.mTopSwipeEnabled) {
            return false;
        }
        if (this.mCurrentDirectionIndex != this.mBottomIndex || this.mBottomSwipeEnabled) {
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabledInAdapterView() || !isEnabled()) {
            return true;
        }
        if (!isSwipeEnabled()) {
            return super.onTouchEvent(event);
        }
        int action = event.getActionMasked();
        ViewParent parent = getParent();
        this.gestureDetector.onTouchEvent(event);
        Status status = getOpenStatus();
        ViewGroup touching = null;
        if (status == Status.Close) {
            touching = getSurfaceView();
        } else if (status == Status.Open) {
            touching = (ViewGroup) getBottomViews().get(this.mCurrentDirectionIndex);
        }
        switch (action) {
            case 0:
                this.mDragHelper.processTouchEvent(event);
                parent.requestDisallowInterceptTouchEvent(true);
                this.sX = event.getRawX();
                this.sY = event.getRawY();
                if (touching != null) {
                    touching.setPressed(true);
                }
                return true;
            case 1:
            case 3:
                this.sX = -1.0f;
                this.sY = -1.0f;
                if (touching != null) {
                    touching.setPressed(false);
                    break;
                }
                break;
            case 2:
                float distanceX = event.getRawX() - this.sX;
                float distanceY = event.getRawY() - this.sY;
                float angle = (float) Math.toDegrees(Math.atan((double) Math.abs(distanceY / distanceX)));
                if (getOpenStatus() == Status.Close) {
                    int lastCurrentDirectionIndex = this.mCurrentDirectionIndex;
                    if (angle < 45.0f) {
                        if (this.mLeftIndex != -1 && distanceX > 0.0f && isLeftSwipeEnabled()) {
                            this.mCurrentDirectionIndex = this.mLeftIndex;
                        } else if (this.mRightIndex != -1 && distanceX < 0.0f && isRightSwipeEnabled()) {
                            this.mCurrentDirectionIndex = this.mRightIndex;
                        }
                    } else if (this.mTopIndex != -1 && distanceY > 0.0f && isTopSwipeEnabled()) {
                        this.mCurrentDirectionIndex = this.mTopIndex;
                    } else if (this.mBottomIndex != -1 && distanceY < 0.0f && isBottomSwipeEnabled()) {
                        this.mCurrentDirectionIndex = this.mBottomIndex;
                    }
                    if (lastCurrentDirectionIndex != this.mCurrentDirectionIndex) {
                        updateBottomViews();
                    }
                }
                if (shouldAllowSwipe()) {
                    boolean suitable;
                    boolean doNothing = false;
                    if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
                        suitable = (status == Status.Open && distanceX > ((float) this.mTouchSlop)) || (status == Status.Close && distanceX < ((float) (-this.mTouchSlop)));
                        suitable = suitable || status == Status.Middle;
                        if (angle > 30.0f || !suitable) {
                            doNothing = true;
                        }
                    }
                    if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left) {
                        suitable = (status == Status.Open && distanceX < ((float) (-this.mTouchSlop))) || (status == Status.Close && distanceX > ((float) this.mTouchSlop));
                        suitable = suitable || status == Status.Middle;
                        if (angle > 30.0f || !suitable) {
                            doNothing = true;
                        }
                    }
                    if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Top) {
                        suitable = (status == Status.Open && distanceY < ((float) (-this.mTouchSlop))) || (status == Status.Close && distanceY > ((float) this.mTouchSlop));
                        suitable = suitable || status == Status.Middle;
                        if (angle < 60.0f || !suitable) {
                            doNothing = true;
                        }
                    }
                    if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Bottom) {
                        suitable = (status == Status.Open && distanceY > ((float) this.mTouchSlop)) || (status == Status.Close && distanceY < ((float) (-this.mTouchSlop)));
                        suitable = suitable || status == Status.Middle;
                        if (angle < 60.0f || !suitable) {
                            doNothing = true;
                        }
                    }
                    if (!doNothing) {
                        if (touching != null) {
                            touching.setPressed(false);
                        }
                        parent.requestDisallowInterceptTouchEvent(true);
                        this.mDragHelper.processTouchEvent(event);
                        break;
                    }
                    parent.requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                return super.onTouchEvent(event);
                break;
        }
        parent.requestDisallowInterceptTouchEvent(true);
        this.mDragHelper.processTouchEvent(event);
        return true;
    }

    private boolean isEnabledInAdapterView() {
        AdapterView adapterView = getAdapterView();
        if (adapterView == null) {
            return true;
        }
        Adapter adapter = adapterView.getAdapter();
        if (adapter == null) {
            return true;
        }
        int p = adapterView.getPositionForView(this);
        if (adapter instanceof BaseAdapter) {
            return ((BaseAdapter) adapter).isEnabled(p);
        }
        if (adapter instanceof ListAdapter) {
            return ((ListAdapter) adapter).isEnabled(p);
        }
        return true;
    }

    public void setSwipeEnabled(boolean enabled) {
        this.mSwipeEnabled = enabled;
    }

    public boolean isSwipeEnabled() {
        return this.mSwipeEnabled;
    }

    public boolean isLeftSwipeEnabled() {
        return this.mLeftSwipeEnabled;
    }

    public void setLeftSwipeEnabled(boolean leftSwipeEnabled) {
        this.mLeftSwipeEnabled = leftSwipeEnabled;
    }

    public boolean isRightSwipeEnabled() {
        return this.mRightSwipeEnabled;
    }

    public void setRightSwipeEnabled(boolean rightSwipeEnabled) {
        this.mRightSwipeEnabled = rightSwipeEnabled;
    }

    public boolean isTopSwipeEnabled() {
        return this.mTopSwipeEnabled;
    }

    public void setTopSwipeEnabled(boolean topSwipeEnabled) {
        this.mTopSwipeEnabled = topSwipeEnabled;
    }

    public boolean isBottomSwipeEnabled() {
        return this.mBottomSwipeEnabled;
    }

    public void setBottomSwipeEnabled(boolean bottomSwipeEnabled) {
        this.mBottomSwipeEnabled = bottomSwipeEnabled;
    }

    private boolean insideAdapterView() {
        return getAdapterView() != null;
    }

    private AdapterView getAdapterView() {
        for (ViewParent t = getParent(); t != null; t = t.getParent()) {
            if (t instanceof AdapterView) {
                return (AdapterView) t;
            }
        }
        return null;
    }

    private void performAdapterViewItemClick(MotionEvent e) {
        ViewParent t = getParent();
        while (t != null) {
            if (t instanceof AdapterView) {
                AdapterView view = (AdapterView) t;
                int p = view.getPositionForView(this);
                if (p != -1 && view.performItemClick(view.getChildAt(p - view.getFirstVisiblePosition()), p, view.getAdapter().getItemId(p))) {
                    return;
                }
            } else if ((t instanceof View) && ((View) t).performClick()) {
                return;
            }
            t = t.getParent();
        }
    }

    public void setDragEdge(DragEdge dragEdge) {
        this.mDragEdges = new ArrayList();
        this.mDragEdges.add(dragEdge);
        this.mCurrentDirectionIndex = 0;
        populateIndexes();
        requestLayout();
        updateBottomViews();
    }

    public void setDragDistance(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Drag distance can not be < 0");
        }
        this.mDragDistance = dp2px((float) max);
        requestLayout();
    }

    public void setShowMode(ShowMode mode) {
        this.mShowMode = mode;
        requestLayout();
    }

    public DragEdge getDragEdge() {
        return (DragEdge) this.mDragEdges.get(this.mCurrentDirectionIndex);
    }

    public int getDragDistance() {
        return this.mDragDistance;
    }

    public ShowMode getShowMode() {
        return this.mShowMode;
    }

    public ViewGroup getSurfaceView() {
        return (ViewGroup) getChildAt(getChildCount() - 1);
    }

    public List<ViewGroup> getBottomViews() {
        List<ViewGroup> lvg = new ArrayList();
        if (this.mBottomViewIdsSet) {
            if (this.mDragEdges.contains(DragEdge.Left)) {
                lvg.add(this.mLeftIndex, (ViewGroup) findViewById(((Integer) this.mBottomViewIdMap.get(DragEdge.Left)).intValue()));
            }
            if (this.mDragEdges.contains(DragEdge.Right)) {
                lvg.add(this.mRightIndex, (ViewGroup) findViewById(((Integer) this.mBottomViewIdMap.get(DragEdge.Right)).intValue()));
            }
            if (this.mDragEdges.contains(DragEdge.Top)) {
                lvg.add(this.mTopIndex, (ViewGroup) findViewById(((Integer) this.mBottomViewIdMap.get(DragEdge.Top)).intValue()));
            }
            if (this.mDragEdges.contains(DragEdge.Bottom)) {
                lvg.add(this.mBottomIndex, (ViewGroup) findViewById(((Integer) this.mBottomViewIdMap.get(DragEdge.Bottom)).intValue()));
            }
        } else {
            for (int i = 0; i < getChildCount() - 1; i++) {
                lvg.add((ViewGroup) getChildAt(i));
            }
        }
        return lvg;
    }

    public void setBottomViewIds(int left, int right, int top, int bottom) {
        if (this.mDragEdges.contains(DragEdge.Left)) {
            if (left == -1) {
                this.mBottomViewIdsSet = false;
            } else {
                this.mBottomViewIdMap.put(DragEdge.Left, Integer.valueOf(left));
                this.mBottomViewIdsSet = true;
            }
        }
        if (this.mDragEdges.contains(DragEdge.Right)) {
            if (right == -1) {
                this.mBottomViewIdsSet = false;
            } else {
                this.mBottomViewIdMap.put(DragEdge.Right, Integer.valueOf(right));
                this.mBottomViewIdsSet = true;
            }
        }
        if (this.mDragEdges.contains(DragEdge.Top)) {
            if (top == -1) {
                this.mBottomViewIdsSet = false;
            } else {
                this.mBottomViewIdMap.put(DragEdge.Top, Integer.valueOf(top));
                this.mBottomViewIdsSet = true;
            }
        }
        if (!this.mDragEdges.contains(DragEdge.Bottom)) {
            return;
        }
        if (bottom == -1) {
            this.mBottomViewIdsSet = false;
            return;
        }
        this.mBottomViewIdMap.put(DragEdge.Bottom, Integer.valueOf(bottom));
        this.mBottomViewIdsSet = true;
    }

    public Status getOpenStatus() {
        int surfaceLeft = getSurfaceView().getLeft();
        int surfaceTop = getSurfaceView().getTop();
        if (surfaceLeft == getPaddingLeft() && surfaceTop == getPaddingTop()) {
            return Status.Close;
        }
        if (surfaceLeft == getPaddingLeft() - this.mDragDistance || surfaceLeft == getPaddingLeft() + this.mDragDistance || surfaceTop == getPaddingTop() - this.mDragDistance || surfaceTop == getPaddingTop() + this.mDragDistance) {
            return Status.Open;
        }
        return Status.Middle;
    }

    private void processSurfaceRelease(float xvel, float yvel) {
        if (xvel == 0.0f && getOpenStatus() == Status.Middle) {
            close();
        }
        if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left || this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
            if (xvel > 0.0f) {
                if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left) {
                    open();
                } else {
                    close();
                }
            }
            if (xvel >= 0.0f) {
                return;
            }
            if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left) {
                close();
                return;
            } else {
                open();
                return;
            }
        }
        if (yvel > 0.0f) {
            if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Top) {
                open();
            } else {
                close();
            }
        }
        if (yvel >= 0.0f) {
            return;
        }
        if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Top) {
            close();
        } else {
            open();
        }
    }

    private void processBottomPullOutRelease(float xvel, float yvel) {
        if (xvel == 0.0f && getOpenStatus() == Status.Middle) {
            close();
        }
        if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left || this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
            if (xvel > 0.0f) {
                if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left) {
                    open();
                } else {
                    close();
                }
            }
            if (xvel >= 0.0f) {
                return;
            }
            if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left) {
                close();
                return;
            } else {
                open();
                return;
            }
        }
        if (yvel > 0.0f) {
            if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Top) {
                open();
            } else {
                close();
            }
        }
        if (yvel >= 0.0f) {
            return;
        }
        if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Top) {
            close();
        } else {
            open();
        }
    }

    private void processBottomLayDownMode(float xvel, float yvel) {
        if (xvel == 0.0f && getOpenStatus() == Status.Middle) {
            close();
        }
        int l = getPaddingLeft();
        int t = getPaddingTop();
        if (xvel < 0.0f && this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
            l -= this.mDragDistance;
        }
        if (xvel > 0.0f && this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left) {
            l += this.mDragDistance;
        }
        if (yvel > 0.0f && this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Top) {
            t += this.mDragDistance;
        }
        if (yvel < 0.0f && this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Bottom) {
            t -= this.mDragDistance;
        }
        this.mDragHelper.smoothSlideViewTo(getSurfaceView(), l, t);
        invalidate();
    }

    public void open() {
        open(true, true);
    }

    public void open(boolean smooth) {
        open(smooth, true);
    }

    public void open(boolean smooth, boolean notify) {
        ViewGroup surface = getSurfaceView();
        ViewGroup bottom = (ViewGroup) getBottomViews().get(this.mCurrentDirectionIndex);
        Rect rect = computeSurfaceLayoutArea(true);
        if (smooth) {
            this.mDragHelper.smoothSlideViewTo(getSurfaceView(), rect.left, rect.top);
        } else {
            int dx = rect.left - surface.getLeft();
            int dy = rect.top - surface.getTop();
            surface.layout(rect.left, rect.top, rect.right, rect.bottom);
            if (getShowMode() == ShowMode.PullOut) {
                Rect bRect = computeBottomLayoutAreaViaSurface(ShowMode.PullOut, rect);
                bottom.layout(bRect.left, bRect.top, bRect.right, bRect.bottom);
            }
            if (notify) {
                dispatchRevealEvent(rect.left, rect.top, rect.right, rect.bottom);
                dispatchSwipeEvent(rect.left, rect.top, dx, dy);
            } else {
                safeBottomView();
            }
        }
        invalidate();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void open(com.lib.swipelayout.SwipeLayout.DragEdge r4) {
        /*
        r3 = this;
        r2 = 1;
        r0 = com.lib.swipelayout.SwipeLayout.C06742.$SwitchMap$com$lib$swipelayout$SwipeLayout$DragEdge;
        r1 = r4.ordinal();
        r0 = r0[r1];
        switch(r0) {
            case 1: goto L_0x0018;
            case 2: goto L_0x001c;
            case 3: goto L_0x0010;
            case 4: goto L_0x0014;
            default: goto L_0x000c;
        };
    L_0x000c:
        r3.open(r2, r2);
        return;
    L_0x0010:
        r0 = r3.mLeftIndex;
        r3.mCurrentDirectionIndex = r0;
    L_0x0014:
        r0 = r3.mRightIndex;
        r3.mCurrentDirectionIndex = r0;
    L_0x0018:
        r0 = r3.mTopIndex;
        r3.mCurrentDirectionIndex = r0;
    L_0x001c:
        r0 = r3.mBottomIndex;
        r3.mCurrentDirectionIndex = r0;
        goto L_0x000c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lib.swipelayout.SwipeLayout.open(com.lib.swipelayout.SwipeLayout$DragEdge):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void open(boolean r3, com.lib.swipelayout.SwipeLayout.DragEdge r4) {
        /*
        r2 = this;
        r0 = com.lib.swipelayout.SwipeLayout.C06742.$SwitchMap$com$lib$swipelayout$SwipeLayout$DragEdge;
        r1 = r4.ordinal();
        r0 = r0[r1];
        switch(r0) {
            case 1: goto L_0x0018;
            case 2: goto L_0x001c;
            case 3: goto L_0x0010;
            case 4: goto L_0x0014;
            default: goto L_0x000b;
        };
    L_0x000b:
        r0 = 1;
        r2.open(r3, r0);
        return;
    L_0x0010:
        r0 = r2.mLeftIndex;
        r2.mCurrentDirectionIndex = r0;
    L_0x0014:
        r0 = r2.mRightIndex;
        r2.mCurrentDirectionIndex = r0;
    L_0x0018:
        r0 = r2.mTopIndex;
        r2.mCurrentDirectionIndex = r0;
    L_0x001c:
        r0 = r2.mBottomIndex;
        r2.mCurrentDirectionIndex = r0;
        goto L_0x000b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lib.swipelayout.SwipeLayout.open(boolean, com.lib.swipelayout.SwipeLayout$DragEdge):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void open(boolean r3, boolean r4, com.lib.swipelayout.SwipeLayout.DragEdge r5) {
        /*
        r2 = this;
        r0 = com.lib.swipelayout.SwipeLayout.C06742.$SwitchMap$com$lib$swipelayout$SwipeLayout$DragEdge;
        r1 = r5.ordinal();
        r0 = r0[r1];
        switch(r0) {
            case 1: goto L_0x0017;
            case 2: goto L_0x001b;
            case 3: goto L_0x000f;
            case 4: goto L_0x0013;
            default: goto L_0x000b;
        };
    L_0x000b:
        r2.open(r3, r4);
        return;
    L_0x000f:
        r0 = r2.mLeftIndex;
        r2.mCurrentDirectionIndex = r0;
    L_0x0013:
        r0 = r2.mRightIndex;
        r2.mCurrentDirectionIndex = r0;
    L_0x0017:
        r0 = r2.mTopIndex;
        r2.mCurrentDirectionIndex = r0;
    L_0x001b:
        r0 = r2.mBottomIndex;
        r2.mCurrentDirectionIndex = r0;
        goto L_0x000b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lib.swipelayout.SwipeLayout.open(boolean, boolean, com.lib.swipelayout.SwipeLayout$DragEdge):void");
    }

    public void close() {
        close(true, true);
    }

    public void close(boolean smooth) {
        close(smooth, true);
    }

    public void close(boolean smooth, boolean notify) {
        ViewGroup surface = getSurfaceView();
        if (smooth) {
            this.mDragHelper.smoothSlideViewTo(getSurfaceView(), getPaddingLeft(), getPaddingTop());
        } else {
            Rect rect = computeSurfaceLayoutArea(false);
            int dx = rect.left - surface.getLeft();
            int dy = rect.top - surface.getTop();
            surface.layout(rect.left, rect.top, rect.right, rect.bottom);
            if (notify) {
                dispatchRevealEvent(rect.left, rect.top, rect.right, rect.bottom);
                dispatchSwipeEvent(rect.left, rect.top, dx, dy);
            } else {
                safeBottomView();
            }
        }
        invalidate();
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean smooth) {
        if (getOpenStatus() == Status.Open) {
            close(smooth);
        } else if (getOpenStatus() == Status.Close) {
            open(smooth);
        }
    }

    private Rect computeSurfaceLayoutArea(boolean open) {
        int l = getPaddingLeft();
        int t = getPaddingTop();
        if (open) {
            if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left) {
                l = getPaddingLeft() + this.mDragDistance;
            } else if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
                l = getPaddingLeft() - this.mDragDistance;
            } else {
                t = this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Top ? getPaddingTop() + this.mDragDistance : getPaddingTop() - this.mDragDistance;
            }
        }
        return new Rect(l, t, getMeasuredWidth() + l, getMeasuredHeight() + t);
    }

    private Rect computeBottomLayoutAreaViaSurface(ShowMode mode, Rect surfaceArea) {
        Rect rect = surfaceArea;
        int bl = rect.left;
        int bt = rect.top;
        int br = rect.right;
        int bb = rect.bottom;
        if (mode == ShowMode.PullOut) {
            if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left) {
                bl = rect.left - this.mDragDistance;
            } else if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
                bl = rect.right;
            } else {
                bt = this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Top ? rect.top - this.mDragDistance : rect.bottom;
            }
            if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left || this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
                bb = rect.bottom;
                br = bl + ((ViewGroup) getBottomViews().get(this.mCurrentDirectionIndex)).getMeasuredWidth();
            } else {
                bb = bt + ((ViewGroup) getBottomViews().get(this.mCurrentDirectionIndex)).getMeasuredHeight();
                br = rect.right;
            }
        } else if (mode == ShowMode.LayDown) {
            if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left) {
                br = bl + this.mDragDistance;
            } else if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
                bl = br - this.mDragDistance;
            } else if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Top) {
                bb = bt + this.mDragDistance;
            } else {
                bt = bb - this.mDragDistance;
            }
        }
        return new Rect(bl, bt, br, bb);
    }

    private Rect computeBottomLayDown(DragEdge dragEdge) {
        int br;
        int bb;
        int bl = getPaddingLeft();
        int bt = getPaddingTop();
        if (dragEdge == DragEdge.Right) {
            bl = getMeasuredWidth() - this.mDragDistance;
        } else if (dragEdge == DragEdge.Bottom) {
            bt = getMeasuredHeight() - this.mDragDistance;
        }
        if (dragEdge == DragEdge.Left || dragEdge == DragEdge.Right) {
            br = bl + this.mDragDistance;
            bb = bt + getMeasuredHeight();
        } else {
            br = bl + getMeasuredWidth();
            bb = bt + this.mDragDistance;
        }
        return new Rect(bl, bt, br, bb);
    }

    public void setOnDoubleClickListener(DoubleClickListener doubleClickListener) {
        this.mDoubleClickListener = doubleClickListener;
    }

    private int dp2px(float dp) {
        return (int) ((getContext().getResources().getDisplayMetrics().density * dp) + 0.5f);
    }

    public List<DragEdge> getDragEdges() {
        return this.mDragEdges;
    }

    public void setDragEdges(List<DragEdge> mDragEdges) {
        this.mDragEdges = mDragEdges;
        this.mCurrentDirectionIndex = 0;
        populateIndexes();
        updateBottomViews();
    }

    public void setDragEdges(DragEdge... mDragEdges) {
        this.mDragEdges = new ArrayList();
        for (DragEdge e : mDragEdges) {
            this.mDragEdges.add(e);
        }
        this.mCurrentDirectionIndex = 0;
        populateIndexes();
        updateBottomViews();
    }

    private void populateIndexes() {
        this.mLeftIndex = this.mDragEdges.indexOf(DragEdge.Left);
        this.mRightIndex = this.mDragEdges.indexOf(DragEdge.Right);
        this.mTopIndex = this.mDragEdges.indexOf(DragEdge.Top);
        this.mBottomIndex = this.mDragEdges.indexOf(DragEdge.Bottom);
    }

    private float getCurrentOffset() {
        if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Left) {
            return this.mLeftEdgeSwipeOffset;
        }
        if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Right) {
            return this.mRightEdgeSwipeOffset;
        }
        if (this.mDragEdges.get(this.mCurrentDirectionIndex) == DragEdge.Top) {
            return this.mTopEdgeSwipeOffset;
        }
        return this.mBottomEdgeSwipeOffset;
    }

    private void updateBottomViews() {
        if (this.mShowMode == ShowMode.PullOut) {
            layoutPullOut();
        } else if (this.mShowMode == ShowMode.LayDown) {
            layoutLayDown();
        }
        safeBottomView();
        if (this.mOnLayoutListeners != null) {
            for (int i = 0; i < this.mOnLayoutListeners.size(); i++) {
                ((OnLayout) this.mOnLayoutListeners.get(i)).onLayout(this);
            }
        }
    }
}
