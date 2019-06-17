package com.jeans.rewardsunlimited;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

public class ProductDetailsStringLimiterTextView extends android.support.v7.widget.AppCompatTextView
{
    private static final int DEFAULT_TRIM_LENGTH=200;
    private static final String ELLIPSIS = "... See More";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private BufferType bufferType;
    private boolean trim = true;
    private int trimLength;
    public ProductDetailsStringLimiterTextView(Context context) {
        this(context, null);
    }

    public ProductDetailsStringLimiterTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StringLimiterTextView);
        this.trimLength = typedArray.getInt(R.styleable.StringLimiterTextView_trimLength, DEFAULT_TRIM_LENGTH);
        typedArray.recycle();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trim = !trim;
                setText();
                requestFocusFromTouch();
            }
        });

    }
    private void setText() {
        super.setText(getDisplayableText(), bufferType);
    }

    private CharSequence getDisplayableText() {
        return trim ? trimmedText : originalText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText(text);
        bufferType = type;
        setText();
    }

    private CharSequence getTrimmedText(CharSequence text)
    {
        if (originalText != null && originalText.length() > trimLength) {
            SpannableStringBuilder builder=new SpannableStringBuilder(originalText,0,trimLength+1);
            SpannableString ellipsis=new SpannableString(ELLIPSIS);
            ellipsis.setSpan(new ForegroundColorSpan(Color.parseColor("#388e3c")),0,ELLIPSIS.length(),0);
            builder.append(ellipsis);
            return builder;
            //return new SpannableStringBuilder(originalText, 0, trimLength + 1).append(ELLIPSIS);
        } else {
            return originalText;
        }
    }

    public CharSequence getOriginalText() {
        return originalText;
    }

    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        trimmedText = getTrimmedText(originalText);
        setText();
    }

    public int getTrimLength() {
        return trimLength;
    }
}
