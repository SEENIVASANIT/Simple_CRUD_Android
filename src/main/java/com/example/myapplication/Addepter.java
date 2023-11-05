package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class Addepter extends RecyclerView.Adapter<Addepter.MyViwholder> {
    private  View_list showActivity;
    private List<Module> myList;
    private FirebaseFirestore db;
    FirebaseStorage storageReference;
    public Addepter(View_list activity,List<Module> myList){
        this.showActivity=activity;
        this.myList=myList;
    }
    @NonNull
    @Override
    public MyViwholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(showActivity).inflate(R.layout.listlayaout,parent,false);
        db = FirebaseFirestore.getInstance();
        return new MyViwholder(v);

    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MyViwholder holder, int position) {
holder.title.setText(myList.get(position).getName());
holder.desc.setText(myList.get(position).getDesc());
Picasso.with(showActivity).load(myList.get(position).getImg()).placeholder(R.drawable.ic_launcher_background).fit().into(holder.imageView);

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("name",myList.get(position).getName());
                bundle.putString("desc",myList.get(position).getDesc());
                bundle.putString("img",myList.get(position).getImg());
                bundle.putString("id",myList.get(position).getId());


                Intent intent=new Intent(showActivity, MainActivity.class);
                intent.putExtras(bundle);
                showActivity.startActivity(intent);
            }
        });
       ////////////////////delete
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.title.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Delete this image...");
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("imageWithdata").document(myList.get(position).getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    notifyItemRemoved(position);
                                    Toast.makeText(showActivity, "Delete successful.", Toast.LENGTH_SHORT).show();

                                }
                                storageReference= FirebaseStorage.getInstance();
                                StorageReference delectstorage=storageReference.getReferenceFromUrl(myList.get(position).img);
                                delectstorage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(showActivity, "Something woring,plase check your network and Tryagin!", Toast.LENGTH_LONG).show();

                                    }
                                });

                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            }
        });




    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public static class MyViwholder extends RecyclerView.ViewHolder{
TextView title,desc;
ImageView imageView;
Button delete,edit;
        public MyViwholder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.showname);
            desc=itemView.findViewById(R.id.showdesc);
            imageView=itemView.findViewById(R.id.img);
            delete=itemView.findViewById(R.id.delete);
            edit=itemView.findViewById(R.id.edits);
        }
    }
}
