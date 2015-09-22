package com.ustbeatclub.adapter;

import java.util.List;

import com.ustbeatclub.jinchisha.R;
import com.ustbeatclub.local.DataManager;
import com.ustbeatclub.local.Sort;
import com.ustbeatclub.other.DefaultImage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SortListAdapter extends ArrayAdapter<Sort> {

	Activity context;
	int resource;
	public SortListAdapter(Context context, int resource, List<Sort> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = (Activity)context;
		this.resource = resource;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        
        View view = convertView;
        
        if(view == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(resource, null);
        }        
        
        Sort sort = getItem(position);
        
        ImageView sortIV= (ImageView)view.findViewById(R.id.sortImageView);
        Bitmap image = sort.getImage();
        if (image==null)
        {
        	sortIV.setImageBitmap(DefaultImage.defaultImage);
        	DataManager.INSTANCE.addSortImgTask(sort.getId(), position, sort.getMd5());
        }
        else sortIV.setImageBitmap(image);
        TextView sortTV = (TextView)view.findViewById(R.id.sortTextView);
        sortTV.setText(sort.getSpanName());
        TextView timeTV = (TextView)view.findViewById(R.id.timeTextView);
        timeTV.setText(sort.getTime());
        TextView priceTV = (TextView)view.findViewById(R.id.priceTextView);
        priceTV.setText(sort.getPrice());
        TextView suggestTV = (TextView)view.findViewById(R.id.suggestTextView);
        suggestTV.setText(sort.getSuggest());
        
    	return view;
	}
}