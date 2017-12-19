package com.chapman.seankeelan.pocketwordsfinal.activities;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chapman.seankeelan.pocketwordsfinal.R;
import com.chapman.seankeelan.pocketwordsfinal.api.DictionaryAPI;
import com.chapman.seankeelan.pocketwordsfinal.data.WordData;


import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private ScrollView fullDefinitionScrollView;
    private ScrollView translationScrollView;

    private TextView wordTextView;
    private TextView definitionTextView;
    private TextView translationTextView;
    private TextView engToSpanTranslationTextView;

    private LinearLayout fullDefinitionsLayout;
    private LinearLayout translationLayout;

    private EditText searchbarEditText;

    private Button defineButton;
    private Button translateButton;

    private ProgressBar loadingWheel;

    public String word;
    public WordData entry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Layouts
        this.wordTextView = findViewById(R.id.tv_searched_word);
        this.fullDefinitionsLayout = findViewById(R.id.vg_full_definition);
        this.translationLayout = findViewById(R.id.vg_translation);

        // ScrollViews
        this.fullDefinitionScrollView = findViewById(R.id.sv_full_definition);
        this.translationScrollView = findViewById(R.id.sv_translation);

        // TextViews
        this.definitionTextView = findViewById(R.id.tv_word_definition);
        this.translationTextView = findViewById(R.id.tv_translation);

        // EditText
        this.searchbarEditText = findViewById(R.id.et_searchbar);

        // Buttons
        this.defineButton = findViewById(R.id.btn_define);
        this.translateButton = findViewById(R.id.btn_translate);

        // ProgressBar
        this.loadingWheel = findViewById(R.id.pb_loading_wheel);

        // WordData object
        this.entry = new WordData("");

        // Before a word is searched, these views are gone, and do not take up space.
        // Once a word is searched for either its full definition, its English translation, or
        // its Spanish translation, their corresponding layouts, which contain TextViews showing
        // the word and definition, will become visible.

        fullDefinitionScrollView.setVisibility(View.GONE);
        translationScrollView.setVisibility(View.GONE);

        defineButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                word = getWord();

                lookupDef(word);
            }
        });

        translateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                word = getWord();

                lookupTranslation(word);
            }
        });
    }

    private void toggleLoading(boolean loading)
    {
        this.loadingWheel.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    // Returns input text in EditText widget
    public String getWord()
    {
        this.word = searchbarEditText.getText().toString();
        if (isEmpty(searchbarEditText))
        {
            this.word = "We found nothing!";
        }
        return this.word;
    }

    // Check to see if the EditText widget is empty by seeing if the trimmed string length
    // is equal to 0.
    private boolean isEmpty(EditText editText)
    {
        return searchbarEditText.getText().toString().trim().length() == 0;
    }

    private void lookupDef(final String searchWord)
    {
        DictionaryAPI.LookupDef(searchWord, new DictionaryAPI.Callback() {
            @Override
            public void onFailure(Exception e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        toggleLoading(false);
                    }
                });
            }

            public void onResult(final List<WordData> result)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String resultString = "";
                        for (int i = 0; i < result.size(); i++)
                        {
                            resultString = resultString.concat(result.get(i).getDefinition());
                            resultString = resultString.concat("\n");
                        }
                        wordTextView.setText(searchWord);
                        definitionTextView.setText(resultString);
                        toggleLoading(false);
                    }
                });
            }
        });
        translationScrollView.setVisibility(View.GONE);
        fullDefinitionScrollView.setVisibility(View.VISIBLE);
    }

    private void lookupTranslation(final String searchWord)
    {
        DictionaryAPI.LookupTranslation(searchWord, new DictionaryAPI.Callback() {
            @Override
            public void onFailure(Exception e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        toggleLoading(false);
                    }
                });
            }

            public void onResult(final List<WordData> result)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String resultString = "";
                        for (int i = 0; i < result.size(); i++)
                        {
                            resultString = resultString.concat(result.get(i).getDefinition());
                            resultString = resultString.concat("\n");
                        }

                        wordTextView.setText(searchWord);
                        translationTextView.setText(resultString);
                        toggleLoading(false);
                    }
                });
            }
        });

        fullDefinitionScrollView.setVisibility(View.GONE);
        translationScrollView.setVisibility(View.VISIBLE);
    }

}
