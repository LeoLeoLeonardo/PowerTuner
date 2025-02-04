package com.example.powertuner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MICROPHONE = 1;
    private static final int SAMPLE_RATE = 44100;

    private AudioRecord audioRecord;
    private int bufferSize;
    private short[] audioBuffer;
    private boolean isRecording = false;

    private TunerView tunerView;
    private TextView notaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tunerView = findViewById(R.id.tunerView);
        notaTextView = findViewById(R.id.nota);

        // Verificar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MICROPHONE);
        } else {
            setupAudioRecord();
        }

        // Pasar el TextView a TunerView
        tunerView.setNotaTextView(notaTextView);
    }

    private void setupAudioRecord() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioBuffer = new short[bufferSize];

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

            startRecording();
        }
    }

    private void startRecording() {
        if (audioRecord == null) return;

        isRecording = true;
        audioRecord.startRecording();

        new Thread(() -> {
            while (isRecording) {
                int read = audioRecord.read(audioBuffer, 0, bufferSize);
                if (read > 0) {
                    processAudio(audioBuffer);
                }
            }
        }).start();
    }

    private void processAudio(short[] audioBuffer) {
        double frequency = FFTUtils.getDominantFrequency(audioBuffer, SAMPLE_RATE);
        String[] notes = FFTUtils.getNotesFromFrequency(frequency);

        runOnUiThread(() -> {
            tunerView.updateFFTData(frequency);
            notaTextView.setText(notes[1]); // Nota detectada
            ((TextView) findViewById(R.id.pre)).setText(notes[0]); // Nota anterior
            ((TextView) findViewById(R.id.post)).setText(notes[2]); // Nota siguiente
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }
}
