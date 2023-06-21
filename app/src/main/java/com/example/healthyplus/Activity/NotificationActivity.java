package com.example.healthyplus.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyplus.Adapter.AlarmAdapter;
import com.example.healthyplus.Model.Alarm;
import com.example.healthyplus.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class NotificationActivity extends AppCompatActivity  {

    private Spinner spHour, spMinute;
    Button btnSet, btnBack, btnCancel;
    TextView txvHour, txvMinute, txvDay;
    ConstraintLayout constraintLayout;
    private AlarmManager alarmManager;

    private PendingIntent pendingIntent;
    private Calendar calendar;
    private ArrayList<Boolean> day;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    int countDoc=0;

    private AlarmAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String hour[]={"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23", "0"};
        String minute[]={"00","10","20","30","40","50"};
        ArrayList<Boolean> day = new ArrayList<Boolean>();
        for(int i=0; i<7;i++){
            day.add(true);
        }

        btnSet=findViewById(R.id.btn_set);
        btnBack=findViewById(R.id.btn_back);
        btnCancel=findViewById(R.id.btn_cancel);
        txvHour=findViewById(R.id.txv_hour);
        txvMinute=findViewById(R.id.txv_minute);
        txvDay=findViewById(R.id.txv_day);
        spHour=findViewById(R.id.sp_hour);
        spMinute=findViewById(R.id.sp_minute);
        constraintLayout=findViewById(R.id.layout_repeat);
        db = FirebaseFirestore.getInstance();


        ArrayAdapter adapterHour = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hour);
        adapterHour.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spHour.setAdapter(adapterHour);

        ArrayAdapter adapterMinute = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, minute);
        adapterMinute.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spMinute.setAdapter(adapterMinute);

        createNotificationChanel();

        spHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                txvHour.setText(hour[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spMinute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                txvMinute.setText(minute[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NotificationActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View viewDay = inflater.inflate(R.layout.dialog_choose_day, null);
                builder.setView(viewDay);
                Dialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog);
                dialog.show();

                CheckBox cbMon= viewDay.findViewById(R.id.cb_mon);
                CheckBox cbTue= viewDay.findViewById(R.id.cb_tue);
                CheckBox cbWed= viewDay.findViewById(R.id.cb_wed);
                CheckBox cbThu= viewDay.findViewById(R.id.cb_thu);
                CheckBox cbFri= viewDay.findViewById(R.id.cb_fri);
                CheckBox cbSat= viewDay.findViewById(R.id.cb_sat);
                CheckBox cbSun= viewDay.findViewById(R.id.cb_sun);

                Button btnCancel = viewDay.findViewById(R.id.btn_cancel);
                Button btnConfirm = viewDay.findViewById(R.id.btn_confirm);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        day.set(0, cbMon.isChecked());
                        day.set(1, cbTue.isChecked());
                        day.set(2, cbWed.isChecked());
                        day.set(3, cbThu.isChecked());
                        day.set(4, cbFri.isChecked());
                        day.set(5, cbSat.isChecked());
                        day.set(6, cbSun.isChecked());

                        String dayChange="";
                        if(day.get(0)==true){
                            dayChange=dayChange+"T2 ";
                        }
                        if(day.get(1)==true){
                            dayChange=dayChange+"T3 ";
                        }
                        if(day.get(2)==true){
                            dayChange=dayChange+"T4 ";
                        }
                        if(day.get(3)==true){
                            dayChange=dayChange+"T5 ";
                        }
                        if(day.get(4)==true){
                            dayChange=dayChange+"T6 ";
                        }
                        if(day.get(5)==true){
                            dayChange=dayChange+"T7 ";
                        }
                        if(day.get(6)==true){
                            dayChange=dayChange+"CN ";
                        }
                        txvDay.setText(dayChange);
                        dialog.dismiss();
                    }
                });
            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                calendar=Calendar.getInstance();
//
//                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt((String) txvHour.getText()));
//                calendar.set(Calendar.MINUTE, Integer.parseInt((String) txvMinute.getText()));
//                calendar.set(Calendar.SECOND, 0);
//                calendar.set(Calendar.MILLISECOND,0);
//                setAlarm();

                Query query1 = db.collection("alarm").whereEqualTo("name", (String) txvHour.getText()+txvMinute.getText());
                AggregateQuery countQuery1 = query1.count();
                countQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Count fetched successfully
                            AggregateQuerySnapshot snapshot = task.getResult();
                            Log.e(TAG, "Count: " + snapshot.getCount());
                            if(snapshot.getCount()>0){
                                Toast.makeText(getApplication(), "Bạn đã thiết lập thông báo lúc "+txvHour.getText()+":"+txvMinute.getText(), Toast.LENGTH_LONG).show();
                            }else {
                                DocumentReference doc = db.collection("alarm").document();
                                Alarm alarm = new Alarm("", (String) txvHour.getText()+ txvMinute.getText()
                                        , currentUser.getUid()
                                        , (String) txvHour.getText()
                                        , (String) txvMinute.getText()
                                        , true, day);
                                alarm.setId(doc.getId());
                                doc.set(alarm);
                            }
                            finish();
                        } else {
                            Log.e(TAG, "Count failed: ", task.getException());
                        }
                    }
                });
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void setAlarm(){
        alarmManager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);

        pendingIntent=PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,pendingIntent);
        Toast.makeText(getApplication(), "Set remind sucess", Toast.LENGTH_LONG).show();

    }


    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name ="abc";
            String desc="123";
            int importane= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel("healthyplus", name, importane);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

    }


}