package com.loopd.dropdownmenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DropDownMenu extends FrameLayout {
    private static final String TAG = "DropDownMenu";
    private static final int SLIDING_MENU_SLIDING_DURATION = 400;
    private Context mContext;
    private LinearLayout mMenuButtonsLayout;
    private ImageView mCloseButton;
    private OnMenuCollapsedListener mOnMenuCollapsedListener;
    private OnMenuButtonClickListener mOnMenuButtonClickListener;
    private boolean mAnimationLock = false;
    private List<View> mMenuButtons = new ArrayList<>();
    private ColorStateList mMenuButtonTextColors;

    public DropDownMenu(Context context) {
        super(context);
        init(context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        View dropDownMenuLayout = LayoutInflater.from(context).inflate(R.layout.drop_down_menu, null);
        mCloseButton = (ImageView) dropDownMenuLayout.findViewById(R.id.close_btn);
        mMenuButtonsLayout = (LinearLayout) dropDownMenuLayout.findViewById(R.id.menu_buttons_layout);
        initCloseButtonListener();
        addView(dropDownMenuLayout);
        setVisibility(INVISIBLE);
    }

    private void initCloseButtonListener() {
        mCloseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    public void setGravity(int gravity) {
        mMenuButtonsLayout.setGravity(gravity);
    }

    public void setCloseButtonDrawable(int drawableRes) {
        mCloseButton.setImageResource(drawableRes);
    }

    public void addMenuButton(String title, int drawableRes) {
        View menuButton = inflateMenuButton(title, drawableRes);
        final int buttonPosition = mMenuButtonsLayout.getChildCount();
        menuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close(new OnMenuCollapsedListener() {
                    @Override
                    public void onCollapsed() {
                        if (mOnMenuButtonClickListener != null) {
                            mOnMenuButtonClickListener.onMenuButtonClick(buttonPosition);
                        }
                    }
                });
            }
        });
        applyTextColors((TextView) menuButton.findViewById(R.id.title));
        mMenuButtonsLayout.addView(menuButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        mMenuButtons.add(menuButton);
    }

    private void applyTextColors(TextView textView) {
        if (mMenuButtonTextColors != null) {
            textView.setTextColor(mMenuButtonTextColors);
        }
    }

    public void addMenuButton(int stringRes, int drawableRes) {
        addMenuButton(mContext.getString(stringRes), drawableRes);
    }

    private View inflateMenuButton(String title, int drawableRes) {
        View dropDownButton = LayoutInflater.from(mContext).inflate(R.layout.drop_down_button, null);
        ImageView iconImageView = (ImageView) dropDownButton.findViewById(R.id.icon);
        TextView titleTextView = (TextView) dropDownButton.findViewById(R.id.title);
        iconImageView.setImageResource(drawableRes);
        titleTextView.setText(title);
        return dropDownButton;
    }

    public void open() {
        if (!mAnimationLock) {
            mAnimationLock = true;
            AnimatorSet slideAnimationSet = new AnimatorSet();
            slideAnimationSet.playTogether(
                    Glider.glide(Skill.CubicEaseOut, SLIDING_MENU_SLIDING_DURATION, ObjectAnimator.ofFloat(mMenuButtonsLayout, "translationY", -mMenuButtonsLayout.getHeight(), 0)),
                    Glider.glide(Skill.CubicEaseOut, SLIDING_MENU_SLIDING_DURATION, ObjectAnimator.ofFloat(mCloseButton, "rotation", 0, -180))
            );
            slideAnimationSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimationLock = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            slideAnimationSet.setDuration(SLIDING_MENU_SLIDING_DURATION);
            slideAnimationSet.start();
        }
    }

    public void close() {
        close(null);
    }

    public void close(final OnMenuCollapsedListener callback) {
        if (!mAnimationLock) {
            mAnimationLock = true;
            AnimatorSet slideAnimationSet = new AnimatorSet();
            slideAnimationSet.playTogether(
                    Glider.glide(Skill.CubicEaseOut, SLIDING_MENU_SLIDING_DURATION, ObjectAnimator.ofFloat(mMenuButtonsLayout, "translationY", 0, -mMenuButtonsLayout.getHeight())),
                    Glider.glide(Skill.CubicEaseOut, SLIDING_MENU_SLIDING_DURATION, ObjectAnimator.ofFloat(mCloseButton, "rotation", -180, 0))
            );
            slideAnimationSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(GONE);
                    if (mOnMenuCollapsedListener != null) {
                        mOnMenuCollapsedListener.onCollapsed();
                    }
                    if (callback != null) {
                        callback.onCollapsed();
                    }
                    mAnimationLock = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            slideAnimationSet.setDuration(SLIDING_MENU_SLIDING_DURATION);
            slideAnimationSet.start();
        }
    }

    public void setTextColorRes(int colorXmlResId) {
        try {
            XmlResourceParser parser = getResources().getXml(colorXmlResId);
            ColorStateList colors = ColorStateList.createFromXml(getResources(), parser);
            for (View menuButton : mMenuButtons) {
                TextView titleTextVuew = (TextView) menuButton.findViewById(R.id.title);
                titleTextVuew.setTextColor(colors);
            }
            mMenuButtonTextColors = colors;
        } catch (Exception e) {
            Log.e(TAG, "setTextColorRes error: " + e.getMessage());
        }
    }

    public void setOnMenuCollapsedListener(OnMenuCollapsedListener callback) {
        mOnMenuCollapsedListener = callback;
    }

    public void setOnMenuButtonClickListener(OnMenuButtonClickListener callback) {
        mOnMenuButtonClickListener = callback;
    }

    public interface OnMenuCollapsedListener {
        void onCollapsed();
    }

    public interface OnMenuButtonClickListener {
        void onMenuButtonClick(int position);
    }
}
