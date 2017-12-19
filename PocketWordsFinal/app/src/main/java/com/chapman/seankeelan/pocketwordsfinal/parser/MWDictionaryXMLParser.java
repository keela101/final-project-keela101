package com.chapman.seankeelan.pocketwordsfinal.parser;

import java.io.InputStream;
import java.io.IOException;


import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import java.util.List;

import android.util.Xml;
import android.util.Log;

import com.chapman.seankeelan.pocketwordsfinal.data.WordData;

// Uses code provided by Android Oreo Developer's Guide at:
// https://developer.android.com/reference/org/xmlpull/v1/XmlPullParser.html
// and:
// https://developer.android.com/training/basics/network-ops/xml.html
// Code has been changed to be able to parse the Merriam-Webster Collegiate Dictionary and
// Spanish-English dictionary XML files.

public class MWDictionaryXMLParser {
    private final String LOGTAG = "MWDictionaryXMLParser";

    private WordData data;
    private static final String ns = null;

    public List<WordData> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            Log.i(LOGTAG, "I'M TRYING");
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, "utf-8");
            parser.nextTag();
            return readFeed(parser);
        }
        catch (XmlPullParserException e)
        {
            Log.i(LOGTAG, "XmlPullParserException called");
            Log.i(LOGTAG, e.getMessage());
            e.getMessage();
        }
        catch (IOException e)
        {
            Log.i(LOGTAG, "IOException called");
            e.getMessage();
        }
        return new ArrayList<WordData>();
    }

    private List<WordData> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<WordData> entries = new ArrayList<WordData>();

        parser.require(XmlPullParser.START_TAG, ns, "entry_list");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.i(LOGTAG, "From readFeed, parser.getname() = " + name);
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }


    private WordData readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(LOGTAG, "At readEntry");

        String definition = "";
        String fl = "";

        parser.require(XmlPullParser.START_TAG, ns, "entry");
        WordData entry = new WordData("");

        while (parser.next() != XmlPullParser.END_TAG) {
            Log.i(LOGTAG, "Inside readEntry while loop");
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.i(LOGTAG, "Current name: " + name);
            // The functional label will be included in the definition to make showing the result
            // in the Textview easier.
            if (name.equals("fl")) {    // Each entry has only one functional label
                fl = "\n" + readText(parser);
                entry.setDefinition(fl);
            }
            else if (name.equals("def")) {
                definition = readDefinition(parser);
                Log.i(LOGTAG, "Definition: " + definition);
                entry.setDefinition(definition);
            }
            else {
                skip(parser);
            }
        }
        return entry;
    }

    private String readDefinition(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(LOGTAG, "At readDefinition");
        parser.require(XmlPullParser.START_TAG, ns, "def");
        String definition = "";
        String senseNumber = "";    // <sn> in XML file
        String definingText = "";   // <dt>
        String senseDivider = "";   // <sd>

        while (parser.next() != XmlPullParser.END_TAG) {
            Log.i(LOGTAG, "Inside readDefinition while loop");
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.i(LOGTAG, "readDefinition current name: " + name);
            // Check sn, dt, sd, sx
            if (name.equals("sn")) {
                senseNumber = readSn(parser);
                Log.i(LOGTAG, "Sense number: " + senseNumber);
                definition = definition.concat("\n" + senseNumber);
            }
            else if (name.equals("dt")) {
                definingText = readDt(parser);
                Log.i(LOGTAG, "Defining text: " + definingText);
                definition = definition.concat(definingText);
            }
            else if (name.equals("sd")) {
                senseDivider = readSd(parser);
                Log.i(LOGTAG, "Sense divider: " + senseDivider);
                definition = definition.concat(senseDivider);
            }
            else if (name.equals("sx")) {

            }
            else {
                skip(parser);
            }
        }
        //String definition = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "def");
        Log.i(LOGTAG, "End of definition!");
        return definition;
    }

    private String readSn(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "sn");
        String sn = "";
        String snp = "";

        sn = readText(parser);
        if (parser.getName().equals("snp"))
        {
            snp = readText(parser);
            sn = sn.concat(snp);
            parser.next();
        }

        parser.require(XmlPullParser.END_TAG, ns, "sn");
        return sn;
    }

    // <dt> tag and its potential inner tags
    // Ignoring <un> tag for now
    private String readDt(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "dt");
        String dt = "";
        String sx = "";
        String un = "";
        String reflink = "";
        String vi = "";

        dt = readText(parser);

        while (parser.getEventType() != XmlPullParser.END_TAG)
        {
            if (parser.getName() != null && parser.getName().equals("sx"))
            {
                sx = readSx(parser);
                dt = dt.concat(sx);
                parser.next();  // Pass over <sx> END_TAG
            }
            else if (parser.getName() != null && parser.getName().equals("un"))
            {
                un = readUn(parser);
                dt = dt.concat(un);
                parser.next();  // Pass over <un> END_TAG
            }
            else if (parser.getName() != null && parser.getName().equals("ref-link"))
            {
                reflink = "; " + readRefLink(parser);
                dt = dt.concat(reflink);
                parser.next();
            }
            else if (parser.getName() != null && !parser.getName().equals("dt"))
            {
                Log.i(LOGTAG, "Encountered unwanted tag");
                skip(parser);
                parser.next();  // Pass over the <un> or <vi> END_TAG
            }
            else if (parser.getName() == null)
            {
                parser.next();
            }
            else
            {
                parser.next();
            }
        }

        Log.i(LOGTAG, "attempting to read dt END_TAG at: " + parser.getName());
        parser.require(XmlPullParser.END_TAG, ns, "dt");
        return dt;
    }

    private String readSx(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "sx");
        String sx = "";
        String sxn = "";

        sx = readText(parser);  // Reads text in sx
        if(parser.getName().equals("sxn"))
        {
            sxn = readSxn(parser);
            sx = sx.concat(sxn);
            parser.next();  // Pass over <sxn> END_TAG
        }

        parser.require(XmlPullParser.END_TAG, ns, "sx");
        Log.i(LOGTAG, "sx END_TAG read");
        return sx;
    }

    private String readSxn(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "sxn");
        String sxn = "";

        sxn = readText(parser); // Calls parser.next() if there is a text body here

        parser.require(XmlPullParser.END_TAG, ns, "sxn");
        return sxn;
    }

    private String readUn(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "un");
        String un = "";

        un = readText(parser);
        while (parser.getEventType() != XmlPullParser.END_TAG)
        {
            if(parser.getName() != null && !parser.getName().equals("un"))
            {
                skip(parser);
                parser.next();
            }
            else if(parser.getName() == null)
            {
                parser.next();
            }
        }

        parser.require(XmlPullParser.END_TAG, ns, "un");
        return un;
    }

    private String readRefLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "ref-link");
        String refLink = "";

        refLink = readText(parser); // Calls parser.next() if there is a text body here

        parser.require(XmlPullParser.END_TAG, ns, "ref-link");
        return refLink;
    }

    // <sd> tag
    private String readSd(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "sd");
        String sd = "";
        String snp = "";    // If there's an snp (parenthesized number) in the sd parameters,
                            // this will call the readSnp() String and concatenate it to the
                            // String sd.

        sd = readText(parser);
        Log.i(LOGTAG, "Got sd text, current name: " + parser.getName());

        parser.require(XmlPullParser.END_TAG, ns, "sd");
        return sd;
    }

    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(LOGTAG, "At readText, name = " + parser.getName());
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            Log.i(LOGTAG, "Read text: " + result);
            parser.nextTag();
            Log.i(LOGTAG, "successful move to " + parser.getName());
        }
        return result;
    }

    private String readSnpText(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(LOGTAG, "In readSnpText");
        parser.require(XmlPullParser.START_TAG, ns, "snp");
        String snp = "";

        if (parser.next() == XmlPullParser.TEXT)
        {
            snp = parser.getText();
            Log.i(LOGTAG, "Parenthesized number: " + snp);
            parser.next();
        }

        parser.require(XmlPullParser.END_TAG, ns, "snp");
        parser.next();
        return snp;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Log.i(LOGTAG, "At skip function");
        if (parser.getEventType() != XmlPullParser.START_TAG)
        {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next())
            {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}