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
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class WeeklyAnalyticsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef,personalRef;

    private TextView totalBudgetAmountTextView, analyticsTransportAmount,analyticsFoodAmount,analyticsHouseExpensesAmount,analyticsEntertainmentAmount;
    private TextView analyticsApparelAmount,analyticsPersonalExpensesAmount,analyticsOtherAmount, weekSpentAmount;

    private RelativeLayout linearLayoutFood,linearLayoutTransport,linearLayoutFoodHouse,linearLayoutEntertainment;
    private RelativeLayout linearLayoutApparel,linearLayoutPersonalExp,linearLayoutOther, linearLayoutAnalysis;

    private AnyChartView anyChartView;
    private TextView progress_ratio_transport,progress_ratio_food,progress_ratio_house,progress_ratio_ent, progress_ratio_app,progress_ratio_per,progress_ratio_oth, monthRatioSpending;
    private ImageView status_Image_transport, status_Image_food,status_Image_house,status_Image_ent,status_Image_app,status_Image_per,status_Image_oth, monthRatioSpending_Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_analytics);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);


        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);

        //general analytic
        weekSpentAmount = findViewById(R.id.weekSpentAmount);
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

        getTotalWeekExpense("Transport","expenses","weekTrans");
        getTotalWeekExpense("Food","expenses","weekFood");
        getTotalWeekExpense("House","expenses","weekHouse");
        getTotalWeekExpense("Entertainment","expenses","weekEnt");
        getTotalWeekExpense("Apparel","expenses","weekApp");
        getTotalWeekExpense("Personal","expenses","weekPer");
        getTotalWeekExpense("Other","expenses","weekOther");
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

    private void getTotalWeekExpense(String name,String ref, String child){
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);//set to epoch time
        DateTime now=new DateTime();
        Weeks week=Weeks.weeksBetween(epoch,now);

        String itemNweek=name +week.getWeeks();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference(ref).child(onlineUserId);
        Query query=reference.orderByChild("itemNweek").equalTo(itemNweek);
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
                    }

                    if(name.equals("Transport")){
                        analyticsTransportAmount.setText("Spent: " + totalAmount);
                    }else if(name.equals("Food")){
                        analyticsFoodAmount.setText("Spent: " + totalAmount);
                    }else if(name.equals("House")){
                        analyticsHouseExpensesAmount.setText("Spent: " + totalAmount);
                    }else if(name.equals("Entertainment")){
                        analyticsEntertainmentAmount.setText("Spent: " + totalAmount);
                    }else if(name.equals("Apparel")){
                        analyticsApparelAmount.setText("Spent: " + totalAmount);
                    }else if(name.equals("Personal")){
                        analyticsPersonalExpensesAmount.setText("Spent: " + totalAmount);
                    }else{
                        analyticsOtherAmount.setText("Spent: " + totalAmount);
                    }

                    personalRef.child(child).setValue(totalAmount);
                }else{
                    if(name.equals("Transport")){
                        linearLayoutTransport.setVisibility(View.GONE);
                    }else if(name.equals("Food")){
                        linearLayoutFood.setVisibility(View.GONE);
                    }else if(name.equals("House")){
                        linearLayoutFoodHouse.setVisibility(View.GONE);
                    }else if(name.equals("Entertainment")){
                        linearLayoutEntertainment.setVisibility(View.GONE);
                    }else if(name.equals("Apparel")){
                        linearLayoutApparel.setVisibility(View.GONE);
                    }else if(name.equals("Personal")){
                        linearLayoutPersonalExp.setVisibility(View.GONE);
                    }else{
                        linearLayoutOther.setVisibility(View.GONE);
                    }
                    personalRef.child(child).setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekSpending(){
        MutableDateTime epoch=new MutableDateTime();
        epoch.setDate(0);//set to epoch time
        DateTime now=new DateTime();
        Weeks weeks=Weeks.weeksBetween(epoch,now);

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("week").equalTo(weeks.getWeeks());
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
                    totalBudgetAmountTextView.setText("Total week's spending:$"+totalAmount);
                    weekSpentAmount.setText("Total spent:$"+totalAmount);
                }else{
                    anyChartView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGraph(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    int traTotal;
                    if (snapshot.hasChild("weekTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("weekTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    int foodTotal;
                    if (snapshot.hasChild("weekFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("weekFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    int houseTotal;
                    if (snapshot.hasChild("weekHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("weekHouse").getValue().toString());

                    } else {
                        houseTotal = 0;
                    }

                    int entTotal;
                    if (snapshot.hasChild("weekEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("weekEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }


                    int appTotal;
                    if (snapshot.hasChild("weekApp")) {
                        appTotal = Integer.parseInt(snapshot.child("weekApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    int perTotal;
                    if (snapshot.hasChild("weekPer")) {
                        perTotal = Integer.parseInt(snapshot.child("weekPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    int othTotal;
                    if (snapshot.hasChild("weekOther")) {
                        othTotal = Integer.parseInt(snapshot.child("weekOther").getValue().toString());
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

                    pie.title("Week Analytics");
                    pie.labels().position("outside");

                    pie.legend().title().enabled(true);
                    pie.legend().title().text("Items Spent On").padding(0d, 0d, 10d, 0d);

                    pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER);

                    anyChartView.setChart(pie);


                } else {
                    Toast.makeText(WeeklyAnalyticsActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
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
                    if (snapshot.hasChild("weekTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("weekTrans").getValue().toString());

                    } else {
                        traTotal = 0;
                    }

                    float foodTotal;
                    if (snapshot.hasChild("weekFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("weekFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    float houseTotal;
                    if (snapshot.hasChild("weekHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("weekHouse").getValue().toString());

                    } else {
                        houseTotal = 0;
                    }

                    float entTotal;
                    if (snapshot.hasChild("weekEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("weekEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }


                    float appTotal;
                    if (snapshot.hasChild("weekApp")) {
                        appTotal = Integer.parseInt(snapshot.child("weekApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }


                    float perTotal;
                    if (snapshot.hasChild("weekPer")) {
                        perTotal = Integer.parseInt(snapshot.child("weekPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    float othTotal;
                    if (snapshot.hasChild("weekOther")) {
                        othTotal = Integer.parseInt(snapshot.child("weekOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }
                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("week")) {
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("week").getValue().toString());
                    } else {
                        monthTotalSpentAmount = 0;
                    }

                    // GETTING RATIO
                    float traRatio;
                    if (snapshot.hasChild("weekTransRatio")) {
                        traRatio = Integer.parseInt(snapshot.child("weekTransRatio").getValue().toString());
                    } else {
                        traRatio = 0;
                    }

                    float foodRatio;
                    if (snapshot.hasChild("weekFoodRatio")) {
                        foodRatio = Integer.parseInt(snapshot.child("weekFoodRatio").getValue().toString());
                    } else {
                        foodRatio = 0;
                    }

                    float houseRatio;
                    if (snapshot.hasChild("weekHouseRatio")) {
                        houseRatio = Integer.parseInt(snapshot.child("weekHouseRatio").getValue().toString());
                    } else {
                        houseRatio = 0;
                    }

                    float entRatio;
                    if (snapshot.hasChild("weekEntRatio")) {
                        entRatio = Integer.parseInt(snapshot.child("weekEntRatio").getValue().toString());
                    } else {
                        entRatio = 0;
                    }



                    float appRatio;
                    if (snapshot.hasChild("weekAppRatio")) {
                        appRatio = Integer.parseInt(snapshot.child("weekAppRatio").getValue().toString());
                    } else {
                        appRatio = 0;
                    }

                    float perRatio;
                    if (snapshot.hasChild("weekPerRatio")) {
                        perRatio = Integer.parseInt(snapshot.child("weekPerRatio").getValue().toString());
                    } else {
                        perRatio = 0;
                    }

                    float othRatio;
                    if (snapshot.hasChild("weekOtherRatio")) {
                        othRatio = Integer.parseInt(snapshot.child("weekOtherRatio").getValue().toString());
                    } else {
                        othRatio = 0;
                    }
                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("weeklyBudget")) {
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("weeklyBudget").getValue().toString());
                    } else {
                        monthTotalSpentAmountRatio = 0;
                    }


                    float monthPercent = (monthTotalSpentAmount / monthTotalSpentAmountRatio) * 100;
                    if (monthPercent < 50) {
                        monthRatioSpending.setText(monthPercent + "$" + " user of " + monthTotalSpentAmountRatio + "Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.green);
                    } else if (monthPercent >= 50 && monthPercent < 100) {
                        monthRatioSpending.setText(monthPercent + "$" + "user of " + monthTotalSpentAmountRatio + "Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.brown);
                    } else {
                        monthRatioSpending.setText(monthPercent + "$" + "user of " + monthTotalSpentAmountRatio + "Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.red);
                    }

                    float transportPercent = (traTotal / traRatio) * 100;
                    if (transportPercent < 50) {
                        progress_ratio_transport.setText(transportPercent + "%" + " used of " + traRatio + "Status");
                        status_Image_transport.setImageResource(R.drawable.green);
                    } else if (transportPercent >= 50 && transportPercent < 100) {
                        progress_ratio_transport.setText(transportPercent + "%" + " used of" + traRatio + "Status");
                        status_Image_transport.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_transport.setText(transportPercent + "%" + " used of" + traRatio + "Status");
                        status_Image_transport.setImageResource(R.drawable.red);
                    }

                    float foodPercent = (foodTotal / foodRatio) * 100;
                    if (foodPercent < 50) {
                        progress_ratio_food.setText(foodPercent + "%" + " used of" + foodRatio + "Status");
                        status_Image_food.setImageResource(R.drawable.green);
                    } else if (foodPercent >= 50 && foodPercent < 100) {
                        progress_ratio_food.setText(foodPercent + "%" + " used of" + foodRatio + "Status");
                        status_Image_food.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_food.setText(foodPercent + "%" + " used of" + foodRatio + "Status");
                        status_Image_food.setImageResource(R.drawable.red);
                    }

                    float housePercent = (houseTotal / houseRatio) * 100;
                    if (housePercent < 50) {
                        progress_ratio_house.setText(housePercent + "%" + " used of" + houseRatio + "Status");
                        status_Image_house.setImageResource(R.drawable.green);
                    } else if (housePercent >= 50 && housePercent < 100) {
                        progress_ratio_house.setText(housePercent + "%" + " used of" + houseRatio + "Status");
                        status_Image_house.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_house.setText(housePercent + "%" + " used of" + houseRatio + "Status");
                        status_Image_house.setImageResource(R.drawable.red);
                    }


                    float entPercent = (entTotal / entRatio) * 100;
                    if (entPercent < 50) {
                        progress_ratio_ent.setText(entPercent + "%" + " used of" + entRatio + " Status");
                        status_Image_ent.setImageResource(R.drawable.green);
                    } else if (entPercent >= 50 && entPercent < 100) {
                        progress_ratio_ent.setText(entPercent + "%" + " used of" + entRatio + " Status");
                        status_Image_ent.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_ent.setText(entPercent + "%" + " used of" + entRatio + " Status");
                        status_Image_ent.setImageResource(R.drawable.red);
                    }

                    float appPercent = (appTotal / appRatio) * 100;
                    if (appPercent < 50) {
                        progress_ratio_app.setText(appPercent + "%" + "used of" + appRatio + " Status");
                        status_Image_app.setImageResource(R.drawable.green);
                    } else if (appPercent >= 50 && appPercent < 100) {
                        progress_ratio_app.setText(appPercent + "%" + "used of" + appRatio + " Status");
                        status_Image_app.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_app.setText(appPercent + "%" + "used of" + appRatio + " Status");
                        status_Image_app.setImageResource(R.drawable.red);
                    }

                    float perPercent = (perTotal / perRatio) * 100;
                    if (perPercent < 50) {
                        progress_ratio_per.setText(perPercent + "%" + " used of" + perRatio + " Status");
                        status_Image_per.setImageResource(R.drawable.green);
                    } else if (perPercent >= 50 && perPercent < 100) {
                        progress_ratio_per.setText(perPercent + "%" + " used of" + perRatio + " Status");
                        status_Image_per.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_per.setText(perPercent + "%" + " used of" + perRatio + " Status");
                        status_Image_per.setImageResource(R.drawable.red);
                    }
                    float otherPercent = (othTotal / othRatio) * 100;
                    if (otherPercent < 50) {
                        progress_ratio_oth.setText(otherPercent + "%" + " used of " + othRatio + " Status");
                        status_Image_oth.setImageResource(R.drawable.green);
                    } else if (otherPercent >= 50 && otherPercent < 100) {
                        progress_ratio_oth.setText(otherPercent + "%" + " used of " + othRatio + " Status");
                        status_Image_oth.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_oth.setText(otherPercent + "%" + " used of " + othRatio + " Status");
                        status_Image_oth.setImageResource(R.drawable.red);
                    }
                } else {
                    Toast.makeText(WeeklyAnalyticsActivity.this, "setStatusAndImageResource Errors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}