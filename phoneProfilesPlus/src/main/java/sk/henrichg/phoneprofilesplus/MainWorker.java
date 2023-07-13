package sk.henrichg.phoneprofilesplus;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.Set;

public class MainWorker extends Worker {

    static final String APPLICATION_FULLY_STARTED_WORK_TAG = "applicationFullyStartedWork";
    static final String ACCESSIBILITY_SERVICE_CONNECTED_NOT_RECEIVED_WORK_TAG = "accessibilityServiceConnectedWork";

    static final String LOCATION_SCANNER_SWITCH_GPS_TAG_WORK = "locationScannerSwitchGPSWork";
    static final String LOCK_DEVICE_FINISH_ACTIVITY_TAG_WORK = "lockDeviceFinishActivityWork";
    static final String LOCK_DEVICE_AFTER_SCREEN_OFF_TAG_WORK = "lockDeviceAfterScreenOffWork";
    static final String EVENT_DELAY_START_TAG_WORK = "eventDelayStartWork";
    static final String EVENT_DELAY_END_TAG_WORK = "eventDelayEndWork";
    static final String CLOSE_ALL_APPLICATIONS_WORK_TAG = "closeAllApplicationsWork";

    static final String SCHEDULE_AVOID_RESCHEDULE_RECEIVER_WORK_TAG = "scheduleAvoidRescheduleReceiverWork";
    static final String SCHEDULE_LONG_INTERVAL_WIFI_WORK_TAG = "scheduleLongIntervalWifiWork";
    static final String SCHEDULE_LONG_INTERVAL_BLUETOOTH_WORK_TAG = "scheduleLongIntervalBluetoothWork";
    //static final String SCHEDULE_LONG_INTERVAL_GEOFENCE_WORK_TAG = "scheduleLongIntervalGeofenceWork";
    static final String SCHEDULE_LONG_INTERVAL_PERIODIC_EVENTS_HANDLER_WORK_TAG = "scheduleLongIntervalPeriodicEventsHandlerWork";
    static final String SCHEDULE_LONG_INTERVAL_SEARCH_CALENDAR_WORK_TAG = "scheduleLongIntervalSearchCalendarWork";

    static final String ORIENTATION_SCANNER_WORK_TAG = "handleEventsOrientationScannerWork";

    static final String HANDLE_EVENTS_BLUETOOTH_LE_SCANNER_WORK_TAG = "handleEventsBluetoothLEScannerWork";
    static final String HANDLE_EVENTS_BLUETOOTH_CE_SCANNER_WORK_TAG = "handleEventsBluetoothCLScannerWork";
    static final String HANDLE_EVENTS_WIFI_SCANNER_FROM_RECEIVER_WORK_TAG = "handleEventsWifiScannerFromReceiverWork";
    static final String HANDLE_EVENTS_WIFI_SCANNER_FROM_SCANNER_WORK_TAG = "handleEventsWifiScannerFromScannerWork";
    static final String HANDLE_EVENTS_TWILIGHT_SCANNER_WORK_TAG = "handleEventsTwilightScannerWork";
    static final String HANDLE_EVENTS_MOBILE_CELLS_SCANNER_WORK_TAG = "handleEventsMobileCellsScannerWork";
    static final String HANDLE_EVENTS_NOTIFICATION_POSTED_SCANNER_WORK_TAG = "handleEventsNotificationPostedScannerWork";
    static final String HANDLE_EVENTS_NOTIFICATION_REMOVED_SCANNER_WORK_TAG = "handleEventsNotificationRemovedScannerWork";
    static final String HANDLE_EVENTS_NOTIFICATION_RESCAN_SCANNER_WORK_TAG = "handleEventsNotificationRescanScannerWork";
    static final String HANDLE_EVENTS_SOUND_PROFILE_WORK_TAG = "handleEventsSoundProfileWork";
    static final String HANDLE_EVENTS_PERIODIC_WORK_TAG = "handleEventsPeriodicWork";
    static final String HANDLE_EVENTS_VOLUMES_WORK_TAG = "handleEventsVolumesWork";
    static final String HANDLE_EVENTS_MOBILE_DATA_NETWORK_CALLBACK_WORK_TAG = "handleEventsMobileDataNetworkCallbackWork";
    static final String HANDLE_EVENTS_WIFI_NETWORK_CALLBACK_WORK_TAG = "handleEventsWifiNetworkCallbackWork";
    static final String HANDLE_EVENTS_BLUETOOTH_CONNECTION_WORK_TAG = "handleEventsBluetoothConnectionWork";

