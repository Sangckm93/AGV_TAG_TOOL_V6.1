package com.gmail.agv_tag_tool_v6;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TagShowAdapter extends BaseAdapter {//implements Filterable {
    private Context context;
    private int layout;
    private  List<TagShow> tagShowList;
    private ArrayList<TagShow> tagShowArrayList;    // temp array
//    CustomFilter tempFilter;

    public TagShowAdapter(Context context, int layout, List<TagShow> tagShowList1) {
        this.context = context;
        this.layout = layout;
        this.tagShowList = tagShowList1;
        this.tagShowArrayList = new ArrayList<>();
        this.tagShowArrayList.addAll(tagShowList);
    }
    @Override
    public int getCount() {
        return tagShowList.size();
    }

    @Override
    public Object getItem(int i) {
        return tagShowList.get(i).getTag_value();
//        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{
        TextView textViewTagValue, textViewTagType, textViewDateTime;
        ImageView imageViewSymbol;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layout,null);
            holder = new ViewHolder();
            // init view
            holder.textViewTagValue = view.findViewById(R.id.textviewTagValue);
            holder.textViewTagType  = view.findViewById(R.id.textviewTagType);
            holder.imageViewSymbol = view.findViewById(R.id.imageviewSymbol);
            holder.textViewDateTime = view.findViewById(R.id.textviewDateTime);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        // set view value

        TagShow tagShow = tagShowList.get(i);
        holder.textViewTagValue.setText(tagShow.getTag_value());
        holder.textViewTagType.setText(tagShow.getTag_type());
        holder.imageViewSymbol.setImageResource(tagShow.getType_symbol());
        holder.textViewDateTime.setText(tagShow.getDate_time());
        return view;
    }
//    @Override
//    public Filter getFilter() {
//        if (tempFilter == null) {
//            tempFilter = new CustomFilter();
//        }
//        return tempFilter;
//    }
    // Filter Class

    public  void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        tagShowList.clear();
        if (charText.length()==0) tagShowList.addAll(tagShowArrayList);
        else {
            for(TagShow tag:tagShowArrayList)
            {
                if (tag.getTag_value().toLowerCase(Locale.getDefault()).contains(charText)){
                    tagShowList.add(tag);
                }
            }
        }
        notifyDataSetChanged();
    }

//
//    class CustomFilter extends Filter
//    {
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            FilterResults results = new FilterResults();
//            if (constraint!= null && constraint.length()>0)
//            {
//                constraint = constraint.toString().toLowerCase();
//                ArrayList<TagShow> filters = new ArrayList<>();
//                for(TagShow tag:tagShowArrayList)
//                {
//                    if (tag.getTag_value().toLowerCase().contains(constraint)){
//                        filters.add(tag);
//                    }
//                }
//                results.count = filters.size();
//                results.values = filters;
//            }else {
//                results.count = tagShowArrayList.size();
//                results.values = tagShowArrayList;
//            }
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//
//            tagShowList = (ArrayList<TagShow>)filterResults.values;
//            notifyDataSetChanged();
//        }
//    }
}

