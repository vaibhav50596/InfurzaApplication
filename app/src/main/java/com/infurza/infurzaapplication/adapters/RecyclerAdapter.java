package com.infurza.infurzaapplication.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infurza.infurzaapplication.R;



/**
 * Created by Akshay on 15-04-2017.
 */
public class RecyclerAdapter extends  RecyclerView.Adapter <RecyclerAdapter.RecyclerListViewHolder>  {

    private String[] mobileBrandsList;
    private Context context;
    private ClickListener clickListner;


    public RecyclerAdapter(Context context, String[] android, ClickListener listener) {
        if (android == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        this.mobileBrandsList = android;
        this.context = context;
        this.clickListner = listener;
        //this.tag=tag;
    }

    @Override
    public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recycler, parent, false);
        return new RecyclerListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerListViewHolder holder, final int position) {

        holder.listText.setText(mobileBrandsList[position]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    clickListner.onClick("selectionOfMobileBrand", mobileBrandsList[position]);
                }
        });

    }

    @Override
    public int getItemCount() {
        return mobileBrandsList.length;
    }

    public class RecyclerListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        TextView listText;
        ImageView listImage;
        LinearLayout listItemLayout;

        public RecyclerListViewHolder(View itemView) {
            super(itemView);
            listText = (TextView) itemView.findViewById(R.id.list_item_text);
            listImage = (ImageView) itemView.findViewById(R.id.list_item_Image);
            listItemLayout = (LinearLayout) itemView.findViewById(R.id.list_item_layout);
        }

        @Override
        public void onClick(View v) {
            String layout = "selectionOfMobileBrand";
            clickListner.onClick(layout, mobileBrandsList[getLayoutPosition()]);
        }
    }

    public interface ClickListener
    {
        public void onClick(String layout, String selectionOfMobileBrand);
    }
}
