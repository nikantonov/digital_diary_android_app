package com.example.digitaldiary.othertests;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.digitaldiary.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

import internet.InternetCommunication;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class InternetCommunicationTest {
    private InternetCommunication internet;

    @Before
    public void init(){
        internet = new InternetCommunication();
    }

    @Test
    public void internet_getCountryCode_test() {
        String countryCode = internet.getCountryCode("Italy");
        assertEquals(countryCode, "IT");
    }

    @Test
    public void internet_proofIfCountryExists_test() {
        boolean proof_true = internet.proofIfCountryExists("Germany");
        boolean proof_false = internet.proofIfCountryExists("Germanyyy");

        assertTrue(proof_true);
        assertFalse(proof_false);
    }

    @Test
    public void internet_proofGetFlag_test() {
        InputStream input_not_null = internet.getFlag("IT");
        InputStream input_null = internet.getFlag("HGFGGF");

        assertNotNull(input_not_null);
        assertNull(input_null);
    }

    @Test
    public void internet_proofCountryInfo_test() {
        String info = internet.getCountryInformation("Fgggg");
        String info_true = internet.getCountryInformation("Germany");

        assertNotNull(info_true);
        assertNull(info);
    }

    @After
    public void end() {
        //for log
    }
}
