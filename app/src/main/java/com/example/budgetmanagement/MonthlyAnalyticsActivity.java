package com.example.budgetmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class MonthlyAnalyticsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef,personalRef;

    private TextView totalBudgetAmountTextView, analyticsTransportAmount,analyticsFoodAmount,analyticsHouseExpensesAmount,analyticsEntertainmentAmount;
    private TextView analyticsApparelAmount,analyticsPersonalExpensesAmount,analyticsOtherAmount, monthSpentAmount;

    private RelativeLayout linearLayoutFood,linearLayoutTransport,linearLayoutFoodHouse,linearLayoutEntertainment;
    private RelativeLayout linearLayoutApparel,linearLayoutPersonalExp,linearLayoutOther, linearLayoutAnalysis;

    private AnyChartView anyChartView;
    private TextView progress_ratio_transport,progress_ratio_food,progress_ratio_house,progress_ratio_ent, progress_ratio_app,progress_ratio_per,progress_ratio_oth, monthRatioSpending;
    private ImageView status_Image_transport, status_Image_food,status_Image_house,status_Image_ent,status_Image_app,status_Image_per,status_Image_oth, monthRatioSpending_Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_analytics);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);


        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);

        //general analytic
        monthSpentAmount = findViewById(R.id.monthSpentAmount);
        linearLayoutAnalysis = findViewById(R.id.linearLayoutAnalysis);
        monthRatioSpending = findViewById(R.id.monthRatioSpending);
        monthRatioSpending_Image = findViewById(R.id.monthRatioSpending_Image);

        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount = findViewById(R.id.analyticsFoodAmount);
        analyticsHouseExpensesAmount = findViewById(R.id.analyticsHouseExpensesAmount);
        analyticsEntertainmentAmount = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsApparelAmount = findViewById(R.id.analyticsApparelAmount);
        analyticsPersonalExpensesAmount = findViewById(R.id.analyticsPersonalExpensesAmount);
        analyticsOtherAmount = findViewById(R.id.analyticsOtherAmount);

        //Relative layouts views
        linearLayoutTransport = findViewById(R.id.linearLayoutTransport);
        linearLayoutFood = findViewById(R.id.linearLayoutFood);
        linearLayoutFoodHouse = findViewById(R.id.linearLayoutHouse);
        linearLayoutEntertainment = findViewById(R.id.relativeLayoutEntertainment);
        linearLayoutApparel = findViewById(R.id.linearLayoutApparel);
        linearLayoutPersonalExp = findViewById(R.id.linearLayoutPersonalExp);
        linearLayoutOther = findViewById(R.id.linearLayoutOther);

        //textviews
        progress_ratio_transport = findViewById(R.id.progress_ratio_transport);
        progress_ratio_food = findViewById(R.id.progress_ratio_food);
        progress_ratio_house = findViewById(R.id.progress_ratio_house);
        progress_ratio_ent = findViewById(R.id.progress_ratio_ent);
        progress_ratio_app = findViewById(R.id.progress_ratio_app);
        progress_ratio_per = findViewById(R.id.progress_ratio_per);
        progress_ratio_oth = findViewById(R.id.progress_ratio_oth);
        //imageviews
        status_Image_transport = findViewById(R.id.status_Image_transport);
        status_Image_food = findViewById(R.id.status_Image_food);
        status_Image_house = findViewById(R.id.status_Image_house);
        status_Image_ent = findViewById(R.id.status_Image_ent);
        status_Image_app = findViewById(R.id.status_Image_app);
        status_Image_per = findViewById(R.id.status_Image_per);
        status_Image_oth = findViewById(R.id.status_Image_oth);

        //anyChartView
        anyChartView = findViewById(R.id.anyChartView);

        getTotalWeekTransportExpense();
        getTotalWeekFoodExpenses();
        getTotalWeekHouseExpenses();
        getTotalWeekEntertainmentExpenses();
        getTotalWeekApparelExpenses();
        getTotalWeekPersonalExpenses();
        getTotalWeekOtherExpenses();
        getTotalWeekSpending();

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                loadGraph();
                setStatusAndImageResources();
            }
        };
        new java.util.Timer().schedule(
                t, 2000
        );

    }
    private void getTotalWeekTransportExpense(){
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);//set to epoch time
        DateTime now=new DateTime();
        Months months=Months.monthsBetween(epoch,now);

        String itemNmonth="Transport"+months.getMonths();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalAmount=0;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String,Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticsTransportAmount.setText("Spent:"+totalAmount);
                    }
                    personalRef.child("monthTrans").setValue(totalAmount);
                }else{
                    linearLayoutTransport.setVisibility(View.GONE);
                    personalRef.child("monthTrans").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalWeekFoodExpenses(){
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);//set to epoch time
        DateTime now=new DateTime();
        Months months=Months.monthsBetween(epoch,now);

        String itemNmonth="Food"+months.getMonths();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalAmount=0;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String,Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticsFoodAmount.setText("Spent:"+totalAmount);
                    }
                    personalRef.child("monthFood").setValue(totalAmount);
                }else{
                    linearLayoutFood.setVisibility(View.GONE);
                    personalRef.child("monthFood").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalWeekHouseExpenses(){
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);//set to epoch time
        DateTime now=new DateTime();
        Months months=Months.monthsBetween(epoch,now);

        String itemNmonth="House"+months.getMonths();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalAmount=0;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String,Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticsHouseExpensesAmount.setText("Spent:"+totalAmount);
                    }
                    personalRef.child("monthHouse").setValue(totalAmount);
                }else{
                    linearLayoutFoodHouse.setVisibility(View.GONE);
                    personalRef.child("monthHouse").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalWeekEntertainmentExpenses(){
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);//set to epoch time
        DateTime now=new DateTime();
        Months months=Months.monthsBetween(epoch,now);

        String itemNmonth="Entertainment"+months.getMonths();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalAmount=0;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String,Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticsEntertainmentAmount.setText("Spent:"+totalAmount);
                    }
                    personalRef.child("monthEnt").setValue(totalAmount);
                }else{
                    linearLayoutEntertainment.setVisibility(View.GONE);
                    personalRef.child("monthEnt").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekApparelExpenses(){
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);//set to epoch time
        DateTime now=new DateTime();
        Months months=Months.monthsBetween(epoch,now);

        String itemNmonth="Apparel"+months.getMonths();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalAmount=0;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String,Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticsApparelAmount.setText("Spent:"+totalAmount);
                    }
                    personalRef.child("monthApp").setValue(totalAmount);
                }else{
                    linearLayoutApparel.setVisibility(View.GONE);
                    personalRef.child("monthApp").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalWeekPersonalExpenses(){
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);//set to epoch time
        DateTime now=new DateTime();
        Months months=Months.monthsBetween(epoch,now);

        String itemNmonth="Personal"+months.getMonths();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalAmount=0;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String,Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticsPersonalExpensesAmount.setText("Spent:"+totalAmount);
                    }
                    personalRef.child("monthPer").setValue(totalAmount);
                }else{
                    linearLayoutPersonalExp.setVisibility(View.GONE);
                    personalRef.child("monthPer").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalWeekOtherExpenses(){
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);//set to epoch time
        DateTime now=new DateTime();
        Months months=Months.monthsBetween(epoch,now);

        String itemNmonth="Other"+months.getMonths();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalAmount=0;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String,Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticsOtherAmount.setText("Spent:"+totalAmount);
                    }
                    personalRef.child("monthOther").setValue(totalAmount);
                }else{
                    linearLayoutOther.setVisibility(View.GONE);
                    personalRef.child("monthOther").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekSpending(){
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);//set to epoch time
        DateTime now=new DateTime();
        Months months=Months.monthsBetween(epoch,now);


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.getChildrenCount()>0){
                    int totalAmount=0;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map=(Map<String,Object>) ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;

                    }
                    totalBudgetAmountTextView.setText("Total Month's spending:$"+totalAmount);
                    monthSpentAmount.setText("Total spent:$"+totalAmount);
                }else{
                    anyChartView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGraph(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    int traTotal;
                    if (snapshot.hasChild("monthTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("monthTrans").getValue().toString());

                    } else {
                        traTotal = 0;
                    }

                    int foodTotal;
                    if (snapshot.hasChild("monthFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    int houseTotal;
                    if (snapshot.hasChild("monthHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString());

                    } else {
                        houseTotal = 0;
                    }

                    int entTotal;
                    if (snapshot.hasChild("monthEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("monthEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }


                    int appTotal;
                    if (snapshot.hasChild("monthApp")) {
                        appTotal = Integer.parseInt(snapshot.child("monthApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    int perTotal;
                    if (snapshot.hasChild("monthPer")) {
                        perTotal = Integer.parseInt(snapshot.child("monthPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    int othTotal;
                    if (snapshot.hasChild("monthOther")) {
                        othTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport", traTotal));
                    data.add(new ValueDataEntry("House expenses", houseTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("Entertainment", entTotal));
                    data.add(new ValueDataEntry("Apparel", appTotal));
                    data.add(new ValueDataEntry("Personal", perTotal));
                    data.add(new ValueDataEntry("other", othTotal));

                    pie.data(data);

                    pie.title("Month Analytics");
                    pie.labels().position("outside");

                    pie.legend().title().enabled(true);
                    pie.legend().title().text("Items Spent On").padding(0d, 0d, 10d, 0d);

                    pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER);

                    anyChartView.setChart(pie);


                } else {
                    Toast.makeText(MonthlyAnalyticsActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setStatusAndImageResources() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float traTotal;
                    if (snapshot.hasChild("monthTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("monthTrans").getValue().toString());

                    } else {
                        traTotal = 0;
                    }
                    float foodTotal;
                    if (snapshot.hasChild("monthFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    float houseTotal;
                    if (snapshot.hasChild("monthHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString());

                    } else {
                        houseTotal = 0;
                    }

                    float entTotal;
                    if (snapshot.hasChild("monthEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("monthEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }


                    float appTotal;
                    if (snapshot.hasChild("monthApp")) {
                        appTotal = Integer.parseInt(snapshot.child("monthApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }


                    float perTotal;
                    if (snapshot.hasChild("monthPer")) {
                        perTotal = Integer.parseInt(snapshot.child("monthPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    float othTotal;
                    if (snapshot.hasChild("monthOther")) {
                        othTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }
                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("month")) {
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("month").getValue().toString());
                    } else {
                        monthTotalSpentAmount = 0;
                    }

                    // GETTING RATIO
                    float traRatio;
                    if (snapshot.hasChild("monthTransRatio")) {
                        traRatio = Integer.parseInt(snapshot.child("monthTransRatio").getValue().toString());
                    } else {
                        traRatio = 0;
                    }

                    float foodRatio;
                    if (snapshot.hasChild("monthFoodRatio")) {
                        foodRatio = Integer.parseInt(snapshot.child("monthFoodRatio").getValue().toString());
                    } else {
                        foodRatio = 0;
                    }

                    float houseRatio;
                    if (snapshot.hasChild("monthHouseRatio")) {
                        houseRatio = Integer.parseInt(snapshot.child("monthHouseRatio").getValue().toString());
                    } else {
                        houseRatio = 0;
                    }

                    float entRatio;
                    if (snapshot.hasChild("monthEntRatio")) {
                        entRatio = Integer.parseInt(snapshot.child("monthEntRatio").getValue().toString());
                    } else {
                        entRatio = 0;
                    }


                    float appRatio;
                    if (snapshot.hasChild("monthAppRatio")) {
                        appRatio = Integer.parseInt(snapshot.child("monthAppRatio").getValue().toString());
                    } else {
                        appRatio = 0;
                    }

                    float perRatio;
                    if (snapshot.hasChild("monthPerRatio")) {
                        perRatio = Integer.parseInt(snapshot.child("monthPerRatio").getValue().toString());
                    } else {
                        perRatio = 0;
                    }

                    float othRatio;
                    if (snapshot.hasChild("monthOtherRatio")) {
                        othRatio = Integer.parseInt(snapshot.child("monthOtherRatio").getValue().toString());
                    } else {
                        othRatio = 0;
                    }
                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("budget")) {
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("budget").getValue().toString());
                    } else {
                        monthTotalSpentAmountRatio = 0;
                    }


                    float monthPercent = (monthTotalSpentAmount / monthTotalSpentAmountRatio) * 100;
                    if (monthPercent < 50) {
                        monthRatioSpending.setText(monthPercent + " % " + "used of " + monthTotalSpentAmountRatio + "Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.green);
                    } else if (monthPercent >= 50 && monthPercent < 100) {
                        monthRatioSpending.setText(monthPercent + " % " + "used of " + monthTotalSpentAmountRatio + "Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.brown);
                    } else {
                        monthRatioSpending.setText(monthPercent + " % " + "used of " + monthTotalSpentAmountRatio + "Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.red);
                    }

                    float transportPercent = (traTotal / traRatio) * 100;
                    if (transportPercent < 50) {
                        progress_ratio_transport.setText(transportPercent + " % " + "used of" + traRatio + "Status");
                        status_Image_transport.setImageResource(R.drawable.green);
                    } else if (transportPercent >= 50 && transportPercent < 100) {
                        progress_ratio_transport.setText(transportPercent + " % " + "used of" + traRatio + "Status");
                        status_Image_transport.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_transport.setText(transportPercent + " % " + "used of" + traRatio + "Status");
                        status_Image_transport.setImageResource(R.drawable.red);
                    }

                    float foodPercent = (foodTotal / foodRatio) * 100;
                    if (foodPercent < 50) {
                        progress_ratio_food.setText(foodPercent + " % " + "used of" + foodRatio + "Status");
                        status_Image_food.setImageResource(R.drawable.green);
                    } else if (foodPercent >= 50 && foodPercent < 100) {
                        progress_ratio_food.setText(foodPercent + " % " + "used of" + foodRatio + "Status");
                        status_Image_food.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_food.setText(foodPercent + " % " + "used of" + foodRatio + "Status");
                        status_Image_food.setImageResource(R.drawable.red);
                    }

                    float housePercent = (houseTotal / houseRatio) * 100;
                    if (housePercent < 50) {
                        progress_ratio_house.setText(housePercent + " % " + "user of" + houseRatio + "Status");
                        status_Image_house.setImageResource(R.drawable.green);
                    } else if (housePercent >= 50 && housePercent < 100) {
                        progress_ratio_house.setText(housePercent + " % " + "user of" + houseRatio + "Status");
                        status_Image_house.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_house.setText(housePercent + " %  " + "user of" + houseRatio + "Status");
                        status_Image_house.setImageResource(R.drawable.red);
                    }


                    float entPercent = (entTotal / entRatio) * 100;
                    if (entPercent < 50) {
                        progress_ratio_ent.setText(entPercent + " % " + "used of" + entRatio + "Status");
                        status_Image_ent.setImageResource(R.drawable.green);
                    } else if (entPercent >= 50 && entPercent < 100) {
                        progress_ratio_ent.setText(entPercent + " % " + "used of" + entRatio + "Status");
                        status_Image_ent.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_ent.setText(entPercent + " % " + "used of" + entRatio + "Status");
                        status_Image_ent.setImageResource(R.drawable.red);
                    }

                    float appPercent = (appTotal / appRatio) * 100;
                    if (appPercent < 50) {
                        progress_ratio_app.setText(appPercent + " % " + "used of" + appRatio + "Status");
                        status_Image_app.setImageResource(R.drawable.green);
                    } else if (appPercent >= 50 && appPercent < 100) {
                        progress_ratio_app.setText(appPercent + " % " + "used of" + appRatio + "Status");
                        status_Image_app.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_app.setText(appPercent + " % " + "used of" + appRatio + "Status");
                        status_Image_app.setImageResource(R.drawable.red);
                    }

                    float perPercent = (perTotal / perRatio) * 100;
                    if (perPercent < 50) {
                        progress_ratio_per.setText(perPercent + " % " + "used of" + perRatio + "Status");
                        status_Image_per.setImageResource(R.drawable.green);
                    } else if (perPercent >= 50 && perPercent < 100) {
                        progress_ratio_per.setText(perPercent + " % " + "used of" + perRatio + "Status");
                        status_Image_per.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_per.setText(perPercent + "%" + " used of" + perRatio + "Status");
                        status_Image_per.setImageResource(R.drawable.red);
                    }
                    float otherPercent = (othTotal / othRatio) * 100;
                    if (otherPercent < 50) {
                        progress_ratio_oth.setText(otherPercent + " % " + "used of " + othRatio + " Status");
                        status_Image_oth.setImageResource(R.drawable.green);
                    } else if (otherPercent >= 50 && otherPercent < 100) {
                        progress_ratio_oth.setText(otherPercent + " % " + "used of " + othRatio + " Status");
                        status_Image_oth.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_oth.setText(otherPercent + " % " + "used of" + othRatio + " Status");
                        status_Image_oth.setImageResource(R.drawable.red);
                    }
                } else {
                    Toast.makeText(MonthlyAnalyticsActivity.this, "setStatusAndImageResource Errors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}