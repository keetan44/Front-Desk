package app.nowcare4u.comm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {


    private ImageView imageView,certi;
    private EditText Name,Mobile,MeetPerson;
    private String name,mobile,meetPerson;
    private Button takePicture,takeCertificate,next;
    Uri uri;
    private Bitmap bitmap,certificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Name = findViewById(R.id.name);
        Mobile = findViewById(R.id.mobile);
        MeetPerson=findViewById(R.id.toMeet);
        takePicture = findViewById(R.id.selectImage);
        next = findViewById(R.id.next);
        imageView = findViewById(R.id.image);
        certi = findViewById(R.id.certi);
        takeCertificate = findViewById(R.id.selectCerti);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });

        takeCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Name.getText().toString();
                mobile = Mobile.getText().toString();
                meetPerson = MeetPerson.getText().toString();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray = stream.toByteArray();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                certificate.compress(Bitmap.CompressFormat.PNG,100,stream1);
                byte[] bytes = stream1.toByteArray();

                if(name.isEmpty()||mobile.isEmpty()||meetPerson.isEmpty()||mobile.length()!=10)
                    Toast.makeText(MainActivity.this,"Please Enter Valid Details",Toast.LENGTH_LONG).show();
                else
                {
                    Intent i = new Intent(MainActivity.this,Second.class);
                    i.putExtra("name",name);
                    i.putExtra("mobile",mobile);
                    i.putExtra("meetPerson",meetPerson);

                    i.putExtra("image",byteArray);
                    i.putExtra("certi",bytes);


                    startActivity(i);
                    finish();
                }
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 0 && resultCode ==RESULT_OK && data!=null &&data.getData()!=null)
        {
            uri = data.getData();
        }
        if(requestCode==0)
        {


            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            imageView.setRotation(270);
            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            imageView.setVisibility(View.VISIBLE);
            takeCertificate.setVisibility(View.VISIBLE);
        }

        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            uri = data.getData();
        }

        if(requestCode == 1)
        {
            certificate = (Bitmap) data.getExtras().get("data");
            certi.setImageBitmap(certificate);
            certi.setRotation(90);
            certificate = ((BitmapDrawable)certi.getDrawable()).getBitmap();
            certi.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }

    }


}
