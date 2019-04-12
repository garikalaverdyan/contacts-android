package com.garikalaverdyan.contacts.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DefaultContactsDataBaseHelper extends SQLiteOpenHelper implements ContactsDataBaseHelper {
    private SQLiteDatabase db = getWritableDatabase();
    private final String dbName = "contactsTable";

    public DefaultContactsDataBaseHelper(Context context) {
        super(context, "myDB", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table contactsTable ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "surname text,"
                + "number text,"
                + "gender text,"
                + "day text,"
                + "month text,"
                + "year text,"
                + "isFavorite integer,"
                + "imagePath text"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public int addContact(Contact contact) {
        ContentValues cv = new ContentValues();
        cv.put("name", contact.getName());
        cv.put("surname", contact.getSurname());
        cv.put("number", contact.getNumber());
        cv.put("gender", contact.getGender());
        cv.put("day", contact.getDay());
        cv.put("month", contact.getMonth());
        cv.put("year", contact.getYear());
        cv.put("isFavorite", contact.isFavorite());
        cv.put("imagePath", contact.getImagePath());
        return (int) db.insert("contactsTable", null, cv);
    }

    @Override
    public Contact getContact(int id) {
        Contact contact = null;
        Cursor c = db.query(dbName, null, "id=?", new String[]{String.valueOf(id)}, null, null,null);
        if (c.moveToFirst()) {
            contact = parseCursor(c);
        } else {
            Log.d("DB empty", "database is empty!");
        }

        c.close();
        return contact;
    }

    @Override
    public ArrayList<Contact> getContactList() {
        Cursor c = db.query(dbName, null, null, null, null, null, "name");
        ArrayList<Contact> contactList = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                contactList.add(parseCursor(c));
            }while(c.moveToNext());
        }else{
            Log.d("DB empty", "database is empty!");
        }
        c.close();
        return contactList;
    }

    @Override
    public void changeContact(Contact contact) {
        ContentValues cv = new ContentValues();
        cv.put("name", contact.getName());
        cv.put("surname", contact.getSurname());
        cv.put("number", contact.getNumber());
        cv.put("gender", contact.getGender());
        cv.put("day", contact.getDay());
        cv.put("month", contact.getMonth());
        cv.put("year", contact.getYear());
        cv.put("isFavorite", contact.isFavorite());

        int updCount = db.update(dbName, cv, "id = ?", new String[]{String.valueOf(contact.getId())});
        Log.d("update count", String.valueOf(updCount));
    }

    @Override
    public void deleteContact(int id) {
        if (id < 1) {
            Log.e("Delete contact", "Something went wrong");
        }

        db.delete("contactsTable", "id = " + id, null);
    }

    @Override
    public ArrayList<Contact> getFavoriteContacts() {
        Cursor cursor = db.query(dbName, null, "isFavorite=1", null, null, null, null);
        ArrayList<Contact> favoriteContactList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                favoriteContactList.add(parseCursor(cursor));
            }while (cursor.moveToNext());
        }else{
            Log.d("DB empty", "database is empty!");
        }
        cursor.close();
        return favoriteContactList;
    }

    @Override
    public void changeFavoriteContactValue(int id, boolean isFavorite) {
        int favoriteValue = parseBoolean(isFavorite);
        ContentValues cv = new ContentValues();
        cv.put("isFavorite", favoriteValue);
        db.update(dbName, cv, "id = ?", new String[]{String.valueOf(id)});
    }

    @Override
    public void changeImagePath(int id, String path) {
        ContentValues cv = new ContentValues();
        cv.put("imagePath" , path);
        db.update(dbName, cv, "id = ?", new String[]{String.valueOf(id)});
    }

    private Contact parseCursor(Cursor c) {
        int nameColIndex = c.getColumnIndex("name");
        int surnameColIndex = c.getColumnIndex("surname");
        int numberColIndex = c.getColumnIndex("number");
        int genderColIndex = c.getColumnIndex("gender");
        int dayColIndex = c.getColumnIndex("day");
        int monthColIndex = c.getColumnIndex("month");
        int yearColIndex = c.getColumnIndex("year");
        int isFavoriteColIndex = c.getColumnIndex("isFavorite");
        int idColIndex = c.getColumnIndex("id");
        int idImagePathColIndex = c.getColumnIndex("imagePath");

        boolean isFavorite = parseInteger(c.getInt(isFavoriteColIndex));

        Contact contact = new Contact(
                c.getString(nameColIndex),
                c.getString(surnameColIndex),
                c.getString(numberColIndex),
                c.getString(genderColIndex),
                c.getString(dayColIndex),
                c.getString(monthColIndex),
                c.getString(yearColIndex),
                isFavorite,
                c.getString(idImagePathColIndex));

        contact.setId(c.getInt(idColIndex));
        return contact;
    }

    private int parseBoolean(boolean isFavorite) {
        if (isFavorite) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean parseInteger(int isFavorite) {
        return isFavorite == 1;
    }
}