package sk.henrichg.phoneprofilesplus;

import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import java.lang.ref.WeakReference;

class ShortcutCreatorListAdapter extends BaseAdapter {

    private ShortcutCreatorListFragment fragment;
    private DataWrapper activityDataWrapper;

    ShortcutCreatorListAdapter(ShortcutCreatorListFragment f, DataWrapper dataWrapper)
    {
        fragment = f;
        activityDataWrapper = dataWrapper;
    }

    public void release()
    {
        fragment = null;
        activityDataWrapper = null;
    }

    public int getCount() {
//        PPApplicationStatic.logE("[SYNCHRONIZED] ShortcutCreatorListAdapter.getCount", "DataWrapper.profileList");
        synchronized (activityDataWrapper.profileList) {
            fragment.viewNoData.setVisibility(
                    ((activityDataWrapper.profileListFilled &&
                            (!activityDataWrapper.profileList.isEmpty()))
                    ) ? View.GONE : View.VISIBLE);

            return activityDataWrapper.profileList.size();
        }
    }

    public Object getItem(int position) {
//        PPApplicationStatic.logE("[SYNCHRONIZED] ShortcutCreatorListAdapter.getItem", "DataWrapper.profileList");
        synchronized (activityDataWrapper.profileList) {
            return activityDataWrapper.profileList.get(position);
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ShortcutCreatorListViewHolder holder;
        
        View vi = convertView;

        //boolean applicationActivatorPrefIndicator = ApplicationPreferences.applicationActivatorPrefIndicator(fragment.getActivity());
        boolean applicationActivatorPrefIndicator = ApplicationPreferences.applicationEditorPrefIndicator;

        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(fragment.getActivity());
            if (applicationActivatorPrefIndicator)
                vi = inflater.inflate(R.layout.listitem_shortcut, parent, false);
            else
                vi = inflater.inflate(R.layout.listitem_shortcut_no_indicator, parent, false);
            holder = new ShortcutCreatorListViewHolder();
            holder.radioButton = vi.findViewById(R.id.shortcut_list_item_radiobtn);
            holder.profileName = vi.findViewById(R.id.shortcut_list_item_profile_name);
            holder.profileIcon = vi.findViewById(R.id.shortcut_list_item_profile_icon);
            if (applicationActivatorPrefIndicator)
                holder.profileIndicator = vi.findViewById(R.id.shortcut_list_profile_pref_indicator);
            vi.setTag(holder);        
        }
        else
        {
            holder = (ShortcutCreatorListViewHolder)vi.getTag();
        }


        Profile profile;
//        PPApplicationStatic.logE("[SYNCHRONIZED] ShortcutCreatorListAdapter.getView", "DataWrapper.profileList");
        synchronized (activityDataWrapper.profileList) {
            profile = activityDataWrapper.profileList.get(position);
        }
        if (profile != null) {
            Spannable profileName = profile.getProfileNameWithDuration("", "", false, false, fragment.activityDataWrapper.context);
            holder.profileName.setText(profileName);

            if (profile.getIsIconResourceID()) {
                Bitmap bitmap = profile.increaseProfileIconBrightnessForActivity(fragment.getActivity(), profile._iconBitmap);
                if (bitmap != null)
                    holder.profileIcon.setImageBitmap(bitmap);
                else {
                    if (profile._iconBitmap != null)
                        holder.profileIcon.setImageBitmap(profile._iconBitmap);
                    else {
                        //holder.profileIcon.setImageBitmap(null);
                        //int res = vi.getResources().getIdentifier(profile.getIconIdentifier(), "drawable",
                        //        vi.getContext().PPApplication.PACKAGE_NAME);
                        int res = ProfileStatic.getIconResource(profile.getIconIdentifier());
                        holder.profileIcon.setImageResource(res); // icon resource
                    }
                }
            } else {
                //Bitmap bitmap = profile.increaseProfileIconBrightnessForActivity(fragment.getActivity(), profile._iconBitmap);
                //Bitmap bitmap = profile._iconBitmap;
                //if (bitmap != null)
                //    holder.profileIcon.setImageBitmap(bitmap);
                //else
                    holder.profileIcon.setImageBitmap(profile._iconBitmap);
            }

            if (applicationActivatorPrefIndicator) {
                if (profile._preferencesIndicator != null) {
                    //profilePrefIndicatorImageView.setImageBitmap(null);
                    //Bitmap bitmap = ProfilePreferencesIndicator.paint(profile, vi.getContext());
                    //profilePrefIndicatorImageView.setImageBitmap(bitmap);
                    if (profile._name.equals(activityDataWrapper.context.getString(R.string.menu_restart_events)))
                        holder.profileIndicator.setVisibility(View.GONE);
                    else {
                        holder.profileIndicator.setVisibility(View.VISIBLE);
                        holder.profileIndicator.setImageBitmap(profile._preferencesIndicator);
                    }
                } else {
                    if (profile._name.equals(activityDataWrapper.context.getString(R.string.menu_restart_events)))
                        holder.profileIndicator.setVisibility(View.GONE);
                    else {
                        holder.profileIndicator.setVisibility(View.VISIBLE);
                        holder.profileIndicator.setImageResource(R.drawable.ic_empty);
                    }
                }
            }

            holder.radioButton.setTag(position);
            holder.radioButton.setOnClickListener(v -> {
                final RadioButton rb = (RadioButton) v;
                rb.setChecked(true);
                final Handler handler = new Handler(activityDataWrapper.context.getMainLooper());
                final WeakReference<ShortcutCreatorListFragment> dialogWeakRef = new WeakReference<>(fragment);
                handler.postDelayed(() -> {
                    ShortcutCreatorListFragment _fragment = dialogWeakRef.get();
                    if (_fragment != null)
                        _fragment.createShortcut((int) rb.getTag());
                }, 200);
            });

        }
        
        return vi;
    }

    /*
    public void setList(List<Profile> pl) {
        profileList = pl;
        notifyDataSetChanged();
    }
    */
}
