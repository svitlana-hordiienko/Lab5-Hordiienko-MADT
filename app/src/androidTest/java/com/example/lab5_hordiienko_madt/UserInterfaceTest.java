package com.example.lab5_hordiienko_madt;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Instrumented test, which will execute on an Android device, testing the spinner filter updating, display of items, display of empty list etc.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UserInterfaceTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testSpinnerFilterUpdatesList() {

        activityRule.getScenario().onActivity(activity -> {
            //set a dataset for testing
            activity.allRates.clear();
            activity.allRates.add("USD – 1.16");
            activity.allRates.add("GBP – 0.87");
            activity.allRates.add("AUD – 1.77");

            activity.filterOptions.clear();
            activity.filterOptions.addAll(activity.allRates);

            activity.setupListView();
            activity.setupSpinner();
            activity.listAdapter.notifyDataSetChanged();
        });

        //select "USD" from the spinner
        onView(withId(R.id.ratesFilter)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("USD"))).perform(click());

        //verify that the list now contains only the USD item
        activityRule.getScenario().onActivity(activity -> {
            ListView listView = activity.findViewById(R.id.listOfRates);
            assertTrue(listView.getAdapter().getCount() == 1);
            String actual = (String) listView.getAdapter().getItem(0);
            assertTrue(actual.contains("USD"));
            assertTrue(actual.contains("1.16"));
        });
    }

    @Test
    public void testSpinnerAllShowsAllItems() {
        activityRule.getScenario().onActivity(activity -> {
            activity.allRates.clear();
            //add rates to list
            activity.allRates.add("USD – 1.16");
            activity.allRates.add("GBP – 0.87");
            activity.allRates.add("AUD – 1.77");

            activity.filterOptions.clear();
            //add filtering options
            activity.filterOptions.addAll(activity.allRates);

            activity.setupListView();
            activity.setupSpinner();

            //set spinner to "All"
            activity.ratesFilter.setSelection(0);
            activity.filterList();

            ListView listView = activity.findViewById(R.id.listOfRates);
            assertTrue(listView.getAdapter().getCount() == 3);

            for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                String item = (String) listView.getAdapter().getItem(i);
                assertTrue(item.contains("USD") || item.contains("GBP") || item.contains("AUD"));
            }
        });
    }

    @Test
    public void testEmptyAllRatesShowsEmptyList() {
        activityRule.getScenario().onActivity(activity -> {
            //clear rates
            activity.allRates.clear();
            activity.filterOptions.clear();

            activity.setupListView();
            activity.setupSpinner();

            //set spinner to "All"
            activity.ratesFilter.setSelection(0);
            activity.filterList();

            ListView listView = activity.findViewById(R.id.listOfRates);
            //expect 0 items
            assertTrue(listView.getAdapter().getCount() == 0);
        });
    }

    @Test
    public void testSpinnerFilterSingleCurrency() {
        activityRule.getScenario().onActivity(activity -> {
            activity.allRates.clear();
            //add rates to list
            activity.allRates.add("USD – 1.16");
            activity.allRates.add("GBP – 0.87");
            activity.allRates.add("AUD – 1.77");

            activity.filterOptions.clear();
            activity.filterOptions.addAll(activity.allRates);

            activity.setupListView();
            activity.setupSpinner();

            //select "USD"
            activity.ratesFilter.setSelection(1); //"USD" is at position 1, since "All" is 0
            activity.filterList();

            ListView listView = activity.findViewById(R.id.listOfRates);
            assertTrue(listView.getAdapter().getCount() == 1);

            String item = (String) listView.getAdapter().getItem(0);
            assertTrue(item.contains("USD") && item.contains("1.16"));
        });
    }

    @Test
    public void testSpinnerSequentialSelectionUpdatesList() {
        activityRule.getScenario().onActivity(activity -> {
            activity.allRates.clear();
            //add rates to list
            activity.allRates.add("USD – 1.16");
            activity.allRates.add("GBP – 0.87");
            activity.allRates.add("AUD – 1.77");

            activity.filterOptions.clear();
            activity.filterOptions.addAll(activity.allRates);

            activity.setupListView();
            activity.setupSpinner();

            ListView listView = activity.findViewById(R.id.listOfRates);

            java.util.function.Consumer<String> selectCurrency = currency -> {
                ArrayAdapter adapter = (ArrayAdapter) activity.ratesFilter.getAdapter();
                int index = adapter.getPosition(currency); // get index dynamically
                activity.ratesFilter.setSelection(index);
                activity.filterList();
            };

            //select "All"
            selectCurrency.accept("All");
            activity.filterList();
            assertTrue(listView.getAdapter().getCount() == 3);

            //select "USD"
            selectCurrency.accept("USD");
            activity.filterList();
            assertTrue(listView.getAdapter().getCount() == 1);
            assertTrue(((String) listView.getAdapter().getItem(0)).contains("USD"));

            //select "AUD"
            selectCurrency.accept("AUD");
            activity.filterList();
            assertTrue(listView.getAdapter().getCount() == 1);
            assertTrue(((String) listView.getAdapter().getItem(0)).contains("AUD"));
        });
    }
}