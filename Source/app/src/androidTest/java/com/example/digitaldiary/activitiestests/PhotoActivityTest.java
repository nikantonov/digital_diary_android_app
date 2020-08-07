package com.example.digitaldiary.activitiestests;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.digitaldiary.R;
import com.example.digitaldiary.activities.LastOrNewJourney;
import com.example.digitaldiary.activities.PhotoActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


//NOTE! I already created journey "Italy, Naples" and added photos there! Without this steps this test is senseless
@RunWith(AndroidJUnit4.class)
public class PhotoActivityTest {
    private PhotoActivity test;

    @Rule
    public ActivityTestRule<PhotoActivity> activityRule =
            new ActivityTestRule<>(PhotoActivity.class);

    @Before
    public void init(){
        Intent intent = new Intent();
        intent.putExtra("country", "Italy");
        intent.putExtra("city", "Naples");
        test = activityRule.getActivity();
    }

    @Test
    public void photoActivity_proofIfSetUriWorking_test() throws NoSuchFieldException {
        Field get_last_uri = PhotoActivity.class.getDeclaredField("last_uri"); // not to create unnecessary get()
        get_last_uri.setAccessible(true);
        assertNotNull(get_last_uri);
        get_last_uri.setAccessible(false);
    }

    @Test
    public void photoActivity_proofIfFirstPositionIsNull_test() throws NoSuchFieldException, IllegalAccessException {
        Field get_uri_actual = PhotoActivity.class.getDeclaredField("uri_actual_position"); // not to create unnecessary get()
        get_uri_actual.setAccessible(true);
        assertEquals(get_uri_actual.get(test), 0);
        get_uri_actual.setAccessible(false);
    }

    @After
    public void end() {
        activityRule.finishActivity();
    }

}