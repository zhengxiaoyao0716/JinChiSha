package com.ustbeatclub.adapter;

import java.util.List;

import com.ustbeatclub.jinchisha.R;
import com.ustbeatclub.local.DataManager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FoodListAdapter extends ArrayAdapter<String> {

	Activity context;
	int resource;
	public FoodListAdapter(Context context, int resource, List<String> objects) {
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
        
        String foodStr = getItem(position);
        
        TextView foodTV = (TextView)view.findViewById(R.id.foodListTextView);
        foodTV.setText(foodStr);
        if (position==DataManager.INSTANCE.getFood())
        {
        	view.setBackgroundResource(R.drawable.food_list_light);
        }
        else
        {
        	view.setBackgroundResource(R.drawable.food_list_bg);
        }
        
    	return view;
	}
}
