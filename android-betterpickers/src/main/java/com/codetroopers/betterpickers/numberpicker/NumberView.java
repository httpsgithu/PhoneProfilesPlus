package com.codetroopers.betterpickers.numberpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.codetroopers.betterpickers.R;
import com.codetroopers.betterpickers.widget.ZeroTopPaddingTextView;

@SuppressWarnings("resource")
@SuppressLint("CustomViewStyleable")
public class NumberView extends LinearLayout {

    private ZeroTopPaddingTextView mNumber, mDecimal;
    private ZeroTopPaddingTextView mDecimalSeparator;
    private ZeroTopPaddingTextView mMinusLabel;
    //private final Typeface mAndroidClockMonoThin;
    private Typeface mOriginalNumberTypeface;

    private ColorStateList mTextColor;

    /**
     * Instantiate a NumberView
     *
     * @param context the Context in which to inflate the View
     */
    public NumberView(Context context) {
        this(context, null);
    }

    /**
     * Instantiate a NumberView
     *
     * @param context the Context in which to inflate the View
     * @param attrs attributes that define the title color
     */
    public NumberView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //mAndroidClockMonoThin =
        //        Typeface.createFromAsset(context.getAssets(), "fonts/AndroidClockMono-Thin.ttf");

        // Init defaults
        mTextColor = ContextCompat .getColorStateList(context, R.color.dialog_text_color_holo_dark);
                //getResources().getColorStateList(R.color.dialog_text_color_holo_dark);
    }

    /**
     * Set a theme and restyle the views. This View will change its title color.
     *
     * @param themeResId the resource ID for theming
     */
    public void setTheme(int themeResId) {
        if (themeResId != -1) {
            TypedArray a = getContext().obtainStyledAttributes(themeResId, R.styleable.BetterPickersDialogFragment);

            mTextColor = a.getColorStateList(R.styleable.BetterPickersDialogFragment_bpTextColor);

            a.recycle();
        }

        restyleViews();
    }

    private void restyleViews() {
        if (mNumber != null) {
            mNumber.setTextColor(mTextColor);
        }
        if (mDecimal != null) {
            mDecimal.setTextColor(mTextColor);
        }
        if (mDecimalSeparator != null) {
            mDecimalSeparator.setTextColor(mTextColor);
        }
        if (mMinusLabel != null) {
            mMinusLabel.setTextColor(mTextColor);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mNumber = findViewById(R.id.number);
        mDecimal = findViewById(R.id.decimal);
        mDecimalSeparator = findViewById(R.id.decimal_separator);
        mMinusLabel = findViewById(R.id.minus_label);
        if (mNumber != null) {
            mOriginalNumberTypeface = mNumber.getTypeface();
        }
        // Set the lowest time unit with thin font
        if (mNumber != null) {
            //mNumber.setTypeface(mAndroidClockMonoThin);
            mNumber.updatePadding();
        }
        if (mDecimal != null) {
            //mDecimal.setTypeface(mAndroidClockMonoThin);
            mDecimal.updatePadding();
        }

        restyleViews();
    }

    /**
     * Set the number shown
     *
     * @param numbersDigit the non-decimal digits
     * @param decimalDigit the decimal digits
     * @param showDecimal whether it's a decimal or not
     * @param isNegative whether it's positive or negative
     */
    public void setNumber(String numbersDigit, String decimalDigit, boolean showDecimal,
            boolean isNegative) {
        mMinusLabel.setVisibility(isNegative ? View.VISIBLE : View.GONE);
        if (mNumber != null) {
            if (numbersDigit.isEmpty()) {
                // Set to -
                mNumber.setText("-");
                //mNumber.setTypeface(mAndroidClockMonoThin);
                mNumber.setEnabled(false);
                mNumber.updatePadding();
                mNumber.setVisibility(View.VISIBLE);
            } else if (showDecimal) {
                // Set to bold
                mNumber.setText(numbersDigit);
                mNumber.setTypeface(mOriginalNumberTypeface);
                mNumber.setEnabled(true);
                mNumber.updatePaddingForBoldDate();
                mNumber.setVisibility(View.VISIBLE);
            } else {
                // Set to thin
                mNumber.setText(numbersDigit);
                //mNumber.setTypeface(mAndroidClockMonoThin);
                mNumber.setEnabled(true);
                mNumber.updatePadding();
                mNumber.setVisibility(View.VISIBLE);
            }
        }
        if (mDecimal != null) {
            // Hide digit
            if (decimalDigit.isEmpty()) {
                mDecimal.setVisibility(View.GONE);
            } else {
                mDecimal.setText(decimalDigit);
                //mDecimal.setTypeface(mAndroidClockMonoThin);
                mDecimal.setEnabled(true);
                mDecimal.updatePadding();
                mDecimal.setVisibility(View.VISIBLE);
            }
        }
        if (mDecimalSeparator != null) {
            // Hide separator
            mDecimalSeparator.setVisibility(showDecimal ? View.VISIBLE : View.GONE);
        }
    }
}