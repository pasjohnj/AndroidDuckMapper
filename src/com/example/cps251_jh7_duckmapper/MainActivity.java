package com.example.cps251_jh7_duckmapper;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView; 

enum counties {WASHTENAW, JACKSON, WAYNE, LENAWEE, MONROE, INGHAM};

public class MainActivity extends Activity implements OnClickListener {
    //layout variables
    Spinner speciesSpinner, countiesSpinner, seasonSpinner, rangeSpinner, resultsSpinner;
    TextView results, banner;
    Button queryButton, resetButton;
    
    //location manager variables
    LocationManager locationManager;
    Location previousLocation = null;
    String myProvider = LocationManager.GPS_PROVIDER;  //"gps"
    double initLat, initLng;
    
    //database related variable
    SQLiteOpenHelper duckHelper;
    String theDuck, theCounty, theRange, theSeason;
    int resultsNum;
    
    //Maps related variables
    GoogleMap googleMap;
    CameraPosition.Builder cameraPosition_Builder=null;
    LatLng initLatLng;
    LatLng[] countiesLatLng = {
        new LatLng(42.2534106, -83.8367374),
        new LatLng(42.247602, -84.4247015),
        new LatLng(42.2449276, -83.2109739),
        new LatLng(41.8945094, -84.0632225),
        new LatLng(41.9189482, -83.3879398),
        new LatLng(42.5992881, -84.3718689)
    };
    
    //Button cameraButton; //testing button
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //layout
        speciesSpinner = (Spinner)findViewById(R.id.speciesSpinner);
        countiesSpinner = (Spinner)findViewById(R.id.countiesSpinner);
        seasonSpinner = (Spinner)findViewById(R.id.seasonSpinner);
        rangeSpinner = (Spinner)findViewById(R.id.rangeSpinner);
        resultsSpinner = (Spinner)findViewById(R.id.resultsSpinner);
        initSpeciesSpinner();
        initCountiesSpinner();
        initSeasonSpinner();
        initRangeSpinner();
        initResultsSpinner();
        
        banner = (TextView)findViewById(R.id.banner);
        banner.setTextColor(Color.BLUE);
        banner.setTypeface(Typeface.DEFAULT_BOLD);
        
        results = (TextView)findViewById(R.id.results);
        
