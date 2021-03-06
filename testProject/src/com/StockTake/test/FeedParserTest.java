package com.StockTake.test;

import java.io.BufferedReader;

import android.content.Intent;
import android.test.AndroidTestCase;

import com.StockTake.FeedParser;
import com.StockTake.Finance;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

public class FeedParserTest extends AndroidTestCase
{
	FeedParser feedparse;
	Finance finance;
	Intent intent;
	
	protected void setUp() throws Exception {
		super.setUp();
		feedparse = new FeedParser(getContext());
		finance = new Finance(getContext());
	}
	
	public static Test suite()
	{
		return new TestSuite(FeedParserTest.class);
	}
	
	public void testNotNull() throws Throwable
	{
		Assert.assertNotSame("failure - The feedparser is null after initialisation! ",feedparse, null);
	}

	/**
	 * OK
	 * Test method with existing stock symbol.
	 */
	public void testJSONPars() throws Throwable
	{
		feedparse.parseJSON(finance, "BP");

		assertNotNull("failure - Close is still null",finance.getClosingValue());
		assertNotNull("failure - Name is still null",finance.getStockSymbol());
		assertNotNull("failure - Summary is still null",finance.getSummary());
		assertNotNull("failure - Volume is still null",finance.getInstantVolume());
		assertNotNull("failure - Last is still null",finance.getLastValue());
		assertNotNull("failure - Market Value is still null",finance.getMarket());
				
		Assert.assertEquals("failure - The stockSymbol is not BP as it should be",(String)finance.getStockSymbol(), "BP");
	}

	/**
	 * OK
	 * Test method with NULL stock symbol.
	 */
	public void testGetHistoricNull() throws Throwable
	{
		Assert.assertNotNull("failure - finance is still null",finance);
		finance.setStockSymbol(null);
		Assert.assertEquals("failure - Name should be null but isnt",(String)finance.getStockSymbol(), null);

		Assert.assertEquals("failure - Historic data should be null but isnt",feedparse.getHistoric(finance, null), false);
		
		Assert.assertSame("failure - volumeHistoric should be 0",finance.getVolumeHistoric(),0);
		Assert.assertEquals("failure - closingValue should be 0",finance.getClosingValue(), 0.0, 0.0);
	}
	
	/**
	 * OK
	 * Test method with existing stock symbol.
	 */
	public void testGetHistoricExisting() throws Throwable
	{
		Assert.assertNotNull("failure - is null but should be initialised",finance);
		finance.setStockSymbol("BP");
		Assert.assertEquals("failure - stockSymbol should be BP",(String)finance.getStockSymbol(), "BP");

		Assert.assertEquals("failure - historic is null/false",feedparse.getHistoric(finance, "BP"), true);
		
		Assert.assertNotNull("failure - volumeHistoric is null!",finance.getVolumeHistoric());
		Assert.assertNotNull("failure - closingValue is null!",finance.getClosingValue());
	}
	
	/**
	 * OK
	 * Test method with not existing stock symbol.
	 */
	public void testGetHistoricNotExisting() throws Throwable
	{
		Assert.assertNotNull("failure - finance is null",finance);
		finance.setStockSymbol("THIS STOCK DOES NOT EXIST");
		Assert.assertEquals("failure - setStockSymbol did not run correctly",(String)finance.getStockSymbol(), "THIS STOCK DOES NOT EXIST");

		Assert.assertEquals("failure - Getting stock data should have failed using broken data",feedparse.getHistoric(finance, "THIS STOCK DOES NOT EXIST"), false);
		
		Assert.assertNotNull("failure - volumeHistoric is null!",finance.getVolumeHistoric());
		Assert.assertNotNull("failure - closingValue is null!",finance.getClosingValue());
	}
	
	/**
	 * OK
	 */
	public void testVolCharToInt() throws Throwable
	{
		String testAmount = "123M";
		Assert.assertEquals("failure - parsed amount using M is not as expected",123000000, feedparse.volCharToInt(testAmount));
		
		testAmount = "100K";
		Assert.assertEquals("failure - parsed amount using K is not as expected",100000, feedparse.volCharToInt(testAmount));
		
		testAmount = "0";
		Assert.assertEquals("failure - parsed amount entering 0 did not return expected 0",0, feedparse.volCharToInt(testAmount));
		
		testAmount = "-1";
		Assert.assertEquals("failure - parsed amount using negative number did not return default 0",0, feedparse.volCharToInt(testAmount));
		
		testAmount = "aWrongString";
		Assert.assertEquals("failure - parsed amount using unparsable string did not return default 0",0, feedparse.volCharToInt(testAmount));
	}
	
	/**
	 * OK
	 * Test method with an existing stock symbol.
	 */
	public void testParseCsvStringExisting() throws Throwable
	{
		BufferedReader csvBr;
		csvBr   = feedparse.getCsvFeed("BP");

		
		Assert.assertNotNull("failure - parsing if CSV String failed and returned null",feedparse.parseCsvString(csvBr));
	}
	
	/**
	 * OK
	 * Test method with a null stock symbol.
	 */
	public void testParseCsvStringNull() throws Throwable
	{
		Assert.assertNull("failure - parsing CSV string failed and returned null",feedparse.parseCsvString(null));
	}

}
