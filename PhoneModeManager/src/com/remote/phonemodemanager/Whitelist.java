package com.remote.phonemodemanager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Whitelist {
	// database name
    public static final String WHITELISTED_TABLE_NAME = "whitelisted";
    
    // column names
    static final String ID = "id";
    static final String NAME = "name";
    static final String NR = "nr";
    static final String LEVEL = "level";
    static final String UN_NOTICED_CALLS = "uCalls";
    
    // SQL statement to create our table
    public static final String WHITELISTED_CREATE_TABLE = "CREATE TABLE " + Whitelist.WHITELISTED_TABLE_NAME + " ("
    		 										+ Whitelist.ID + " INTEGER PRIMARY KEY,"
                                                    + Whitelist.NAME + " TEXT,"
                                                    + Whitelist.NR + " TEXT,"
                                                    + Whitelist.LEVEL + " INTEGER,"
                                                    + Whitelist.UN_NOTICED_CALLS + " INTEGER"
                                                    + ");";
	
    // currently stored variables
	long id;
	String name;
	String nr;
	int level;
	int uCalls;
    
    private Whitelist() {}
    
    public Whitelist(final String name, final String nr, final int level) {
    	this.name = name;
    	this.nr = nr;
    	this.level = level;
    	this.uCalls = 0;
    }
    
    public static void clear(DatabaseHelper dbHelper) {
    	final SQLiteDatabase db = dbHelper.getReadableDatabase();
    	
    	db.delete(WHITELISTED_TABLE_NAME, null, null);
    	
    	db.close();
    }
    
    public void save(DatabaseHelper dbHelper) {
            final ContentValues values = new ContentValues();
            values.put(NAME, this.name);
            values.put(NR, this.nr);
            values.put(LEVEL, this.level);
            values.put(UN_NOTICED_CALLS, this.uCalls);
            
            final SQLiteDatabase db = dbHelper.getReadableDatabase();
            this.id = db.insert(WHITELISTED_TABLE_NAME, null, values);
            db.close();
    }
    
    public int delete(DatabaseHelper dbHelper, String name){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        //return db.delete(WHITELISTED_TABLE_NAME, ID + "=" + id, null) > 0;
        return db.delete(WHITELISTED_TABLE_NAME, NAME + "=?", new String[] { name });
}
    
    public static Whitelist[] getAll(final DatabaseHelper dbHelper) {
             final List<Whitelist> whitelisted = new ArrayList<Whitelist>();
             final SQLiteDatabase db = dbHelper.getWritableDatabase();
             final Cursor c = db.query(WHITELISTED_TABLE_NAME,
                             new String[] { ID, NAME, NR, LEVEL, UN_NOTICED_CALLS}, null, null, null, null, null);
             // make sure you start from the first item
             c.moveToFirst();
             while (!c.isAfterLast()) {
                     final Whitelist whitelist = cursorToWhitelist(c);
                     whitelisted.add(whitelist);
                 c.moveToNext();
             }
             // Make sure to close the cursor
             c.close();
             return whitelisted.toArray(new Whitelist[whitelisted.size()]);
    }
    
    public static Whitelist cursorToWhitelist(Cursor c) {
            final Whitelist whitelist = new Whitelist();
            whitelist.setName(c.getString(c.getColumnIndex(NAME)));
            whitelist.setNr(c.getString(c.getColumnIndex(NR)));
            whitelist.setLevel(c.getInt(c.getColumnIndex(LEVEL)));
            whitelist.setUnNoticedCalls(c.getInt(c.getColumnIndex(UN_NOTICED_CALLS)));
            return whitelist;
    }

    public String getName() {
            return name;
    }

    public void setName(String name) {
            this.name = name;
    }
    
    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
    public int getUnNoticedCalls() {
        return uCalls;
    }

    public void setUnNoticedCalls(int UnNoticedCalls) {
        this.uCalls = UnNoticedCalls;
    }
}