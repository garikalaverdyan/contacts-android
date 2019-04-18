package com.garikalaverdyan.contacts.data;

import java.util.ArrayList;

public class ContactRepository {
    private ArrayList<ContactChangeListener> listeners = new ArrayList<>();
    private final ContactsDataBaseHelper dbHelper;

    public ContactRepository(ContactsDataBaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public interface ContactChangeListener {
        void onAdd(int id);
        void onChange(int id, Contact c);
        void onDelete(int id);
        void onChangeImagePath(int id, String path);
        void onToggleFavorite(int id, boolean isFavorite);
    }

    public void addContact(Contact c){
        int id = dbHelper.addContact(c);

        for (ContactChangeListener listener : listeners) {
            listener.onAdd(id);
        }
    }

    public void changeContact(Contact c){
        dbHelper.changeContact(c);

        for(ContactChangeListener listener : listeners){
            listener.onChange(c.getId(), c);
        }
    }

    public void changeContactFavoriteValue(int id, boolean isFavorite){
        dbHelper.changeFavoriteContactValue(id, isFavorite);
        for (ContactChangeListener listener : listeners) {
            listener.onToggleFavorite(id, isFavorite);
        }
    }

    public void changeImagePath(int id, String path){
        dbHelper.changeImagePath(id, path);
        for (ContactChangeListener listener : listeners) {
            listener.onChangeImagePath(id, path);
        }
    }

    public Contact getContact(int id){
        return dbHelper.getContact(id);
    }

    public void removeContact(int id){
        dbHelper.deleteContact(id);

        for (ContactChangeListener listener : listeners) {
            listener.onDelete(id);
        }
    }

    public void addListener(ContactChangeListener listener){
        listeners.add(listener);
    }

    public void removeListener(ContactChangeListener listener){
        listeners.remove(listener);
    }

    public ArrayList<Contact> getContactList(){
        return new ArrayList<>(dbHelper.getContactList());
    }

    public ArrayList<Contact> getFavoriteContacts(){
        return dbHelper.getFavoriteContacts();
    }
}