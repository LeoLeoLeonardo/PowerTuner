package com.example.powertuner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class TunerView extends View {

    private Paint paint;
    private double currentFrequency = 0;
    private String currentNote = "No detectada";
    private TextView notaTextView;

    public void setNotaTextView(TextView textView) {
        this.notaTextView = textView;
    }

    private void updateNoteDisplay(String note) {
        currentNote = note;
        if (notaTextView != null) {
            notaTextView.post(() -> notaTextView.setText(note));
        }
        invalidate();
    }

    // Notas y sus frecuencias (C0 a B8)
    private static final double[] NOTES_FREQUENCIES = {
            16.35, 17.32, 18.35, 19.45, 20.60, 21.83, 23.12, 24.50, 25.96, 27.50, 29.14, 30.87,
            32.70, 34.65, 36.71, 38.89, 41.20, 43.65, 46.25, 49.00, 51.91, 55.00, 58.27, 61.74,
            65.41, 69.30, 73.42, 77.78, 82.41, 87.31, 92.50, 98.00, 103.83, 110.00, 116.54, 123.47,
            130.81, 138.59, 146.83, 155.56, 164.81, 174.61, 184.99, 195.99, 207.65, 220.00, 233.08, 246.94,
            261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 392.00, 415.30, 440.00, 466.16, 493.88,
            523.25, 554.37, 587.33, 622.25, 659.25, 698.46, 739.99, 783.99, 830.61, 880.00, 932.33, 987.77
    };

    public TunerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
    }

    public void updateFFTData(double frequency) {
        this.currentFrequency = frequency;
        String note = getClosestNote(frequency);
        updateNoteDisplay(note);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Fondo de la barra de afinación
        paint.setColor(Color.LTGRAY);
        canvas.drawRoundRect(0f, height / 2f - 20, width, height / 2f + 20, 20f, 20f, paint);

        // Indicador de frecuencia
        float notePosition = getNotePosition(currentFrequency);
        paint.setColor(Color.GREEN);
        canvas.drawRect(notePosition - 5, height / 2f - 20, notePosition + 5, height / 2f + 20, paint);
    }

    private String getClosestNote(double frequency) {
        int closestIndex = 0;
        double minDiff = Double.MAX_VALUE;

        for (int i = 0; i < NOTES_FREQUENCIES.length; i++) {
            double diff = Math.abs(NOTES_FREQUENCIES[i] - frequency);
            if (diff < minDiff) {
                minDiff = diff;
                closestIndex = i;
            }
        }

        String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        int octave = closestIndex / 12;
        String note = NOTE_NAMES[closestIndex % 12] + octave;
        return note;
    }

    private float getNotePosition(double frequency) {
        int width = getWidth();
        int centerX = width / 2;

        int closestIndex = 0;
        double minDiff = Double.MAX_VALUE;

        for (int i = 0; i < NOTES_FREQUENCIES.length; i++) {
            double diff = Math.abs(NOTES_FREQUENCIES[i] - frequency);
            if (diff < minDiff) {
                minDiff = diff;
                closestIndex = i;
            }
        }

        double referenceFreq = NOTES_FREQUENCIES[closestIndex];
        double centDifference = 1200 * Math.log(frequency / referenceFreq) / Math.log(2);

        // Define un rango de ±50 cent para el centro de la barra
        double maxCents = 50;
        double positionShift = (centDifference / maxCents) * (width / 2);

        return (float) (centerX + positionShift);
    }
}