    static final String START_EVENT_NOTIFICATION_WORK_TAG = "startEventNotificationWork";
    static final String RUN_APPLICATION_WITH_DELAY_WORK_TAG = "runApplicationWithDelayWork";
    static final String PROFILE_DURATION_WORK_TAG = "profileDurationWork";
    static final String DISABLE_NOT_USED_SCANNERS_WORK_TAG = "dislableNotUsedScannersWork";
    static final String DETECT_MERGE_RING_NOTIFICATION_VOLUMES_WORK_TAG = "detectRingNotificationVolumes";

    final Context context;

    public MainWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
//            long start = System.currentTimeMillis();

            Context appContext = context.getApplicationContext();

            Set<String> tags = getTags();
            for (String tag : tags) {
                // ignore tags with package name
                if (tag.startsWith(PPApplication.PACKAGE_NAME)) {
//                    PPApplicationStatic.logE("[IN_WORKER]  MainWorker.doWork", "PPApplication.PACKAGE_NAME");
                    continue;
                }
//                else
//                    PPApplicationStatic.logE("[IN_WORKER]  MainWorker.doWork", "--------------- START tag=" + tag);

                switch (tag) {
                    case ORIENTATION_SCANNER_WORK_TAG:
//                        PPApplicationStatic.logE("[TEST BATTERY] ******** MainWorker.doWork", "******** ### *******");
                    case HANDLE_EVENTS_VOLUMES_WORK_TAG: // !!! this is required, look at SettingsContentObserver.onChange()
                        if (!PPApplicationStatic.getApplicationStarted(true, true))
                            // application is not started
                            return Result.success();

                        int sensorType = getInputData().getInt(PhoneProfilesService.EXTRA_SENSOR_TYPE, 0);
                        if (EventStatic.getGlobalEventsRunning(appContext) && (sensorType != 0)) {
                            // start events handler
                            EventsHandler eventsHandler = new EventsHandler(appContext);
                            eventsHandler.handleEvents(sensorType);
                        }

                        break;

                    case HANDLE_EVENTS_BLUETOOTH_CONNECTION_WORK_TAG:
                        if (!PPApplicationStatic.getApplicationStarted(true, true))
                            // application is not started
                            return Result.success();

                        if (EventStatic.getGlobalEventsRunning(appContext)) {
//                            Log.e("MainWorker.doWork", "HANDLE_EVENTS_BLUETOOTH_CONNECTION_WORK_TAG");
                            EventsHandler eventsHandler = new EventsHandler(appContext);
                            eventsHandler.handleEvents(EventsHandler.SENSOR_TYPE_RADIO_SWITCH);

                            eventsHandler.handleEvents(EventsHandler.SENSOR_TYPE_BLUETOOTH_CONNECTION);

                            PPApplicationStatic.restartBluetoothScanner(appContext);
                        }

                        break;
                    case HANDLE_EVENTS_MOBILE_DATA_NETWORK_CALLBACK_WORK_TAG:
                        if (!PPApplicationStatic.getApplicationStarted(true, true))
                            // application is not started
                            return Result.success();

//                        PPApplicationStatic.logE("[IN_WORKER]  MainWorker.doWork", "tag=" + tag);
                        MobileDataNetworkCallback._doConnection(appContext);
                        break;
                    case HANDLE_EVENTS_WIFI_NETWORK_CALLBACK_WORK_TAG:
                        if (!PPApplicationStatic.getApplicationStarted(true, true))
                            // application is not started
                            return Result.success();

//                        PPApplicationStatic.logE("[IN_WORKER]  MainWorker.doWork", "tag=" + tag);
                        WifiNetworkCallback._doConnection(appContext);
                        break;
                    case LOCATION_SCANNER_SWITCH_GPS_TAG_WORK:
                        if (!PPApplicationStatic.getApplicationStarted(true, true))
                            // application is not started
                            return Result.success();

//                        PPApplicationStatic.logE("[IN_WORKER]  MainWorker.doWork", "tag=" + tag);
                        LocationScannerSwitchGPSBroadcastReceiver.doWork(appContext);
                        break;
                    case APPLICATION_FULLY_STARTED_WORK_TAG:
                        if (!PPApplicationStatic.getApplicationStarted(true, false))
                            // application is not started
                            return Result.success();

                        PPApplicationStatic.setApplicationFullyStarted(appContext);
//                        PPApplicationStatic.logE("[APPLICATION_FULLY_STARTED] MainWorker.doWork", "(1)");
                        PPApplication.showToastForProfileActivation = true;
                        break;
                    case ACCESSIBILITY_SERVICE_CONNECTED_NOT_RECEIVED_WORK_TAG:
                        if (!PPApplicationStatic.getApplicationStarted(true, false))
                            // application is not started
                            return Result.success();

                        int oldAccessibilityServiceForPPPExtenderConnected = PPApplication.accessibilityServiceForPPPExtenderConnected;
                        if (sk.henrichg.phoneprofilesplus.PPExtenderBroadcastReceiver.isAccessibilityServiceEnabled(appContext, false, false
                                /*, "MainWorker.doWork (ACCESSIBILITY_SERVICE_CONNECTED_NOT_RECEIVED_WORK_TAG)"*/)) {
                            PPApplication.accessibilityServiceForPPPExtenderConnected = 1;
                        }
                        else {
                            PPApplication.accessibilityServiceForPPPExtenderConnected = 2;

                            boolean displayNotification = getInputData().getBoolean(sk.henrichg.phoneprofilesplus.PPExtenderBroadcastReceiver.EXTRA_DISPLAY_NOTIFICATION, true);
                            if (displayNotification) {
                                if (sk.henrichg.phoneprofilesplus.PPExtenderBroadcastReceiver.isExtenderInstalled(appContext) != 0) {
                                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    String nTitle = context.getString(R.string.extender_accessibility_setting_not_enabled_title);
                                    String nText = context.getString(R.string.extender_accessibility_setting_not_enabled_text);

                                    PPApplicationStatic.createExclamationNotificationChannel(getApplicationContext(), false);
                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext, PPApplication.EXCLAMATION_NOTIFICATION_CHANNEL)
                                            .setColor(ContextCompat.getColor(appContext, R.color.error_color))
                                            .setSmallIcon(R.drawable.ic_ppp_notification/*ic_exclamation_notify*/) // notification icon
                                            .setLargeIcon(BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ic_exclamation_notification))
                                            .setContentTitle(nTitle) // title for notification
                                            .setContentText(nText) // message for notification
                                            .setAutoCancel(true); // clear notification after click
                                    mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(nText));

                                    PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(pi);

                                    mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                                    mBuilder.setCategory(NotificationCompat.CATEGORY_RECOMMENDATION);
                                    mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                                    mBuilder.setOnlyAlertOnce(true);

                                    mBuilder.setGroup(PPApplication.SYTEM_CONFIGURATION_ERRORS_NOTIFICATION_GROUP);

                                    NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
                                    try {
                                        mNotificationManager.notify(
                                                PPApplication.EXTENDER_ACCESSIBILITY_SERVICE_NOT_ENABLED_NOTIFICATION_TAG,
                                                PPApplication.EXTENDER_ACCESSIBILITY_SERVICE_NOT_ENABLED_NOTIFICATION_ID, mBuilder.build());
                                    } catch (SecurityException en) {
                                        Log.e("MainWorker.doWork", Log.getStackTraceString(en));
                                    } catch (Exception e) {
                                        //Log.e("MainWorker.doWork", Log.getStackTraceString(e));
                                        PPApplicationStatic.recordException(e);
                                    }
                                }
                            }

                        }
                        if (oldAccessibilityServiceForPPPExtenderConnected == 0) {
                            // answer from Extender not returned
                            PPApplicationStatic.restartAllScanners(appContext, false);
                            DataWrapper dataWrapper = new DataWrapper(getApplicationContext(), false, 0/*monochrome, monochromeValue*/, false, DataWrapper.IT_FOR_EDITOR, 0, 0f);

                            PPApplicationStatic.addActivityLog(dataWrapper.context, PPApplication.ALTYPE_EXTENDER_ACCESSIBILITY_SERVICE_NOT_ENABLED,
                                    null, null, "");

                            dataWrapper.restartEventsWithDelay(false, true, false, PPApplication.ALTYPE_UNDEFINED);
                        }
                        break;
                    case PPApplication.AFTER_FIRST_START_WORK_TAG:
                        if (!PPApplicationStatic.getApplicationStarted(true, false))
                            // application is not started
                            return Result.success();

                        doAfterFirstStart(appContext,
                                getInputData().getBoolean(PhoneProfilesService.EXTRA_ACTIVATE_PROFILES, true),
                                getInputData().getBoolean(PhoneProfilesService.EXTRA_START_FOR_EXTERNAL_APPLICATION, false),
                                getInputData().getString(PhoneProfilesService.EXTRA_START_FOR_EXTERNAL_APP_ACTION),
                                getInputData().getInt(PhoneProfilesService.EXTRA_START_FOR_EXTERNAL_APP_DATA_TYPE, 0),
                                getInputData().getString(PhoneProfilesService.EXTRA_START_FOR_EXTERNAL_APP_DATA_VALUE));
                                //getInputData().getBoolean(PhoneProfilesService.EXTRA_SHOW_TOAST, true));
                        break;
                    case SCHEDULE_AVOID_RESCHEDULE_RECEIVER_WORK_TAG:
                        AvoidRescheduleReceiverWorker.enqueueWork();
                        break;
                    case DISABLE_NOT_USED_SCANNERS_WORK_TAG:
                        if (!PPApplicationStatic.getApplicationStarted(true, false))
                            // application is not started
                            return Result.success();

