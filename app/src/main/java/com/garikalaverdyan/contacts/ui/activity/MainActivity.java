package com.garikalaverdyan.contacts.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.garikalaverdyan.contacts.data.Contact;
import com.garikalaverdyan.contacts.data.ContactRepository;
import com.garikalaverdyan.contacts.ui.adapter.ContactListAdapter;
import com.garikalaverdyan.contacts.App;
import com.garikalaverdyan.contacts.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ContactListAdapter.ContactListAdapterListener,
        ContactRepository.ContactChangeListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.contactView)
    RecyclerView recyclerView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_activity_toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.floatingActionBar)
    FloatingActionButton addContactButton;
    public static final int REQUEST_CODE_ADD_BUTTON = 1;
    private ContactListAdapter contactAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private int selectedItemID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setTitle(R.string.Contacts);

        ButterKnife.bind(this);

        swipeRefreshLayout = findViewById(R.id.swipeContacts);
        swipeRefreshLayout.setOnRefreshListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactAdapter = new ContactListAdapter();
        contactAdapter.setClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        ContactsDataTask getData = new ContactsDataTask(MainActivity.this);
        getData.execute();

        getContactRepository().addListener(this);

        navigationView.setNavigationItemSelectedListener(item -> {
            selectedItemID = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (selectedItemID == R.id.nav_favorites) {
                    Intent intent = new Intent(MainActivity.this, FavoriteListActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @OnClick(R.id.floatingActionBar)
    void addButtonClick() {
        Intent intent = new Intent(MainActivity.this, AddAndEditActivity.class);
        intent.setAction(AddAndEditActivity.ACTION_ADD);
        startActivityForResult(intent, REQUEST_CODE_ADD_BUTTON);
    }

    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_contact_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnCloseListener(() -> {
            contactAdapter.changeAdapterData(getContactRepository().getContactList());
            contactAdapter.notifyDataSetChanged();
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contactAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_BUTTON && resultCode == RESULT_OK) {
            Contact c = data.getParcelableExtra("Contact");
            getContactRepository().addContact(c);
            onRefresh();
            showToast("Contact added");
        }
    }

    @Override
    public void onItemClick(Contact contact) {
        startActivity(ContactInfoActivity.createIntent(this, contact.getId()));
        contactAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAdd(int id) {
        int index = contactAdapter.getItemIndex(id);
        if (index != -1) {
            contactAdapter.setContact(index, getContactRepository().getContact(id));
            contactAdapter.notifyItemChanged(index);
        } else {
            contactAdapter.addContact(getContactRepository().getContact(id));
            contactAdapter.notifyItemInserted(contactAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onChange(int id, Contact c) {
        int index = contactAdapter.getItemIndex(id);
        contactAdapter.setContact(index, c);
        contactAdapter.notifyItemChanged(index);
    }

    @Override
    public void onToggleFavorite(int id, boolean isFavorite) {
    }

    @Override
    public void onDelete(int id) {
        int index = contactAdapter.getItemIndex(id);
        contactAdapter.removeContact(index);
        contactAdapter.notifyItemRemoved(index);
    }

    @Override
    public void onRefresh() {
        contactAdapter.changeAdapterData(getContactRepository().getContactList());
        contactAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContactRepository().removeListener(this);
    }

    private void showToast(String textToShow) {
        Toast.makeText(this, textToShow, Toast.LENGTH_SHORT).show();
    }

    private ContactRepository getContactRepository() {
        return ((App) getApplication()).getContactRepository();
    }

    private static class ContactsDataTask extends AsyncTask<Void, Void, ArrayList<Contact>> {
        private WeakReference<MainActivity> reference;

        public ContactsDataTask(MainActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Contact> doInBackground(Void... params) {
            return reference.get().getContactRepository().getContactList();
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> result) {
            super.onPostExecute(result);
            reference.get().contactAdapter.addData(result);
            reference.get().contactAdapter.notifyDataSetChanged();
        }
    }
}