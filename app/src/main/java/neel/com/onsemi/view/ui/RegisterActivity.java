package neel.com.onsemi.view.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import neel.com.onsemi.R;

import static neel.com.onsemi.util.Util.DATABASE_EMPLOYEE;
import static neel.com.onsemi.util.Util.DATABASE_EMPLOYEE_LIST;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmployeeIdEt;
    private EditText mPhoneNumber;
    private CardView mRegisterCv;
    private DatabaseReference mEmployeeListRef;

    private FirebaseAuth mAuth;

    public static final int REQUEST_LOGIN = 999;
    private String employee_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();



        mEmployeeIdEt = (EditText)findViewById(R.id.register_input_employee_id);
      //  mPhoneNumber =(EditText)findViewById(R.id.register_input_phone_number);
        mRegisterCv = (CardView)findViewById(R.id.cv_ac_register);

        mRegisterCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                employee_id = mEmployeeIdEt.getText().toString();

                if(!TextUtils.isEmpty(employee_id)) {


                                    startActivityForResult(AuthUI.getInstance()
                                            .createSignInIntentBuilder().setAvailableProviders(
                                                    Arrays.asList(
                                                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build())
                                            ).build(), REQUEST_LOGIN);

                                } else {

                    Toast.makeText(RegisterActivity.this, "Employee Id field is blank", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode ==  REQUEST_LOGIN){

            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){

                if(!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {

                    updateUserInfo();

                }else{

                    if(response == null){
                        Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.getErrorCode() == ErrorCodes.NO_NETWORK){

                        Toast.makeText(this,"No network",Toast.LENGTH_SHORT).show();
                    }

                    if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR){

                        Toast.makeText(this,"Unknown Error",Toast.LENGTH_SHORT).show();
                    }

                }
            }else{

                Toast.makeText(this,"Unknown sign in Error !!!",Toast.LENGTH_SHORT).show();

            }

        }
    }


    private void updateUserInfo() {

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(DATABASE_EMPLOYEE);

            Map<String,Object> userInfo = new HashMap<>();
            userInfo.put("username",employee_id);
            userInfo.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            userInfo.put("device_token", FirebaseInstanceId.getInstance().getToken());
            userInfo.put("thumb_image","default");


            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                }
            });
        }
    }
}












