package com.example.budgetmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerView;

    private TodayItemsAdapter todayItemsAdapter;
    public static ArrayList<Data> myDataList = new ArrayList<Data>();

    private FirebaseAuth mAuth;
    private String onlineUserId="";
    private DatabaseReference expensesRef,personalRef;

    private Toolbar settingsToolbar;

    private Button search;
    private TextView historyTotalAmountSpent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        search=findViewById(R.id.search);
        historyTotalAmountSpent=findViewById(R.id.historyTotalAmountSpent);

        mAuth=FirebaseAuth.getInstance();
        onlineUserId=mAuth.getCurrentUser().getUid();

        recyclerView=findViewById(R.id.recycler_View_Id_Feed);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //
        recyclerView.setLayoutManager(layoutManager);

        myDataList=new ArrayList<>();
        todayItemsAdapter=new TodayItemsAdapter(HistoryActivity.this, myDataList);
        recyclerView.setAdapter(todayItemsAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDiaglog();
            }
        });
    }

    private void showDatePickerDiaglog(){
        DatePickerDialog datePickerDialog=new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        int months=month+1;
        //String date = dayOfMonth+"-"+"0"+months +"-"+year;
        String date = dayOfMonth + "-"+months +"-"+year;
        if(months < 10){
            date = dayOfMonth + "-0"+months +"-"+year;
            if(dayOfMonth < 10){
                date = "0"+dayOfMonth + "-0"+months +"-"+year;
            }
        }else{
            if(dayOfMonth < 10){
                date = "0"+dayOfMonth + "-"+months +"-"+year;
            }
        }

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query=reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDataList.clear();
                int totalAmount=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Map<String,Object> map=(Map<String, Object>) snapshot.getValue();
                    Object total=map.get("amount");
                    int pTotal=Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;

                    Data data =  snapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                todayItemsAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                if(totalAmount > 0){
                    historyTotalAmountSpent.setVisibility(View.VISIBLE);
                    historyTotalAmountSpent.setText("This day you spent $: "+totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}