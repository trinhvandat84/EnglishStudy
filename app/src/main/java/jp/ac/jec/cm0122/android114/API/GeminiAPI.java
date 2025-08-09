package jp.ac.jec.cm0122.android114.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.ac.jec.cm0122.android114.Helpers.GeminiCallback;

public class GeminiAPI {
    private static final String TAG = "GeminiAPI";
    GenerativeModel gm = new GenerativeModel(
            "gemini-2.0-flash",
            "AIzaSyA74UlibgAsMOxuUD4yyrQvVHqhqiRt7iI");


    public void chatWithAI(String prompt, GeminiCallback callback) {
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(prompt)
                .build();
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String responseText = result.getText();
                callback.onResponse(responseText);
                Log.d(TAG, "response: " + responseText);
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                callback.onError("ERROR");
            }
        }, executor);
    }

    public void speakWithAI(String prompt, GeminiCallback callback) {
        // The Gemini 1.5 models are versatile and work with multi-turn conversations (like chat)
        GenerativeModel gm = new GenerativeModel(
                "gemini-2.0-flash",
                "AIzaSyA74UlibgAsMOxuUD4yyrQvVHqhqiRt7iI");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

// (optional) Create previous chat history for context
        Content.Builder userContentBuilder = new Content.Builder();
        userContentBuilder.setRole("user");
        userContentBuilder.addText("prompt");
        Content userContent = userContentBuilder.build();

        Content.Builder modelContentBuilder = new Content.Builder();
        modelContentBuilder.setRole("model");
        modelContentBuilder.addText("Great to meet you. What would you like to know?");
        Content modelContent = userContentBuilder.build();

        List<Content> history = Arrays.asList(userContent, modelContent);

// Initialize the chat
        ChatFutures chat = model.startChat(history);

// Create a new user message
        Content.Builder userMessageBuilder = new Content.Builder();
        userMessageBuilder.setRole("user");
        userMessageBuilder.addText(prompt);
        Content userMessage = userMessageBuilder.build();

        Executor executor = Executors.newSingleThreadExecutor();
// Send the message
        ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessage);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                callback.onResponse(resultText);
                Log.d(TAG, "responseAI: " + resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
    }

}