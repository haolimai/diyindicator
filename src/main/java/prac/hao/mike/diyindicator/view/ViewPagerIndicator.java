package prac.hao.mike.diyindicator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import prac.hao.mike.diyindicator.R;

/**
 * Created by mike on 2016/4/23.
 */
public class ViewPagerIndicator extends LinearLayout {
    private int mTriangleWidth;
    private int mInitTranslationX;
    private int mTranslationX;
    private static final float RADIO_TRIANGLE_WIDTH = 1 / 6F;

    private Paint mPaint;
    private Path mPath;

    private int visible_item_count;
    private static final int DEFAULT_COUNT = 4;

    private static final int TEXT_COLOR_NORMAL = 0x77fffafa;
    private static final int TEXT_COLOR_HIGHLIGHT = 0xfffffafa;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        visible_item_count = ta.getInt(R.styleable.ViewPagerIndicator_visible_item_count, DEFAULT_COUNT);
        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(TEXT_COLOR_HIGHLIGHT);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.translate(mInitTranslationX + mTranslationX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTriangleWidth = (int) (w / visible_item_count * RADIO_TRIANGLE_WIDTH);
        mInitTranslationX = w / visible_item_count / 2 - mTriangleWidth / 2;
        initTriangle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / visible_item_count;
            child.setLayoutParams(lp);
        }

        setIndicatorItemClickListener();
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void initTriangle() {
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -getMeasuredHeight() / 6);
        mPath.close();
    }

    public void scroll(int position, float positionOffset) {
        int itemWidth = getWidth() / visible_item_count;
        mTranslationX = (int) (itemWidth * (position + positionOffset));
        Log.d("hlm", "position = " + position);
        Log.d("hlm", "positionOffset = " + positionOffset);

        if (position >= visible_item_count - 2 && positionOffset > 0 && getChildCount() > visible_item_count) {
            Log.d("hlm", "i am here");
            if (visible_item_count != 1) {
                this.scrollTo(
                        (int) (itemWidth * positionOffset + (position - (visible_item_count - 2)) * itemWidth),
                        0
                );
            } else {
                this.scrollTo((int) (position * itemWidth + itemWidth * positionOffset), 0);
            }

        }

        invalidate();
    }

    public void setVisibleItemCount(int count) {
        visible_item_count = count;
    }

    public void setIndicatorItem(List<String> itemText) {
        if (itemText != null && itemText.size() > 0) {
            removeAllViews();
            for (String item : itemText) {
                addView(generateTextView(item));
            }
        }

        setIndicatorItemClickListener();
    }

    private View generateTextView(String item) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / visible_item_count;
        tv.setLayoutParams(lp);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(TEXT_COLOR_NORMAL);
        return tv;
    }

    private ViewPager mViewPager;

    public interface OnPageChangListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    private OnPageChangListener mListener;

    public void setOnPageChangeListener(OnPageChangListener listener) {
        this.mListener = listener;
    }

    public void setViewPager(ViewPager viewPager, int pos) {
        this.mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position, positionOffset);
                if (mListener != null) {
                    mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPageSelected(position);
                }
                indicatorItemSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });
        mViewPager.setCurrentItem(pos);
        indicatorItemSelected(pos);
    }

    private void indicatorItemUnselected() {
        for (int i = 0; i < getChildCount(); i++) {
            TextView tv = (TextView) getChildAt(i);
            if (tv != null) {
                tv.setTextColor(TEXT_COLOR_NORMAL);
            }
        }
    }

    private void indicatorItemSelected(int pos) {
        indicatorItemUnselected();
        TextView tv = (TextView) getChildAt(pos);
        if (tv != null) {
            tv.setTextColor(TEXT_COLOR_HIGHLIGHT);
        }
    }

    private void setIndicatorItemClickListener() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            final int j = i;
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

}
