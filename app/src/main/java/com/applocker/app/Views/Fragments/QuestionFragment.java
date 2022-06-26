package com.applocker.app.Views.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Interface.AppConfigListener;
import com.applocker.app.R;
import com.applocker.app.Views.Activity.PremiumActivity;
import com.applocker.app.Views.Activity.SettingActivity;

import java.util.ArrayList;
import java.util.List;


public class QuestionFragment extends Fragment {

    public final String TAG = "QuestionFragment";
    private AppConfigListener appConfigListener;
    private TinyDB tinyDB;

    Context context;
    Spinner questionsSpinner;
    EditText answer;
    Button confirmButton;
    int questionNumber = 0;

    public QuestionFragment(AppConfigListener appConfigListener) {
        this.appConfigListener = appConfigListener;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        tinyDB = new TinyDB(context);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        if (getActivity() instanceof SettingActivity) {
            //do something
            ImageView back = view.findViewById(R.id.back);
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });

            if (!tinyDB.getBoolean(AppConfig.PURCHASE)) {
                RelativeLayout main_setting = view.findViewById(R.id.main_setting);
                main_setting.setVisibility(View.VISIBLE);
                main_setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(AppConfig.IN_APP, true);
                        startActivity(new Intent(context, PremiumActivity.class).putExtras(bundle));
                    }
                });
            }
        }

        confirmButton = (Button) view.findViewById(R.id.confirmButton);
        questionsSpinner = (Spinner) view.findViewById(R.id.questionsSpinner);
        answer = (EditText) view.findViewById(R.id.answer);

        List<String> list = new ArrayList<String>();
        list.add("Select your security question?");
        list.add("What is your pet name?");
        list.add("Who is your favorite teacher?");
        list.add("Who is your favorite actor?");
        list.add("Who is your favorite actress?");
        list.add("Who is your favorite cricketer?");
        list.add("Who is your favorite footballer?");

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionsSpinner.setAdapter(stringArrayAdapter);

        questionsSpinner.setSelection(0);
        questionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionNumber = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionNumber != 0 && !answer.getText().toString().isEmpty()) {

                    tinyDB.putInt(AppConfig.QUESTION_NUMBER, questionNumber);
                    tinyDB.putString(AppConfig.ANSWER, answer.getText().toString());
                    tinyDB.putBoolean(AppConfig.FIRST_TIME_APP_INSTALL, true);
                    appConfigListener.onSuccess("Success", 1);

                } else {
                    Toast.makeText(context, "Please select a question and write an answer", Toast.LENGTH_SHORT).show();
                }


            }
        });
        return view;
    }
}