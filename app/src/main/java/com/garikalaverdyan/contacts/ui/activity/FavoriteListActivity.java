package com.garikalaverdyan.contacts.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;
import com.garikalaverdyan.contacts.data.Contact;
import com.garikalaverdyan.contacts.data.ContactRepository;
import com.garikalaverdyan.contacts.App;
import com.garikalaverdyan.contacts.R;
import com.garikalaverdyan.contacts.ui.adapter.FavoriteContactsAdapter;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteListActivity extends AppCompatActivity implements FavoriteContactsAdapter.FavoriteListAdapterListener,
        ContactRepository.ContactChangeListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int REQUEST_CODE_CONTACT_INFO = 2;
    private static final int REQUEST_PHONE_CALL = 999;
    private FavoriteContactDataTask task;
    private FavoriteContactsAdapter favoriteContactsAdapter;
    @BindView(R.id.favorite_contacts_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipeFavoriteContacts) SwipeRefreshLayout favoriteContactsRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContactRepository().addListener(this);
        setContentView(R.layout.activity_favorite_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.Favorites);

        ButterKnife.bind(this);

        favoriteContactsAdapter = new FavoriteContactsAdapter();
        favoriteContactsAdapter.setClickListener(this);

        favoriteContactsRefresh.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(favoriteContactsAdapter);

        task = new FavoriteContactDataTask(getContactRepository());
        task.setListener(data -> {
            favoriteContactsAdapter.addData(data);
            favoriteContactsAdapter.notifyDataSetChanged();
        });
        task.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAdd(int id) {
        favoriteContactsAdapter.addContact(getContactRepository().getContact(id));
        favoriteContactsAdapter.notifyItemInserted(favoriteContactsAdapter.getItemCount() - 1);
        showToast("Contact added");
    }

    @Override
    public void onChange(int id, Contact c) {
        int index = favoriteContactsAdapter.getItemIndex(id);
        favoriteContactsAdapter.setContact(index, c);
        favoriteContactsAdapter.notifyItemChanged(index);
    }

    @Override
    public void onToggleFavorite(int id, boolean isFavorite) {
        if (!isFavorite) {
            int index = favoriteContactsAdapter.getItemIndex(id);
            favoriteContactsAdapter.removeContact(index);
            favoriteContactsAdapter.notifyItemRemoved(index);
        }else{
            favoriteContactsAdapter.addContact(getContactRepository().getContact(id));
            favoriteContactsAdapter.notifyItemInserted(favoriteContactsAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onDelete(int id) {
        int index = favoriteContactsAdapter.getItemIndex(id);
        favoriteContactsAdapter.removeContact(index);
        favoriteContactsAdapter.notifyItemRemoved(index);
    }

    @Override
    public void onItemCall(String number) {
        if (ActivityCompat.checkSelfPermission
                (FavoriteListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions
                    (FavoriteListActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            String temp = "tel:" + number;
            callIntent.setData(Uri.parse(temp));
            startActivity(callIntent);
        }
    }

    @Override
    public void onItemClick(Contact contact) {
        Intent intent = new Intent(FavoriteListActivity.this, ContactInfoActivity.class);
        intent.putExtra("contact_id", contact.getId());
        startActivityForResult(intent, REQUEST_CODE_CONTACT_INFO);
    }

    @Override
    public void onRefresh() {
        favoriteContactsAdapter.changeAdapterData(getContactRepository().getFavoriteContacts());
        favoriteContactsAdapter.notifyDataSetChanged();
        favoriteContactsRefresh.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContactRepository().removeListener(this);
        task.removeListener();
    }

    private ContactRepository getContactRepository() {
        return ((App) getApplication()).getContactRepository();
    }

    private void showToast(String textToShow) {
        Toast.makeText(this, textToShow, Toast.LENGTH_SHORT).show();
    }

    private static class FavoriteContactDataTask extends AsyncTask<Void, Void, ArrayList<Contact>> {
        interface ResultListener {
            void onResult(ArrayList<Contact> data);
        }

        private final ContactRepository contactRepository;
        private ResultListener listener;

        private void setListener(ResultListener listener) {
            this.listener = listener;
        }

        private void removeListener() {
            this.listener = null;
        }

        public FavoriteContactDataTask(ContactRepository contactRepository) {
            this.contactRepository = contactRepository;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Contact> doInBackground(Void... voids) {
            return contactRepository.getFavoriteContacts();
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> contacts) {
            super.onPostExecute(contacts);
            if (listener != null) {
                listener.onResult(contacts);
            }
        }
    }
}