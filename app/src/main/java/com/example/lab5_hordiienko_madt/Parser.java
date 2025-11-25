package com.example.lab5_hordiienko_madt;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Parser {

    //code written based on the professor's example
    public static ArrayList<String> getCurrencyRatesEur(InputStream stream) throws IOException {
        ArrayList<String> resultsList = new ArrayList<>();
        try {
            DocumentBuilderFactory xmlDocFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder xmlDocBuilder = xmlDocFactory.newDocumentBuilder();
            Document doc = xmlDocBuilder.parse(stream);

            NodeList rateNodes = doc.getElementsByTagName("item");
            for (int i = 0; i < rateNodes.getLength(); i++) {
                Element rateNode = (Element) rateNodes.item(i);
                String code = rateNode.getElementsByTagName("targetCurrency").item(0).getTextContent().trim();
                String rate = rateNode.getElementsByTagName("exchangeRate").item(0).getTextContent().trim();

                resultsList.add(code + " – " + rate);
            }
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        return resultsList;
    }
}
