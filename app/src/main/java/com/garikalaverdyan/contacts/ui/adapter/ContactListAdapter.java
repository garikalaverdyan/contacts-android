package com.garikalaverdyan.contacts.ui.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.garikalaverdyan.contacts.data.Contact;
import com.garikalaverdyan.contacts.R;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>
        implements Filterable, FastScrollRecyclerView.SectionedAdapter {

    public interface ContactListAdapterListener {
        void onItemClick(Contact contact);
    }

    private ArrayList<Contact> contactsList = new ArrayList<>();
    private ArrayList<Contact> contactListFiltered;
    private ContactListAdapterListener clickListener;
    private boolean isFiltered = false;
    private Context context;

    public ContactListAdapter(Context context) {
        contactListFiltered = new ArrayList<>();
        this.context = context;
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contactListViewName)
        TextView name;
        @BindView(R.id.contactPhoto)
        ImageView imageView;
        @BindView(R.id.contactListViewTel)
        TextView telView;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(getContact(getAdapterPosition()));
                }
            });
        }

        public void setData(Contact contact) {
            String path = contact.getImagePath();
            File f;

            name.setText(contact.getName());
            telView.setText(contact.getNumber());

            if(path != null){
                f = new File(contact.getImagePath());
            }else{
                f = null;
            }

            Glide.with(context)
                    .load(f)
                    .placeholder(R.drawable.ic_face)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }
    }

    @NonNull
    @Override
    public ContactListAdapter.ContactViewHolder onCreateViewHolder
            (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.contacts_list_view, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        if(isFiltered){
            holder.setData(contactsList.get(position));
        }else{
            holder.setData(contactListFiltered.get(position));
        }
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(contactsList.get(position).getName().toUpperCase().charAt(0));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                Log.e("contact list size", String.valueOf(contactsList.size()));
                Log.e("Filtered list size", String.valueOf(contactListFiltered.size()));
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    isFiltered = true;
                    contactListFiltered = contactsList;
                }else{
                    isFiltered = false;
                    ArrayList<Contact> filteredList = new ArrayList<>();
                    for(Contact row: contactsList){
                        if(row.getName().toLowerCase().startsWith(charString.toLowerCase()) || row.getNumber().startsWith(String.valueOf(charSequence))){
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactListFiltered = new ArrayList<>();
                Object values = results.values;
                if(values instanceof ArrayList){
                    for (Object a : (ArrayList)values){
                        if(a instanceof Contact){
                            contactListFiltered.add((Contact) a);
                        }
                    }
                }

                changeAdapterData(contactListFiltered);
                notifyDataSetChanged();
            }
        };
    }

    public void addContact(Contact c) {
        contactsList.add(c);
    }

    public void addData(ArrayList<Contact> data) {
        contactsList.addAll(data);
        contactListFiltered.addAll(data);
    }

    public void changeImagePath(int index, String path){
        contactsList.get(index).setImagePath(path);
        contactListFiltered.get(index).setImagePath(path);
    }

    public void removeContact(int index) {
        contactsList.remove(index);
    }

    public void setContact(int index, Contact contact) {
        contactsList.set(index, contact);
        contactListFiltered.set(index, contact);
    }

    public int getItemIndex(int id) {
        for (int counter = 0; counter < contactsList.size(); counter++) {
            if (contactsList.get(counter).getId() == id) {
                return counter;
            }
        }
        return -1;
    }

    public Contact getContact(int position) {
        return contactsList.get(position);
    }

    public void setClickListener(ContactListAdapterListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void changeAdapterData(ArrayList<Contact> contactList) {
        this.contactListFiltered = contactList;
    }
}