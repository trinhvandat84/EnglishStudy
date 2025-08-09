package jp.ac.jec.cm0122.android114.API;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import jp.ac.jec.cm0122.android114.Helpers.UpdateWordCallback;
import jp.ac.jec.cm0122.android114.Models.DictionaryResponse;
import jp.ac.jec.cm0122.android114.Models.Word;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {

    private static final String TAG = "NetworkManager";

    public static List<DictionaryResponse> getJson(String word) throws IOException {
        String urlStr = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream in = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            if (null == encoding) {
                encoding = "UTF-8";
            }
            StringBuffer result = new StringBuffer();
            final InputStreamReader inputStreamReader = new InputStreamReader(in, encoding);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            System.out.println(result);
            Gson gson = new Gson();
            List<DictionaryResponse> wordData = gson.fromJson(result.toString(), new TypeToken<List<DictionaryResponse>>() { }.getType());
            System.out.println("Word: " + wordData.get(0).getWord());
            return wordData;
        }
        return null;
    }

    public static void addWord(FirebaseUser user, Word word) {

        Log.d(TAG, "word: " + word.toString());

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("23cm0122.main.jp");
        uriBuilder.path("studyenglish/addword.php");

        final FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("userID", user.getUid());
     //   formBuilder.add("userID", "abc");
        formBuilder.add("word", word.getEnglish());
    //    formBuilder.add("meaning", "abbc");
        if (word.getMeaning() == null) {
            formBuilder.add("meaning", "");
        } else {
            formBuilder.add("meaning", word.getMeaning());
        }
        if (word.getPronunciation() == null) {
            formBuilder.add("pronunciation", "");
        } else {
            formBuilder.add("pronunciation", word.getPronunciation());
        }
        if (word.getAudioUrl() != null) {
            formBuilder.add("audioURL", word.getAudioUrl());
        } else {
            formBuilder.add("audioURL", "");
        }
        RequestBody requestBody = formBuilder.build();

        Log.d(TAG, "uriBuilder: " + uriBuilder);
        final Request request = new Request.Builder()
                .url(uriBuilder.toString())
                .post(requestBody)
                .build();

        final OkHttpClient client = new OkHttpClient.Builder().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String resString = response.body().string();
                Log.d(TAG, "onResponse: " + resString);
            }
        });

    }

    public static List<Word> getAllWords(String userID) throws IOException {
        String urlString = "https://23cm0122.main.jp/studyenglish/getallwords.php?userID=" + userID;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream in = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            if (null == encoding) {
                encoding = "UTF-8";
            }
            StringBuffer result = new StringBuffer();
            final InputStreamReader inputStreamReader = new InputStreamReader(in, encoding);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();

            Gson gson = new Gson();
            List<Word> wordData = gson.fromJson(result.toString(), new TypeToken<List<Word>>() { }.getType());
            Log.d(TAG, "JSON: " + gson.fromJson(result.toString(), new TypeToken<List<Word>>() { }.getType()));
            return wordData;
        }
        return null;
    }

    public static void updateWord(String word, String newMeaning, UpdateWordCallback callback) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("23cm0122.main.jp");
        uriBuilder.path("studyenglish/editword.php");

        final FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("userID", FirebaseAuth.getInstance().getUid());
        formBuilder.add("word", word);
        formBuilder.add("meaning", newMeaning);

        RequestBody requestBody = formBuilder.build();

        Log.d(TAG, "uriBuilder: " + uriBuilder);
        final Request request = new Request.Builder()
                .url(uriBuilder.toString())
                .post(requestBody)
                .build();

        final OkHttpClient client = new OkHttpClient.Builder().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: ", e);
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "isSuccessful: " + response.isSuccessful());
                callback.onSuccess(response.isSuccessful());
            }
        });
    }

    public static void deleteWord(String word, UpdateWordCallback callback) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("23cm0122.main.jp");
        uriBuilder.path("studyenglish/deleteword.php");

        final FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("userID", FirebaseAuth.getInstance().getUid());
        formBuilder.add("word", word);

        RequestBody requestBody = formBuilder.build();

        Log.d(TAG, "uriBuilder: " + uriBuilder);
        final Request request = new Request.Builder()
                .url(uriBuilder.toString())
                .post(requestBody)
                .build();

        final OkHttpClient client = new OkHttpClient.Builder().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: ", e);
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "isSuccessful: " + response.isSuccessful());
                callback.onSuccess(response.isSuccessful());
            }
        });
    }
}
