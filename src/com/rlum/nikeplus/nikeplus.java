package com.rlum.nikeplus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class nikeplus extends Activity {
	
	private String nikeFeed;
	private TextView theDisplay;
	private InputStream in;
	private ArrayList<runItem> runs;
	private ListView runsView;
	private CustomArrayAdapter aa;
	
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);



		//theDisplay = (TextView)findViewById(R.id.display1);
		nikeFeed = getString(R.string.nikeRunFeed)+getString(R.string.nikeID);
		runs=new ArrayList<runItem>();
		runsView = (ListView) findViewById(R.id.runs);
		aa = new CustomArrayAdapter(this,R.layout.runview,runs);
		runsView.setAdapter(aa);

		runItem testRun = new runItem();
		testRun.setRunID("RunID");
		testRun.setCalorieValue("CALories");
		testRun.setDistanceValue("Distance");
		testRun.setStartValue("STartVAlue");
		testRun.setWorkoutType("WorkoutType");
		testRun.setDurationValue("Duration");
		runs.add(testRun);
		aa.notifyDataSetChanged();

		// build the button that gets the data from nike
		Button b1 = (Button) findViewById(R.id.button1);
		b1.setText("Get " + R.string.nikeID);
		b1.setOnClickListener(new OnClickListener(){

			//			@Override
			//			public void onClick(View v) {
			//				getRunData();
			//			}

			public void onClick(View v) {
				// put getRunData into seperate thread
				// it calls notifychanged to the arrayadapter but does not
				// directly update any UI elements from outside the main thread
				// otherwise would have to do a post (updateview) from within thread
				//to keep  ui stuff in main thread. (ui is not threadsafe)
				Toast.makeText(nikeplus.this,  "Launching Request, Please wait", Toast.LENGTH_LONG).show();

				final View v1 = v;
				Thread athread = new Thread(new Runnable() {
					public void run() {
						getRunData();
						v1.post(new Runnable (){
							// posting this runnable means
							// it is run from within the ui thread.
							// synchronization issue?
							@Override
							public void run() {
								aa.notifyDataSetChanged();
								// make newly populated list clickable
								//    					    	runsView.setOnClickListener(new OnClickListener(){
								//    								@Override
								//    								public void onClick(View v) {
								//    									Log.i("myTag", "onclick view = " + v.toString() + ", id = " + v.getId());
								//    									Log.i("myTag", "view parent = " + v.getParent().toString() );
								//    								}
								//    				      	
								//    				      		});
							}

						});
					};
				});
				athread.start();

			}//onClick

		});//b1.setOnClickListener





		Button b2 = (Button) findViewById(R.id.button2);
		b2.setText("Clear All");
		b2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(nikeplus.this, "Removing Runs", Toast.LENGTH_SHORT).show();
				runs.clear();
				aa.notifyDataSetChanged();
			}

		});

		Button b3 = (Button) findViewById(R.id.button3);
		b3.setText("Toast");
		b3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(nikeplus.this, "Qty Runs= "+ runs.size(), Toast.LENGTH_SHORT).show();
			}

		});
		

		runsView.setLongClickable(true);
		runsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
		
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
								int position, long id) {
				Toast.makeText(nikeplus.this, "RunID= " +runs.get(position).getRunID(), Toast.LENGTH_SHORT).show();
				Log.i("myTag", "parent= " + v.getId());
				Log.i("myTag", "parent= " + v.getParent().toString());
				return true;
			}

			
		});

	}//onCreate

 
    
    private void getRunData(){
		// help prime dns lookup - esp helpful for emulator
	    try {
	        InetAddress i = InetAddress.getByName(nikeFeed);
	        Log.i("myTag", "host = "  + i.getHostAddress());
	      } catch (UnknownHostException e1) {
	        e1.printStackTrace();
	      }
		
		// get the xml data from the nikeplus site
		try{
			URL url = new URL(nikeFeed);
			Log.i("myTag", nikeFeed);
			Log.i("myTag", url.toString());
			
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection)connection;
			
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK){
				Log.i("myTag","http ok");
//				Toast.makeText(nikeplus.this, "Connection Made, Retrieving Data", 
//						Toast.LENGTH_SHORT).show();
				in = httpConnection.getInputStream();
//this code can be used to emit xml text into window for direct viewing if desired						
//				BufferedReader br = new BufferedReader( new InputStreamReader (in)); 
//				String line = br.readLine();
//				while (line!=null){
//					theDisplay.append(line);
//					line = br.readLine();
//				}
				// use DOM Parser to get xml object
				DOMParser dp = new DOMParser(in);
				
			}else{
				Log.i("myTag", "Connection Failure, responseCode = " 
						+ responseCode + " " 
						+ httpConnection.getResponseMessage());
//				
//				Toast.makeText(nikeplus.this, "Connection Failure, responseCode = " 
//						+ responseCode + " " 
//						+ httpConnection.getResponseMessage() , 
//						Toast.LENGTH_SHORT).show();
			}
			Log.i("myTag", "completed");
		
			for (int i=0;i<runs.size();i++){
				Log.i("myTag", runs.get(i).toString());
			}
		//	aa.notifyDataSetChanged();
			
		}catch (MalformedURLException e) {
			Log.i("myTag", e.getMessage());
		}catch (IOException e){
			Log.i("myTag", e.getMessage());
		}
	}
    	
    
    
    
    
    public class DOMParser
    {
    	private Document doc = null;
    	
    	public DOMParser(InputStream is)
    	{
    		try
    		{
    			doc = parserXML(is);
    			
    			visit(doc, 0);
    		}
    		catch(Exception error)
    		{
    			Log.i("myTag", error.getMessage());
    		}
    	}
    	
    	// xml labels used by nikeplus system.
    	private final String runlabel = "run";
    	private final String startLabel = "startTime";
    	private final String valueLabel = "#text";
    	private final String distanceLabel = "distance";
    	private final String durationLabel = "duration";
    	private final String calorieLabel = "calories";    	
    	private final String idLabel = "id";
    	private final String workoutTypeLabel = "workoutType";
    	
    	public void visit(Node node, int level){
    		
    		NodeList nl = node.getChildNodes();
    		for (int i=0;i<nl.getLength();i++){
    			Log.i("myTag", "L:" + level + " i:" + i + " " +  nl.item(i).getNodeName() + " = " + nl.item(i).getNodeValue() );
    			switch (level){ 
    			case 2 :
    				if (nl.item(i).getNodeName().equals(runlabel)){
    					
    					runItem aRunItem = new runItem();
    					Node aNode = nl.item(i);
    					aRunItem.setRunID(aNode.getAttributes().getNamedItem(idLabel).getNodeValue() );
    					aRunItem.setWorkoutType(aNode.getAttributes().getNamedItem(workoutTypeLabel).getNodeValue() );    					
    					getMyData( aNode, aRunItem);
    					runs.add(aRunItem);
						
    					// we have enough to update view so update it while
						// we finish processing the rest
    					
//    					if (runs.size() == 20)  // post it to the ui thread to do ui update. 
//    						runsView.post( new Runnable (){
//
//    							@Override
//    							public void run() {
//    								aa.notifyDataSetChanged();
//    							}
//    						});
//    					

    				}// todo = handle other xml level2 tags -  run summary, status etc.
    				break;
    			case 0 :
    			case 1 : 
    				visit (nl.item(i),level+1);
    				// todo = check to make sure this is a plusServices xml document and status is good.
    				break;
    			default : 
    				break;
    			}
    			
    		}
		
		
    	}
	
    	/**
    	 * utilitymethod given a run node, searh it's children for known desired field/values
    	 * @param aNode
    	 * @param aRunItem
    	 */
    	public void getMyData(Node aNode, runItem aRunItem){
    		NodeList nl = aNode.getChildNodes();
    		int qty = nl.getLength();
    		for (int i=0;i< qty;i++){
        		String nodeName = nl.item(i).getNodeName();
				if (nodeName.equals(startLabel)){
					aRunItem.setStartValue(getChildNodeValue(nl.item(i)));
				}else if (nodeName.equals(distanceLabel)){
					aRunItem.setDistanceValue(getChildNodeValue(nl.item(i)));
				}else if (nodeName.equals(durationLabel)){
					aRunItem.setDurationValue(getChildNodeValue(nl.item(i)));
				}else if (nodeName.equals(calorieLabel)){
					aRunItem.setCalorieValue(getChildNodeValue(nl.item(i)));
				}else if (nodeName.equals(distanceLabel)){
					aRunItem.setDistanceValue(getChildNodeValue(nl.item(i)));
				}
				//getMyData(nl.item(i), aRunItem);
    		}
    		
    	}
    	
    	/**
    	 * Utility method to search children of node for #TEXT type
    	 * xml node values are stored in a child node of type #text
    	 * usually the first and only child node but for robustness
    	 * we will assume that it may not be the only or first child node.
    	 * @param n
    	 * @return
    	 */
    	private String getChildNodeValue(Node n){
    		if (n.hasChildNodes()){
    			NodeList nodelist = n.getChildNodes();
    			int size = nodelist.getLength();
    			for (int j=0; j<size ;j++ ){
    				if (nodelist.item(j).getNodeName()==valueLabel){
    					return nodelist.item(j).getNodeValue();
    				}
    			}
    			return null;
    		}else{
    			return null;
    		}
    	}

    	/**
    	 * Factory to get DOM parser
    	 * @param is
    	 * @return
    	 * @throws SAXException
    	 * @throws IOException
    	 * @throws ParserConfigurationException
    	 */
    	public Document parserXML(InputStream is) throws SAXException, IOException, ParserConfigurationException
    	{
    		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
    	}
    	

    }  	
    	
    	
    	
    /**
     * customarrayadapter required to map arraylist of runs to complex 
     * listview for display of runitems.
     */
    public class CustomArrayAdapter extends ArrayAdapter<runItem> {
    	List<runItem> theEntries;
    	Context context;
    	int resource;

    	public CustomArrayAdapter(Context context, int textViewResourceId,
    			List<runItem> objects) {
    		super(context, textViewResourceId, objects);
    		this.resource = textViewResourceId;

    	}

    	@Override
    	public View getView (int position, View convertView, ViewGroup parent){
    		LinearLayout newView;
    		// getItem is from the ArrayAdapter
    		runItem e = getItem(position);

    		if (convertView ==null){
    			// ArrayAdapter.getContext
    			newView = new LinearLayout(getContext());
    			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			vi.inflate(resource, newView,true);
    			//    				newView = (LinearLayout) vi.inflate(android.R.layout.simple_list_item_2, parent, false);
    		}else{
    			newView = (LinearLayout)convertView;
    		}
    		// note that findViewBy must be called from the inflated layout parent that
    		// contains the fields being datafilled.  a non specific findViewById will
    		// not be a compile error but will return nulls since these fields are
    		// associated with a specific instance parent.

    		TextView runid = (TextView) newView.findViewById(R.id.runid);
    		TextView distance = (TextView) newView.findViewById(R.id.distance);
    		TextView duration = (TextView) newView.findViewById(R.id.duration);
    		TextView start = (TextView) newView.findViewById(R.id.start);
    		TextView calories = (TextView) newView.findViewById(R.id.calories);


    		runid.setText( e.getRunID() );
    		distance.setText(e.getDistanceValue());
    		duration.setText(e.getDurationValue());
    		start.setText(e.getStartValue());
    		calories.setText(e.getCalorieValue());

    		return (newView);

    	}


    }

    
    
    
}