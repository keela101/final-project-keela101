package com.chapman.seankeelan.pocketwordsfinal.api;

import android.util.Log;

import com.chapman.seankeelan.pocketwordsfinal.data.WordData;
import com.chapman.seankeelan.pocketwordsfinal.parser.MWDictionaryXMLParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DictionaryAPI
{
    private static final String LOGTAG = "DictionaryAPI";

    private static String base_collegiate_url = Constants.COLLEGIATE_URL;
    private static String base_spanish_url = Constants.SPANISH_URL;

    private static String collegiate_key = Constants.COLLEGIATE_KEY;
    private static String spanish_key = Constants.SPANISH_KEY;

    public static void LookupDef(String input, final Callback callback)
    {
        String collegiate_url = base_collegiate_url + input + "?key=" + collegiate_key;

        WordData defaultEntry = new WordData("We found nothing...");
        final List<WordData> defaultEntries = new ArrayList<WordData>();
        defaultEntries.add(defaultEntry);

        OkHttpClient client = new OkHttpClient();

        final Request req = new Request.Builder()
                .url(collegiate_url)
                .build();

        client.newCall(req).enqueue(new com.squareup.okhttp.Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                Log.i(LOGTAG, "We've got nothing...");
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Response response) throws IOException{
                MWDictionaryXMLParser parser = new MWDictionaryXMLParser();
                List<WordData> entries = new ArrayList<WordData>();
                try {
                    // The logs below are used to read the input stream. By knowing which tags are
                    // used and where they are placed, you can build the parser to account for their
                    // placements.

                    //Log.i(LOGTAG, "Printing Response body: " + response.body().string());
                    entries = parser.parse(response.body().byteStream());
                    if (entries.isEmpty())
                    {
                        entries = defaultEntries;
                    }
                    Log.i(LOGTAG, "First entry: " + entries.get(0).getDefinition());
                    //Log.i(LOGTAG, "Printing definition: " + entry.getDefinition());
                    Log.i(LOGTAG, "Printing InputStream in: " + response.body().byteStream());
                    //Log.i(LOGTAG, "Response body: " + response.body().string());
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                callback.onResult(entries);
            }
        });
    }

    public static void LookupTranslation(String input, final Callback callback)
    {
        String spanish_url = base_spanish_url + input + "?key=" + spanish_key;

        WordData defaultEntry = new WordData("We found nothing...");
        final List<WordData> defaultEntries = new ArrayList<WordData>();
        defaultEntries.add(defaultEntry);

        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .url(spanish_url)
                .build();

        client.newCall(req).enqueue(new com.squareup.okhttp.Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                Log.i(LOGTAG, "Nothin'");
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Response response) throws IOException{
                MWDictionaryXMLParser parser = new MWDictionaryXMLParser();
                List<WordData> entries = new ArrayList<WordData>();
                try {
                    //Log.i(LOGTAG, "Response body: " + response.body().string());
                    entries = parser.parse(response.body().byteStream());
                    if (entries.isEmpty())
                    {
                        entries = defaultEntries;
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                callback.onResult(entries);
            }
        });
    }

    public interface Callback
    {
        void onFailure(Exception e);
        void onResult(List<WordData> result);
    }
}
