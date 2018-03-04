package com.infurza.infurzaapplication.fragments;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infurza.infurzaapplication.R;
import com.infurza.infurzaapplication.adapters.RecyclerAdapter;
import com.infurza.infurzaapplication.adapters.ViewPagerAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EWasteSellFragment extends Fragment implements RecyclerAdapter.ClickListener {

    private ViewPager sellImagesViewPager;
    private ViewPagerAdapter mAdapter;
    public String flag = "";
    private TextView selectionOfProduct, questions, selectedItem;

    private int[] mImageResources = {
            R.drawable.sliderone,
            R.drawable.slidertwo,
            R.drawable.sliderthree
    };

    String[] mobileBrandsList;
    RecyclerView listRecyclerView;

    Spinner spinner;
    //String URL="http://techiesatish.com/demo_api/spinner.php";
    ArrayList<String> productNames;
    private DatabaseReference mDatabase;

    String product, typeOfQuestion;
    LinearLayout answerTypeRadio, previousQuestion, nextQuestion;
    EditText answerTypeEdit;
    Button submitAnswer, answerTypeButton;
    String Q1_Mobile,Q2_Mobile, Q3_Mobile, Q4_Mobile, Q5_Mobile;
    String answer;
    AppCompatRadioButton radioAnswerYes, radioAnswerNo;
    int clickCount;
    AlertDialog contentDialog;

    public EWasteSellFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ewaste_sell, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        sellImagesViewPager = (ViewPager) getView().findViewById(R.id.sellImages_view_pager);
        selectionOfProduct = (TextView) getView().findViewById(R.id.selection_of_product);
        questions = (TextView) getView().findViewById(R.id.questions_textView);
        answerTypeRadio = (LinearLayout) getView().findViewById(R.id.answerType_radio);
        answerTypeEdit = (EditText) getView().findViewById(R.id.answerType_edit);
        answerTypeButton = (Button) getView().findViewById(R.id.answerType_button);
        selectedItem = (TextView) getView().findViewById(R.id.selected_recycler_item);
        previousQuestion = (LinearLayout) getView().findViewById(R.id.previous_question);
        nextQuestion = (LinearLayout) getView().findViewById(R.id.next_question);
        submitAnswer = (Button) getView().findViewById(R.id.submit_answer_button);
        radioAnswerYes = (AppCompatRadioButton) getView().findViewById(R.id.yes_answer_radio);
        radioAnswerNo = (AppCompatRadioButton) getView().findViewById(R.id.no_answer_radio);


        mobileBrandsList = getResources().getStringArray(R.array.mobileBrands);

        questions.setVisibility(View.GONE);
        answerTypeRadio.setVisibility(View.GONE);
        answerTypeEdit.setVisibility(View.GONE);
        answerTypeButton.setVisibility(View.GONE);
        selectedItem.setVisibility(View.GONE);
        previousQuestion.setVisibility(View.GONE);
        nextQuestion.setVisibility(View.GONE);
        submitAnswer.setVisibility(View.GONE);
        radioAnswerYes.setChecked(false);
        radioAnswerNo.setChecked(false);

        flag = "SellImagesViewPager";
        mAdapter = new ViewPagerAdapter(getContext(), mImageResources, flag);
        sellImagesViewPager.setAdapter(mAdapter);
        sellImagesViewPager.setCurrentItem(0, true);

        //static data for products user can sell, afterwards list of products will be fetched from Firebase database
        productNames = new ArrayList<>();
        productNames.add("Select a product you want to sell");
        productNames.add("Mobile");
        productNames.add("Laptop");
        productNames.add("Computer");
        productNames.add("TV/LCD/LED");
        productNames.add("Camera");
        productNames.add("Gaming Console");
        productNames.add("Printer");
        productNames.add("Telephone");
        productNames.add("Modem/Router");
        productNames.add("Other");

        spinner = (Spinner) getView().findViewById(R.id.country_Name);
        //loadSpinnerData(URL);
        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, productNames));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItem() == "Select a product you want to sell") {
                    selectionOfProduct.setVisibility(View.GONE);
                } else {
                    selectionOfProduct.setVisibility(View.VISIBLE);
                    product = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                    typeOfQuestion = "YesNo"; //default answer type for all question
                    BindQuestionsDependingOnSelectedProduct(product, typeOfQuestion);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
                selectionOfProduct.setText("Select a product you want to sell");
            }
        });

        //mDatabase.setValue(product);

        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCount++;
                answerTypeRadio.setVisibility(View.GONE);
                FireQuestionsForMobile(clickCount, answer);
                if(clickCount == 2 && (radioAnswerYes.isChecked() || radioAnswerNo.isChecked())){
                    questions.setText(Q2_Mobile);
                    SetAnswerForMobile(typeOfQuestion);
                }
            }
        });
    }

    public String BindQuestionsDependingOnSelectedProduct(String product, String questionType){
        switch (product){
            case "Mobile":
                questions.setVisibility(View.VISIBLE);
                answer = "";  //this will change, depends on click of radio buttons
                clickCount = 1;
                FireQuestionsForMobile(clickCount, answer);
                questions.setText(Q1_Mobile);
                SetAnswerForMobile(questionType);
                nextQuestion.setVisibility(View.VISIBLE);
                //FireQuestionsForMobile(clickCount, answer);



                break;
            case "Laptop":
                break;
            case "Computer":
                break;
            case "TV/LCD/LED":
                break;
            case "Camera":
                break;
            case "Gaming Console":
                break;
            case "Printer":
                break;
            case "Telephone":
                break;
            case "Modem/Router":
                break;
            case "Other":
                break;

        }

        return "";
    }

    public String FireQuestionsForMobile(int questionNumber, String answer){
        // first question's condition is always true and its not important
         if(questionNumber == 1){
             Q1_Mobile = "Is your Mobile Phone in working condition?";
             typeOfQuestion = "YesNo";
             return Q1_Mobile;
         } else if(questionNumber == 2 && (radioAnswerYes.isChecked() || radioAnswerNo.isChecked())){
             answerTypeButton.setText("Select a brand");
             Q2_Mobile = "Which brand is your Mobile Phone?";
             typeOfQuestion = "Button";
             return Q2_Mobile;
         }
         return "";
         //if(answer.equalsIgnoreCase("Yes")){

         //}
         //return "";
    }

    public String SetAnswerForMobile(String questionType){
        if(questionType.equalsIgnoreCase("YesNo")){
            answerTypeRadio.setVisibility(View.VISIBLE);
            radioAnswerYes.setChecked(false);
            radioAnswerNo.setChecked(false);

            radioAnswerYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(compoundButton.isChecked()){
                        radioAnswerNo.setChecked(false);
                        //radioAnswerYes.setChecked(true);
                        answer = "Yes";
                    }
                }
            });
            radioAnswerNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (compoundButton.isChecked()){
                        radioAnswerYes.setChecked(false);
                        //radioAnswerNo.setChecked(true);
                        answer = "No";
                    }
                }
            });

            return answer;
        }
        else if(questionType.equalsIgnoreCase("Button")){
            answerTypeButton.setVisibility(View.VISIBLE);
            answerTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Select a brand of Mobile Phone");
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.list_recycler_view, null);
                    dialog.setView(dialogView);

                    listRecyclerView = (RecyclerView) dialogView.findViewById(R.id.list_recycle_view);
                    listRecyclerView.setHasFixedSize(true);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    listRecyclerView.setLayoutManager(linearLayoutManager);

                    RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), mobileBrandsList, EWasteSellFragment.this);
                    listRecyclerView.setAdapter(adapter);

                    //android.support.v7.app.AlertDialog b = dialog.create();
                    contentDialog = dialog.create();
                    contentDialog.show();

                }
            });

        }

        else {

        }
        return "";
    }

    @Override
    public void onClick(String layout, String selectionOfMobileBrand) {
        selectedItem.setVisibility(View.VISIBLE);
        selectedItem.setText(selectionOfMobileBrand);
        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        contentDialog.dismiss();
                    }
                },
                500);
        //Toast.makeText(getActivity(), layout + " " + selectionOfMobileBrand, Toast.LENGTH_SHORT).show();
    }
}

