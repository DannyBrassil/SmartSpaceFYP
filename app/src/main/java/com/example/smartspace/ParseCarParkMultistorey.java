package com.example.smartspace;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ParseCarParkMultistorey extends AsyncTask {


    @Override
    protected Object doInBackground(Object[] objects) {
        String url = "https://data.gov.ie/dataset/multi-story-car-parking-space-availability/resource/a5012481-97a2-45f8-b735-1f7f0e7ba4ce/view/a52f3ec5-a4f1-4d5b-93d9-c492922de1e2";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = builder.parse(url);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }



         //   Log.w("Root element: " , doc.getDocumentElement().getNodeName()); //<xml-tables>

        Log.w("carparks", doc.getDocumentElement().getNodeName());

            NodeList carparks = doc.getElementsByTagName("carpark");

            for (int i = 0; i < carparks.getLength(); i++) {
                Node carpark = carparks.item(i);
                Log.w("carparks", carpark.getNodeName());

                if (carpark.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) carpark;

                    Node name = elem.getElementsByTagName("name").item(0);
                    String CarparkName = name.getTextContent();
                    Log.w("carpark name", CarparkName);

                }
            }

        return null;
    }
}
