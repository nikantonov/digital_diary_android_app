package com.example.digitaldiary.activitiestests;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.digitaldiary.activities.LastOrNewJourney;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.*;

//NOTE! This test has a value, when you already have last journey
@RunWith(AndroidJUnit4.class)
public class LastOrNewJourneyTest {
    private LastOrNewJourney test;

    @Rule
    public ActivityTestRule<LastOrNewJourney> activityRule =
            new ActivityTestRule<>(LastOrNewJourney.class);

    @Before
    public void init(){
        Intent intent = new Intent();
        intent.putExtra("type", "photo");
        activityRule.launchActivity(intent);
        test = activityRule.getActivity();
    }

    @Test
    public void lastOrNewJourney_proofIfLastJourneyPresent_test() {
        boolean test_variable = test.proofIfLastJourneyPresent();
        assertTrue(test_variable);
    }

    @Test
    public void lastOrNewJourney_onClickLast_test() throws NoSuchFieldException, IllegalAccessException {
        String city = null;
        SQLiteDatabase db = test.getBaseContext().openOrCreateDatabase("DigitalDiary.db", MODE_PRIVATE, null);
        Cursor journeyCursor = db.rawQuery("SELECT * FROM LASTTRIP", null);
        if (journeyCursor.moveToFirst()) {
            city = journeyCursor.getString(1);
        }
        journeyCursor.close();
        db.close();
        View v = new View(test);
        test.onClickLast(v);
        Field get_city = LastOrNewJourney.class.getDeclaredField("city"); // not to create unnecessary get()
        get_city.setAccessible(true);
        assertEquals(get_city.get(test), city);
        get_city.setAccessible(false);
    }

    @After
    public void end() {
        activityRule.finishActivity();
    }

}
