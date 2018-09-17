package jeeryweb.geocast.Adapters;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import jeeryweb.geocast.Models.InboxRowRecord;
import jeeryweb.geocast.Models.SentRowRecord;
import jeeryweb.geocast.R;
import jeeryweb.geocast.databinding.SentRowRecordBinding;

/**
 * Created by Jeery on 16-03-2018.
 */

public class SentListviewAdapter {


    SentRowRecordAdapter sentRowRecordAdapter;
    ListView lv;
    Context con;
    List<SentRowRecord> rows;
    final String TAG= "SentListviewAdapter";

    public void recordsInListview(Context con, ListView lv, Activity activity, List<SentRowRecord> rows) {
        sentRowRecordAdapter =new SentRowRecordAdapter(activity ,rows);
        this.lv=lv;
        this.rows=rows;
        this.con=con;
        lv.setAdapter(sentRowRecordAdapter);

    }
    public class SentRowRecordAdapter extends BaseAdapter implements Filterable {
        LayoutInflater inflater;
        Activity activity;
        ValueFilter valueFilter;
        List rows;
        List filter;

        public SentRowRecordAdapter( Activity activity, List rows) {
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
            final SentRowRecordBinding sentRowRecordBinding= DataBindingUtil.inflate(inflater, R.layout.sent_row_record ,parent,false);
            final SentRowRecord sentRowRecord=(SentRowRecord) rows.get(position);
            Log.v("SentListviewAdapter",position+" ");

            /*
            if(position%2==0)
                rowRecordBinding.backRow.setBackgroundColor(Color.LTGRAY);
            else rowRecordBinding.backRow.setBackgroundColor(Color.GRAY);
            */
            sentRowRecordBinding.sentMessageTxt.setText(sentRowRecord.message);
            sentRowRecordBinding.sentMessageTime.setText(sentRowRecord.time);

            return sentRowRecordBinding.getRoot();
        }

        @Override
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
        sentRowRecordAdapter.getFilter().filter(newText);
    }

}
