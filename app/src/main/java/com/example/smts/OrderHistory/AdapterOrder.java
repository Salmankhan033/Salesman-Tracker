package com.example.smts.OrderHistory;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.database.FirebaseDatabase;


class AdapterOrder  extends FirebaseRecyclerAdapter<ModelOrder, AdapterOrder.ViewHolder> {


    public AdapterOrder(@NonNull FirebaseRecyclerOptions<ModelOrder> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterOrder.ViewHolder holder, final int position, @NonNull final ModelOrder model) {

        int no = position+1;
        holder.tv_order.setText("Order No : "+no);
        holder.tvSalesmanName.setText("Salesman Name : "+model.getSalesmanName());
        holder.tvCutomerName.setText("Cutomer Name : "+model.getCustomerName());

        holder.item_viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDetail = new Intent(view.getContext(), OrderDetailsActivity.class);
//                intentDetail.putExtra("orderId",model.getOrderId());
                intentDetail.putExtra("orderId",model.getOrderId());
                intentDetail.putExtra("salesmanId",model.getSalesmanId());
                intentDetail.putExtra("salesmanName",model.getSalesmanName());
                intentDetail.putExtra("customerId",model.getCustomerId());
                intentDetail.putExtra("customerName",model.getCustomerName());
                intentDetail.putExtra("mob",model.getMob());
                intentDetail.putExtra("quantity",model.getQuantity());
                intentDetail.putExtra("product",model.getProduct());
                intentDetail.putExtra("workingArea",model.getWorkingArea());
                intentDetail.putExtra("latLong",model.getLatLong());
                intentDetail.putExtra("currentDate",model.getCurrentDate());
                intentDetail.putExtra("currentTime",model.getCurrentTime());
                view.getContext().startActivity(intentDetail);

            }
        });

        holder.btnget_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String latLong = model.getLatLong();

                Uri uri = Uri.parse("geo:0,0?q="+latLong);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                view.getContext().startActivity(intent);


            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDeleteDialog(view.getContext(), position);

            }
        });

    }

    @NonNull
    @Override
    public AdapterOrder.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        return new AdapterOrder.ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_order,tvSalesmanName, tvCutomerName;
        private RelativeLayout item_viewDetail;
        private ImageButton btnget_location, btn_delete;


        public ViewHolder(final View itemView) {
            super(itemView);
            tv_order = itemView.findViewById(R.id.tv_order_id);
            tvSalesmanName = itemView.findViewById(R.id.tvSalesmanName_id);
            tvCutomerName = itemView.findViewById(R.id.tvCutomerName_id);
            item_viewDetail = itemView.findViewById(R.id.item_viewDetail_id);
            btnget_location = itemView.findViewById(R.id.btnget_location_id);
            btn_delete = itemView.findViewById(R.id.btn_delete_id);


        }

    }

    public void openDeleteDialog(final Context context, final int pos) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setCancelable(true);
        mDialog.setTitle("Delete Alert..!");
        mDialog.setMessage("Do you want to Delete this Order..!");
        mDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseDatabase.getInstance().getReference("Salesman_Placed_Orders")
                        .child(getRef(pos)
                                .getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Orders_list_activity.adapter.notifyDataSetChanged();
                            Toast.makeText(context, "Order Deleted Successfully.", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context, "Order Not Deleted", Toast.LENGTH_SHORT).show();
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


/* extends RecyclerView.Adapter<AdapterOrder.ViewHolder>{
    private List<ModelOrder> listData;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String name = "";
    int id = 0;
    String TAG = "TAG";
    ModelOrder modelOrder;

    public AdapterOrder(List<ModelOrder> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);

        modelOrder = new ModelOrder();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ModelOrder ld=listData.get(position);
        holder.tv_order.setText("Order No : "+position+1);
        holder.tvOrderName.setText("Order Name : "+ld.getOrderName());
        holder.tvCutomerName.setText("Cutomer Name : "+ld.getCustomerName());


        holder.item_viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDetail = new Intent(view.getContext(), OrderDetailsActivity.class);
                intentDetail.putExtra("orderId",ld.getOrderId());
                intentDetail.putExtra("OrderId",ld.getOrderId());
                intentDetail.putExtra("OrderName",ld.getOrderName());
                intentDetail.putExtra("customerId",ld.getCustomerId());
                intentDetail.putExtra("customerName",ld.getCustomerName());
                intentDetail.putExtra("mob",ld.getMob());
                intentDetail.putExtra("quantity",ld.getQuantity());
                intentDetail.putExtra("product",ld.getProduct());
                intentDetail.putExtra("workingArea",ld.getWorkingArea());
                intentDetail.putExtra("latLong",ld.getLatLong());
                intentDetail.putExtra("currentDate",ld.getCurrentDate());
                intentDetail.putExtra("currentDate",ld.getCurrentDate());
                intentDetail.putExtra("currentTime",ld.getCurrentTime());
                view.getContext().startActivity(intentDetail);

            }
        });

        holder.btnget_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

               String latLong = ld.getLatLong();

                Uri uri = Uri.parse("geo:0,0?q="+latLong);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                view.getContext().startActivity(intent);


            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDeleteDialog(view.getContext(), ld.getOrderId());

            }
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_order,tvOrderName, tvCutomerName;
        private RelativeLayout item_viewDetail;
        private ImageButton btnget_location, btn_delete;


        public ViewHolder(final View itemView) {
            super(itemView);
            tv_order = itemView.findViewById(R.id.tv_order_id);
            tvOrderName = itemView.findViewById(R.id.tvOrderName_id);
            tvCutomerName = itemView.findViewById(R.id.tvCutomerName_id);
            item_viewDetail = itemView.findViewById(R.id.item_viewDetail_id);
            btnget_location = itemView.findViewById(R.id.btnget_location_id);
            btn_delete = itemView.findViewById(R.id.btn_delete_id);

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("Order_Placed_Orders");

        }
    }

    public void openDeleteDialog(final Context context, final String order_id) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setCancelable(true);
        mDialog.setTitle("Delete Alert..!");
        mDialog.setMessage("Do you want to Delete this Order..!");
        mDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Query query = databaseReference.orderByChild("Order_Placed_Orders").equalTo(order_id);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query query = ref.child("Order_Placed_Orders").orderByChild("orderId").equalTo(order_id);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                }
//                     Toast.makeText(view.getContext(), "product Data Deleted successfully", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
*/
}