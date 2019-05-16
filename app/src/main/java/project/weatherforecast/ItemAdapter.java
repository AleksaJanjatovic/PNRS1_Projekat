package project.weatherforecast;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;


public class ItemAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CityListItem> mCityListItems;

    public ItemAdapter(Context context) {
        mContext = context;
        mCityListItems = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mCityListItems.size();
    }

    @Override
    public Object getItem(int position) {
       Object rv = null;
       try {
           rv = mCityListItems.get(position);
       } catch (IndexOutOfBoundsException e) {
           e.printStackTrace();
       }
       return rv;
    }

    public void addCity(CityListItem city) {
        mCityListItems.add(city);
        notifyDataSetChanged();
    }

    public void removeCity(int position) {
        mCityListItems.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        CityListItem item = (CityListItem) getItem(position);
        holder = (ViewHolder) view.getTag();
        holder.cityName.setText(item.getTextCity());
        holder.citySelected.setChecked(item.getRadioCity());
        return view;
    }

    private class ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView cityName = null;
        public RadioButton citySelected = null;

        public ViewHolder(View v){
            cityName = v.findViewById(R.id.listItemTextView);
            citySelected = v.findViewById(R.id.listItemRadioButton);
            cityName.setOnLongClickListener(this);
            citySelected.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //SystemClock.sleep(300);
            for(int i = 0; i < mCityListItems.size(); i++){
                if(mCityListItems.get(i).getTextCity().equals(cityName)) {
                    continue;
                }
                mCityListItems.get(i).setRadioCity(false);
            }
            notifyDataSetChanged();

            Intent intent = new Intent(mContext, DetailsActivity.class);
            intent.putExtra(mContext.getResources().getString(R.string.lokacija_str), cityName.getText().toString());
            mContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            for(int i = 0; i < mCityListItems.size(); i++) {
                if(mCityListItems.get(i).getTextCity().equals(((TextView) v).getText().toString())) {
                    MainActivity.dbHelper.deleteCityWeather(mCityListItems.get(i).getTextCity());
                    mCityListItems.remove(i);
                    break;
                }
            }
            notifyDataSetChanged();
            return true;
        }
    }
}
