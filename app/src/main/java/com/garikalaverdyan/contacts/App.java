package com.garikalaverdyan.contacts;

import android.app.Application;

import com.garikalaverdyan.contacts.data.ContactRepository;
import com.garikalaverdyan.contacts.data.DefaultContactsDataBaseHelper;

public class App extends Application {
    private ContactRepository contactRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        contactRepository = new ContactRepository(new DefaultContactsDataBaseHelper(this));
    }

    public ContactRepository getContactRepository(){
        return contactRepository;
    }
}