package sk.henrichg.phoneprofilesplus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.ArrayMap;

import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/** @noinspection ExtractMethodRecommender*/
class ProfileStatic {

    private ProfileStatic() {
        // private constructor to prevent instantiation
    }

    static String getIconIdentifier(String icon)
    {
        String value;
        try {
            String[] splits = icon.split(StringConstants.STR_SPLIT_REGEX);
            value = splits[0];
        } catch (Exception e) {
            value = StringConstants.PROFILE_ICON_DEFAULT;
        }
        return value;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean getIsIconResourceID(String icon)
    {
        boolean value;
        try {
            String[] splits = icon.split(StringConstants.STR_SPLIT_REGEX);
            value = splits[1].equals("1");

        } catch (Exception e) {
            value = true;
        }
        return value;
    }

    static int getVolumeValue(String volume)
    {
        int value;
        try {
            String[] splits = volume.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[0]);
        } catch (Exception e) {
            value = 0;
        }
        return value;
    }

    static boolean getVolumeChange(String volume)
    {
        int value;
        try {
            String[] splits = volume.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[1]);
        } catch (Exception e) {
            value = 1;
        }
        return value == 0; // in preference dialog is checked=No change
    }

    static int getDeviceBrightnessValue(String _deviceBrightness)
    {
        int maximumValue = 100;
        int defaultValue = 50;
        int value;
        try {
            String[] splits = _deviceBrightness.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[0]);
            if ((value < 0) || (value > maximumValue))
                value = defaultValue;
        } catch (Exception e) {
            value = defaultValue;
        }
        return value;
    }

    /*
    static boolean getDeviceBrightnessChange(String _deviceBrightness)
    {
        int value;
        try {
            String[] splits = _deviceBrightness.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[1]);
        } catch (Exception e) {
            value = 1;
        }
        return value == 0; // in preference dialog is checked=No change
    }
    */

    static boolean getDeviceBrightnessAutomatic(String _deviceBrightness)
    {
        int value;
        try {
            String[] splits = _deviceBrightness.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[2]);
        } catch (Exception e) {
            value = 1;
        }
        return value == 1;
    }

    static boolean getDeviceBrightnessChangeLevel(String _deviceBrightness)
    {
        int value;
        try {
            String[] splits = _deviceBrightness.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[4]);
        } catch (Exception e) {
            value = 1;
        }
        return value == 1;
    }


//    private static int getMinimumScreenBrightnessSetting (Context context)
//    {
//        return context.getResources().getInteger(com.android.internal.R.integer.config_screenBrightnessSettingMinimum);
//
        /*
        final Resources res = Resources.getSystem();
        int id = res.getIdentifier("config_screenBrightnessSettingMinimum", "integer", "android"); // API17+
        if (id == 0)
            id = res.getIdentifier("config_screenBrightnessDim", "integer", "android"); // lower API levels
        if (id != 0)
        {
            try {
                return res.getInteger(id);
            }
            catch (Exception ignored) {}
        }
        return 0;
        */
//    }

//    static int getMaximumScreenBrightnessSetting (Context context)
//    {
//        return context.getResources().getInteger(com.android.internal.R.integer.config_screenBrightnessSettingMaximum);
        /*
        final Resources res = Resources.getSystem();
        final int id = res.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");  // API17+
        if (id != 0)
        {
            try {
                int value = res.getInteger(id);
                if (value > 255)
                    value = 255;
                return value;
            }
            catch (Resources.NotFoundException e) {
                // ignore
            }
        }
        return 255;
        */
