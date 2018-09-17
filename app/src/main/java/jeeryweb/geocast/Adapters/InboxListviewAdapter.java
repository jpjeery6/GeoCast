package jeeryweb.geocast.Adapters;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jeeryweb.geocast.Models.InboxRowRecord;
import jeeryweb.geocast.R;
import jeeryweb.geocast.databinding.InboxRowRecordBinding;

/**
 * Created by Jeery on 16-03-2018.
 */

public class InboxListviewAdapter {


    InboxRowRecordAdapter inboxRowRecordAdapter;
    ListView lv;
    Context con;
    List<InboxRowRecord> rows;
    final String TAG= "InboxListviewAdapter";
    RequestQueue rq;
    Boolean[] doneArr;
    private ImageLoader imageLoader;
    HashMap<String, Bitmap> cacheProPic = new HashMap<>();

    public void recordsInListview(Context con, ListView lv, Activity activity, List<InboxRowRecord> rows) {
        inboxRowRecordAdapter=new InboxRowRecordAdapter(activity ,rows);
        this.lv=lv;
        this.rows=rows;
        this.con=con;
        lv.setAdapter(inboxRowRecordAdapter);
        rq = Volley.newRequestQueue(con);
        doneArr = new Boolean[rows.size()];
        Arrays.fill(doneArr, Boolean.FALSE);



    }
    public class InboxRowRecordAdapter extends BaseAdapter implements Filterable {
        LayoutInflater inflater;
        Activity activity;
        ValueFilter valueFilter;
        List rows;
        List filter;

        public InboxRowRecordAdapter( Activity activity, List rows) {
            this.activity = activity;
            this.filter=rows;
            this.rows = rows;




        }



        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(inflater==null)
                inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final InboxRowRecordBinding inboxRowRecordBinding= DataBindingUtil.inflate(inflater, R.layout.inbox_row_record ,parent,false);
            final InboxRowRecord inboxRowRecord=(InboxRowRecord) rows.get(position);
            Log.v("InboxListviewAdapter",position+" ");

            /*
            if(position%2==0)
                rowRecordBinding.backRow.setBackgroundColor(Color.LTGRAY);
            else rowRecordBinding.backRow.setBackgroundColor(Color.GRAY);
            */
            inboxRowRecordBinding.Name.setText(inboxRowRecord.sender);
            inboxRowRecordBinding.messageTxt.setText(inboxRowRecord.txt);
            inboxRowRecordBinding.timeLast.setText(inboxRowRecord.time);

            final String url = inboxRowRecord.displayPic;

            //if(!doneArr[position]){
            //Log.e(TAG, "Url is "+url);
                if(!url.equals("NA")){

                    if (cacheProPic.containsKey(url)) {
                        inboxRowRecordBinding.profileImage.setImageBitmap(cacheProPic.get(url));
                        Log.e(TAG, "found in cache");
                    } else {
                        Log.e(TAG, "not found in cache");
                        ImageRequest ir = new ImageRequest(url,
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap response) {
                                        Log.e(TAG, "Recieved response");
                                        inboxRowRecordBinding.profileImage.setImageBitmap(response);
                                        cacheProPic.put(url, response);
                                    }
                                }, 0, 0, null, null);
                        rq.add(ir);
                    }


//                    imageLoader = CustomVolleyRequest.getInstance(con.getApplicationContext())
//                            .getImageLoader();
//                    imageLoader.get(url, ImageLoader.getImageListener(inboxRowRecordBinding.imageView,
//                            R.drawable.ic_phone, android.R.drawable
//                                    .ic_dialog_alert));
//                    inboxRowRecordBinding.imageView.setImageUrl(url, imageLoader);
                }
                doneArr[position]=true;
            //}


            return inboxRowRecordBinding.getRoot();
        }



        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

        private class ValueFilter extends Filter {
            InboxRowRecord inboxRowRecord;
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {
                    List<InboxRowRecord> filterList = new ArrayList<>();
                    for (int i = 0; i < filter.size(); i++) {
                        inboxRowRecord= (InboxRowRecord) filter.get(i);
                        if ((inboxRowRecord.sender.toUpperCase()).contains(constraint.toString().toUpperCase())) {
                            filterList.add(inboxRowRecord);
                        }
                    }
                    results.count = filterList.size();
                    results.values = filterList;
                } else {
                    results.count = filter.size();
                    results.values = filter;
                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                rows = (List<InboxRowRecord>) results.values;
                notifyDataSetChanged();
            }

        }

    }
    //handling search in table rows view
    public void handleSearch(String newText)
    {
        inboxRowRecordAdapter.getFilter().filter(newText);
    }

}
