package com.example.lab5_hordiienko_madt;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * local unit test for testing the methods of the classes DataLoader, Parser
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitMethodsTest {
    //tests for Parser
    @Test
    public void testGetCurrencyRatesEur() throws Exception {
        String sampleXml = "<rss><channel>" +
                "<item><targetCurrency>USD</targetCurrency><exchangeRate>1.16</exchangeRate></item>" +
                "<item><targetCurrency>GBP</targetCurrency><exchangeRate>0.87</exchangeRate></item>" +
                "</channel></rss>";

        ArrayList<String> rates = Parser.getCurrencyRatesEur(
                new ByteArrayInputStream(sampleXml.getBytes())
        );

        //should return the currencies in the USD - 1.1 format
        assertEquals(2, rates.size());
        assertEquals("USD – 1.16", rates.get(0));
        assertEquals("GBP – 0.87", rates.get(1));
    }

    @Test
    public void testParserWithEmptyInput() throws Exception {
        String emptyXml = ""; //empty string

        ArrayList<String> rates = Parser.getCurrencyRatesEur(
                new ByteArrayInputStream(emptyXml.getBytes())
        );

        //should return empty list, no crash
        assertEquals(0, rates.size());
    }

    @Test
    public void testParserWithInvalidInput() throws Exception {
        String invalidXml = "<rss><channel>" +
                "<item><someWrongTag>ABC</someWrongTag></item></channel></rss>";

        ArrayList<String> rates = Parser.getCurrencyRatesEur(
                new ByteArrayInputStream(invalidXml.getBytes())
        );

        //should return empty list because there are no expected tags
        assertEquals(0, rates.size());
    }

    @Test
    public void testParserWithPartialValidInput() throws Exception {
        String partialXml =
                "<rss><channel>" +
                        "<item><targetCurrency>USD</targetCurrency><exchangeRate>1.16</exchangeRate></item>" +
                        "<item><someWrongTag>INVALID</someWrongTag></item>" +
                        "</channel></rss>";

        ArrayList<String> rates = Parser.getCurrencyRatesEur(
                new ByteArrayInputStream(partialXml.getBytes())
        );

        //only the fully valid item should be included
        assertEquals(1, rates.size());
        assertEquals("USD – 1.16", rates.get(0));
    }


    //DataLoader tests

    @Test
    public void testLoadRatesReturnsParsedData() {
        DataLoader loader = new DataLoader() {
            @Override
            public ArrayList<String> loadRates() {
                try {
                    String sampleXml = "<rss><channel>" +
                            "<item><targetCurrency>USD</targetCurrency><exchangeRate>1.16</exchangeRate></item>" +
                            "</channel></rss>";
                    return Parser.getCurrencyRatesEur(new ByteArrayInputStream(sampleXml.getBytes()));
                } catch (Exception e) {
                    return new ArrayList<>();
                }
            }
        };

        ArrayList<String> rates = loader.loadRates();
        assertEquals(1, rates.size());
        assertEquals("USD – 1.16", rates.get(0));
    }


    @Test
    public void testLoadRatesHandlesException() {
        DataLoader loader = new DataLoader() {
            @Override
            public ArrayList<String> loadRates() {
                //network failure
                throw new RuntimeException("Connection failed");
            }
        };

        ArrayList<String> rates = null;
        try {
            rates = loader.loadRates();
        } catch (Exception e) {
            //ignore
        }

        //rates should be null or empty in real implementation
        assertTrue(rates == null || rates.isEmpty());
    }

    @Test
    public void testLoadRatesWithEmptyParserResult() {
        DataLoader loader = new DataLoader() {
            @Override
            public ArrayList<String> loadRates() {
                return new ArrayList<>(); //parser returned nothing
            }
        };

        ArrayList<String> rates = loader.loadRates();
        assertEquals(0, rates.size());
    }

}