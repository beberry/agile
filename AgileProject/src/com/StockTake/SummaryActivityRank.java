package com.StockTake;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SummaryActivityRank extends Activity
{
	/** Called when the activity is first created. */

	StockManager myStockmanager;
	
	/* Create Error Messages */
	TableLayout table; 
	TableRow errorRow;
	TextView error1;
	TableRow.LayoutParams params;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		
		
		System.out.println("summaryactivity - oncreate");
		// Get the StockManager
		myStockmanager = ((StockManager)getApplicationContext());
		
		setContentView(R.layout.summary);	

	    update();
	}
	
	
	public void update() {
		System.out.println("summaryactivity - update");
		table = (TableLayout) this.findViewById(R.id.tableLayout1); 
		
		errorRow = new TableRow(this);
		error1 = new TextView(this);
		params = new TableRow.LayoutParams();  
	    params.span = 4;
		
		if (checkInternetConnection()) {
			try {
				System.out.println("do this!");
				onClick();
				System.out.println("and this!");
				
			} catch(Exception e) {
				/* Parse Error */ 
       		error1.setText(Html.fromHtml(" <big>Oops!</big><br/><br/> Something went wrong when we tried to retrieve your share portfolio.<br/><br/> Please try again later."));
       		errorRow.addView(error1, params);
                table.addView(errorRow); 
			}
				
		} else {
			/* No Internet Connection */
			error1.setText(Html.fromHtml(" <big>Oops!</big><br/><br/> It seems there is a problem with your internet connection."));
			errorRow.addView(error1, params);
            table.addView(errorRow);
		}
	}
	private class CreateFinanceObjectAsync extends AsyncTask<Activity, Void, Void>
	{
		Activity parent;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar1);
			pb.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Activity... params) {
			// TODO Auto-generated method stub
			parent = params[0];
			System.out.println("summaryactivity - onclick");
			myStockmanager.clearPortfolio();
			try {
				myStockmanager.addPortfolioEntry("SN", "Smith & Nephew Plc Ord.", 1219);
				myStockmanager.addPortfolioEntry("BP", "BP Amoco Plc", 192);
				myStockmanager.addPortfolioEntry("HSBA", "HSBC Holdings Plc Ord.", 343);
				myStockmanager.addPortfolioEntry("EXPN", "Experian", 258);
				myStockmanager.addPortfolioEntry("MKS", "Marks & Spencer Ord.", 485);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar1);
			pb.setVisibility(View.GONE);
			myStockmanager.summaryTable(parent,StockManager.SortParameter.VALUE);
			super.onPostExecute(result);
		}
		
	}
	/* Click Refresh */
	public void onClick() throws IOException, JSONException {
		new CreateFinanceObjectAsync().execute(this, null, null);
		System.out.println("summaryactivity - onclick");
	}
	
	private boolean checkInternetConnection() {
		System.out.println("summaryactivity - checkinternetconnection");
		
	
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	
		// ARE WE CONNECTED TO THE INTERNET
		if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}