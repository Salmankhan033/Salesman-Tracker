package com.example.smts.product;

import android.app.AlertDialog;
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

class AdapterProducts extends FirebaseRecyclerAdapter<ModelProducts, AdapterProducts.ViewHolder> {
        DatabaseReference databaseReference;
        String TAG = "TAG";

    public AdapterProducts(@NonNull FirebaseRecyclerOptions<ModelProducts> options) {
            super(options);
            }

        @Override
        protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final ModelProducts model) {

        holder.tvName.setText("Product Name : "+model.getProduct_name());
        holder.tvPrice.setText("Product Price : "+model.getProduct_price());

       holder.item_viewDetail.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intentDetail = new Intent(view.getContext(), ProductDetailsActivity.class);
            intentDetail.putExtra("key_id",getRef(position).getKey());
                intentDetail.putExtra("product_name",model.getProduct_name());
                intentDetail.putExtra("product_id",model.getProduct_id());
                intentDetail.putExtra("product_price",model.getProduct_price());
                view.getContext().startActivity(intentDetail);

                }
                });

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

      private TextView tvName, tvPrice;
        private RelativeLayout item_viewDetail;
        private ImageButton btn_delete;


        public ViewHolder(final View itemView) {
            super(itemView);
             tvName = itemView.findViewById(R.id.tvName_id);
             tvPrice = itemView.findViewById(R.id.tvMobile_id);
             item_viewDetail = itemView.findViewById(R.id.item_viewDetail_id);
             btn_delete = itemView.findViewById(R.id.btn_delete_id);

    }

}


    public void openDeleteDialog(final Context context, final int pos) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setCancelable(true);
        mDialog.setTitle("Delete Alert..!");
        mDialog.setMessage("Do you want to Delete this Product..!");
        mDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseDatabase.getInstance().getReference("ProductsTable")
                        .child(getRef(pos)
                                .getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(context, "Products Deleted Successfully.", Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(context, "Product Not Deleted", Toast.LENGTH_SHORT).show();
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