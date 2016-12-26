package com.example.root.navigationdrawertest.data;

import android.provider.BaseColumns;

/**
 * Created by root on 17.12.16.
 */

public final class DataContract {

    public static final class UsersEntry implements BaseColumns{
        public static final String TABLE_NAME = "users";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_NICK = "nick";
        public static final String COLUMN_MAC = "mac";
        public static final String COLUMN_IMG_ID = "img_id";
    }

    public static final class MessagesEntry implements BaseColumns{
        public static final String TABLE_NAME = "messages";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_ID_DLG = "id_dlg";
        public static final String COLUMN_ID_USER = "id_user";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_BROADCAST = "broadcast";
    }

    public static final class DialogsEntry implements BaseColumns{
        public static final String TABLE_NAME = "dialogs";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_ID_USER = "id_user";
    }
}
