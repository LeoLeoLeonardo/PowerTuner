package com.example.powertuner;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Bienvenido extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido);

        ImageView imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView);

        startAnimations(imageView, textView);
    }

    private void startAnimations(View imageView, View textView) {
        // Animación de aparición (fade-in)
        ObjectAnimator fadeInImage = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
        fadeInImage.setDuration(1000);

        ObjectAnimator fadeInText = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
        fadeInText.setDuration(1000);

        // Animación de luz (brillo con escalado)
        ObjectAnimator scaleXImage = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleYImage = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.2f, 1f);
        scaleXImage.setDuration(1000);
        scaleYImage.setDuration(1000);

        ObjectAnimator scaleXText = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleYText = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.2f, 1f);
        scaleXText.setDuration(1000);
        scaleYText.setDuration(1000);

        // Agrupar animaciones
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeInImage, fadeInText);
        animatorSet.play(scaleXImage).with(scaleYImage).after(fadeInImage);
        animatorSet.play(scaleXText).with(scaleYText).after(scaleXImage);

        // Al finalizar la animación, iniciar MainActivity
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                startActivity(new Intent(Bienvenido.this, MainActivity.class));
                finish();
            }
        });

        animatorSet.start();
    }
}
