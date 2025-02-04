package com.example.powertuner;

import org.jtransforms.fft.DoubleFFT_1D;

public class FFTUtils {

    // Método para aplicar FFT usando JTransforms y obtener la frecuencia dominante
    public static double getDominantFrequency(short[] audioBuffer, int sampleRate) {
        int n = audioBuffer.length;

        // Convertir los datos de 16-bit a un arreglo de double
        double[] audioData = new double[n];
        for (int i = 0; i < n; i++) {
            audioData[i] = audioBuffer[i];
        }

        // Crear la instancia de FFT
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(audioData);

        // Extraer magnitudes de las frecuencias
        double[] magnitudes = new double[n / 2];
        int maxIndex = 0;
        double maxMagnitude = 0;

        for (int i = 0; i < n / 2; i++) {
            magnitudes[i] = Math.sqrt(audioData[2 * i] * audioData[2 * i] + audioData[2 * i + 1] * audioData[2 * i + 1]);

            // Encontrar el pico más alto en la FFT
            if (magnitudes[i] > maxMagnitude) {
                maxMagnitude = magnitudes[i];
                maxIndex = i;
            }
        }
        // Convertir el índice de la FFT en una frecuencia
        double dominantFrequency = (double) maxIndex * sampleRate / n;
        return dominantFrequency;
    }

    // Método para obtener la nota a partir de la frecuencia detectada
    public static String[] getNotesFromFrequency(double frequency) {
        String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        double A4 = 440.0;

        int n = (int) Math.round(12 * Math.log(frequency / A4) / Math.log(2));
        int octave = (n / 12) + 4;

        int index = (n + 9) % 12;
        if (index < 0) index += 12;

        int prevIndex = (index - 1 + 12) % 12;
        int nextIndex = (index + 1) % 12;

        String prevNote = notes[prevIndex] + (prevIndex == 11 ? octave - 1 : octave);
        String currentNote = notes[index] + octave;
        String nextNote = notes[nextIndex] + (nextIndex == 0 ? octave + 1 : octave);

        return new String[]{prevNote, currentNote, nextNote};
    }

    public static double[] computeFFT(short[] audioBuffer, int sampleRate) {
        int n = audioBuffer.length;
        double[] audioData = new double[n];

        for (int i = 0; i < n; i++) {
            audioData[i] = audioBuffer[i];
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(audioData);

        double[] magnitudes = new double[n / 2];

        for (int i = 0; i < n / 2; i++) {
            magnitudes[i] = Math.sqrt(audioData[2 * i] * audioData[2 * i] + audioData[2 * i + 1] * audioData[2 * i + 1]);
        }
        return magnitudes; // Devuelve el espectro completo
    }


}
