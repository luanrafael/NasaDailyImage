package com.nasa.dailyimage;
/**
 * IotdHandler
 * This handler will collect the Image Of The Day
 *
 *  @author Geroen Joris - http://www.headfirstandroid.com/
 *
 */

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class IotdHandler extends DefaultHandler {
    private static final String TAG = IotdHandler.class.getSimpleName();

    private boolean inTitle = false;
    private boolean inDescription = false;
    private boolean inItem = false;
    private boolean inDate = false;

    private String url = null;
    private StringBuffer title = new StringBuffer();
    private StringBuffer description = new StringBuffer();
    private String date = null;

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {



        if (localName.equals("enclosure")) {
            url = attributes.getValue("url");
        }

        if (localName.startsWith("item")) {
            inItem = true;
        } else {
            if (inItem) {
                if (localName.equals("title")) {
                    inTitle = true;
                } else {
                    inTitle = false;
                }

                if (localName.equals("description")) {
                    inDescription = true;
                } else {
                    inDescription = false;
                }

                if (localName.equals("pubDate")) {
                    inDate = true;
                } else {
                    inDate = false;
                }
            }
        }

    }

    private IotdHandlerListener listener;

    public void characters(char ch[], int start, int length) {
        String chars = (new String(ch).substring(start, start + length));

        if (inTitle) {
            title.append(chars);
        }

        if (inDescription) {
            description.append(chars);
        }

        if (date == null) {
            //Example: Tue, 21 Dec 2010 00:00:00 EST
            String rawDate = chars;
            try {
                SimpleDateFormat parseFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
                Date sourceDate = parseFormat.parse(rawDate);

                SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, dd MMM yyyy");
                date = outputFormat.format(sourceDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void processFeed(URL url) {
        try {

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xr.setContentHandler(this);
            xr.parse(new InputSource(url.openStream()));
            date = new SimpleDateFormat("EEE, dd MMM yyyy").format(new Date());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (SAXException e) {
            Log.e(TAG, e.toString());
        } catch (ParserConfigurationException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void endElement(String uri, String localName, String qName) {
        if (url != null && title != null && description != null && date != null) {
            if (listener != null) {
                listener.iotdParsed(url, title.toString(), description.toString(), date);
                listener = null;
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title.toString();
    }

    public String getDescription() {
        return description.toString();
    }

    public String getDate() {
        return date;
    }


    public IotdHandlerListener getListener() {
        return listener;
    }

    public void setListener(IotdHandlerListener listener) {
        this.listener = listener;
    }


}