//                        PPApplicationStatic.logE("[IN_WORKER]  MainWorker.doWork", "tag=" + tag);
                        DataWrapper dataWrapper = new DataWrapper(appContext, false, 0, false, 0, 0, 0f);
                        PhoneProfilesServiceStatic.disableNotUsedScanners(dataWrapper);
                        break;

                    case DETECT_MERGE_RING_NOTIFICATION_VOLUMES_WORK_TAG:
                        if (!PPApplicationStatic.getApplicationStarted(true, true))
                            // application is not started
                            return Result.success();

                        ActivateProfileHelper.setMergedRingNotificationVolumes(appContext);
                        break;

                    default:
                        if (tag.startsWith(PROFILE_DURATION_WORK_TAG)) {
                            if (!PPApplicationStatic.getApplicationStarted(true, true))
                                // application is not started
                                return Result.success();

                            long profileId = getInputData().getLong(PPApplication.EXTRA_PROFILE_ID, 0);
                            boolean forRestartEvents = getInputData().getBoolean(ProfileDurationAlarmBroadcastReceiver.EXTRA_FOR_RESTART_EVENTS, false);
                            int startupSource = getInputData().getInt(PPApplication.EXTRA_STARTUP_SOURCE, PPApplication.STARTUP_SOURCE_EVENT_MANUAL);
                            ProfileDurationAlarmBroadcastReceiver.doWork(false, appContext, profileId, forRestartEvents, startupSource);
                        }
                        else
                        if (tag.startsWith(RUN_APPLICATION_WITH_DELAY_WORK_TAG)) {
                            if (!PPApplicationStatic.getApplicationStarted(true, true))
                                // application is not started
                                return Result.success();

                            String profileName = getInputData().getString(PPApplication.EXTRA_PROFILE_NAME);
                            String runApplicationData = getInputData().getString(RunApplicationWithDelayBroadcastReceiver.EXTRA_RUN_APPLICATION_DATA);
//                            Log.e("MainWorker.dWork", "call of RunApplicationWithDelayBroadcastReceiver.doWork");
                            RunApplicationWithDelayBroadcastReceiver.doWork(appContext, profileName, runApplicationData);
                        }
                        else
                        if (tag.startsWith(EVENT_DELAY_START_TAG_WORK)) {
                            if (!PPApplicationStatic.getApplicationStarted(true, true))
                                // application is not started
                                return Result.success();

                            EventDelayStartBroadcastReceiver.doWork(false, appContext);
                        }
                        else
                        if (tag.startsWith(EVENT_DELAY_END_TAG_WORK)) {
                            if (!PPApplicationStatic.getApplicationStarted(true, true))
                                // application is not started
                                return Result.success();

                            EventDelayEndBroadcastReceiver.doWork(false, appContext);
                        }
                        else
                        if (tag.startsWith(START_EVENT_NOTIFICATION_WORK_TAG)) {
                            if (!PPApplicationStatic.getApplicationStarted(true, true))
                                // application is not started
                                return Result.success();

                            long eventId = getInputData().getLong(PPApplication.EXTRA_EVENT_ID, 0);
                            StartEventNotificationBroadcastReceiver.doWork(false, appContext, eventId);
                        }

                        break;
                }

