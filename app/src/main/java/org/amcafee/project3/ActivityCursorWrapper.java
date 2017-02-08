package org.amcafee.project3;

import android.database.Cursor;
import org.amcafee.project3.ActivityDatabaseSchema.ActivityTable;

import java.util.Date;
import java.util.UUID;
import android.database.CursorWrapper;

import org.amcafee.project3.Activity;

import java.util.Date;
import java.util.UUID;

/**
 * Created by andy on 2/8/17.
 */

public class ActivityCursorWrapper extends CursorWrapper {

    public ActivityCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Activity getActivity() {
        String uuidString = getString(getColumnIndex(ActivityTable.Cols.UUID));
        String type = getString(getColumnIndex(ActivityTable.Cols.TYPE));
        long date = getLong(getColumnIndex(ActivityTable.Cols.TIME));

        Activity activity = new Activity(UUID.fromString(uuidString));
        activity.mType = type;
        activity.mDate = new Date(date);

        return activity;
    }
}
