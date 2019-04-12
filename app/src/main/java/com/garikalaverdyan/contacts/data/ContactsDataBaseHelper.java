package com.garikalaverdyan.contacts.data;

import java.util.ArrayList;

public interface ContactsDataBaseHelper {
    Contact getContact(int id);
    ArrayList<Contact> getContactList();
    ArrayList<Contact> getFavoriteContacts();
    int addContact(Contact contact);
    void changeContact(Contact contact);
    void deleteContact(int id);
    void changeFavoriteContactValue(int id, boolean isFavorite);
    void changeImagePath(int id, String path);
}