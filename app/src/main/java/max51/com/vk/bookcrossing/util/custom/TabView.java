package max51.com.vk.bookcrossing.util.custom;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;

import max51.com.vk.bookcrossing.R;

public class TabView extends View {   //Кастомный tabview для фильтраии объявлений
                                      //Взят с github https://github.com/Dsiner/TabView
    private int mWidth;
    private int mHeight;

    private Rect mRect;
    private RectF mRectF;
    private Paint mPaintA;
    private Paint mPaintB;
    private Paint mPaintTitle;
    private Paint mPaintTitleCur;

    private int mCount;
    private float mWidthB;
    private float mTouchX, mTouchY;
    private int mLastIndex;
    private int mCurIndex;
    private int mDownIndex = 0;
    private int mTouchSlop;
    private boolean mIsDragging;

    private float mRectRadius;
    private String[] mTitles;
    private int mTextSize;
    private int mColorStroke, mColorStrokeBlank, mColorText, mColorTextCur;
    private float mPadding;
    private float mReservedPadding;
    private int mDuration;

    private ValueAnimator mAnimation;
    private float mFactor;

    private OnTabSelectedListener mOnTabSelectedListener;

    public TabView(Context context) {
        this(context, null);
    }

    public TabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypedArray(context, attrs);
        init(context);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabView);
        String title = typedArray.getString(R.styleable.TabView_tabv_title);
        mTitles = title != null ? title.split(";") : null;
        mTextSize = (int) typedArray.getDimension(R.styleable.TabView_tabv_textSize,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics()));
        mColorStroke = mColorText = typedArray.getColor(R.styleable.TabView_tabv_colorMain, Color.parseColor("#FF4081"));
        mColorStrokeBlank = mColorTextCur = typedArray.getColor(R.styleable.TabView_tabv_colorSub, Color.parseColor("#ffffff"));
        mPadding = (int) typedArray.getDimension(R.styleable.TabView_tabv_padding, 2);
        mReservedPadding = (int) typedArray.getDimension(R.styleable.TabView_tabv_paddingSide, -1);
        mDuration = typedArray.getInteger(R.styleable.TabView_tabv_duration, 250);
        typedArray.recycle();
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mCount = mTitles != null ? mTitles.length : 0;

        mRect = new Rect();
        mRectF = new RectF();
        mPaintA = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintA.setColor(mColorStroke);

        mPaintB = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintB.setColor(mColorStrokeBlank);

        mPaintTitle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTitle.setTextSize(mTextSize);
        mPaintTitle.setTextAlign(Paint.Align.CENTER);
        mPaintTitle.setColor(mColorText);

        mPaintTitleCur = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTitleCur.setTextSize(mTextSize);
        mPaintTitleCur.setTextAlign(Paint.Align.CENTER);
        mPaintTitleCur.setColor(mColorTextCur);

        mAnimation = ValueAnimator.ofFloat(0f, 1f);
        mAnimation.setDuration(mDuration);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFactor = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mFactor == 1 && mOnTabSelectedListener != null) {
                    mOnTabSelectedListener.onTabSelected(mCurIndex);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCount <= 0) {
            return;
        }
        mRect.set(0, 0, mWidth, mHeight);
        mRectF.set(mRect);
        canvas.drawRoundRect(mRectF, mRectRadius, mRectRadius, mPaintA);

        mRect.set((int) mPadding, (int) mPadding, (int) (mWidth - mPadding), (int) (mHeight - mPadding));
        mRectF.set(mRect);
        canvas.drawRoundRect(mRectF, mRectRadius, mRectRadius, mPaintB);

        float start = mReservedPadding + mWidthB * mLastIndex;
        float end = mReservedPadding + mWidthB * mCurIndex;
        float offsetX = start + (end - start) * mFactor;

        mRect.set((int) (offsetX - mReservedPadding), 0, (int) (offsetX + mWidthB + mReservedPadding), mHeight);
        mRectF.set(mRect);

        canvas.drawRoundRect(mRectF, mRectRadius, mRectRadius, mPaintA);

        int textheight = (int) getTextHeight(mPaintTitle);
        int starty = (mHeight + textheight) / 2;

        for (int i = 0; i < mCount; i++) {
            float startx = mReservedPadding + mWidthB * i + mWidthB / 2;
            float cursor = (offsetX + mWidthB / 2) - mReservedPadding;
            if (cursor < 0) {
                cursor = 0;
            }
            int offsetCur = (int) (cursor / mWidthB);
            if (offsetCur == i && (offsetCur == mCurIndex || offsetCur == mLastIndex)) {
                canvas.drawText(mTitles[i], startx, starty, mPaintTitleCur);
            } else {
                canvas.drawText(mTitles[i], startx, starty, mPaintTitle);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mRectRadius = (mHeight + 0.5f) / 2;
        mReservedPadding = mReservedPadding == -1 ? (int) (mRectRadius * 0.85f) : mReservedPadding;
        mWidthB = (mWidth - mReservedPadding * 2) / (mCount > 0 ? mCount : 1);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mCount <= 0 || !(mFactor == 0 || mFactor == 1)) {
            return false;
        }
        final int action = ev.getActionMasked();
        final int actionIndex = ev.getActionIndex();
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = x;
                mTouchY = y;
                mDownIndex = (int) ((x - mReservedPadding) / mWidthB);
                mDownIndex = Math.max(mDownIndex, 0);
                mDownIndex = Math.min(mDownIndex, mCount - 1);
                mIsDragging = false;
                return mDownIndex != mCurIndex;

            case MotionEvent.ACTION_MOVE:
                if (!mIsDragging && (Math.abs(x - mTouchX) > mTouchSlop
                        || Math.abs(y - mTouchY) > mTouchSlop)) {
                    mIsDragging = true;
                }
                return !mIsDragging;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsDragging
                        || x < 0 || x > mWidth
                        || y < 0 || y > mHeight) {
                    return false;
                }
                int upIndex = (int) ((x - mReservedPadding) / mWidthB);
                upIndex = Math.max(upIndex, 0);
                upIndex = Math.min(upIndex, mCount - 1);
                if (upIndex == mDownIndex) {
                    mLastIndex = mCurIndex;
                    mCurIndex = mDownIndex;
                    startAnim();
                    return true;
                }
                return false;
        }
        return super.onTouchEvent(ev);
    }

    private float getTextHeight(Paint p) {
        Paint.FontMetrics fm = p.getFontMetrics();
        return (float) ((Math.ceil(fm.descent - fm.top) + 2) / 2);
    }

    private void startAnim() {
        stopAnim();
        if (mAnimation != null) {
            mAnimation.start();
        }
    }

    private void stopAnim() {
        if (mAnimation != null) {
            mAnimation.cancel();
        }
    }

    public void setTitle(String[] title) {
        if (title == null || title.length <= 0) {
            return;
        }
        mTitles = title;
        mCount = mTitles.length;
        mWidthB = (mWidth - mReservedPadding * 2) / (mCount > 0 ? mCount : 1);
        postInvalidate();
    }

    public void select(int index, boolean withAnim) {
        if (index == mCurIndex) {
            return;
        }
        mLastIndex = mCurIndex;
        mCurIndex = index;
        if (withAnim) {
            startAnim();
        } else {
            mFactor = 1f;
            invalidate();
        }
    }

    public void setOnTabSelectedListener(OnTabSelectedListener l) {
        this.mOnTabSelectedListener = l;
    }

    public interface OnTabSelectedListener {
        void onTabSelected(int index);
    }
}