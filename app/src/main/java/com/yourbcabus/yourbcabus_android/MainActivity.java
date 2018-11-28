package com.yourbcabus.yourbcabus_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://db.yourbcabus.com/schools/5bca51e785aa2627e14db459/buses";

    RecyclerView recyclerView;
    BusAdapter adapter;

    List<BusModel> busList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        busList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadBuses();
    }

    private void loadBuses() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray busArray = new JSONArray(response);

                            for (int i = 0; i < busArray.length(); i++) {
                                JSONObject busObject = busArray.getJSONObject(i);

                                String name = busObject.getString("name");
                                String invalidateTime = busObject.getString("invalidate_time");
                                String location = "";

                                try {
                                    location = busObject.getJSONArray("locations").getString(0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    location = "?";
                                }

                                BusModel bus = new BusModel(name, location, invalidateTime);
                                busList.add(bus);
                            }

                            Collections.sort(busList, new BusComparator());

                            adapter = new BusAdapter(MainActivity.this, busList);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    public class BusComparator implements Comparator<BusModel> {
        @Override
        public int compare(BusModel o1, BusModel o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }
}