        queryButton = new Button(this);
        resetButton = new Button(this);
        queryButton = (Button)findViewById(R.id.queryButton);
        queryButton.setOnClickListener(this);
        resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this);
        
        //cameraButton = (Button)findViewById(R.id.cameraButton);
        //cameraButton.setOnClickListener(this);
        
        //create the database if not installed already
        duckHelper = new DuckHelper(this);
        duckHelper.getReadableDatabase();
        
        //initialize the Map
        if (googleMap == null) {
            FragmentManager manager = getFragmentManager();
            MapFragment fragment = 
                    (MapFragment) manager.findFragmentById(R.id.map);
            googleMap = fragment.getMap();
        }
        
    //set up camera and go to your location
    initializeCamera();
    setInitLatLng();
    addSingleMarker(initLatLng, "Your Location");
    goToSingleLocation(initLatLng);    
        
    }
    
    //START CAMERA AND LOCATION RELATED METHODS\\
    void initializeCamera(){
         cameraPosition_Builder = new CameraPosition.Builder();
         cameraPosition_Builder.zoom(16.5f);
         cameraPosition_Builder.bearing(0);
         cameraPosition_Builder.tilt(0);
         
    }
    
    void setCameraZoom(){ //this method feels clunky, but I coudn't think of a more dynamic, terser way to get the county
        
        if(theCounty.equals("") && theRange.equals("")){
            cameraPosition_Builder.zoom(8.66f);
            goToSingleLocation(countiesLatLng[counties.WASHTENAW.ordinal()]);
            return;
        } else if(!theCounty.equals("") && theRange.equals("")){
            if(theCounty.equals("Washtenaw")){
                cameraPosition_Builder.zoom(10.15f);
                goToSingleLocation(countiesLatLng[counties.WASHTENAW.ordinal()]);
                return;
            } else if(theCounty.equals("Jackson")){
                cameraPosition_Builder.zoom(10.15f);
                goToSingleLocation(countiesLatLng[counties.JACKSON.ordinal()]);
                return;
            } else if(theCounty.equals("Wayne")){
                cameraPosition_Builder.zoom(10.15f);
                goToSingleLocation(countiesLatLng[counties.WAYNE.ordinal()]);
                return;
            } else if(theCounty.equals("Lenawee")){
                cameraPosition_Builder.zoom(10.15f);
                goToSingleLocation(countiesLatLng[counties.LENAWEE.ordinal()]);
                return;
            } else if(theCounty.equals("Monroe")){
                cameraPosition_Builder.zoom(10.15f);
                goToSingleLocation(countiesLatLng[counties.MONROE.ordinal()]);
                return;
            } else if(theCounty.equals("Ingham")){
                cameraPosition_Builder.zoom(10.15f);
                goToSingleLocation(countiesLatLng[counties.INGHAM.ordinal()]);
                return;
            }
        } else if(theCounty.equals("") && !theRange.equals("")){
        	addSingleMarker(initLatLng, "Your Location");
            goToSingleLocation(initLatLng);
        	int rangeNum = Integer.parseInt(theRange);
        	switch(rangeNum){
        	case(5):
        		cameraPosition_Builder.zoom(11.70f);
        		goToSingleLocation(initLatLng);
        		return;
        	case(10):
        		cameraPosition_Builder.zoom(11.05f);
    			goToSingleLocation(initLatLng);
    			return;
        	case(25):
        		cameraPosition_Builder.zoom(10.13f);
				goToSingleLocation(initLatLng);
				return;
        	}
        }
    }
    
    void setInitLatLng(){
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(myProvider);
        initLat = location.getLatitude();
        initLng = location.getLongitude();
        initLatLng = new LatLng(initLat, initLng);
        
    }
    
    void goToSingleLocation(LatLng latLng){ //could be used later on to zoom to a single sighting
        cameraPosition_Builder.target(latLng);
        CameraPosition cp = cameraPosition_Builder.build();
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cp);
        googleMap.animateCamera(cu);
        
    }
    
    int checkUniqueCoords(LatLng SingleDuckCoords, ArrayList<LatLng> uniqueDuckCoords){
        
        for(int i = 0; i < uniqueDuckCoords.size(); i++){ //if a positive hit (not unique), return the index 
            if(uniqueDuckCoords.get(i).equals(SingleDuckCoords)){
                return i;
            }
        }
        
        return -1; //return -1 if unique
    }
    
    void addDuckMarkers(ArrayList<LatLng> duckCoords, ArrayList<String> species){
        ArrayList<LatLng> uniqueDuckCoords = new ArrayList<LatLng>();
        ArrayList<String> uniqueTitles = new ArrayList<String>();
        String titleToBeManipulated = "";

        //this logic feels really clunky, but I needed a way to prevent different sightings at the same coordinates from being overwritten on the map
        for(int i = 0; i < duckCoords.size(); i++){ //creates two arrayLists that key track of all the unique coordinates and their associated sightings
            titleToBeManipulated = "Sighting #'s:"; //reset the string variable
            int uniqueIndex = checkUniqueCoords(duckCoords.get(i), uniqueDuckCoords); //if the coordinates exist already in this set, get the index of where they live 
            if (uniqueIndex == -1){ //this means they haven't appeared yet
                uniqueDuckCoords.add(duckCoords.get(i)); //add the coordinates to the ArrayList to be displayed
                titleToBeManipulated += " " + (i + 1); //add the sighting number
                uniqueTitles.add(titleToBeManipulated);
            } else { //if the coordinates ARE in the uniqueCoordinates ArrayList
                titleToBeManipulated = uniqueTitles.get(uniqueIndex); //pull out the relevant title string
                titleToBeManipulated += ", " + (i + 1); //add the relevant index
                uniqueTitles.set(uniqueIndex, titleToBeManipulated); //return it to the arrayList 
            }
        }
        
        for(int i = 0; i < uniqueDuckCoords.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(uniqueDuckCoords.get(i));
            markerOptions.title(uniqueTitles.get(i));
            googleMap.addMarker(markerOptions);
        }
        
    }
    
    void addSingleMarker(LatLng singleLatLng, String title){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(singleLatLng);
        markerOptions.title(title);
        googleMap.addMarker(markerOptions);
    }
    //END CAMERA AND LOCATION METHODS\\
    
    //START DATABASE RELATED METHODS\\
    void showDucks(){
        ArrayList<LatLng> duckCoords = new ArrayList<LatLng>();
        ArrayList<String> species = new ArrayList<String>();
        
        //Query Parameters: (These are null for now, in theory I could make this a more granular searching app, and set these to other things)
        String[] result_columns = null; //return each column in a record--need them all
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;
        String order = null;
        int queryResultsNum = resultsNum + 1; //for some reason the database is always returning the desired results - 1.  This increments to get the desired amount
        String limit = Integer.toString(queryResultsNum);
        String whereClause = makeWhereClause(theDuck, theSeason, theCounty, theRange);

        //Display information about the query in the banner
        String resultsHeader = "Results for: ";
        resultsHeader += makeResultsHeader(theDuck, theSeason, theCounty, theRange);
        banner.setText(resultsHeader);
        
        //perform the query and display relevant information
        SQLiteDatabase ducksDB = duckHelper.getReadableDatabase();        
        StringBuilder sb = new StringBuilder();
        
        try {
            //actual query performed here
            Cursor cursor = ducksDB.query("ducks", result_columns, whereClause, whereArgs, groupBy, having, order, limit);
            
            if (!(cursor.moveToFirst()) || cursor.getCount() == 0){
                 sb.append("No Results.");
                 cursor.close();
            } else { //move through cursor and output results to the results TextView
                int counter = 1; //TESTING
                cursor.moveToFirst();
                
                while (cursor.moveToNext()){
                	//Log.d("RANGE", theRange);
                	if(!theRange.equals("") && !inDistance(cursor.getDouble(3), cursor.getDouble(4), Integer.parseInt(theRange))){
                		continue;
                	} else {
	                    duckCoords.add(new LatLng(cursor.getDouble(3), cursor.getDouble(4)));
	                    species.add(cursor.getString(0));
	                    
	                    sb.append(counter + ".) ");
	                    String monthName = getMonthName(Integer.parseInt(cursor.getString(5)));
	                    String startingString, countyString;
	                    
	                    if(theDuck.equals("")){
	                        startingString = cursor.getString(0) + " sighting";
	                    } else {
	                        startingString = "Sighting";
	                    }
	                    
	                    if(theCounty.equals("")){
	                        countyString = " in " + cursor.getString(1) + ":";
	                    } else {
	                        countyString = " in ";
	                    }
	                    String output = startingString + countyString + cursor.getString(2) + " in: " + monthName + "\n";
	                    sb.append(output);
	                    counter++;
                	}
                }
                
                cursor.close();
            }
            
            addDuckMarkers(duckCoords, species);
            results.setText(sb);
        } catch (SQLException e){
            Log.d("MINE SQLEXCEPTION", "BAD QUERY!!!");
        }

    }
    
    String makeWhereClause(String theDuck, String theSeason, String theCounty, String theRange){ //pass global variables by copy because they will be manipulated here and I want to keep originals for other use
        String whereClause = "";
        if(theDuck.equals("Ross's Goose")){ //to get around my hacky deletion of all ' in strings in the database
            theDuck = "Ross Goose";
        } else if (theDuck.equals("Barrow's Goldeneye")){
            theDuck = "Barrow Goldeneye";
        }
        
        if(!theDuck.equals("")){ //if duck is selected, first column in database
            whereClause = "species='" + theDuck + "'";
        }
        
        if(!theCounty.equals("") && !whereClause.equals("")){ //if county is selected and species is already selected
            whereClause += " AND county='" + theCounty + "'";
        } else if (!theCounty.equals("") && theDuck.equals("")){ //if county is selected and nothing else is set
            whereClause = "county='" + theCounty + "'";
        }
        
        if(!theSeason.equals("") && !whereClause.equals("")){ //if season is selected and there are other parameters selected
            whereClause += " AND " + getSeasonParameter();
        } else if (!theSeason.equals("") && theDuck.equals("") && theCounty.equals("")){ //if season is selected and nothing else is set
            whereClause = getSeasonParameter();
        }
        
        if(!theRange.equals("") && !whereClause.equals("")){ //if range is selected and there are other parameters selected
            whereClause += " AND latitude<" + (initLat + .6) + " AND latitude>" + (initLat - .6) + " AND longitude<" + (initLng + .6) + " AND longitude>" + (initLng - .6); 
        } else if (!theRange.equals("") && theDuck.equals("") && theCounty.equals("") && theSeason.equals("")){ //if range is set and nothing else is set
            whereClause = "latitude<" + (initLat + 1) + " AND latitude>" + (initLat - 1) + " AND longitude<" + (initLng + 1) + " AND longitude>" + (initLng - 1);
        }
        
        Log.d("MINE: WHERE CLAUSE: ", whereClause);
        return whereClause;
    }
    
    String getSeasonParameter(){
        if(theSeason.equals("Winter")){
            return "(month=12 OR month=1 OR month=2)"; 
        } else if (theSeason.equals("Spring")){
            return "(month>2 AND month<6)";
        } else if (theSeason.equals("Summer")){
            return "month>5 AND month<9";
        } else if (theSeason.equals("Fall")){
            return "(month>8 AND month<12)";
        }
        
        return "Season: Bad Input!"; //shouldn't get here
    }
    
    String makeResultsHeader(String theDuck, String theSeason, String theCounty, String theRange){//pass global variables by copy because they will be manipulated here and I want to keep originals for other use
        String resultsHeader = "";
        
        if(theDuck.equals("")){
            theDuck = "All waterfowl";
        } 
        if(theSeason.equals("")){
            theSeason = "all seasons";
        }
        if(theCounty.equals("") && theRange.equals("")){
            theCounty = "all counties";
        }
        
        resultsHeader += theDuck;
        
        if(!theSeason.equals("")){
            resultsHeader += ", " + theSeason;
        }
        if(!theCounty.equals("")){
            resultsHeader += ", " + theCounty;
        }
        if(!theRange.equals("")){
            resultsHeader += ", for " + theRange + " miles.";
        }
        
        return resultsHeader;
    }
    
    String getMonthName(int monthNum){ //replace month number with string
        switch(monthNum){
        case(1):
            return "January";
        case(2):
            return "February";
        case(3):
            return "March";
        case(4):
            return "April";
        case(5):
            return "May";
        case(6):
            return "June";
        case(7):
            return "July";
        case(8):
            return "August";
        case(9):
            return "September";
        case(10):
            return "October";
        case(11):
            return "November";
        case(12):
            return "December";
        default: //shouldn't get here
            return "Unkown Month!";
        }
    }
    
    private boolean inDistance(double duckLat, double duckLng, int distance){
    	float[] results = new float[3];
    	Location.distanceBetween(initLat, initLng, duckLat, duckLng, results);
    	double resultsToMiles = results[0] * 0.00062137119;
    	if(resultsToMiles < distance){
    		return true;
    	} else {
    		return false;
    	}
    	
    }
    //END DATABASE RELATED METHODS\\
    
    //START SPINNER INITALIZATIONS\\ These taken directly from class code--apart from changing variable names, they are unchanged
    private String[] resultsArray;
    private void initResultsSpinner()
    {
        resultsArray = getResources().getStringArray(R.array.results_array);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.results_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        resultsSpinner.setAdapter(adapter);
        resultsSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
            {
    
                @Override
                public void onNothingSelected(AdapterView<?> av) {
                    Log.d("Mine", "onNothingSelected .... when is this called?");
    
                }

                @Override
                public void onItemSelected(AdapterView<?> av, View v,
                        int pos, long id) {
                    
                    if(resultsArray[pos].contains("Select")){
                        resultsNum = 25;
                    } else {
                        resultsNum = Integer.parseInt(resultsArray[pos]);
                    }
                    
                }
    
            }
        );
    }
    
    private String[] speciesArray;
    private void initSpeciesSpinner()
    {
        speciesArray = getResources().getStringArray(R.array.species_array);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.species_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        speciesSpinner.setAdapter(adapter);
        speciesSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
            {
    
                @Override
                public void onNothingSelected(AdapterView<?> av) {
                    Log.d("Mine", "onNothingSelected .... when is this called?");
    
                }

                @Override
                public void onItemSelected(AdapterView<?> av, View v,
                        int pos, long id) {
                        
                    theDuck = speciesArray[pos];
                    if(theDuck.contains("Select")){
                        theDuck = "";
                    }
                }
    
            }
        );
    }
    
    private String[] seasonArray;
    private void initSeasonSpinner()
    {
        seasonArray = getResources().getStringArray(R.array.season_array);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.season_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        seasonSpinner.setAdapter(adapter);
        seasonSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
            {
    
                @Override
                public void onNothingSelected(AdapterView<?> av) {
                    Log.d("Mine", "onNothingSelected .... when is this called?");
    
                }

                @Override
                public void onItemSelected(AdapterView<?> av, View v,
                        int pos, long id) {
                        theSeason = seasonArray[pos];
                        if(theSeason.contains("Select")){
                            theSeason = "";
                        }
                }
    
            }
        );
    }
    
    private String[] countiesArray;
    private void initCountiesSpinner()
    {
        countiesArray = getResources().getStringArray(R.array.counties_array);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.counties_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        countiesSpinner.setAdapter(adapter);
        countiesSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
            {
    
                @Override
                public void onNothingSelected(AdapterView<?> av) {
                    Log.d("Mine", "onNothingSelected .... when is this called?");
    
                }

                @Override
                public void onItemSelected(AdapterView<?> av, View v,
                        int pos, long id) {
                        theCounty = countiesArray[pos];
                        if(theCounty.contains("Select")){
                            theCounty = "";
                        }
                }
    
            }
        );
    }
    
    private String[] rangeArray;
    private void initRangeSpinner()
    {
        rangeArray = getResources().getStringArray(R.array.range_array);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.range_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        rangeSpinner.setAdapter(adapter);
        rangeSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
            {
    
                @Override
                public void onNothingSelected(AdapterView<?> av) {
                    Log.d("Mine", "onNothingSelected .... when is this called?");
    
                }

                @Override
                public void onItemSelected(AdapterView<?> av, View v,
                        int pos, long id) {
                		String[] burgerKing = rangeArray[pos].split(" ");
                        theRange = burgerKing[0];
                        if(theRange.contains("Select")){
                            theRange = "";
                        }
                }
    
            }
        );
    }
  //END SPINNER INITALIZATIONS\\

    @Override
    public void onClick(View v) {
        int index = v.getId();
        
        /*if(index == cameraButton.getId()){
        	Log.d("CAMERA ZOOM", "" + googleMap.getCameraPosition());
        	return;
        }*/
        
        if(index == resetButton.getId()){
            initSpeciesSpinner();
            initSeasonSpinner();
            initCountiesSpinner();
            initRangeSpinner();
            initResultsSpinner();
            
            banner.setText("Results will appear below.");
            results.setText("Dataset derived from: eBird Basic Dataset. Version: EBD_relFeb-2014. Cornell Lab of Ornithology, Ithaca, New York. February 2014.\n\nMany thanks to the Cornell Lab of Ornithology for access to their data.");
            
            googleMap.clear();
            addSingleMarker(initLatLng, "Your Location");
            cameraPosition_Builder.zoom(16.5f);
            goToSingleLocation(initLatLng);
            
            return;
        } else if(index == queryButton.getId()){
            googleMap.clear();
            goToSingleLocation(initLatLng);

            if(theDuck.equals("") && theSeason.equals("") && theRange.equals("") && theCounty.equals("")){
                banner.setText("SELECT PARAMETERS!");
                addSingleMarker(initLatLng, "Your Location");
                return;
            } else if(!theDuck.equals("") && theSeason.equals("") && theRange.equals("") && theCounty.equals("")){
                banner.setText("YOU MUST SELECT AT LEAST ONE OTHER PARAMETER!");
                results.setText("Dataset derived from: eBird Basic Dataset. Version: EBD_relFeb-2014. Cornell Lab of Ornithology, Ithaca, New York. February 2014.\n\nMany thanks to the Cornell Lab of Ornithology for access to their data.");
                addSingleMarker(initLatLng, "Your Location");
                return;
            } else if(!theCounty.equals("") && !theRange.equals("")){
                banner.setText("YOU CAN SELECT ONLY 1 GEOGRAPHIC PARAMETER!");
                results.setText("Dataset derived from: eBird Basic Dataset. Version: EBD_relFeb-2014. Cornell Lab of Ornithology, Ithaca, New York. February 2014.\n\nMany thanks to the Cornell Lab of Ornithology for access to their data.");
                addSingleMarker(initLatLng, "Your Location");
                initCountiesSpinner();
                initRangeSpinner();
                return;
            } else {
                setCameraZoom();
                showDucks();
                return;
            }
        }
    
    }
    
}