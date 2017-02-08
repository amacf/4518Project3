package org.amcafee.project3;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by Paul on 2/1/16.
 */
public class ActivityRecognizedService extends IntentService {

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    if (activity.getConfidence() >= 75) {
                        Intent sendActivity = new Intent(MainActivity.RECEIVE_ACT);
                        sendActivity.putExtra("activityType", "driving");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(sendActivity);
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    if (activity.getConfidence() >= 75) {
                        Intent sendActivity = new Intent(MainActivity.RECEIVE_ACT);
                        sendActivity.putExtra("activityType", "biking");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(sendActivity);
                    }
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    if (activity.getConfidence() >= 75) {
                        Intent sendActivity = new Intent(MainActivity.RECEIVE_ACT);
                        sendActivity.putExtra("activityType", "running");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(sendActivity);
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    if (activity.getConfidence() >= 75) {
                        Intent sendActivity = new Intent(MainActivity.RECEIVE_ACT);
                        sendActivity.putExtra("activityType", "still");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(sendActivity);
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if (activity.getConfidence() >= 75) {
                        Intent sendActivity = new Intent(MainActivity.RECEIVE_ACT);
                        sendActivity.putExtra("activityType", "walking");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(sendActivity);
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    break;
                }
            }
        }
    }
}
