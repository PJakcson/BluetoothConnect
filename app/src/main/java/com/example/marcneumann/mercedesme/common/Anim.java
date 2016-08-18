package com.example.marcneumann.mercedesme.common;

import android.animation.ValueAnimator;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.marcneumann.mercedesme.R;

import java.util.List;

public class Anim {

    public static void setColoured(final ImageView greyView, @Nullable final ImageView coloredView) {
        if (greyView == coloredView) {
            return;
        }

        final ColorMatrix greyMatrix = new ColorMatrix();
        final ColorMatrix colorMatrix = new ColorMatrix();
        greyMatrix.setSaturation(1f);
        colorMatrix.setSaturation(0);

        ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
        animation.addUpdateListener(animation1 -> {
            greyMatrix.setSaturation(1 - animation1.getAnimatedFraction());
            colorMatrix.setSaturation(animation1.getAnimatedFraction());

            ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
            greyView.setColorFilter(colorFilter);

            ColorMatrixColorFilter greyFilter = new ColorMatrixColorFilter(greyMatrix);
            if (coloredView != null) {
                coloredView.setColorFilter(greyFilter);
            }
        });
        animation.start();
    }

    public static void greyAllOut(List<ImageView> imageViews) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

        for (ImageView imageView : imageViews) {
            imageView.setColorFilter(filter);
        }
    }

    public static void rotate(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }
}
