package com.example.covid_19tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    CountryCodePicker countryCodePicker;
    TextView mtodaytotal,mtotal,mactive,mtodayactive,mrecovered,mtodayrecovered,mmdeaths,mtodaydeaths;
    String country;
    TextView mfilter;
    Spinner spinner;
    String[] types={"cases","deaths","recovered","active"};
    private List<ModelClass> modelClassList;
    private List<ModelClass> modelClassList2;
    PieChart mpiechart;
    private RecyclerView recyclerView;
    com.example.covid_19tracker.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        countryCodePicker=findViewById(R.id.cpp);
        mtodayactive=findViewById(R.id.todayactive);
        mactive=findViewById(R.id.activecase);
        mmdeaths=findViewById(R.id.totaldeath);
        mtodaydeaths=findViewById(R.id.todaydeath);
        mrecovered=findViewById(R.id.recoveredcase);
        mtodayrecovered=findViewById(R.id.todayrecovered);
        mtotal=findViewById(R.id.totalcase);
        mtodaytotal=findViewById(R.id.todaytotal);
        mpiechart=findViewById(R.id.piechart);
        spinner=findViewById(R.id.spinner);
        mfilter=findViewById(R.id.filter);
        recyclerView=findViewById(R.id.recylerview);
        modelClassList=new ArrayList<>();
        modelClassList2=new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getApiInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter=new Adapter(getApplicationContext(),modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        countryCodePicker.setAutoDetectedCountry(true);
        country=countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country=countryCodePicker.getSelectedCountryName();
                fetchdata();

            }
        });
        fetchdata();
    }

    private void fetchdata() {
        ApiUtilities.getApiInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());
                for(int i=0;i<modelClassList.size();i++){
                    if(modelClassList.get(i).getCountry().equals(country)){
                        mactive.setText((modelClassList.get(i).getActive()));
                        mtodaydeaths.setText((modelClassList.get(i).getTodayDeaths()));
                        mtodayrecovered.setText((modelClassList.get(i).getTodayRecovered()));
                        mtodaytotal.setText((modelClassList.get(i).getTodayCases()));
                        mtotal.setText((modelClassList.get(i).getCases()));
                        mmdeaths.setText((modelClassList.get(i).getDeaths()));
                        mrecovered.setText((modelClassList.get(i).getRecovered()));

                        int active,total,recovered,deaths;

                        active=Integer.parseInt(modelClassList.get(i).getActive());
                        total=Integer.parseInt(modelClassList.get(i).getCases());
                        recovered=Integer.parseInt(modelClassList.get(i).getRecovered());
                        deaths=Integer.parseInt(modelClassList.get(i).getDeaths());
                        updateGraph(active,total,recovered,deaths);

                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
    }

    private void updateGraph(int active, int total, int recovered, int deaths) {
        mpiechart.clearChart();
        mpiechart.addPieSlice(new PieModel("Confirm",total,Color.parseColor("#FFB701")));
        mpiechart.addPieSlice(new PieModel("Active",active,Color.parseColor("#FF4CAF50")));
        mpiechart.addPieSlice(new PieModel("Recovered",recovered,Color.parseColor("#38ACCD")));
        mpiechart.addPieSlice(new PieModel("Deaths",deaths,Color.parseColor("#F55C47")));
        mpiechart.startAnimation();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item=types[position];
        mfilter.setText(item);
        adapter.filter(item);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}