package org.amcafee.project3;

import java.sql.Time;

public class ActivityDatabaseSchema {
    public static final class ActivityTable {
        public static final String NAME = "activity";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TYPE = "type";
            public static final String TIME = "date";
        }
    }
}
