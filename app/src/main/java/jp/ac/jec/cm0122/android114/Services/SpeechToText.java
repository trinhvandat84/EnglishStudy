package jp.ac.jec.cm0122.android114.Services;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SpeechToText {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription("izKZSTAT47XRnDV2rqJOkc0E7kC6c5rd0daIp0QAtXw9F6JNlHjzJQQJ99BBACYeBjFXJ3w3AAAYACOGizrn", "eastus");
    public static void fromMic(SpeechConfig speechConfig) throws InterruptedException, ExecutionException {
        AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();
        SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);
        System.out.println("Speak into your microphone.");
        Future<SpeechRecognitionResult> task = speechRecognizer.recognizeOnceAsync();
        SpeechRecognitionResult speechRecognitionResult = task.get();
        System.out.println("RECOGNIZED: Text=" + speechRecognitionResult.getText());
    }
}
