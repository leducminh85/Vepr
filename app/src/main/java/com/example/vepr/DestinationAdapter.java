package com.example.vepr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {
    private ArrayList mDestinations;
    private final Context mContext;
    public DestinationAdapter(ArrayList<Destination> _destination, Context mContext) {
        this.mDestinations = _destination;
        this.mContext = mContext;
    }

    //Adapter's Implemented methods
    @Override
    public int getItemViewType(int position) {
        Destination des = (Destination) mDestinations.get(position);
        if(des.type.equals("parking")) return 1;
        if(des.type.equals("repair")) return 2;
        if(des.type.equals("store")) return 3;
        return 0;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        if(viewType==1) itemView = inflater.inflate(R.layout.destination_item_parking, parent, false);
        else if(viewType==2) itemView = inflater.inflate(R.layout.destination_item_repair,parent, false);
        else if(viewType==3)itemView = inflater.inflate(R.layout.destination_item_store, parent, false);
        else itemView = inflater.inflate(R.layout.destination_item_notype, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull DestinationAdapter.ViewHolder holder, int position) {
        Destination des = (Destination) mDestinations.get(position);
        holder.desNameItemView.setText(des.desName);
        holder.desAddressItemView.setText(des.address);
        if(des.openTime!=null)holder.desDistanceItemView.setText(des.openTime);
    }
    @Override
    public int getItemCount() {
        return mDestinations.size();
    }


    //ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mItemView;
        public TextView desNameItemView;
        public TextView desAddressItemView;
        public TextView desDistanceItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            desNameItemView=mItemView.findViewById(R.id.idDesName);
            desAddressItemView=mItemView.findViewById(R.id.idDesAddress);
            desDistanceItemView=mItemView.findViewById(R.id.idDesDistance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();
                    Context context=view.getContext();
                    Intent intent =new Intent(context,DetailActivity.class);
                    Destination desObject=(Destination) mDestinations.get(pos);
                    intent.putExtra("desObject",desObject);
                    context.startActivity(intent);
                }
            });
        }
    }


}





