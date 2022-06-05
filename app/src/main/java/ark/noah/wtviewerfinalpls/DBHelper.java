package ark.noah.wtviewerfinalpls;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ark.noah.wtviewerfinalpls.ui.main.ToonsContainer;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Content.db";
    public static final int DATABASE_VERSION = 1;

    public final String TABLE_NAME_TOONS = "Table_toons";
    public final String ID = "id";
    public final String COL_TITLE = "title";
    public final String COL_TYPE = "type";
    public final String COL_TOONID = "toonid";
    public final String COL_EPIID = "epiid";
    public final String COL_RELEASEDAY = "releaseweekday";

    private final String createQueryToons = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME_TOONS +"("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TITLE + " TEXT, "
            + COL_TYPE + " TEXT, "
            + COL_TOONID + " INTEGER, "
            + COL_EPIID + " INTEGER, "
            + COL_RELEASEDAY + " INTEGER )";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createQueryToons);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public Cursor loadDBCursorToons(){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NAME_TOONS + "";

        return db.rawQuery(selectQuery, null);
    }

    public void insertToonContent(String title, String type, int toonid, int epiid, int releaseday) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, title);
        contentValues.put(COL_TYPE, type);
        contentValues.put(COL_TOONID, toonid);
        contentValues.put(COL_EPIID, epiid);
        contentValues.put(COL_RELEASEDAY, releaseday);
        db.insert(TABLE_NAME_TOONS, null, contentValues);

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }

    public void insertToonContent(ToonsContainer container) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, container.dbID);
        contentValues.put(COL_TITLE, container.toonName);
        contentValues.put(COL_TYPE, container.toonType);
        contentValues.put(COL_TOONID, container.toonID);
        contentValues.put(COL_EPIID, container.episodeID);
        contentValues.put(COL_RELEASEDAY, container.releaseWeekdays);
        db.insert(TABLE_NAME_TOONS, null, contentValues);

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }

    public int editToonContent(int id, String title, String type, int toonid, int epiid, int releaseday) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, title);
        contentValues.put(COL_TYPE, type);
        contentValues.put(COL_TOONID, toonid);
        contentValues.put(COL_EPIID, epiid);
        contentValues.put(COL_RELEASEDAY, releaseday);
        int toReturn = db.update(TABLE_NAME_TOONS, contentValues, ID + "='" + id + "'", null);

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();

        return toReturn;
    }

    public int editToonContent(ToonsContainer container) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, container.toonName);
        contentValues.put(COL_TYPE, container.toonType);
        contentValues.put(COL_TOONID, container.toonID);
        contentValues.put(COL_EPIID, container.episodeID);
        contentValues.put(COL_RELEASEDAY, container.releaseWeekdays);
        int toReturn = db.update(TABLE_NAME_TOONS, contentValues, ID + "='" + container.dbID + "'", null);

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();

        return toReturn;
    }

    public int deleteToonContent(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int toReturn = db.delete(TABLE_NAME_TOONS, ID + "='" + id + "'", null);
        db.close();
        return toReturn;
    }

    @SuppressLint("Range")
    public ArrayList<ToonsContainer> getAllToons() {
        ArrayList<ToonsContainer> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM ( SELECT ROW_NUMBER () OVER ( ORDER BY " + ID + " ) RowNum, " + ID + ", "+ COL_TITLE + ", " + COL_TYPE + ", " + COL_TOONID + ", " + COL_EPIID + ", " + COL_RELEASEDAY + " FROM " + TABLE_NAME_TOONS + " )";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    list.add(
                            new ToonsContainer(
                                    cursor.getInt   (cursor.getColumnIndex(ID)),
                                    cursor.getString(cursor.getColumnIndex(COL_TITLE)),
                                    cursor.getString(cursor.getColumnIndex(COL_TYPE)),
                                    cursor.getInt   (cursor.getColumnIndex(COL_TOONID)),
                                    cursor.getInt   (cursor.getColumnIndex(COL_EPIID)),
                                    cursor.getInt   (cursor.getColumnIndex(COL_RELEASEDAY))
                            )
                    );
                } while(cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();

        return list;
    }

    @SuppressLint("Range")
    public ToonsContainer getToonByID(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME_TOONS + " WHERE " + ID + " = " + id;

        ToonsContainer container = null;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                container = new ToonsContainer(
                        cursor.getInt   (cursor.getColumnIndex(ID)),
                        cursor.getString(cursor.getColumnIndex(COL_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COL_TYPE)),
                        cursor.getInt   (cursor.getColumnIndex(COL_TOONID)),
                        cursor.getInt   (cursor.getColumnIndex(COL_EPIID)),
                        cursor.getInt   (cursor.getColumnIndex(COL_RELEASEDAY))
                );
            }
            cursor.close();
        }

        db.close();

        return container;
    }

    @SuppressLint("Range")
    public int getToonIDAtLastPosition() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM ( SELECT ROW_NUMBER () OVER ( ORDER BY " + ID + " ) RowNum, " + ID + ", "+ COL_TITLE + ", " + COL_TYPE + ", " + COL_TOONID + ", " + COL_EPIID + ", " + COL_RELEASEDAY + " FROM " + TABLE_NAME_TOONS + " )";

        int id = -1;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    id = cursor.getInt(cursor.getColumnIndex(ID));
                } while(cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();

        return id;
    }
}
