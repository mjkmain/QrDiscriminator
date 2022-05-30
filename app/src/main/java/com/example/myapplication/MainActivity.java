package com.example.myapplication;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener{

    private Button mReadBtn;
    private TextView mResult;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    int tokenNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mReadBtn = (Button)findViewById(R.id.capture);
        mResult = (TextView)findViewById(R.id.result);
        mReadBtn.setOnClickListener(this);

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("project").child("UserAccount").child("xWbql1fD4bQuMt6hWNbkinwrCSH2").child("Tokens").child("백반");

//        UserToken userToken = new UserToken();
//        userToken.setMenuName("menuName");
//        userToken.setMenuPrice(999999);
//        userToken.setTokenNumber(9999);
//        userToken.setPayMethod("payMethod");
//
//        myRef.setValue(userToken);

    }

    @Override
    public void onClick(View view) {
        mResult.setText("");
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE :
                if (resultCode == Activity.RESULT_OK) {
                    mFirebaseAuth = FirebaseAuth.getInstance();
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference("project");
                    String contents = data.getStringExtra(Intents.Scan.RESULT);
                    Log.d("TAG", "OK");
                    Log.d("TAG", "RESULT CONTENT : " + contents);
                    Log.d("TAG", "-----------");

                    String[] menuID = contents.split(",");
                    String menuName = menuID[0];
                    String UID = menuID[1];

                    menuName = uniToKor(menuName);
                    menuName = menuName.substring(0, menuName.length()-1);




                    /**
                     *  Firebase 데이터 읽어오기
                     * */
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference("project");
                    mDatabaseRef.child("UserAccount").child(UID).child("Tokens").child(menuName)
                                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(task.getResult().exists()){
                                            DataSnapshot dataSnapshot = task.getResult();
                                            String existTokenNumber = String.valueOf(dataSnapshot.child("tokenNumber").getValue());
                                            tokenNumber = Integer.parseInt(existTokenNumber);

                                            String strMenuPrice = String.valueOf(dataSnapshot.child("menuPrice").getValue());
                                            int menuPrice = Integer.parseInt(strMenuPrice);

                                            String payMethod = String.valueOf(dataSnapshot.child("payMethod").getValue());
                                            String menuName = String.valueOf(dataSnapshot.child("menuName").getValue());

                                            tokenNumber = tokenNumber - 1; // 식권 한 개 사용

                                            UserToken userToken = new UserToken();
                                            userToken.setMenuName(menuName);
                                            userToken.setMenuPrice(menuPrice);
                                            userToken.setTokenNumber(tokenNumber);
                                            userToken.setPayMethod(payMethod);

                                            mResult.setText(menuName +"\n" + payMethod);
                                            mDatabaseRef.child("UserAccount").child(UID).child("Tokens").child(menuName).setValue(userToken);

                                            Toast.makeText(getApplicationContext(), menuName + "식권 사용", Toast.LENGTH_LONG).show();
                                        }else{

                                            Toast.makeText(getApplicationContext(), "실패1", Toast.LENGTH_LONG).show();
                                        }
                                    }else{

                                        Toast.makeText(getApplicationContext(), "실패2", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });






                    mResult.setText(menuName +"\n"+menuName.length()+"\n" +menuName.getClass().getName()+ "\n\n" + UID +"\n"+UID.length()+ "\n" +UID.getClass().getName()) ;
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                    Log.d("TAG", "NOT OK");
                }
                break;
            default :
                Log.d("TAG", "NOT RESULT CODE");
        }
    }
    public String uniToKor(String uni){
        StringBuffer result = new StringBuffer();

        for(int i=0; i<uni.length(); i++){
            if(uni.charAt(i) == '\\' &&  uni.charAt(i+1) == 'u'){
                Character c = (char)Integer.parseInt(uni.substring(i+2, i+6), 16);
                result.append(c);
                i+=5;
            }else{
                result.append(uni.charAt(i));
            }
        }
        return result.toString();
    }
}
