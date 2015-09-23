package sk.henrichg.phoneprofilesplus;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;

public class ExecuteVolumeProfilePrefsService extends IntentService
{

    public ExecuteVolumeProfilePrefsService() {
        super("ExecuteRadioProfilePrefsService");
    }

    //@Override
    protected void onHandleIntent(Intent intent) {

        final Context context = getApplicationContext();

        GlobalData.loadPreferences(context);

        DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
        final ActivateProfileHelper aph = dataWrapper.getActivateProfileHelper();
        aph.initialize(dataWrapper, null, context);

        int linkUnlink = intent.getIntExtra(GlobalData.EXTRA_LINKUNLINK_VOLUMES, PhoneCallService.LINKMODE_NONE);
        long profile_id = intent.getLongExtra(GlobalData.EXTRA_PROFILE_ID, 0);
        boolean merged = intent.getBooleanExtra(GlobalData.EXTRA_MERGED_PROFILE, false);
        Profile profile = dataWrapper.getProfileById(profile_id, merged);
        profile = GlobalData.getMappedProfile(profile, context);

        if (profile != null)
        {
            if (Privileges.checkVolumePreferences(profile)) {

                //Handler audioChangeHandler = new Handler(getMainLooper());

                final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                //audioChangeHandler.post(new Runnable() {
                //    @Override
                //    public void run() {

                // set ringer mode to Ring for proper change ringer mode to Silent
                aph.setRingerMode(profile, audioManager, true);

                    /*
                    TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                    if (telephony.getCallState() != TelephonyManager.CALL_STATE_RINGING) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            //System.out.println(e);
                        }
                    }
                    */

                aph.setVolumes(profile, audioManager, linkUnlink);

                // set ringer mode after volume because volumes change silent/vibrate
                aph.setRingerMode(profile, audioManager, false);
                //    }
                //});

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //System.out.println(e);
                }

            /*	if (intent.getBooleanExtra(GlobalData.EXTRA_SECOND_SET_VOLUMES, false))
                {
                    // run service for execute volumes - second set
                    Intent volumeServiceIntent = new Intent(context, ExecuteVolumeProfilePrefsService.class);
                    volumeServiceIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
                    volumeServiceIntent.putExtra(GlobalData.EXTRA_SECOND_SET_VOLUMES, false);
                    //WakefulIntentService.sendWakefulWork(context, radioServiceIntent);
                    context.startService(volumeServiceIntent);
                } */

            }

        }

    }


}
