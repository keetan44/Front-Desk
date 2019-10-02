package app.nowcare4u.comm;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;


public class Second extends AppCompatActivity {

    private EditText OTP;
    private Button verify;
    RequestQueue requestQueue;
    ProgressBar progressBar;
    int flag=0;

    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    byte[] b,certi;
    TextView head;
    DatabaseReference databaseReference;
    StorageReference storageReference = mStorage.getReferenceFromUrl("gs://intern-2a436.appspot.com/");

    String sesion_ID,name,mobile,meetPerson;
    String SEND_OTP="https://2factor.in/API/V1/7f2904db-e0a3-11e9-ade6-0200cd936042/SMS/+91";
    String VERIFY="https://2factor.in/API/V1/7f2904db-e0a3-11e9-ade6-0200cd936042/SMS/VERIFY/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        OTP = findViewById(R.id.otp);
        verify = findViewById(R.id.verify);
        head=findViewById(R.id.head);
        progressBar=findViewById(R.id.progress_horizontal);

        builder=new AlertDialog.Builder(this);



        builder.setMessage("Uploaded details").setTitle("Success");

        builder.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent act= new Intent(Second.this,MainActivity.class);
                startActivity(act);
                finish();
            }
        });




        databaseReference = FirebaseDatabase.getInstance().getReference("Meeting");


        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        mobile= intent.getStringExtra("mobile");
        meetPerson = intent.getStringExtra("meetPerson");
        b=intent.getByteArrayExtra("image");
        certi = intent.getByteArrayExtra("certi");

        requestQueue = Volley.newRequestQueue(Second.this);

        SEND_OTP =SEND_OTP+mobile+ "/AUTOGEN";
        sendVerificationCode();



        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                OTP.setVisibility(View.GONE);
                verify.setVisibility(View.GONE);
                head.setVisibility(View.GONE);
                VERIFY = VERIFY + sesion_ID +"/"+OTP.getText().toString();
                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, VERIFY, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result= jsonObject.getString("Status");
                            if(result.equals("Success"))
                            {
//                                Toast.makeText(Second.this,"Succeed",Toast.LENGTH_LONG).show();
                                String id = databaseReference.push().getKey();
                                Meeting meeting = new Meeting(id,name,mobile,meetPerson);
                                databaseReference.child(id).setValue(meeting);

                                StorageReference chidRef = storageReference.child(id+".jpg");

                                UploadTask uploadTask = chidRef.child("Face").putBytes(b);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                        Toast.makeText(Second.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                        String id = databaseReference.push().getKey();

                                        StorageReference chidRef = storageReference.child(id+".jpg");


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Second.this, "Upload  Not successful", Toast.LENGTH_SHORT).show();

                                    }
                                });

                                UploadTask uploadTask1 = chidRef.child("ID").putBytes(certi);

                                uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        progressBar.setVisibility(View.GONE);
                                        OTP.setVisibility(View.VISIBLE);
                                        head.setVisibility(View.VISIBLE);
                                        verify.setVisibility(View.VISIBLE);
                                        alertDialog = builder.create();
                                        alertDialog.show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Second.this, "Upload  Not successful", Toast.LENGTH_SHORT).show();

                                    }
                                });



                            }
                            else
                            {
                                Toast.makeText(Second.this,"Not Succeeded",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        builder.setMessage("Failed to upload details").setTitle("Failed");
                        alertDialog = builder.create();
                        alertDialog.show();
//                        Toast.makeText(Second.this,"Please try again later...",Toast.LENGTH_LONG).show();
                    }
                });
                requestQueue.add(stringRequest1);
            }
        });

    }

    private void sendVerificationCode() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, SEND_OTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result=jsonObject.getString("Status");
                    sesion_ID=jsonObject.getString("Details");
                    if(result.equals("Success"))
                    {
                        progressBar.setVisibility(View.GONE);
                        OTP.setVisibility(View.VISIBLE);
                        verify.setVisibility(View.VISIBLE);
                        head.setVisibility(View.VISIBLE);
                        Toast.makeText(Second.this,"OTP sent",Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(Second.this,"OTP not sent!Try later..",Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Second.this,"Please try later",Toast.LENGTH_LONG).show();
                startActivity(new Intent(Second.this,MainActivity.class));
                finish();

            }
        });
        requestQueue.add(stringRequest);
    }

}
