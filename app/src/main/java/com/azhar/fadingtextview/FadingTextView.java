package com.azhar.fadingtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.ArrayRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * Created by Azhar Rivaldi on 07-11-2023
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * LinkedIn : https://www.linkedin.com/in/azhar-rivaldi
 */

public class FadingTextView extends androidx.appcompat.widget.AppCompatTextView {

    public static final int DEFAULT_TIME_OUT = 15000;
    public static final int MILLISECONDS = 1,
            SECONDS = 2,
            MINUTES = 3;

    private Animation fadeInAnimation, fadeOutAnimation;
    private Handler handler;
    private CharSequence[] texts;
    private boolean isShown;
    private int position;
    private int timeout = DEFAULT_TIME_OUT;
    private boolean stopped;

    public FadingTextView(Context context) {
        super(context);
        init();
    }

    public FadingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        handleAttrs(attrs);
    }

    public FadingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        handleAttrs(attrs);
    }

    public void resume() {
        isShown = true;
        startAnimation();
    }

    public void pause() {
        isShown = false;
        stopAnimation();
    }

    public void stop() {
        isShown = false;
        stopped = true;
        stopAnimation();
    }

    public void restart() {
        isShown = true;
        stopped = false;
        startAnimation();
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pause();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resume();
    }

    private void init() {
        fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
        handler = new Handler();
        isShown = true;
    }

    private void handleAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FadingTextView);
        this.texts = a.getTextArray(R.styleable.FadingTextView_texts);
        this.timeout = Math.abs(a.getInteger(R.styleable.FadingTextView_timeout, 14500)) +
                getResources().getInteger(android.R.integer.config_longAnimTime);

        boolean shuffle = a.getBoolean(R.styleable.FadingTextView_shuffle, false);
        if (shuffle) {
            shuffle();
        }

        a.recycle();
    }

    public CharSequence[] getTexts() {
        return texts;
    }

    public void setTexts(@NonNull String[] texts) {
        if (texts.length < 1) {
            throw new IllegalArgumentException("There must be at least one text");
        } else {
            this.texts = texts;
            stopAnimation();
            position = 0;
            startAnimation();
        }
    }

    public void setTexts(@ArrayRes int texts) {
        if (getResources().getStringArray(texts).length < 1) {
            throw new IllegalArgumentException("There must be at least one text");
        } else {
            this.texts = getResources().getStringArray(texts);
            stopAnimation();
            position = 0;
            startAnimation();
        }
    }

    public void forceRefresh() {
        stopAnimation();
        startAnimation();
    }

    public void fadeTo(int position){
        this.position = position;
        isShown = true;
        startAnimation();
        pause();
    }

    public void shuffle() {
        if (this.texts == null) {
            throw new IllegalArgumentException("You must provide a string array t" +
                    "o the FadingTextView using the texts parameter or use " +
                    "FTV.placeholder to leave it empty");
        }
        List<CharSequence> texts = Arrays.asList(this.texts);
        Collections.shuffle(texts);
        this.texts = (CharSequence[]) texts.toArray();
    }

    @Deprecated
    public void setTimeout(int timeout) {
        if (timeout < 1) {
            throw new IllegalArgumentException("Timeout must be longer than 0");
        } else {
            this.timeout = timeout;
        }
    }

    @Deprecated
    public void setTimeout(double timeout, @TimeUnit int timeUnit) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("Timeout must be longer than 0");
        } else {
            int multiplier;
            switch (timeUnit) {
                case MILLISECONDS:
                    multiplier = 1;
                    break;
                case SECONDS:
                    multiplier = 1000;
                    break;
                case MINUTES:
                    multiplier = 60000;
                    break;
                default:
                    multiplier = 1;
                    break;
            }
            this.timeout = (int) (timeout * multiplier);
        }
    }

    public void setTimeout(long timeout, java.util.concurrent.TimeUnit timeUnit) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("Timeout must be longer than 0");
        } else {
            this.timeout = (int) java.util.concurrent.TimeUnit.MILLISECONDS
                    .convert(timeout, timeUnit);
        }
    }

    @Override
    public void startAnimation(Animation animation) {
        if (isShown && !stopped) {
            super.startAnimation(animation);
        }
    }

    protected void startAnimation() {
        if (!isInEditMode()) {
            setText(texts[position]);
            startAnimation(fadeInAnimation);
            handler.postDelayed(() -> {
                startAnimation(fadeOutAnimation);
                if (getAnimation() != null) {
                    getAnimation().setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isShown) {
                                position = position == texts.length - 1 ? 0 : position + 1;
                                startAnimation();
                            }
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            }, timeout);
        }
    }

    private void stopAnimation() {
        handler.removeCallbacksAndMessages(null);
        if (getAnimation() != null) getAnimation().cancel();
    }

    @IntDef({MILLISECONDS, SECONDS, MINUTES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeUnit {}

}