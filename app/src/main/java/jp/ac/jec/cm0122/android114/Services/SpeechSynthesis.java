package jp.ac.jec.cm0122.android114.Services;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.Connection;
import com.microsoft.cognitiveservices.speech.PropertyId;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpeechSynthesis {
    private static final String TAG = "SpeechSynthesis";
    // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
    private static String speechKey = "izKZSTAT47XRnDV2rqJOkc0E7kC6c5rd0daIp0QAtXw9F6JNlHjzJQQJ99BBACYeBjFXJ3w3AAAYACOGizrn";
    private static String speechRegion = "eastus";

    private SpeechConfig speechConfig;
    private SpeechSynthesizer synthesizer;
    private Connection connection;
    private AudioTrack audioTrack;
    private SpeakingRunnable speakingRunnable;
    private ExecutorService singleThreadExecutor;
    private final Object synchronizedObj = new Object();
    private boolean stopped = false;


    public void textToSpeech(String prompt) {
        if (synthesizer != null) {
            speechConfig.close();
            connection.close();
            synthesizer.close();
        }

        singleThreadExecutor = Executors.newSingleThreadExecutor();
        speakingRunnable = new SpeakingRunnable();

        audioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build(),
                new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(24000)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                AudioTrack.getMinBufferSize(
                        24000,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT) * 2,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);

        speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Raw24Khz16BitMonoPcm);
        speechConfig.setSpeechSynthesisVoiceName("en-US-AvaMultilingualNeural");

        synthesizer = new SpeechSynthesizer(speechConfig, null);
        connection = Connection.fromSpeechSynthesizer(synthesizer);
        connection.connected.addEventListener((o, e) -> {
            Log.d(TAG, "textToSpeech: " + "Connection established");
        });

        connection.disconnected.addEventListener((o, e) -> {
            Log.d(TAG, "textToSpeech: " + "Disconnected");
        });

        synthesizer.SynthesisStarted.addEventListener((o, e) -> {
            Log.d(TAG, "textToSpeech: " + "Synthesis started. Result Id: %s.\n");
            e.close();
        });

        synthesizer.Synthesizing.addEventListener((o, e) -> {
            Log.d(TAG, "textToSpeech: " + "Synthesizing. received %d bytes.\n");
            e.close();
        });

        synthesizer.SynthesisCompleted.addEventListener((o, e) -> {
            Log.d(TAG, "textToSpeech: " + "\tFirst byte latency: " + e.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisFirstByteLatencyMs) + " ms.\n");
            Log.d(TAG, "\tFinish latency: " + e.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisFinishLatencyMs) + " ms.\n");
            e.close();
        });

        synthesizer.SynthesisCanceled.addEventListener((o, e) -> {
            String cancellationDetails =
                    SpeechSynthesisCancellationDetails.fromResult(e.getResult()).toString();
            Log.e(TAG, "ERROR: " + "Error synthesizing. Result ID: " + e.getResult().getResultId() +
                    ". Error detail: " + System.lineSeparator() + cancellationDetails +
                    System.lineSeparator() + "Did you update the subscription info?");
            e.close();
        });

        synthesizer.WordBoundary.addEventListener((o, e) -> {

        });


        // Get text from the console and synthesize to the default speaker.
        onPreConnectButtonClicked();
        if (prompt.isEmpty()) {
            return;
        }
        onSpeechButtonClicked(prompt);

    }

    public void onPreConnectButtonClicked() {
        // This method could pre-establish the connection to service to lower the latency
        // This method is useful when you want to synthesize audio in a short time, but the text is
        // not available. E.g. for speech bot, you can warm up the TTS connection when the user is speaking;
        // then call speak() when dialogue utterance is ready.
        if (connection == null) {
            Log.d(TAG, "\"Please initialize the speech synthesizer first\\n\": ");
            return;
        }
        connection.openConnection(true);
    }

    public void onSpeechButtonClicked(String prompt) {
        if (synthesizer == null) {
            Log.d(TAG, "Please initialize the speech synthesizer first");
            return;
        }
        speakingRunnable.setContent(prompt);
        singleThreadExecutor.execute(speakingRunnable);
    }

    public void onStopButtonClicked() {
        if (synthesizer == null) {
            Log.d(TAG, "Please initialize the speech synthesizer first");
            return;
        }

        stopSynthesizing();
    }

    private void stopSynthesizing() {
        if (synthesizer != null) {
            synthesizer.StopSpeakingAsync();
        }
        if (audioTrack != null) {
            synchronized (synchronizedObj) {
                stopped = true;
            }
            audioTrack.pause();
            audioTrack.flush();
        }
    }



    class SpeakingRunnable implements Runnable {
        private String content;

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public void run() {
            try {
                audioTrack.play();
                synchronized (synchronizedObj) {
                    stopped = false;
                }

                SpeechSynthesisResult result = synthesizer.StartSpeakingTextAsync(content).get();
                AudioDataStream audioDataStream = AudioDataStream.fromResult(result);

                // Set the chunk size to 50 ms. 24000 * 16 * 0.05 / 8 = 2400
                byte[] buffer = new byte[2400];
                while (!stopped) {
                    long len = audioDataStream.readData(buffer);
                    if (len == 0) {
                        break;
                    }
                    audioTrack.write(buffer, 0, (int) len);
                }

                audioDataStream.close();
            } catch (Exception ex) {
                Log.e("Speech Synthesis Demo", "unexpected " + ex.getMessage());
                ex.printStackTrace();
                assert (false);
            }
        }
    }
}


