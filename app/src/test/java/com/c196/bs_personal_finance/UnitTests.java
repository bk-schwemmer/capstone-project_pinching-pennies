package com.c196.bs_personal_finance;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.c196.bs_personal_finance.UI.TransactionDetails;

public class UnitTests {

    private String testString;

    @Before
    public void doBeforeEachTest() {
        testString = null;
    }

    @Test
    public void testDotFirst() {
        // Test Case 01
        testString = ".00";
        assertTrue(TransactionDetails.validCurrency(testString));
    }

    @Test
    public void testAllDigits() {
        // Test Case 02
        testString = "99999999999999999999";
        assertTrue(TransactionDetails.validCurrency(testString));
    }

    @Test
    public void testOneOfEverything() {
        // Test Case 03
        testString = "-1.";
        assertTrue(TransactionDetails.validCurrency(testString));
    }

    @Test
    public void testEmptyString() {
        // Test Case 04
        testString = "";
        assertFalse(TransactionDetails.validCurrency(testString));
    }

    @Test
    public void testNegAndDot() {
        // Test Case 05
        testString = "-.";
        assertFalse(TransactionDetails.validCurrency(testString));
    }

    @Test
    public void testDotTooFarLeft() {
        // Test Case 06
        testString = "1.000";
        assertFalse(TransactionDetails.validCurrency(testString));
    }

    @Test
    public void testTwoDots() {
        // Test Case 07
        testString = "..";
        assertFalse(TransactionDetails.validCurrency(testString));
    }

    @Test
    public void testTowNeg() {
        // Test Case 08
        testString = "--";
        assertFalse(TransactionDetails.validCurrency(testString));
    }

    @Test
    public void testOneLetter() {
        // Test Case 09
        testString = "123456789f";
        assertFalse(TransactionDetails.validCurrency(testString));
    }

    @Test
    public void testNegAtEnd() {
        // Test Case 10
        testString = "1-";
        assertFalse(TransactionDetails.validCurrency(testString));
    }
}