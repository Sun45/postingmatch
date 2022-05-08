package cn.sun45.postingmatch.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Created by Sun45 on 2022/5/1
 */
@Database(entities = {PostingModel.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "my_db";

    private static MyDatabase databaseInstance;

    public static synchronized MyDatabase getInstance(Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, DATABASE_NAME).build();
        }
        return databaseInstance;
    }

    public abstract PostingDao postingDao();
}