//    }

    /*
    private static int getBrightnessAdaptiveValueWithLookup(int percentage) {
        //int spaceMax = GAMMA_SPACE_MAX_256;
        //if (PPApplication.romIsOnePlus)
        //    spaceMax = GAMMA_SPACE_MAX_1024;
        //int value = Math.round((GAMMA_SPACE_MAX_256+1) / 100f * (float)(percentage + 1));
        //float value = (GAMMA_SPACE_MAX_256+1) / 100f * (float)(percentage + 1);
        //float systemValue = convertGammaToLinear(value, minValue, maxValue);
        //if (PPApplication.romIsOnePlus)
        //    systemValue = systemValue * 4; // convert from 256 to 1024

        //int maximumValue = 255;
        //if (PPApplication.romIsOnePlus)
        //    maximumValue = 1023;
        //if (systemValue > maximumValue)
        //    systemValue = maximumValue;

        int systemValue = BrightnessLookup.lookup(percentage, false);
        if (PPApplication.deviceIsOnePlus) {
            if (Build.VERSION.SDK_INT < 31)
                systemValue = systemValue * 4; // convert from 256 to 1024
            else
                systemValue = systemValue * 32; // convert from 256 to 8192
        }
        else
        if (PPApplication.deviceIsXiaomi && PPApplication.romIsMIUI)
            systemValue = systemValue * 16; // convert from 256 to 4096

        return Math.round(systemValue);
    }
    */

    // used only in convertBrightnessToPercents(), is only for manual brightness
    private static int getBrightnessPercentageWithLookup(int settingsValue/*, int minValue, int maxValue*/) {
        int _settingsValue = settingsValue;
        if (PPApplication.deviceIsOnePlus) {
            if (Build.VERSION.SDK_INT < 31)
                _settingsValue = Math.round(settingsValue / 4f); // convert from 1024 to 256
            else
                _settingsValue = Math.round(settingsValue / 32f); // convert from 8192 to 256
        }
        else
        if (PPApplication.deviceIsXiaomi && PPApplication.romIsMIUI) {
            if (Build.VERSION.SDK_INT < 33)
                _settingsValue = Math.round(settingsValue / 16f); // convert from 4096 to 256
            else
                _settingsValue = Math.round(settingsValue * 2f); // convert from 128 to 256
        }
        return BrightnessLookup.lookup(_settingsValue, true);
    }

    // tbis is called only from DatabaseHandlerCreateUpdateDB.updateDB for oldVersion < 1165
    // Used is only for manual brightness.
    // in db in old version was value, not percentage and this method converts this old brightness
    // value to percentage
    static long convertBrightnessToPercents(int value/*, int maxValue, int minValue*/)
    {
        long percentage;
        if (value == Profile.BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET)
            percentage = value; // keep BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET
        else {
            if ((Build.VERSION.SDK_INT > 28) &&
                    (!(PPApplication.deviceIsSamsung && PPApplication.romIsGalaxy)) &&
                    (!PPApplication.deviceIsOnePlus) &&
                    (!PPApplication.deviceIsLenovo) &&
                    (!PPApplication.deviceIsDoogee)) {
                percentage = getBrightnessPercentageWithLookup(value/*, minValue, maxValue*/);
            }
            else
            if ((Build.VERSION.SDK_INT == 28) && Build.MODEL.contains(PPApplication.MODEL_NEXUS)) {// Nexus may be LG, Samsung, Huawei, ...
                percentage = getBrightnessPercentageWithLookup(value/*, minValue, maxValue*/);
            }
            else
            if ((Build.VERSION.SDK_INT == 28) &&
                    (!(PPApplication.deviceIsSamsung && PPApplication.romIsGalaxy)) &&
                    (!PPApplication.deviceIsLG)) {
                percentage = getBrightnessPercentageWithLookup(value/*, minValue, maxValue*/);
            }
            else {
                //if (maximumValue-minimumValue > 255) {
                //int minimumValue = 0;
                int maximumValue = 255;

                if (PPApplication.deviceIsOnePlus && (Build.VERSION.SDK_INT >= 28) && (Build.VERSION.SDK_INT < 31))
                    maximumValue = 1023;
                // for OnePlus widh Android 12+ is max value 255
                //else
                //if (PPApplication.deviceIsOnePlus && (Build.VERSION.SDK_INT >= 31))
                //    maximumValue = 255;
                else
                if (PPApplication.deviceIsXiaomi && PPApplication.romIsMIUI && ((Build.VERSION.SDK_INT >= 28))) {
                    if (Build.VERSION.SDK_INT < 33)
                        maximumValue = 4095;
                    else
                        maximumValue = 128;
                }

                percentage = Math.round((float) (value/* - minValue*/) / (maximumValue/* - minValue*/) * 100.0);
            }
        }

        return percentage;
    }

    ////////////////

    private static int getBrightnessManualValueWithLookup(int percentage/*, int minValue, int maxValue*/) {
        //int systemValue = BrightnessLookup.convertGammaToLinear(percentage, 0, 255);
        /*
        int value = Math.round(percentage / 100f * maxValue);
        float _systemValue = PPMathUtils.min(
                BrightnessLookup.convertGammaToLinearFloat(value, minValue, maxValue),
                maxValue);
        Log.e("ProfileStatic.getBrightnessManualValueWithLookup", "systemValue 1="+_systemValue);
        //_systemValue = _systemValue * (maxValue / 2f);
        //Log.e("ProfileStatic.getBrightnessManualValueWithLookup", "systemValue 2="+_systemValue);
        */

        // systemValue is in range 0..255
        float systemValue = BrightnessLookup.lookup(percentage, false);

        if (PPApplication.deviceIsOnePlus) {
            if (Build.VERSION.SDK_INT < 31)
                systemValue = systemValue * 4; // convert from 256 to 1024
        }
        else
        if (PPApplication.deviceIsXiaomi && PPApplication.romIsMIUI) {
            if (Build.VERSION.SDK_INT < 33)
                systemValue = systemValue * 16; // convert from 256 to 4096
            else
                systemValue = systemValue / 2; // convert from 256 to 128
        }

        return Math.round(systemValue);
    }

    static int convertPercentsToBrightnessManualValue(int percentage, Context context) {
        int maximumValue;// = getMaximumScreenBrightnessSetting();
        int minimumValue;// = getMinimumScreenBrightnessSetting();

        //if (maximumValue-minimumValue > 255) {
        minimumValue = 0;
        maximumValue = 255;

        // for OnePlus widh Android 12+ is max value 255
        if (PPApplication.deviceIsOnePlus && (Build.VERSION.SDK_INT >= 28) && (Build.VERSION.SDK_INT < 31))
            maximumValue = 1023;
        else if (PPApplication.deviceIsXiaomi && PPApplication.romIsMIUI) {
            if (Build.VERSION.SDK_INT < 33)
                maximumValue = 4095;
            else
                maximumValue = 128;
        }

        int value;

        if (percentage == Profile.BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET) {
            // brightness is not set, change it to default manual brightness value
            int defaultValue = 128;

            // for OnePlus widh Android 12+ is max value 255
            if (PPApplication.deviceIsOnePlus && (Build.VERSION.SDK_INT >= 28) && (Build.VERSION.SDK_INT < 31))
                defaultValue = 512;
            //else
            //if (PPApplication.deviceIsXiaomi && PPApplication.romIsMIUI && (Build.VERSION.SDK_INT == 28))
            //    defaultValue = 2048;
            else
            if ((Build.VERSION.SDK_INT > 28) &&
                    (!(PPApplication.deviceIsSamsung && PPApplication.romIsGalaxy)) &&
                    (!PPApplication.deviceIsOnePlus) &&
                    (!PPApplication.deviceIsLenovo) &&
                    (!PPApplication.deviceIsDoogee)) {
                defaultValue = getBrightnessManualValueWithLookup(50/*, minimumValue, maximumValue*/);
            }
            else
            if ((Build.VERSION.SDK_INT == 28) && Build.MODEL.contains(PPApplication.MODEL_NEXUS)) {// Nexus may be LG, Samsung, Huawei, ...
                defaultValue = getBrightnessManualValueWithLookup(50/*, minimumValue, maximumValue*/);
            }
            else
            if ((Build.VERSION.SDK_INT == 28) &&
                    (!(PPApplication.deviceIsSamsung && PPApplication.romIsGalaxy)) &&
                    (!PPApplication.deviceIsLG)/* && (!PPApplication.romIsOnePlus)*/) {
                defaultValue = getBrightnessManualValueWithLookup(50/*, minimumValue, maximumValue*/);
            }
            value = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, defaultValue);
        }
        else {
            if ((Build.VERSION.SDK_INT > 28) &&
                    (!(PPApplication.deviceIsSamsung && PPApplication.romIsGalaxy)) &&
                    (!PPApplication.deviceIsOnePlus) &&
                    (!PPApplication.deviceIsLenovo) &&
                    (!PPApplication.deviceIsDoogee)) {
                value = getBrightnessManualValueWithLookup(percentage/*, minimumValue, maximumValue*/);
            }
            else
            if ((Build.VERSION.SDK_INT == 28) && Build.MODEL.contains(PPApplication.MODEL_NEXUS)) {// Nexus may be LG, Samsung, Huawei, ...
                value = getBrightnessManualValueWithLookup(percentage/*, minimumValue, maximumValue*/);
            }
            else
            if ((Build.VERSION.SDK_INT == 28) &&
                    (!(PPApplication.deviceIsSamsung && PPApplication.romIsGalaxy)) &&
                    (!PPApplication.deviceIsLG)/* && (!PPApplication.romIsOnePlus)*/) {
                value = getBrightnessManualValueWithLookup(percentage/*, minimumValue, maximumValue*/);
            }
            else {
                value = Math.round((float) (maximumValue - minimumValue) / 100 * percentage) + minimumValue;
            }
            if (value == 0)
                value = 1;
        }

        return value;
    }

    /*
    // This returns values -1..1
    // In OnePlus with Android 12 values in Settings.System.SCREEN_AUTO_BRIGHTNESS_ADJ are 0..8191. Why???
    //      This is problem when OnePlus is rooted !!!
    static float convertPercentsToBrightnessAdaptiveValue(int percentage, Context context)
    {
        float value;

        if (percentage == Profile.BRIGHTNESS_ADAPTIVE_BRIGHTNESS_NOT_SET)
            // brightness is not set, change it to default adaptive brightness value
            value = Settings.System.getFloat(context.getContentResolver(),
                    Settings.System.SCREEN_AUTO_BRIGHTNESS_ADJ, 0f);
        else {
            boolean exponentialLevel = false;
            if ((Build.VERSION.SDK_INT > 28) &&
                    (!(PPApplication.deviceIsSamsung && PPApplication.romIsGalaxy)) &&
                    (!PPApplication.deviceIsOnePlus) &&
                    (!PPApplication.deviceIsLenovo)) {
                exponentialLevel = true;
            }
            else
            if ((Build.VERSION.SDK_INT == 28) && Build.MODEL.contains("Nexus")) {// Nexus may be LG, Samsung, Huawei, ...
                exponentialLevel = true;
            }
            else
            if ((Build.VERSION.SDK_INT == 28) &&
                    (!(PPApplication.deviceIsSamsung && PPApplication.romIsGalaxy)) &&
                    (!PPApplication.deviceIsLG)) {
                exponentialLevel = true;
            }

            if (!exponentialLevel)
                value = (percentage - 50) / 50f;
            else {
//                int maximumValue;// = getMaximumScreenBrightnessSetting();
//                int minimumValue;// = getMinimumScreenBrightnessSetting();

                //if (maximumValue-minimumValue > 255) {
//                minimumValue = 0;
//                maximumValue = 255;
//                if (PPApplication.romIsOnePlus)
//                    maximumValue = 1023;
                //}

                if (PPApplication.deviceIsOnePlus) {
                    if (Build.VERSION.SDK_INT < 31)
                        value = (getBrightnessAdaptiveValueWithLookup(percentage/) - 512) / 512f;
                    else
                        value = (getBrightnessAdaptiveValueWithLookup(percentage) - 4096) / 4096f;
                }
                else
                if (PPApplication.deviceIsXiaomi && PPApplication.romIsMIUI)
                    value = (getBrightnessAdaptiveValueWithLookup(percentage) - 2048) / 2048f;
                else
                    value = (getBrightnessAdaptiveValueWithLookup(percentage) - 128) / 128f;
            }
        }

        return value;
    }
    */

    /*
    static boolean getGenerateNotificationGenerate(String _generateNotification)
    {
        int value;
        try {
            String[] splits = _generateNotification.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[0]);
        } catch (Exception e) {
            value = 0;
        }
        return value == 1;
    }
    */

    static int getGenerateNotificationIconType(String _generateNotification)
    {
        int value;
        try {
            String[] splits = _generateNotification.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[1]);
        } catch (Exception e) {
            value = 1;
        }
        return value;
    }

    static boolean getGenerateNotificationShowLargeIcon(String _generateNotification)
    {
        int value;
        try {
            String[] splits = _generateNotification.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[4]);
        } catch (Exception e) {
            value = 0;
        }
        return value == 1;
    }

    static boolean getGenerateNotificationReplaceWithPPPIcon(String _generateNotification)
    {
        int value;
        try {
            String[] splits = _generateNotification.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[5]);
        } catch (Exception e) {
            value = 0;
        }
        return value == 1;
    }

    static String getGenerateNotificationTitle(String _generateNotification)
    {
        String value;
        try {
            String[] splits = _generateNotification.split(StringConstants.STR_SPLIT_REGEX);
            value = splits[2];
        } catch (Exception e) {
            value = "";
        }
        return value;
    }

    static String getGenerateNotificationBody(String _generateNotification)
    {
        String value;
        try {
            String[] splits = _generateNotification.split(StringConstants.STR_SPLIT_REGEX);
            value = splits[3];
        } catch (Exception e) {
            value = "";
        }
        return value;
    }

    static int getImageResourcePosition(String imageIdentifier/*, Context context*/) {
        /*for (int pos = 0; pos < Profile.profileIconId.length; pos++) {
            String resName = context.getResources().getResourceEntryName(Profile.profileIconId[pos]);
            if (resName.equals(imageIdentifier))
                return pos;
        }*/
        if (Profile.profileIconIdMap.get(imageIdentifier) != null) {
            int iconResource = getIconResource(imageIdentifier);
            for (int pos = 0; pos < Profile.profileIconId.length; pos++) {
                if (Profile.profileIconId[pos] == iconResource)
                    return pos;
            }
        }
        return 0;
    }

    static String getImageResourceName(int position) {
        int iconResource = Profile.profileIconId[position];
        //noinspection rawtypes
        for(Map.Entry entry: Profile.profileIconIdMap.entrySet()){
            if (entry.getValue().equals(iconResource)) {
                return entry.getKey().toString();
            }
        }
        return StringConstants.PROFILE_ICON_DEFAULT;
    }

    static int getIconDefaultColor(String imageIdentifier/*, Context context*/) {
        return Profile.profileIconColor[getImageResourcePosition(imageIdentifier/*, context*/)];
    }

    @SuppressLint("SimpleDateFormat")
    static String timeDateStringFromTimestamp(Context applicationContext, long timestamp){
        String timeDate;
        String timestampDate = android.text.format.DateFormat.getDateFormat(applicationContext).format(new Date(timestamp));
        Calendar calendar = Calendar.getInstance();
        String currentDate = android.text.format.DateFormat.getDateFormat(applicationContext).format(new Date(calendar.getTimeInMillis()));
        String androidDateTime;
        if (timestampDate.equals(currentDate))
            androidDateTime=android.text.format.DateFormat.getTimeFormat(applicationContext).format(new Date(timestamp));
        else
            androidDateTime=android.text.format.DateFormat.getDateFormat(applicationContext).format(new Date(timestamp))+" "+
                    android.text.format.DateFormat.getTimeFormat(applicationContext).format(new Date(timestamp));
        String javaDateTime = DateFormat.getDateTimeInstance().format(new Date(timestamp));
        String AmPm="";
        if(!Character.isDigit(androidDateTime.charAt(androidDateTime.length()-1))) {
            if(androidDateTime.contains(new SimpleDateFormat().getDateFormatSymbols().getAmPmStrings()[Calendar.AM])){
                AmPm=" "+new SimpleDateFormat().getDateFormatSymbols().getAmPmStrings()[Calendar.AM];
            }else{
                AmPm=" "+new SimpleDateFormat().getDateFormatSymbols().getAmPmStrings()[Calendar.PM];
            }
            androidDateTime=androidDateTime.replace(AmPm, "");
        }
        if(!Character.isDigit(javaDateTime.charAt(javaDateTime.length()-1))){
            javaDateTime=javaDateTime.replace(" "+new SimpleDateFormat().getDateFormatSymbols().getAmPmStrings()[Calendar.AM], "");
            javaDateTime=javaDateTime.replace(" "+new SimpleDateFormat().getDateFormatSymbols().getAmPmStrings()[Calendar.PM], "");
        }
        javaDateTime=javaDateTime.substring(javaDateTime.length()-3);
        timeDate=androidDateTime.concat(javaDateTime);
        return timeDate.concat(AmPm);
    }

    // this change old, no longer used SHARED_PROFILE_VALUE to "Not used" value
    static Profile removeSharedProfileParameters(Profile profile)
    {
        final int SHARED_PROFILE_VALUE = 99;
        final String CONNECTTOSSID_SHAREDPROFILE = "^default_profile^";

        if (profile != null)
        {
            //Profile sharedProfile = getProfileFromSharedPreferences(context, PPApplication.SHARED_PROFILE_PREFS_NAME);

            Profile mappedProfile = new Profile(
                    profile._id,
                    profile._name,
                    profile._icon,
                    profile._checked,
                    profile._porder,
                    profile._volumeRingerMode,
                    profile._volumeRingtone,
                    profile._volumeNotification,
                    profile._volumeMedia,
                    profile._volumeAlarm,
                    profile._volumeSystem,
                    profile._volumeVoice,
                    profile._soundRingtoneChange,
                    profile._soundRingtone,
                    profile._soundNotificationChange,
                    profile._soundNotification,
                    profile._soundAlarmChange,
                    profile._soundAlarm,
                    profile._deviceAirplaneMode,
                    profile._deviceWiFi,
                    profile._deviceBluetooth,
                    profile._deviceScreenTimeout,
                    profile._deviceBrightness,
                    profile._deviceWallpaperChange,
                    profile._deviceWallpaper,
                    profile._deviceMobileData,
                    profile._deviceMobileDataPrefs,
                    profile._deviceGPS,
                    profile._deviceRunApplicationChange,
                    profile._deviceRunApplicationPackageName,
                    profile._deviceAutoSync,
                    profile._showInActivator,
                    profile._deviceAutoRotate,
                    profile._deviceLocationServicePrefs,
                    profile._volumeSpeakerPhone,
                    profile._deviceNFC,
                    profile._duration,
                    profile._afterDurationDo,
                    profile._volumeZenMode,
                    profile._deviceKeyguard,
                    profile._vibrationOnTouch,
                    profile._deviceWiFiAP,
                    profile._devicePowerSaveMode,
                    profile._askForDuration,
                    profile._deviceNetworkType,
                    profile._notificationLed,
                    profile._vibrateWhenRinging,
                    profile._deviceWallpaperFor,
                    profile._hideStatusBarIcon,
                    profile._lockDevice,
                    profile._deviceConnectToSSID,
                    profile._applicationEnableWifiScanning,
                    profile._applicationEnableBluetoothScanning,
                    profile._durationNotificationSound,
                    profile._durationNotificationVibrate,
                    profile._deviceWiFiAPPrefs,
                    profile._applicationEnableLocationScanning,
                    profile._applicationEnableMobileCellScanning,
                    profile._applicationEnableOrientationScanning,
                    profile._headsUpNotifications,
                    profile._deviceForceStopApplicationChange,
                    profile._deviceForceStopApplicationPackageName,
                    profile._activationByUserCount,
                    profile._deviceNetworkTypePrefs,
                    profile._deviceCloseAllApplications,
                    profile._screenDarkMode,
                    profile._dtmfToneWhenDialing,
                    profile._soundOnTouch,
                    profile._volumeDTMF,
                    profile._volumeAccessibility,
                    profile._volumeBluetoothSCO,
                    profile._afterDurationProfile,
                    profile._alwaysOnDisplay,
                    profile._screenOnPermanent,
                    profile._volumeMuteSound,
                    profile._deviceLocationMode,
                    profile._applicationEnableNotificationScanning,
                    profile._generateNotification,
                    profile._cameraFlash,
                    profile._deviceNetworkTypeSIM1,
                    profile._deviceNetworkTypeSIM2,
                    //profile._deviceMobileDataSIM1,
                    //profile._deviceMobileDataSIM2,
                    profile._deviceDefaultSIMCards,
                    profile._deviceOnOffSIM1,
                    profile._deviceOnOffSIM2,
                    profile._soundRingtoneChangeSIM1,
                    profile._soundRingtoneSIM1,
                    profile._soundRingtoneChangeSIM2,
                    profile._soundRingtoneSIM2,
                    profile._soundNotificationChangeSIM1,
                    profile._soundNotificationSIM1,
                    profile._soundNotificationChangeSIM2,
                    profile._soundNotificationSIM2,
                    profile._soundSameRingtoneForBothSIMCards,
                    profile._deviceLiveWallpaper,
                    profile._vibrateNotifications,
                    profile._deviceWallpaperFolder,
                    profile._applicationDisableGloabalEventsRun,
                    profile._deviceVPNSettingsPrefs,
                    profile._endOfActivationType,
                    profile._endOfActivationTime,
                    profile._applicationEnablePeriodicScanning,
                    profile._deviceVPN,
                    profile._vibrationIntensityRinging,
                    profile._vibrationIntensityNotifications,
                    profile._vibrationIntensityTouchInteraction,
                    profile._volumeMediaChangeDuringPlay,
                    profile._applicationWifiScanInterval,
                    profile._applicationBluetoothScanInterval,
                    profile._applicationBluetoothLEScanDuration,
                    profile._applicationLocationScanInterval,
                    profile._applicationOrientationScanInterval,
                    profile._applicationPeriodicScanInterval,
                    profile._phoneCallsContacts,
                    profile._phoneCallsContactGroups,
                    profile._phoneCallsContactListType,
                    profile._phoneCallsBlockCalls,
                    profile._phoneCallsSendSMS,
                    profile._phoneCallsSMSText,
                    profile._deviceWallpaperLockScreen
            );

            if (profile._volumeRingerMode == SHARED_PROFILE_VALUE)
                mappedProfile._volumeRingerMode = 0;
            if (profile._volumeZenMode == SHARED_PROFILE_VALUE)
                mappedProfile._volumeZenMode = 0;
            if (profile.getVolumeRingtoneSharedProfile())
                mappedProfile._volumeRingtone = Profile.defaultValuesString.get(Profile.PREF_PROFILE_VOLUME_RINGTONE);
            if (profile.getVolumeNotificationSharedProfile())
                mappedProfile._volumeNotification = Profile.defaultValuesString.get(Profile.PREF_PROFILE_VOLUME_NOTIFICATION);
            if (profile.getVolumeAlarmSharedProfile())
                mappedProfile._volumeAlarm = Profile.defaultValuesString.get(Profile.PREF_PROFILE_VOLUME_ALARM);
            if (profile.getVolumeMediaSharedProfile())
                mappedProfile._volumeMedia = Profile.defaultValuesString.get(Profile.PREF_PROFILE_VOLUME_MEDIA);
            if (profile.getVolumeSystemSharedProfile())
                mappedProfile._volumeSystem = Profile.defaultValuesString.get(Profile.PREF_PROFILE_VOLUME_SYSTEM);
            if (profile.getVolumeVoiceSharedProfile())
                mappedProfile._volumeVoice = Profile.defaultValuesString.get(Profile.PREF_PROFILE_VOLUME_VOICE);
            if (profile._soundRingtoneChange == SHARED_PROFILE_VALUE)
                mappedProfile._soundRingtoneChange = 0;
            if (profile._soundNotificationChange == SHARED_PROFILE_VALUE)
                mappedProfile._soundNotificationChange = 0;
            if (profile._soundAlarmChange == SHARED_PROFILE_VALUE)
                mappedProfile._soundAlarmChange = 0;
            if (profile._deviceAirplaneMode == SHARED_PROFILE_VALUE)
                mappedProfile._deviceAirplaneMode = 0;
            if (profile._deviceAutoSync == SHARED_PROFILE_VALUE)
                mappedProfile._deviceAutoSync = 0;
            if (profile._deviceMobileData == SHARED_PROFILE_VALUE)
                mappedProfile._deviceMobileData = 0;
            if (profile._deviceMobileDataPrefs == SHARED_PROFILE_VALUE)
                mappedProfile._deviceMobileDataPrefs = 0;
            if (profile._deviceWiFi == SHARED_PROFILE_VALUE)
                mappedProfile._deviceWiFi = 0;
            if (profile._deviceBluetooth == SHARED_PROFILE_VALUE)
                mappedProfile._deviceBluetooth = 0;
            if (profile._deviceGPS == SHARED_PROFILE_VALUE)
                mappedProfile._deviceGPS = 0;
            if (profile._deviceLocationServicePrefs == SHARED_PROFILE_VALUE)
                mappedProfile._deviceLocationServicePrefs = 0;
            if (profile._deviceScreenTimeout == SHARED_PROFILE_VALUE)
                mappedProfile._deviceScreenTimeout = 0;
            if (profile.getDeviceBrightnessSharedProfile())
                mappedProfile._deviceBrightness = Profile.defaultValuesString.get(Profile.PREF_PROFILE_DEVICE_BRIGHTNESS);
            if (profile._deviceAutoRotate == SHARED_PROFILE_VALUE)
                mappedProfile._deviceAutoRotate = 0;
            if (profile._deviceRunApplicationChange == SHARED_PROFILE_VALUE)
                mappedProfile._deviceRunApplicationChange = 0;
            if (profile._deviceWallpaperChange == SHARED_PROFILE_VALUE)
                mappedProfile._deviceWallpaperChange = 0;
            if (profile._volumeSpeakerPhone == SHARED_PROFILE_VALUE)
                mappedProfile._volumeSpeakerPhone = 0;
            if (profile._deviceNFC == SHARED_PROFILE_VALUE)
                mappedProfile._deviceNFC = 0;
            if (profile._deviceKeyguard == SHARED_PROFILE_VALUE)
                mappedProfile._deviceKeyguard = 0;
            if (profile._vibrationOnTouch == SHARED_PROFILE_VALUE)
                mappedProfile._vibrationOnTouch = 0;
            if (profile._deviceWiFiAP == SHARED_PROFILE_VALUE)
                mappedProfile._deviceWiFiAP = 0;
            if (profile._devicePowerSaveMode == SHARED_PROFILE_VALUE)
                mappedProfile._devicePowerSaveMode = 0;
            if (profile._deviceNetworkType == SHARED_PROFILE_VALUE)
                mappedProfile._deviceNetworkType = 0;
            if (profile._notificationLed == SHARED_PROFILE_VALUE)
                mappedProfile._notificationLed = 0;
            if (profile._vibrateWhenRinging == SHARED_PROFILE_VALUE)
                mappedProfile._vibrateWhenRinging = 0;
            if (profile._vibrateNotifications == SHARED_PROFILE_VALUE)
                mappedProfile._vibrateNotifications = 0;
            if (profile._lockDevice == SHARED_PROFILE_VALUE)
                mappedProfile._lockDevice = 0;
            if ((profile._deviceConnectToSSID != null) && (profile._deviceConnectToSSID.equals(CONNECTTOSSID_SHAREDPROFILE)))
                mappedProfile._deviceConnectToSSID = Profile.defaultValuesString.get(Profile.PREF_PROFILE_DEVICE_CONNECT_TO_SSID);
            if (profile._applicationEnableWifiScanning == SHARED_PROFILE_VALUE)
                mappedProfile._applicationEnableWifiScanning = 0;
            if (profile._applicationEnableBluetoothScanning == SHARED_PROFILE_VALUE)
                mappedProfile._applicationEnableBluetoothScanning = 0;
            if (profile._deviceWiFiAPPrefs == SHARED_PROFILE_VALUE)
                mappedProfile._deviceWiFiAPPrefs = 0;
            if (profile._applicationEnableLocationScanning == SHARED_PROFILE_VALUE)
                mappedProfile._applicationEnableLocationScanning = 0;
            if (profile._applicationEnableMobileCellScanning == SHARED_PROFILE_VALUE)
                mappedProfile._applicationEnableMobileCellScanning = 0;
            if (profile._applicationEnableOrientationScanning == SHARED_PROFILE_VALUE)
                mappedProfile._applicationEnableOrientationScanning = 0;
            if (profile._headsUpNotifications == SHARED_PROFILE_VALUE)
                mappedProfile._headsUpNotifications = 0;
            if (profile._deviceForceStopApplicationChange == SHARED_PROFILE_VALUE)
                mappedProfile._deviceForceStopApplicationChange = 0;
            if (profile._deviceNetworkTypePrefs == SHARED_PROFILE_VALUE)
                mappedProfile._deviceNetworkTypePrefs = 0;
            if (profile._deviceCloseAllApplications == SHARED_PROFILE_VALUE)
                mappedProfile._deviceCloseAllApplications = 0;
            if (profile._screenDarkMode == SHARED_PROFILE_VALUE)
                mappedProfile._screenDarkMode = 0;
            if (profile._dtmfToneWhenDialing == SHARED_PROFILE_VALUE)
                mappedProfile._dtmfToneWhenDialing = 0;
            if (profile._soundOnTouch == SHARED_PROFILE_VALUE)
                mappedProfile._soundOnTouch = 0;
            if (profile.getVolumeDTMFSharedProfile())
                mappedProfile._volumeDTMF = Profile.defaultValuesString.get(Profile.PREF_PROFILE_VOLUME_DTMF);
            if (profile.getVolumeAccessibilitySharedProfile())
                mappedProfile._volumeAccessibility = Profile.defaultValuesString.get(Profile.PREF_PROFILE_VOLUME_ACCESSIBILITY);
            if (profile.getVolumeBluetoothSCOSharedProfile())
                mappedProfile._volumeBluetoothSCO = Profile.defaultValuesString.get(Profile.PREF_PROFILE_VOLUME_BLUETOOTH_SCO);
            if (profile._alwaysOnDisplay == SHARED_PROFILE_VALUE)
                mappedProfile._alwaysOnDisplay = 0;
            if (profile._screenOnPermanent == SHARED_PROFILE_VALUE)
                mappedProfile._screenOnPermanent = 0;
            if (profile._deviceLocationMode == SHARED_PROFILE_VALUE)
                mappedProfile._deviceLocationMode = 0;
            if (profile._applicationEnableNotificationScanning == SHARED_PROFILE_VALUE)
                mappedProfile._applicationEnableNotificationScanning = 0;
            if (profile.getGenerateNotificationSharedProfile())
                mappedProfile._generateNotification = Profile.defaultValuesString.get(Profile.PREF_PROFILE_GENERATE_NOTIFICATION);
            if (profile._cameraFlash == SHARED_PROFILE_VALUE)
                mappedProfile._cameraFlash = 0;
            if (profile._deviceNetworkTypeSIM1 == SHARED_PROFILE_VALUE)
                mappedProfile._deviceNetworkTypeSIM1 = 0;
            if (profile._deviceNetworkTypeSIM2 == SHARED_PROFILE_VALUE)
                mappedProfile._deviceNetworkTypeSIM2 = 0;
            //if (profile._deviceMobileDataSIM1 == SHARED_PROFILE_VALUE)
            //    mappedProfile._deviceMobileDataSIM1 = 0;
            //if (profile._deviceMobileDataSIM2 == SHARED_PROFILE_VALUE)
            //    mappedProfile._deviceMobileDataSIM2 = 0;
            // !!! do not add other profile aprameters. Shared profile is never used !!!

            mappedProfile._iconBitmap = profile._iconBitmap;
            mappedProfile._preferencesIndicator = profile._preferencesIndicator;

            return mappedProfile;
        }
        else
            return null;
    }

    static PreferenceAllowed isProfilePreferenceAllowed(String preferenceKey, Profile profile,
                                                        SharedPreferences sharedPreferences,
                                                        boolean fromUIThread, Context context) {
        if ((profile != null) && (!preferenceKey.equals("-")) && (sharedPreferences == null)) {
            sharedPreferences = context.getApplicationContext().getSharedPreferences(PPApplication.TMP_SHARED_PREFS_IS_PROFILE_PREFERENCE_ALLOWED, Context.MODE_PRIVATE);
            profile.saveProfileToSharedPreferences(sharedPreferences);
            profile = null;
        }

        PreferenceAllowed preferenceAllowed = new PreferenceAllowed();

        preferenceAllowed.notAllowedRoot = false;
        preferenceAllowed.notAllowedG1 = false;
        preferenceAllowed.notAllowedPPPPS = false;
        preferenceAllowed.notAllowedShizuku = false;

        //noinspection IfStatementWithIdenticalBranches
        if (profile == null) {
            preferenceAllowed.allowed = PreferenceAllowed.PREFERENCE_NOT_ALLOWED;
            switch (preferenceKey) {
                case Profile.PREF_PROFILE_DEVICE_AIRPLANE_MODE:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_AIRPLANE_MODE(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_DEVICE_WIFI:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_WIFI(preferenceAllowed, null, sharedPreferences/*, fromUIThread*/);
                    break;
                case Profile.PREF_PROFILE_DEVICE_BLUETOOTH:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_BLUETOOTH(preferenceAllowed);
                    break;
                case Profile.PREF_PROFILE_DEVICE_MOBILE_DATA:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_MOBILE_DATA(preferenceAllowed, preferenceKey, null, sharedPreferences, /*fromUIThread,*/ context);
                    break;
                //case Profile.PREF_PROFILE_DEVICE_MOBILE_DATA_SIM1:
                //case Profile.PREF_PROFILE_DEVICE_MOBILE_DATA_SIM2:
                //    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_MOBILE_DATA_DUAL_SIM(preferenceAllowed, preferenceKey, null, sharedPreferences, fromUIThread, context);
                //    break;
                case Profile.PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS(preferenceAllowed);
                    break;
                case Profile.PREF_PROFILE_DEVICE_GPS:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_GPS(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_DEVICE_LOCATION_MODE:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_LOCATION_MODE(preferenceAllowed, null, context);
                    break;
                case Profile.PREF_PROFILE_DEVICE_NFC:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_NFC(preferenceAllowed, null, sharedPreferences, /*fromUIThread,*/ context);
                    break;
                case Profile.PREF_PROFILE_DEVICE_WIFI_AP:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_WIFI_AP(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_VIBRATE_WHEN_RINGING:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VIBRATE_WHEN_RINGING(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_VIBRATE_NOTIFICATIONS:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VIBRATE_NOTIFICATIONS(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_VIBRATION_INTENSITY_RINGING:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VIBRATION_INTENSITY_RINGING(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_VIBRATION_INTENSITY_NOTIFICATIONS:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VIBRATION_INTENSITY_NOTIFICATIONS(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_VIBRATION_INTENSITY_TOUCH_INTERACTION:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VIBRATION_INTENSITY_TOUCH_INTERACTION(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                //case Profile.PREF_PROFILE_DEVICE_ADAPTIVE_BRIGHTNESS:
                    // !!! test this only for preference key !!!
                //    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_ADAPTIVE_BRIGHTNESS(preferenceAllowed, sharedPreferences, fromUIThread);
                //    break;
                case Profile.PREF_PROFILE_DEVICE_POWER_SAVE_MODE:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_POWER_SAVE_MODE(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_DEVICE_NETWORK_TYPE:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_NETWORK_TYPE(preferenceAllowed, preferenceKey, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_DEVICE_NETWORK_TYPE_SIM1:
                case Profile.PREF_PROFILE_DEVICE_NETWORK_TYPE_SIM2:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_NETWORK_TYPE_DUAL_SIM(preferenceAllowed, preferenceKey, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_NOTIFICATION_LED:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_NOTIFICATION_LED(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_DEVICE_KEYGUARD:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_KEYGUARD(preferenceAllowed, context);
                    break;
                case Profile.PREF_PROFILE_DEVICE_CONNECT_TO_SSID:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_CONNECT_TO_SSID(preferenceAllowed);
                    break;
                case Profile.PREF_PROFILE_APPLICATION_ENABLE_WIFI_SCANNING:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_APPLICATION_DISABLE_WIFI_SCANNING(preferenceAllowed);
                    break;
                case Profile.PREF_PROFILE_APPLICATION_ENABLE_BLUETOOTH_SCANNING:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_APPLICATION_DISABLE_BLUETOOTH_SCANNING(preferenceAllowed);
                    break;
                case Profile.PREF_PROFILE_DEVICE_WIFI_AP_PREFS:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_WIFI_AP_PREFS(preferenceAllowed);
                    break;
                case Profile.PREF_PROFILE_APPLICATION_ENABLE_MOBILE_CELL_SCANNING:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_APPLICATION_DISABLE_MOBILE_CELL_SCANNING(preferenceAllowed);
                    break;
                case Profile.PREF_PROFILE_APPLICATION_ENABLE_ORIENTATION_SCANNING:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_APPLICATION_DISABLE_ORIENTATION_SCANNING(preferenceAllowed);
                    break;
                case Profile.PREF_PROFILE_HEADS_UP_NOTIFICATIONS:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_HEADS_UP_NOTIFICATIONS(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_DEVICE_NETWORK_TYPE_PREFS:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_NETWORK_TYPE_PREFS(preferenceAllowed);
                    break;
                case Profile.PREF_PROFILE_VOLUME_ACCESSIBILITY:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VOLUME_ACCESSIBILITY(preferenceAllowed/*, context*/);
                    break;
                case Profile.PREF_PROFILE_ALWAYS_ON_DISPLAY:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_ALWAYS_ON_DISPLAY(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_SCREEN_DARK_MODE:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_SCREEN_DARK_MODE(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_VOLUME_SPEAKER_PHONE:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VOLUME_SPEAKER_PHONE(preferenceAllowed, context);
                    break;
                case Profile.PREF_PROFILE_CAMERA_FLASH:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_CAMERA_FLASH(preferenceAllowed/*, context*/);
                    break;
                case Profile.PREF_PROFILE_DEVICE_DEFAULT_SIM_CARDS:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_DEFAULT_SIM_CARDS(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_DEVICE_ONOFF_SIM1:
                case Profile.PREF_PROFILE_DEVICE_ONOFF_SIM2:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_ONOFF_SIM(preferenceAllowed, preferenceKey, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_SOUND_RINGTONE_CHANGE_SIM1:
                case Profile.PREF_PROFILE_SOUND_RINGTONE_CHANGE_SIM2:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_SOUND_RINGTONE_CHANGE_SIM(preferenceAllowed, null, context);
                    break;
                case Profile.PREF_PROFILE_SOUND_NOTIFICATION_CHANGE_SIM1:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_SOUND_NOTIFICATION_CHANGE_SIM(preferenceAllowed, preferenceKey, null, sharedPreferences, fromUIThread, context, false);
                    break;
                case Profile.PREF_PROFILE_SOUND_NOTIFICATION_CHANGE_SIM2:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_SOUND_NOTIFICATION_CHANGE_SIM(preferenceAllowed, preferenceKey, null, sharedPreferences, fromUIThread, context, true);
                    break;
                case Profile.PREF_PROFILE_SOUND_SAME_RINGTONE_FOR_BOTH_SIM_CARDS:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_SOUND_SAME_RINGTONE_FOR_BOTH_SIM_CARDS(preferenceAllowed, null, sharedPreferences, fromUIThread, context);
                    break;
                case Profile.PREF_PROFILE_LOCK_DEVICE:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_LOCK_DEVICE(preferenceAllowed, null, sharedPreferences);
                    break;
                case Profile.PREF_PROFILE_DEVICE_SCREEN_TIMEOUT:
                    PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_SCREEN_TIMEOUT(preferenceAllowed, preferenceKey, null, sharedPreferences/*, fromUIThread, context*/);
                    break;
                default:
                    preferenceAllowed.allowed = PreferenceAllowed.PREFERENCE_ALLOWED;
            }
            return preferenceAllowed;

        } else {
            // !!! call only methods with profile parameter

            preferenceAllowed.allowed = PreferenceAllowed.PREFERENCE_NOT_ALLOWED;

            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_AIRPLANE_MODE(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_WIFI(preferenceAllowed, profile, sharedPreferences/*, fromUIThread*/);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_BLUETOOTH(preferenceAllowed);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_MOBILE_DATA(preferenceAllowed, "-", profile, sharedPreferences, /*fromUIThread,*/ context);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_MOBILE_DATA_DUAL_SIM(preferenceAllowed, "-", profile, sharedPreferences, fromUIThread, context);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS(preferenceAllowed);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_GPS(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_LOCATION_MODE(preferenceAllowed, profile, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_NFC(preferenceAllowed, profile, sharedPreferences, /*fromUIThread,*/ context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_WIFI_AP(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VIBRATE_WHEN_RINGING(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VIBRATE_NOTIFICATIONS(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VIBRATION_INTENSITY_RINGING(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VIBRATION_INTENSITY_NOTIFICATIONS(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VIBRATION_INTENSITY_TOUCH_INTERACTION(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_POWER_SAVE_MODE(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_NETWORK_TYPE(preferenceAllowed, "-", profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_NETWORK_TYPE_DUAL_SIM(preferenceAllowed, "-", profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_NOTIFICATION_LED(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_KEYGUARD(preferenceAllowed, context);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_CONNECT_TO_SSID(preferenceAllowed);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_APPLICATION_DISABLE_WIFI_SCANNING(preferenceAllowed);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_APPLICATION_DISABLE_BLUETOOTH_SCANNING(preferenceAllowed);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_WIFI_AP_PREFS(preferenceAllowed);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_APPLICATION_DISABLE_MOBILE_CELL_SCANNING(preferenceAllowed);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_APPLICATION_DISABLE_ORIENTATION_SCANNING(preferenceAllowed);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_HEADS_UP_NOTIFICATIONS(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_NETWORK_TYPE_PREFS(preferenceAllowed);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VOLUME_ACCESSIBILITY(preferenceAllowed, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_ALWAYS_ON_DISPLAY(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_SCREEN_DARK_MODE(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_VOLUME_SPEAKER_PHONE(preferenceAllowed, context);
            //PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_CAMERA_FLASH(preferenceAllowed);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_DEFAULT_SIM_CARDS(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_ONOFF_SIM(preferenceAllowed, "-", profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_SOUND_RINGTONE_CHANGE_SIM(preferenceAllowed, profile, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_SOUND_NOTIFICATION_CHANGE_SIM(preferenceAllowed, "-", profile, sharedPreferences, fromUIThread, context, false);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_SOUND_NOTIFICATION_CHANGE_SIM(preferenceAllowed, "-", profile, sharedPreferences, fromUIThread, context, true);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_SOUND_SAME_RINGTONE_FOR_BOTH_SIM_CARDS(preferenceAllowed, profile, sharedPreferences, fromUIThread, context);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_LOCK_DEVICE(preferenceAllowed, profile, sharedPreferences);
            PreferenceAllowed.isProfilePreferenceAllowed_PREF_PROFILE_DEVICE_SCREEN_TIMEOUT(preferenceAllowed, "-", profile, sharedPreferences/*, fromUIThread, context*/);

            if (preferenceAllowed.notAllowedG1 ||
                    preferenceAllowed.notAllowedRoot ||
                    preferenceAllowed.notAllowedPPPPS ||
                    preferenceAllowed.notAllowedShizuku)
                preferenceAllowed.allowed = PreferenceAllowed.PREFERENCE_NOT_ALLOWED;

            return preferenceAllowed;
        }
    }

    /*
    static void getActivatedProfileForDuration(Context context)
    {
        synchronized (PPApplication.profileActivationMutex) {
            ApplicationPreferences.prefActivatedProfileForDuration = ApplicationPreferences.
                    getSharedPreferences(context).getLong(PREF_ACTIVATED_PROFILE_FOR_DURATION, 0);
            //return prefActivatedProfileForDuration;
        }
    }
    static void setActivatedProfileForDuration(Context context, long profileId)
    {
        synchronized (PPApplication.profileActivationMutex) {
            SharedPreferences.Editor editor = ApplicationPreferences.getEditor(context);
            editor.putLong(PREF_ACTIVATED_PROFILE_FOR_DURATION, profileId);
            editor.apply();
            ApplicationPreferences.prefActivatedProfileForDuration = profileId;
        }
    }
    */

    // https://stackoverflow.com/questions/7944601/how-to-save-hashmap-to-shared-preferences
    static void getActivatedProfileEndDurationTime(Context context)
    {
//        PPApplicationStatic.logE("[SYNCHRONIZED] ProfileStatic.getActivatedProfileEndDurationTime", "PPApplication.profileActivationMutex");
        synchronized (PPApplication.profileActivationMutex) {
            String mapString =
                    ApplicationPreferences.getSharedPreferences(context).getString(ApplicationPreferences.PREF_ACTIVATED_PROFILE_END_DURATION_TIMES, "");
            if (mapString.isEmpty())
                ApplicationPreferences.prefActivatedProfileEndDurationTime.clear();
            else {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayMap<Long, Long>>(){}.getType();
                ApplicationPreferences.prefActivatedProfileEndDurationTime = gson.fromJson(mapString, type);
            }
        }
    }

    static void setActivatedProfileEndDurationTime(Context context, long profileId, long time)
    {
//        PPApplicationStatic.logE("[SYNCHRONIZED] ProfileStatic.setActivatedProfileEndDurationTime", "PPApplication.profileActivationMutex");
        synchronized (PPApplication.profileActivationMutex) {
            ApplicationPreferences.prefActivatedProfileEndDurationTime.put(profileId, time);

            Gson gson = new Gson();
            String mapString = gson.toJson(ApplicationPreferences.prefActivatedProfileEndDurationTime);

            SharedPreferences.Editor editor = ApplicationPreferences.getEditor(context);
            editor.putString(ApplicationPreferences.PREF_ACTIVATED_PROFILE_END_DURATION_TIMES, mapString);
            editor.apply();
        }
    }

    static int getIconResource(String identifier) {
        int iconResource = R.drawable.ic_profile_default;
        try {
            if ((identifier != null) && (!identifier.isEmpty())) {
                Object idx = Profile.profileIconIdMap.get(identifier);
                if (idx != null)
                    iconResource = (int) idx;
            }
        } catch (Exception e) {
            PPApplicationStatic.recordException(e);
        }
        return iconResource;
    }

    static int getVibrationIntensityValue(String sValue)
    {
        int value;
        try {
            String[] splits = sValue.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[0]);
        } catch (Exception e) {
            value = 0;
        }
        return value;
    }

    static boolean getVibrationIntensityChange(String sValue)
    {
        int value;
        try {
            String[] splits = sValue.split(StringConstants.STR_SPLIT_REGEX);
            value = Integer.parseInt(splits[1]);
        } catch (Exception e) {
            value = 1;
        }
        return value == 0; // in preference dialog is checked=No change
    }

    static String getColorForChangedPreferenceValue(String preferenceValue,
                                                    PreferenceManager prefMng,
                                                    String preferenceKey,
                                                    Context context) {
        Preference preference = prefMng.findPreference(preferenceKey);
        if ((preference != null) && preference.isEnabled()) {
            int labelColor = ContextCompat.getColor(context, R.color.activityNormalTextColor);
            String colorString = String.format(StringConstants.STR_FORMAT_INT, labelColor).substring(2); // !!strip alpha value!!
            return String.format(StringConstants.TAG_FONT_COLOR_HTML/*+":"*/, colorString, preferenceValue);
        } else
            return preferenceValue;
    }

    static boolean isRedTextNotificationRequired(Profile profile, boolean againCheckAccessibilityInDelay, Context context) {
        boolean grantedAllPermissions = Permissions.checkProfilePermissions(context, profile).isEmpty();
        /*if (Build.VERSION.SDK_INT >= 29) {
            if (!Settings.canDrawOverlays(context))
                grantedAllPermissions = false;
        }*/
        // test only root or G1 or Shizuku parameters, because key is not set but profile is
        PreferenceAllowed preferenceAllowed = ProfileStatic.isProfilePreferenceAllowed("-", profile, null, true, context);
        boolean grantedRoot = true;
        //if (preferenceAllowed.allowed != PreferenceAllowed.PREFERENCE_ALLOWED) {
        if (preferenceAllowed.notAllowedRoot) {
            if (!ApplicationPreferences.applicationNeverAskForGrantRoot)
                //if ((!ApplicationPreferences.applicationNeverAskForGrantRoot) && (preferenceAllowed.notAllowedReason == PreferenceAllowed.PREFERENCE_NOT_ALLOWED_NOT_ROOT_GRANTED))
                grantedRoot = false;
        }
        //preferenceAllowed = Profile.isProfilePreferenceAllowed("-", profile, null, false, true, true, context);
        boolean grantedG1Permission = true;
        if (preferenceAllowed.notAllowedG1) {
            //if (preferenceAllowed.notAllowedReason == PreferenceAllowed.PREFERENCE_NOT_ALLOWED_NOT_GRANTED_G1_PERMISSION)
            grantedG1Permission = false;
        }
        boolean grantedShizukuPermission = true;
        if (preferenceAllowed.notAllowedShizuku) {
            grantedShizukuPermission = false;
        }

        boolean installedPPPPS = true;
        if (preferenceAllowed.notAllowedPPPPS) {
            installedPPPPS = false;
        }

        boolean enabledNotificationAccess = /*(profile._volumeRingerMode == 0) ||*/ ActivateProfileHelper.canChangeZenMode(context);

        boolean accessibilityNotRequired = true;
        if ((profile != null) && ((profile._lockDevice == 3) || (profile._deviceForceStopApplicationChange != 0)))
            accessibilityNotRequired = false;
        boolean accessibilityEnabled = accessibilityNotRequired || (profile.isAccessibilityServiceEnabled(context.getApplicationContext(), againCheckAccessibilityInDelay) == 1);

        boolean defaultAssistantNotRequired = true;
        if ((profile != null) && (profile._deviceAirplaneMode >= 4))
            defaultAssistantNotRequired = false;
        boolean defaultAssistantEnabled = defaultAssistantNotRequired || (ActivateProfileHelper.isPPPSetAsDefaultAssistant(context.getApplicationContext()));

        if (preferenceAllowed.allowed == PreferenceAllowed.PREFERENCE_ALLOWED)
            return (!grantedAllPermissions) ||
                    (!enabledNotificationAccess) ||
                    (!accessibilityEnabled) ||
                    (!defaultAssistantEnabled) ||
                    (!installedPPPPS);
        else
            return (!grantedAllPermissions) ||
                    (!grantedRoot) ||
                    (!grantedG1Permission) ||
                    (!enabledNotificationAccess) ||
                    (!accessibilityEnabled) ||
                    (!defaultAssistantEnabled) ||
                    (!installedPPPPS) ||
                    (!grantedShizukuPermission);
    }

}
