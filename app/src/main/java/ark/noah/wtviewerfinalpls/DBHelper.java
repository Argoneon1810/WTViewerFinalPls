package ark.noah.wtviewerfinalpls;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ark.noah.wtviewerfinalpls.ui.main.ToonsAdapter;
import ark.noah.wtviewerfinalpls.ui.main.ToonsContainer;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Content.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME_TOONS = "Table_toons";
    public static final String ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_TYPE = "type";
    public static final String COL_TOONID = "toonid";
    public static final String COL_EPIID = "epiid";
    public static final String COL_RELEASEDAY = "releaseweekday";
    public static final String COL_HIDE = "hide";

    private final String createQueryToons = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME_TOONS +"("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TITLE + " TEXT, "
            + COL_TYPE + " TEXT, "
            + COL_TOONID + " INTEGER, "
            + COL_EPIID + " INTEGER, "
            + COL_RELEASEDAY + " INTEGER, "
            + COL_HIDE + " INTEGER DEFAULT 0 )";

    private final String DATABASE_TOONS_ALTER_TABLE_1 = "ALTER TABLE "
            + TABLE_NAME_TOONS + " ADD COLUMN " + COL_HIDE + " INTEGER DEFAULT 0 ";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createQueryToons);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            sqLiteDatabase.execSQL(DATABASE_TOONS_ALTER_TABLE_1);
        }
    }

    public Cursor loadDBCursorToons(){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NAME_TOONS + " ";

        return db.rawQuery(selectQuery, null);
    }

    public void insertToonContent(String title, String type, int toonid, int epiid, int releaseday, int hide) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, title);
        contentValues.put(COL_TYPE, type);
        contentValues.put(COL_TOONID, toonid);
        contentValues.put(COL_EPIID, epiid);
        contentValues.put(COL_RELEASEDAY, releaseday);
        contentValues.put(COL_HIDE, hide);
        db.insert(TABLE_NAME_TOONS, null, contentValues);

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }

    public void insertToonContent(ToonsContainer container) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
//        contentValues.put(ID, container.dbID);
        contentValues.put(COL_TITLE, container.toonName);
        contentValues.put(COL_TYPE, container.toonType);
        contentValues.put(COL_TOONID, container.toonID);
        contentValues.put(COL_EPIID, container.episodeID);
        contentValues.put(COL_RELEASEDAY, container.releaseWeekdays);
        contentValues.put(COL_HIDE, container.hide);
        db.insert(TABLE_NAME_TOONS, null, contentValues);

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }

    public int editToonContent(int id, String title, String type, int toonid, int epiid, int releaseday, boolean hide) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, title);
        contentValues.put(COL_TYPE, type);
        contentValues.put(COL_TOONID, toonid);
        contentValues.put(COL_EPIID, epiid);
        contentValues.put(COL_RELEASEDAY, releaseday);
        contentValues.put(COL_HIDE, hide);
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
        contentValues.put(COL_HIDE, container.hide);
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
        String selectQuery = "SELECT * FROM ( SELECT ROW_NUMBER () OVER ( ORDER BY " + ID + " ) RowNum, " + ID + ", "+ COL_TITLE + ", " + COL_TYPE + ", " + COL_TOONID + ", " + COL_EPIID + ", " + COL_RELEASEDAY + ", " + COL_HIDE + " FROM " + TABLE_NAME_TOONS + " )";
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
                                    cursor.getInt   (cursor.getColumnIndex(COL_RELEASEDAY)),
                                    cursor.getInt   (cursor.getColumnIndex(COL_HIDE)) != 0
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
                        cursor.getInt   (cursor.getColumnIndex(COL_RELEASEDAY)),
                        cursor.getInt   (cursor.getColumnIndex(COL_HIDE)) != 0
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
        String selectQuery = "SELECT * FROM ( SELECT ROW_NUMBER () OVER ( ORDER BY " + ID + " ) RowNum, " + ID + ", " + COL_TITLE + ", " + COL_TYPE + ", " + COL_TOONID + ", " + COL_EPIID + ", " + COL_RELEASEDAY + ", " + COL_HIDE + " FROM " + TABLE_NAME_TOONS + " )";

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

    public void wipeToons() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TOONS);
        db.execSQL(createQueryToons);
        db.close();
    }
}
