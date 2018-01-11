package com.example.listview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by John on 4/6/2017.
 */

public class BikeAdapter extends ArrayAdapter<BikeData> {

    SharedPreferences preferences;
    Activity_ListView activity;
    private static String TETON_SOFTWARE_SITE = "http://www.tetonsoftware.com/bikes/bikes.json";
    private static String TETON_SOFTWARE_SITE_SHORT = "http://www.tetonsoftware.com/bikes/";
    private static String CNU_SITE = "http://www.pcs.cnu.edu/~kperkins/bikes/bikes.json";
    private static String CNU_SITE_SHORT = "http://www.pcs.cnu.edu/~kperkins/bikes/";
    private static final String SELECT_WEBSITE = "Select a website in settings";


    public BikeAdapter(Context context, List<BikeData> bikes) {
        super(context, R.layout.listview_row_layout, bikes);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder modelHolder;
        ViewHolder priceHolder;
        ViewHolder descriptionHolder;
        ViewHolder imageHolder;

        BikeData bike = getItem(position);
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_row_layout, parent, false);

            modelHolder = new ViewHolder();
            modelHolder.text = (TextView) convertView.findViewById(R.id.Model);
            priceHolder = new ViewHolder();
            priceHolder.text = (TextView) convertView.findViewById(R.id.Price);
            descriptionHolder = new ViewHolder();
            descriptionHolder.text = (TextView) convertView.findViewById(R.id.Description);
            imageHolder = new ViewHolder();
            imageHolder.image = (ImageView) convertView.findViewById(R.id.imageView1);

            convertView.setTag("model".hashCode(), modelHolder);
            convertView.setTag("price".hashCode(), priceHolder);
            convertView.setTag("description".hashCode(), descriptionHolder);
            convertView.setTag("image".hashCode(), imageHolder);
        }
        else {
            modelHolder = (ViewHolder)convertView.getTag("model".hashCode());
            priceHolder = (ViewHolder)convertView.getTag("price".hashCode());
            descriptionHolder = (ViewHolder)convertView.getTag("description".hashCode());
            imageHolder = (ViewHolder)convertView.getTag("image".hashCode());
        }

        modelHolder.text.setText(bike.getModel());
        priceHolder.text.setText(bike.getPrice() + "");
        descriptionHolder.text.setText(bike.getDescription());

        if(preferences.getString("JSON_url", "-1").equals(TETON_SOFTWARE_SITE)) {
            new DownloadImageTask("picture", imageHolder.image).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, TETON_SOFTWARE_SITE_SHORT + bike.getPicture());
        }
        else if(preferences.getString("JSON_url", "-1").equals(CNU_SITE)) {
            new DownloadImageTask("picture", imageHolder.image).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, CNU_SITE_SHORT + bike.getPicture());
        }
        else if(preferences.getString("JSON_url", "-1").equals("-1")) {
            Toast.makeText(activity, SELECT_WEBSITE, Toast.LENGTH_LONG).show();
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView text;
        public ImageView image;
    }
}
