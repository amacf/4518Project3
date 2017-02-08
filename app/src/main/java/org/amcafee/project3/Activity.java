package org.amcafee.project3;

import java.util.Date;
import java.util.UUID;

/**
 * Created by andy on 2/8/17.
 */

public class Activity {
    public UUID mId;
    public String mType;
    public Date mDate;

    public Activity() {
        this(UUID.randomUUID());
    }

    public Activity(UUID id) {
        mId = id;
        mDate = new Date();
    }
}
