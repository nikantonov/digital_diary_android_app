package com.example.digitaldiary.activitiestests;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.digitaldiary.activities.MainActivity;
import com.example.digitaldiary.activities.PhotoActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//NOTE! You should have at least one journey before this test!
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private MainActivity test;

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init(){
        test = activityRule.getActivity();
    }

    @Test
    public void mainActivity_proofIfCountriesPresent_test() {
        boolean bool_test = test.proofIfCountriesPresent();
        assertTrue(bool_test);
    }

    @After
    public void end() {
        activityRule.finishActivity();
    }
}
