package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
private  Button save,update;
String edit_name=null,edit_desc,edit_img,edit_id;
private FirebaseFirestore db;
    TextInputEditText name,desc,img;
    ImageView imageView;
    ProgressDialog dialag;
    public Uri imageUri;
    StorageReference storageReference= FirebaseStorage.getInstance().getReference("players_profile");
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name=findViewById(R.id.name);
        desc=findViewById(R.id.desc);
        save=findViewById(R.id.save);
        update=findViewById(R.id.update);
        imageView=findViewById(R.id.imageView);
       db=FirebaseFirestore.getInstance();
        dialag = new ProgressDialog(this);

       save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               if(edit_name==null) {
                   String a = name.getText().toString();
                   String b = desc.getText().toString();
                   String id = UUID.randomUUID().toString();
                   uplad_bio_data(imageUri, a, b, id);
               }else{
                update_bio_data(name.getText().toString(),desc.getText().toString(), String.valueOf(imageUri),edit_id);
               }
           }
       });
update.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(MainActivity.this,View_list.class));
    }


});
imageView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        openFileChose1();
    }
});
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            edit_name= bundle.getString("name");
            edit_desc= bundle.getString("desc");
            edit_id=bundle.getString("id");
            edit_img= bundle.getString("img");
            name.setText(edit_name);
            desc.setText(edit_desc);

            Picasso.with(this).load(edit_img).fit().into(imageView);
            save.setText("update");


        }
    }

    public void uplad_bio_data(Uri uri,String name,String desc,String id){
        dialag.setTitle("Uploading information...!");

            dialag.show();
            StorageReference fileRef = storageReference.child(  System.currentTimeMillis()+ "." + getFileExtension(imageUri));
            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String id= UUID.randomUUID().toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id",id);
                            map.put("name",name);
                            map.put("desc",desc);
                            map.put("img",uri.toString());
                            db.collection("imageWithdata").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialag.dismiss();
//                                    Bundle bundle=new Bundle();
//                                    bundle.putString("email",emaild);
//                                    bundle.putString("show_bio","show");
//                                    Intent intent=new Intent(Players_Bio.this, Show_biodata.class);
//                                    intent.putExtras(bundle);
//                                    startActivity(intent);
//                                    Toast.makeText(Players_Bio.this, "Data add successful!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialag.dismiss();
                                    //Toast.makeText(Players_Bio.this, "Something woring,plase check your network and Tryagin!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    float a=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();


                    dialag.setMessage("Place wait!"+(int)a+"%");
                }
            });


    }
    /////////////////////////////////////////////////update/////////////////////////////////////////////////////

    private void saveToFirebase(Uri uri,String name,String desc,String id){
        dialag.show();
        StorageReference fileRef = storageReference.child(  System.currentTimeMillis()+ "." + getFileExtension(imageUri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String id= UUID.randomUUID().toString();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("id",id);
                        map.put("name",name);
                        map.put("desc",desc);
                        map.put("img",uri.toString());
                        db.collection("imageWithdata").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialag.dismiss();
//                                Bundle bundle=new Bundle();
//                                bundle.putString("email",emaild);
//                                bundle.putString("show_bio","show");
//                                Intent intent=new Intent(Players_Bio.this, Show_biodata.class);
//                                intent.putExtras(bundle);
//                                startActivity(intent);
                                Toast.makeText(MainActivity.this, "Saved successfull!!!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialag.dismiss();
                                Toast.makeText(MainActivity.this, "Something woring,plase check your network and Tryagin!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2 && resultCode==RESULT_OK && data !=null){
            imageUri=data.getData();
            //imageView.setImageURI(imageUri);
            Picasso.with(this).load(imageUri).fit().into(imageView);

        }
    }

    //////Update
    public void update_bio_data(String Ename,String Edesc,String Eimg,String id) {
     dialag.setTitle("UPDATE!!!");
//
            dialag.show();
        if(imageUri!=null){
            StorageReference fileRef = storageReference.child(  System.currentTimeMillis()+ "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            db.collection("imageWithdata").document(id).update("name",Ename, "desc", Edesc, "img", imageUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialag.dismiss();
//                                Bundle bundle=new Bundle();
//                                bundle.putString("email",emaild);
//                                bundle.putString("show_bio","show");
//                                Intent intent=new Intent(Players_Bio.this, Show_biodata.class);
//                                intent.putExtras(bundle);
//                                startActivity(intent);
                                    Toast.makeText(MainActivity.this, "Update successfull!!!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialag.dismiss();
                                    Toast.makeText(MainActivity.this, "Something woring,plase check your network and Tryagin!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Not work error!!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    float a=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();


                    dialag.setMessage("Place wait!"+(int)a+"%");
                }
            });
        }
        else {
                    db.collection("imageWithdata").document(id).update("name",Ename, "desc", Edesc, "img", Eimg).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainActivity.this, "Update Successfully!!!", Toast.LENGTH_SHORT).show();
                    dialag.dismiss();
//                    Bundle bundle=new Bundle();
//                    bundle.putString("email",email1);
//                    bundle.putString("show_bio","show");
////                    Intent intent=new Intent(Players_Bio.this, Show_biodata.class);
////                    intent.putExtras(bundle);
////                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialag.dismiss();
                    Toast.makeText(MainActivity.this, "Something woring,plase check your network and Tryagin!", Toast.LENGTH_LONG).show();
                }
            });

        }


        }

    private String getFileExtension(Uri muri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(muri));
    }
    public void openFileChose1(){
        Intent galleryIntent =new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, 2);


    }
}
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.8.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("com.google.firebase:firebase-firestore:24.9.1")
//    implementation("com.google.firebase:firebase-storage:20.3.0")
//    implementation("com.squareup.picasso:picasso:2.5.2")
//    implementation("com.google.firebase:firebase-inappmessaging:20.4.0")
//    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")