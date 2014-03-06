package com.StockTake;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.StringTokenizer;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class for parsing the json and csv stock feeds
 */
public class FeedParser {
    public static final float C_DIV_AMOUNT = 100.0f;
    public static final String TARGET_STRING = ",";
    public static final String REPLACEMENT_STRING = "";
    public static final String IS_USING_MOCK_DATA = "is_using_mock_data";
    public static final String STOCK_SYMBOL_EXTENSION = ".L";
    public static final String MONTH_PARAMETER = "&a=";
    public static final String DAY_PARAMETER = "&b=";
    public static final String YEAR_PARAMETER = "&c=";
    public static final String TEST_DATA_FILE_EXTENSION = ".csv";
    public static final String MILLIONS = "M";
    public static final String THOUSANDS = "K";
    private Context m_context;

    public FeedParser(Context m_context) {
        this.m_context = m_context;
    }


    public void parseJSON(Finance toPopulate, String currentStock) throws IOException, JSONException {
        // Create JSON and Finance objects
        JSONObject jObject;

        // Generate URL
        URL feedUrl = new URL(m_context.getString(R.string.google_finance_url) + currentStock);
        // Read JSON
        InputStream is = feedUrl.openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName(m_context.getString(R.string.input_stream_char_mode))));
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        String jsonText = sb.toString();
        jsonText = jsonText.substring(5, jsonText.length() - 2);
        is.close();
        // Init object

        jObject = new JSONObject(jsonText);

        // Use this, just because some shares (expn) use comma to separate large values.
        String tmpString = jObject.getString(m_context.getString(R.string.last_value_json_tag));
        tmpString = tmpString.replace(TARGET_STRING, REPLACEMENT_STRING);

        // Set 'Last' value
        toPopulate.setLast(Float.parseFloat(tmpString) / C_DIV_AMOUNT);

        // Set 'Company' name
        toPopulate.setName(jObject.getString(m_context.getString(R.string.name_value_json_tag)));

        // Set 'Market'
        toPopulate.setMarket(jObject.getString(m_context.getString(R.string.market_value_json_tag)));
        // Set 'Instant Volume'
        int instantVolume = volCharToInt(jObject.getString(m_context.getString(R.string.instant_volume_value_json_tag)));
        toPopulate.setInstantVolume(instantVolume);
    }

    public boolean getHistoric(Finance toPopulate, String stockToGet) {
        BufferedReader csvBr;
        String csvData[] = null;
        try {
            csvBr = getCsvFeed(stockToGet);
            csvData = parseCsvString(csvBr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (csvData != null) {
            toPopulate.setClose(Float.parseFloat(csvData[0]) / C_DIV_AMOUNT);
            toPopulate.setVolume(Integer.parseInt(csvData[1]));
            return true;
        } else {
            return false;
        }
    }

    public BufferedReader getCsvFeed(String stockSymbol) throws IOException {

        // Check dates
        Calendar cal = Calendar.getInstance();

        int day = cal.get(Calendar.DATE) - 4;
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(m_context);
        boolean useTestData = preferences.getBoolean(IS_USING_MOCK_DATA, false);
        URL feedUrl = new URL(m_context.getString(R.string.yahoo_finance_url) + stockSymbol + STOCK_SYMBOL_EXTENSION +
                MONTH_PARAMETER + month + DAY_PARAMETER + day + YEAR_PARAMETER + year);

        if (useTestData) {
            feedUrl = new URL(m_context.getString(R.string.test_data_url) + stockSymbol + TEST_DATA_FILE_EXTENSION);
        }
        InputStream is = feedUrl.openStream();
        return new BufferedReader(new InputStreamReader(is, Charset.forName(m_context.getString(R.string.input_stream_char_mode))));
    }

    public String[] parseCsvString(BufferedReader csvToParse) throws IOException {
        String strLine;
        StringTokenizer st;
        int lineNumber = 0;
        int tokenNumber = 0;

        String csvdata[] = null;

        if (csvToParse != null) {
            while (((strLine = csvToParse.readLine()) != null)) {
                lineNumber++;
                if (lineNumber == 2) {
                    st = new StringTokenizer(strLine, ",");
                    String token;
                    while (st.hasMoreTokens()) {
                        if (csvdata == null) {
                            csvdata = new String[2];
                        }
                        tokenNumber++;
                        token = st.nextToken();
                        if (tokenNumber == 5) {
                            csvdata[0] = token;
                        }
                        if (tokenNumber == 6) {
                            csvdata[1] = token;
                        }
                    }
                    tokenNumber = 0;
                }
            }
        }
        return csvdata;
    }

    public int volCharToInt(String amount) {
        float convertedVal = 0;
        int multiplier = 1;
        int returnValue = 0;

        try {
            amount = amount.replaceAll(TARGET_STRING, REPLACEMENT_STRING);

            String valComponent = amount.substring(0, amount.length() - 1);
            String multComponent = amount.substring(amount.length() - 1);
            convertedVal = Float.parseFloat(valComponent);
            multComponent = multComponent.toUpperCase();

            if (multComponent.equals(MILLIONS)) {
                multiplier = 1000000;
            }
            if (multComponent.equals(THOUSANDS)) {
                multiplier = 1000;
            }

            convertedVal = convertedVal * (float) multiplier;
            returnValue = (int) convertedVal;
        } catch (Exception e) {
            returnValue = 0;
        }

        if (returnValue < 0) {
            return 0;
        } else {
            return returnValue;
        }
    }


}
