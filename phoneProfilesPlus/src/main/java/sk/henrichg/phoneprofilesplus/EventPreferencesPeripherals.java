package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.Html;

public class EventPreferencesPeripherals extends EventPreferences {

    public int _peripheralType;

    static final String PREF_EVENT_PERIPHERAL_ENABLED = "eventPeripheralEnabled";
    static final String PREF_EVENT_PERIPHERAL_TYPE = "eventPeripheralType";

    static final String PREF_EVENT_PERIPHERAL_CATEGORY = "eventAccessoriesCategory";

    static final int PERIPHERAL_TYPE_DESK_DOCK = 0;
    static final int PERIPHERAL_TYPE_CAR_DOCK = 1;
    static final int PERIPHERAL_TYPE_WIRED_HEADSET = 2;
    static final int PERIPHERAL_TYPE_BLUETOOTH_HEADSET = 3;
    static final int PERIPHERAL_TYPE_HEADPHONES = 4;

    public EventPreferencesPeripherals(Event event,
                                    boolean enabled,
                                    int peripheralType)
    {
        super(event, enabled);

        this._peripheralType = peripheralType;
    }

    @Override
    public void copyPreferences(Event fromEvent)
    {
        this._enabled = ((EventPreferencesPeripherals)fromEvent._eventPreferencesPeripherals)._enabled;
        this._peripheralType = ((EventPreferencesPeripherals)fromEvent._eventPreferencesPeripherals)._peripheralType;
    }

    @Override
    public void loadSharedPreferences(SharedPreferences preferences)
    {
        Editor editor = preferences.edit();
        editor.putBoolean(PREF_EVENT_PERIPHERAL_ENABLED, _enabled);
        editor.putString(PREF_EVENT_PERIPHERAL_TYPE, String.valueOf(this._peripheralType));
        editor.commit();
    }

    @Override
    public void saveSharedPreferences(SharedPreferences preferences)
    {
        this._enabled = preferences.getBoolean(PREF_EVENT_PERIPHERAL_ENABLED, false);
        this._peripheralType = Integer.parseInt(preferences.getString(PREF_EVENT_PERIPHERAL_TYPE, "0"));
    }

    @Override
    public String getPreferencesDescription(boolean addBullet, Context context)
    {
        String descr = "";

        if (!this._enabled)
        {
            //descr = descr + context.getString(R.string.event_type_peripheral) + ": ";
            //descr = descr + context.getString(R.string.event_preferences_not_enabled);
        }
        else
        {
            if (addBullet) {
                descr = descr + "<b>\u2022 </b>";
                descr = descr + "<b>" + context.getString(R.string.event_type_peripheral) + ": " + "</b>";
            }

            String[] peripheralTypes = context.getResources().getStringArray(R.array.eventPeripheralTypeArray);
            descr = descr + peripheralTypes[this._peripheralType];
        }

        return descr;
    }

    @Override
    public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
    {
        if (key.equals(PREF_EVENT_PERIPHERAL_TYPE))
        {
            ListPreference listPreference = (ListPreference)prefMng.findPreference(key);
            int index = listPreference.findIndexOfValue(value);
            CharSequence summary = (index >= 0) ? listPreference.getEntries()[index] : null;
            listPreference.setSummary(summary);
        }
    }

    @Override
    public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
    {
        if (key.equals(PREF_EVENT_PERIPHERAL_TYPE))
        {
            setSummary(prefMng, key, preferences.getString(key, ""), context);
        }
    }

    @Override
    public void setAllSummary(PreferenceManager prefMng, SharedPreferences preferences, Context context)
    {
        setSummary(prefMng, PREF_EVENT_PERIPHERAL_TYPE, preferences, context);
    }

    @Override
    public void setCategorySummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context) {
        if (key.isEmpty() ||
                key.equals(PREF_EVENT_PERIPHERAL_ENABLED)) {
            boolean preferenceChanged = false;
            if (preferences == null) {
                preferenceChanged = this._enabled;
            } else {
                preferenceChanged = preferences.getBoolean(PREF_EVENT_PERIPHERAL_ENABLED, false);
            }
            boolean bold = preferenceChanged;
            Preference preference = prefMng.findPreference(PREF_EVENT_PERIPHERAL_CATEGORY);
            if (preference != null) {
                GUIData.setPreferenceTitleStyle(preference, bold, false);
                if (bold)
                    preference.setSummary(Html.fromHtml(getPreferencesDescription(false, context)));
            }
        }
    }

    @Override
    public boolean activateReturnProfile()
    {
        return true;
    }

    @Override
    public void setSystemRunningEvent(Context context)
    {
    }

    @Override
    public void setSystemPauseEvent(Context context)
    {
    }

    @Override
    public void removeSystemEvent(Context context)
    {
    }

}
