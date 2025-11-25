package com.example.lab5_hordiienko_madt;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DataLoader {

    //downloads and parses EUR-based exchange rates using the floatrates eur.xml
    public ArrayList<String> loadRates() {
        ArrayList<String> rates = new ArrayList<>();

        try {
            String urlString = "https://www.floatrates.com/daily/eur.xml";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            InputStream stream = connection.getInputStream();

            //using parser method
            rates = Parser.getCurrencyRatesEur(stream);

            stream.close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rates;
    }
}
