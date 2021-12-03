package com.example.smts.salesman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smts.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

class AdapterSalesman  extends FirebaseRecyclerAdapter<ModelSalesman, AdapterSalesman.ViewHolder>{


    public AdapterSalesman(@NonNull FirebaseRecyclerOptions<ModelSalesman> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final ModelSalesman model) {

        holder.tvName.setText("Name : "+model.getName_salesman());
        holder.tvMobile.setText("Mobile : "+model.getMob_salesman());

        holder.item_viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDetail = new Intent(view.getContext(), SalesmanDetailsActivity.class);
                intentDetail.putExtra("key_id",getRef(position).getKey());
                intentDetail.putExtra("name_salesman",model.getName_salesman());
                intentDetail.putExtra("cnic_salesman",model.getCnic_salesman());
                intentDetail.putExtra("mob_salesman",model.getMob_salesman());
                intentDetail.putExtra("email_salesman",model.getEmail_salesman());
                intentDetail.putExtra("password_salesman",model.getPassword_salesman());
                intentDetail.putExtra("workingArea",model.getWorkingArea());
                view.getContext().startActivity(intentDetail);

            }
        });
//
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                openDeleteDialog(view.getContext(), position);

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data, parent, false);
        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvName;
        private final TextView tvMobile;
        private final RelativeLayout item_viewDetail;
        private final ImageButton btn_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

             tvName = itemView.findViewById(R.id.tvName_id);
             tvMobile = itemView.findViewById(R.id.tvMobile_id);
             item_viewDetail = itemView.findViewById(R.id.item_viewDetail_id);
             btn_delete = itemView.findViewById(R.id.btn_delete_id);

        }

    }

//     class ViewHolder extends RecyclerView.ViewHolder{
//        public TextView tvName,tvCnic,tvMobile;
//        private RelativeLayout item_viewDetail;
//        private ImageButton btn_delete;
//
//
//        public ViewHolder(final View itemView) {
//            super(itemView);
//             tvName = itemView.findViewById(R.id.tvName_id);
//             tvMobile = itemView.findViewById(R.id.tvMobile_id);
//             item_viewDetail = itemView.findViewById(R.id.item_viewDetail_id);
//             btn_delete = itemView.findViewById(R.id.btn_delete_id);
//
//            firebaseDatabase = FirebaseDatabase.getInstance();
//            databaseReference = firebaseDatabase.getReference().child("SalesmanTable");
//
//        }
//

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    public AdapterSalesman(List<ModelSalesman> listData) {
//        this.listData = listData;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.data,parent,false);
//
//        salesmanAdapter = new Salesman();
//
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        final ModelSalesman ld=listData.get(position);
//        holder.tvName.setText("Name : "+ld.getName_salesman());
//        holder.tvMobile.setText("Mobile : "+ld.getMob_salesman());
//
//
//        holder.item_viewDetail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intentDetail = new Intent(view.getContext(), SalesmanDetailsActivity.class);
//                intentDetail.putExtra("id_salesman",position+1);
//                intentDetail.putExtra("name_salesman",ld.getName_salesman());
//                intentDetail.putExtra("cnic_salesman",ld.getCnic_salesman());
//                intentDetail.putExtra("mob_salesman",ld.getMob_salesman());
//                intentDetail.putExtra("email_salesman",ld.getEmail_salesman());
//                intentDetail.putExtra("password_salesman",ld.getPassword_salesman());
//                intentDetail.putExtra("workingArea",ld.getWorkingArea());
//                view.getContext().startActivity(intentDetail);
//
//            }
//        });
//
//        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//
//                openDeleteDialog(view.getContext(), ld.getCnic_salesman());
//
////                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
////                Query applesQuery = ref.child("Salesman").orderByChild("cnic_salesman").equalTo(ld.getCnic_salesman());
////
////                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(DataSnapshot dataSnapshot) {
////                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
////                            appleSnapshot.getRef().removeValue();
////                        }
//////                     Toast.makeText(view.getContext(), "Salesman Data Deleted successfully", Toast.LENGTH_LONG).show();
////                    }
////
////                    @Override
////                    public void onCancelled(DatabaseError databaseError) {
////                        Log.e(TAG, "onCancelled", databaseError.toException());
////                    }
////                });
//
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return listData.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//        private TextView tvName,tvCnic,tvMobile;
//        private RelativeLayout item_viewDetail;
//        private ImageButton btn_delete;
//
//
//        public ViewHolder(final View itemView) {
//            super(itemView);
//             tvName = itemView.findViewById(R.id.tvName_id);
//             tvMobile = itemView.findViewById(R.id.tvMobile_id);
//             item_viewDetail = itemView.findViewById(R.id.item_viewDetail_id);
//             btn_delete = itemView.findViewById(R.id.btn_delete_id);
//
//            firebaseDatabase = FirebaseDatabase.getInstance();
//            databaseReference = firebaseDatabase.getReference().child("SalesmanTable");
////            databaseReference= FirebaseDatabase.getInstance().getReference("Salesman");
////            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
////                @Override
////                public void onDataChange(DataSnapshot dataSnapshot) {
////                    if (dataSnapshot.exists()){
////                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
////                            ModelSalesman dataItem = npsnapshot.getValue(ModelSalesman.class);
////                            listData.add(dataItem);
////
////                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
////                                Log.e(TAG, "getKey: "+postSnapshot.getKey());
////                                Log.e(TAG, "getValue: "+postSnapshot.getValue());
////                                Log.e(TAG, "Name: "+postSnapshot.child("name_salesman").getValue());
////                                Log.e(TAG, "Email: "+postSnapshot.child("email_salesman").getValue());
//////                            Toast.makeText(context, ""+postSnapshot.getValue(), Toast.LENGTH_SHORT).show();
//////                            Toast.makeText(context, ""+postSnapshot.child("name_salesman").getValue(), Toast.LENGTH_SHORT).show();
//////                            Toast.makeText(context, ""+postSnapshot.child("email_salesman").getValue(), Toast.LENGTH_SHORT).show();
////
////                                name = String.valueOf(postSnapshot.child("name_salesman"));
//////                                id = postSnapshot.getKey();
////                                id = 7;
////
////                            }
////                        }
////
////                    }
////                }
////
////                @Override
////                public void onCancelled(DatabaseError databaseError) {
////
////                }
////            });
//        }

    public void openDeleteDialog(final Context context, final int pos) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setCancelable(true);
        mDialog.setTitle("Delete Alert..!");
        mDialog.setMessage("Do you want to Delete this Record..!");
        mDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseDatabase.getInstance().getReference("SalesmanTable")
                        .child(getRef(pos)
                                .getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(context, "Salesman Profile Deleted Successfully.", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context, "Profile Not Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        mDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog.create().show();
    }

}