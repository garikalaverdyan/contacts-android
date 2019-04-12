package com.garikalaverdyan.contacts.ui.adapter;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import com.garikalaverdyan.contacts.data.Contact;
import com.garikalaverdyan.contacts.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>
        implements Filterable {

    public interface ContactListAdapterListener {
        void onItemClick(Contact contact);
    }

    private ArrayList<Contact> contactsList = new ArrayList<>();
    private ArrayList<Contact> contactListFiltered;
    private ContactListAdapterListener clickListener;
    private boolean isFiltered = false;

    public ContactListAdapter() {
        contactListFiltered = new ArrayList<>();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contactListViewName) TextView name;
        @BindView(R.id.contactListViewCircle) TextView firstLetterOfTheText;

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
            String letter = contact.getName().toUpperCase();
            char c = letter.charAt(0);

            name.setText(contact.getName());
            firstLetterOfTheText.setText(String.valueOf(c));

            Drawable drawable = itemView.getContext().getResources().getDrawable(R.drawable.circle);

            String color = changeCircleBackgroundColor(c);
            if(color != null){
                drawable.mutate().setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN);
                firstLetterOfTheText.setBackground(drawable);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
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

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    public void addContact(Contact c) {
        contactsList.add(c);
    }
    public void addData(ArrayList<Contact> data) {
        contactsList.addAll(data);
        contactListFiltered.addAll(data);
    }

    public void removeContact(int index) {
        contactsList.remove(index);
    }

    public void setContact(int index, Contact contact) {
        contactsList.set(index, contact);
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

    private String changeCircleBackgroundColor(char letter){
        Character l = Character.toLowerCase(letter);
        char[] letters = new char[]{'a', 'b', 'c' ,'d', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        String[] colors = new String[]{"#C4E538", "#FFC312", "#12CBC4", "#FDA7DF", "#ED4C67", "#F79F1F",
                "#A3CB38", "#1289A7", "#D980FA", "#B53471", "#EE5A24", "#009432", "#0652DD", "#9980FA",
                "#833471", "#EA2027", "#006266", "#1B1464", "#5758BB", "#6F1E51",  "#2f3542", "#ffa502",
                "#b8e994", "#1e272e", "#d2dae2", "#0be881"};
        for(int counter = 0; counter < letters.length; counter ++){
            if(l.equals(letters[counter])){
                return colors[counter];
            }
        }
        return null;
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
}