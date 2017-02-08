package org.amcafee.project3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import org.amcafee.project3.ActivityDatabaseSchema.ActivityTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActivityLab {
    private static ActivityLab sActivityLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ActivityLab get(Context context) {
        if (sActivityLab == null) {
            sActivityLab = new ActivityLab(context);
        }
        return sActivityLab;
    }

    private ActivityLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ActivityBaseHelper(mContext)
                .getWritableDatabase();
    }


    public void addActivity(Activity c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(ActivityTable.NAME, null, values);
    }

    public List<Activity> getActivitys() {
        List<Activity> Activitys = new ArrayList<>();

        ActivityCursorWrapper cursor = queryActivitys(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Activitys.add(cursor.getActivity());
            cursor.moveToNext();
        }
        cursor.close();

        return Activitys;
    }

    public Activity getActivity(UUID id) {
        ActivityCursorWrapper cursor = queryActivitys(
                ActivityTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getActivity();
        } finally {
            cursor.close();
        }
    }

    public void updateActivity(Activity Activity) {
        String uuidString = Activity.mId.toString();
        ContentValues values = getContentValues(Activity);

        mDatabase.update(ActivityTable.NAME, values,
                ActivityTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Activity Activity) {
        ContentValues values = new ContentValues();
        values.put(ActivityTable.Cols.UUID, Activity.mId.toString());
        values.put(ActivityTable.Cols.TYPE, Activity.mType);
        values.put(ActivityTable.Cols.TIME, Activity.mDate.getTime());

        return values;
    }

    private ActivityCursorWrapper queryActivitys(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ActivityTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new ActivityCursorWrapper(cursor);
    }
}
