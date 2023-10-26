package sk.henrichg.phoneprofilesplus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class NotUsedMobileCellsDialogAdapter extends BaseAdapter {

    //private final LayoutInflater inflater;

    private final NotUsedMobileCellsDetectedActivity activity;

    NotUsedMobileCellsDialogAdapter(NotUsedMobileCellsDetectedActivity activity)
    {
        this.activity = activity;

        //inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return activity.cellNamesList.size();
    }

    @Override
    public Object getItem(int i) {
        return activity.cellNamesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        TextView cellName;
        //int position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        View vi = convertView;
        if (convertView == null)
        {
            vi = LayoutInflater.from(activity).inflate(R.layout.listitem_mobile_cell_names_dialog, parent, false);

            holder = new ViewHolder();
            holder.cellName = vi.findViewById(R.id.mobile_cell_names_dialog_item_cell_name);
            vi.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)vi.getTag();
        }

        String cellName = activity.cellNamesList.get(position);

        if (cellName != null)
        {
            holder.cellName.setText(cellName);
        }

        return vi;
    }
}