//                PPApplicationStatic.logE("[IN_WORKER]  MainWorker.doWork", "--------------- END tag=" + tag);
            }

//            long finish = System.currentTimeMillis();
//            long timeElapsed = finish - start;
//            PPApplicationStatic.logE("[IN_WORKER]  MainWorker.doWork", "--------------- timeElapsed="+timeElapsed);
            return Result.success();
        } catch (Exception e) {
            PPApplicationStatic.recordException(e);
            return Result.failure();
        }
    }

    private void doAfterFirstStart(Context context,
                                          boolean activateProfiles,
                                          boolean startForExternalApplication,
                                          String startForExternalAppAction,
                                          int startForExternalAppDataType,
                                          String startForExternalAppDataValue) {
        PPApplicationStatic.logE("------- MainWorker.doAfterFirstStart", "START");

        final Context appContext = context.getApplicationContext();

        //BootUpReceiver.bootUpCompleted = true;

        //boolean fromDoFirstStart = getInputData().getBoolean(PhoneProfilesService.EXTRA_FROM_DO_FIRST_START, true);

        //if (fromDoFirstStart) {
        //PPApplication.createNotificationChannels(appContext);

        // activate profile immediately after start of PPP
        // this is required for some users, for example: francescocaldelli@gmail.com
        //PPApplication.applicationPackageReplaced = false;
        //if (fromDoFirstStart) {
        //PhoneProfilesService instance = PhoneProfilesService.getInstance();
        //if (instance != null)
        //    instance.PhoneProfilesService.setApplicationFullyStarted(appContext/*true*/);

//        PPApplication.setApplicationFullyStarted(appContext, showToast);

        //}

        Intent intent;

        final DataWrapper dataWrapper = new DataWrapper(appContext, false, 0, false, 0, 0, 0f);

        if (EventStatic.getGlobalEventsRunning(appContext)) {
            PPApplicationStatic.logE("MainWorker.doAfterFirstStart", "global event run is enabled, first start events");

            dataWrapper.fillEventList();

            if (activateProfiles) {
                if (!DataWrapperStatic.getIsManualProfileActivation(false, appContext)) {
                    PPApplicationStatic.logE("MainWorker.doAfterFirstStart", "pause all events");
                    ////// unblock all events for first start
                    //     that may be blocked in previous application run
                    synchronized (PPApplication.eventsHandlerMutex) {
                        dataWrapper.pauseAllEvents(false, false);
                    }
                }
            }

            dataWrapper.firstStartEvents(true, false);

            // must be used hanlder for this, because of FC:
            // ava.lang.RuntimeException: Can't create handler inside thread Thread[pool-1-thread-2,5,main] that has not called Looper.prepare()
            //	at android.os.Handler.<init>(Handler.java:207)
            //	at android.os.Handler.<init>(Handler.java:119)
            //	at sk.henrichg.phoneprofilesplus.TwilightScanner$LocationHandler.<init>(TwilightScanner.java:198)
            //	at sk.henrichg.phoneprofilesplus.TwilightScanner$LocationHandler.<init>(TwilightScanner.java:198)
            //	at sk.henrichg.phoneprofilesplus.TwilightScanner.<init>(TwilightScanner.java:50)
            //	at sk.henrichg.phoneprofilesplus.PhoneProfilesService.startTwilightScanner(PhoneProfilesService.java:6743)
            //	at sk.henrichg.phoneprofilesplus.PhoneProfilesService.startTwilightScanner(PhoneProfilesService.java:3430)
            //	at sk.henrichg.phoneprofilesplus.PhoneProfilesService.registerEventsReceiversAndWorkers(PhoneProfilesService.java:3611)
            //	at sk.henrichg.phoneprofilesplus.MainWorker.doAfterFirstStart(MainWorker.java:707)
            //
            // !!! Worker do not have Looper !!!
            //PPApplication.startHandlerThreadBroadcast();
            //final Handler __handler = new Handler(PPApplication.handlerThreadBroadcast.getLooper());
            //__handler.post(new PPHandlerThreadRunnable(
            //        appContext, dataWrapper) {
            //__handler.post(() -> {
            //Runnable runnable = () -> {
//                PPApplicationStatic.logE("[IN_THREAD_HANDLER] PPApplication.startHandlerThread", "START run - from=MainWorker.doAfterFirstStart (1)");

                //Context appContext= appContextWeakRef.get();
                //DataWrapper dataWrapper = dataWrapperWeakRef.get();

                //if ((appContext != null) && (dataWrapper != null)) {
//                    PowerManager powerManager = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
//                    PowerManager.WakeLock wakeLock = null;
//                    try {
//                        if (powerManager != null) {
//                            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, PPApplication.PACKAGE_NAME + ":MainWorker_doAfterFirstStart_1");
//                            wakeLock.acquire(10 * 60 * 1000);
//                        }

                        PPApplicationStatic.logE("MainWorker.doAfterFirstStart", "register receivers and workers");
                        PhoneProfilesServiceStatic.disableNotUsedScanners(dataWrapper);
                        PhoneProfilesServiceStatic.registerAllTheTimeRequiredSystemReceivers(true, appContext);
                        PhoneProfilesServiceStatic.registerAllTheTimeContentObservers(true, appContext);
                        PhoneProfilesServiceStatic.registerAllTheTimeCallbacks(true, appContext);
                        PhoneProfilesServiceStatic.registerPPPExtenderReceiver(true, dataWrapper, appContext);
                        PhoneProfilesServiceStatic.registerEventsReceiversAndWorkers(false, appContext);

                        if (PPApplication.deviceBoot) {
                            PPApplication.deviceBoot = false;
                            PPApplicationStatic.logE("MainWorker.doAfterFirstStart", "device boot");
                            boolean deviceBootEvents = dataWrapper.eventTypeExists(DatabaseHandler.ETYPE_DEVICE_BOOT);
                            if (deviceBootEvents) {
                                PPApplicationStatic.logE("MainWorker.doAfterFirstStart", "device boot event exists");

                                // start events handler

//                            PPApplicationStatic.logE("[EVENTS_HANDLER_CALL] MainWorker.doAfterFirstStart", "sensorType=SENSOR_TYPE_DEVICE_BOOT");
                                EventsHandler eventsHandler = new EventsHandler(appContext);

                                Calendar now = Calendar.getInstance();
                                int gmtOffset = 0; //TimeZone.getDefault().getRawOffset();
                                final long _time = now.getTimeInMillis() + gmtOffset;
                                eventsHandler.setEventDeviceBootParameters(_time);

                                eventsHandler.handleEvents(EventsHandler.SENSOR_TYPE_DEVICE_BOOT);

                            }
                        }

//                    } catch (Exception eee) {
//                        Log.e("MainWorker.doAfterFirstStart", Log.getStackTraceString(eee));
//                        //PPApplicationStatic.recordException(eee);
//                    } finally {
//                        if ((wakeLock != null) && wakeLock.isHeld()) {
//                            try {
//                                wakeLock.release();
//                            } catch (Exception ignored) {
//                            }
//                        }
//                    }
                //}
            //}; //);
            //PPApplication.createEventsHandlerExecutor();
            //PPApplication.eventsHandlerExecutor.submit(runnable);

            // !!! FOR TESTING NOT STARTED PPP BUG !!!!
//            PPApplication.setApplicationFullyStarted(appContext);

            //PPApplication.updateNotificationAndWidgets(true, true, appContext);
            //PPApplication.updateGUI(appContext, true, true);
        } else {
            PPApplicationStatic.logE("MainWorker.doAfterFirstStart", "global event run is not enabled, manually activate profile");

            if (activateProfiles) {
                ////// unblock all events for first start
                //     that may be blocked in previous application run
                synchronized (PPApplication.eventsHandlerMutex) {
                    dataWrapper.pauseAllEvents(true, false);
                }
            }

            // must be used hanlder for this, because of FC:
            // ava.lang.RuntimeException: Can't create handler inside thread Thread[pool-1-thread-2,5,main] that has not called Looper.prepare()
            //	at android.os.Handler.<init>(Handler.java:207)
            //	at android.os.Handler.<init>(Handler.java:119)
            //	at sk.henrichg.phoneprofilesplus.TwilightScanner$LocationHandler.<init>(TwilightScanner.java:198)
            //	at sk.henrichg.phoneprofilesplus.TwilightScanner$LocationHandler.<init>(TwilightScanner.java:198)
            //	at sk.henrichg.phoneprofilesplus.TwilightScanner.<init>(TwilightScanner.java:50)
            //	at sk.henrichg.phoneprofilesplus.PhoneProfilesService.startTwilightScanner(PhoneProfilesService.java:6743)
            //	at sk.henrichg.phoneprofilesplus.PhoneProfilesService.startTwilightScanner(PhoneProfilesService.java:3430)
            //	at sk.henrichg.phoneprofilesplus.PhoneProfilesService.registerEventsReceiversAndWorkers(PhoneProfilesService.java:3611)
            //	at sk.henrichg.phoneprofilesplus.MainWorker.doAfterFirstStart(MainWorker.java:707)
            //
            // !!! Worker do not have Looper !!!
            //PPApplication.startHandlerThreadBroadcast();
            //final Handler __handler = new Handler(PPApplication.handlerThreadBroadcast.getLooper());
            //__handler.post(new PPHandlerThreadRunnable(
            //        appContext, dataWrapper) {
            //__handler.post(() -> {
//            Runnable runnable = () -> {
////                PPApplicationStatic.logE("[IN_THREAD_HANDLER] PPApplication.startHandlerThread", "START run - from=MainWorker.doAfterFirstStart (2)");
                PPApplicationStatic.logE("MainWorker.doAfterFirstStart", "START");

                //Context appContext= appContextWeakRef.get();
                //DataWrapper dataWrapper = dataWrapperWeakRef.get();

                //if ((appContext != null) && (dataWrapper != null)) {
//                    PowerManager powerManager = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
//                    PowerManager.WakeLock wakeLock = null;
//                    try {
//                        if (powerManager != null) {
//                            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, PPApplication.PACKAGE_NAME + ":MainWorker_doAfterFirstStart_2");
//                            wakeLock.acquire(10 * 60 * 1000);
//                        }

                        // This is fix for 2, 3 restarts of events after first start.
                        // Bradcasts, observers, callbacks registration starts events and this is not good
                        PPApplicationStatic.logE("MainWorker.doAfterFirstStart", "register receivers and workers");
                        PhoneProfilesServiceStatic.registerAllTheTimeRequiredSystemReceivers(true, appContext);
                        PhoneProfilesServiceStatic.registerAllTheTimeContentObservers(true, appContext);
                        PhoneProfilesServiceStatic.registerAllTheTimeCallbacks(true, appContext);
                        PhoneProfilesServiceStatic.registerPPPExtenderReceiver(true, dataWrapper, appContext);

//                    } catch (Exception eee) {
//                        Log.e("MainWorker.doAfterFirstStart", Log.getStackTraceString(eee));
//                        //PPApplicationStatic.recordException(eee);
//                    } finally {
//                        if ((wakeLock != null) && wakeLock.isHeld()) {
//                            try {
//                                wakeLock.release();
//                            } catch (Exception ignored) {
//                            }
//                        }
//                    }
                //}
//            }; //);
//            PPApplication.createBasicExecutorPool();
//            PPApplication.basicExecutorPool.submit(runnable);

            PPApplicationStatic.setApplicationFullyStarted(appContext);
//            PPApplicationStatic.logE("[APPLICATION_FULLY_STARTED] MainWorker.doWork", "(2)");

            dataWrapper.activateProfileAtFirstStart();
            //PPApplication.updateNotificationAndWidgets(true, true, appContext);
            //PPApplication.updateGUI(appContext, true, true);
        }

//        PPApplication.setApplicationFullyStarted(appContext, showToast);

//        PPApplicationStatic.logE("[PPP_NOTIFICATION] MainWorker.doAfterFirstStart", "call of forceUpdateGUI");
        PPApplication.forceUpdateGUI(appContext, true, true, PPApplication.firstStartAfterInstallation);
        PPApplication.firstStartAfterInstallation = false;
        //}

        // must be first
        PPApplicationStatic.createContactsCache(appContext, true);
        //must be seconds, this ads groups into contacts
        PPApplicationStatic.createContactGroupsCache(appContext, true);
        EventsHandler eventsHandler = new EventsHandler(appContext);
        eventsHandler.handleEvents(EventsHandler.SENSOR_TYPE_CONTACTS_CACHE_CHANGED);

        if (startForExternalApplication) {
            if ((Build.VERSION.SDK_INT < 29) || Settings.canDrawOverlays(appContext)) {
                // Permission SYSTEM_ALERT_WINDOW is required for start activity from Worker
                try {
                    intent = new Intent(startForExternalAppAction);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    if (startForExternalAppDataType == PhoneProfilesService.START_FOR_EXTERNAL_APP_PROFILE)
                        intent.putExtra(PPApplication.EXTRA_PROFILE_NAME, startForExternalAppDataValue);
                    if (startForExternalAppDataType == PhoneProfilesService.START_FOR_EXTERNAL_APP_EVENT)
                        intent.putExtra(ActionForExternalApplicationActivity.EXTRA_EVENT_NAME, startForExternalAppDataValue);
                    appContext.startActivity(intent);
                } catch (Exception ignored) {
                }
            }
        }

        PPApplicationStatic.logE("------- MainWorker.doAfterFirstStart", "END");
    }

/*    private static abstract class PPHandlerThreadRunnable implements Runnable {

        final WeakReference<Context> appContextWeakRef;
        final WeakReference<DataWrapper> dataWrapperWeakRef;

        PPHandlerThreadRunnable(Context appContext,
                                       DataWrapper dataWrapper) {
            this.appContextWeakRef = new WeakReference<>(appContext);
            this.dataWrapperWeakRef = new WeakReference<>(dataWrapper);
        }

    }*/

}
