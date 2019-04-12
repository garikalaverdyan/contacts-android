package com.garikalaverdyan.contacts.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import com.garikalaverdyan.contacts.data.Contact;
import com.garikalaverdyan.contacts.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FavoriteContactsAdapter extends RecyclerView.Adapter<FavoriteContactsAdapter.FavoriteViewHolder>{

    public interface FavoriteListAdapterListener {
        void onItemCall(String number);
        void onItemClick(Contact contact);
    }

    private ArrayList<Contact> favoriteContactList;
    private FavoriteListAdapterListener clickListener;

    public FavoriteContactsAdapter() {
        favoriteContactList = new ArrayList<>();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.favoriteImageCall) ImageButton callButton;
        @BindView(R.id.favorite_contact_name) TextView nameTextView;
        @BindView(R.id.favorite_first_letter_of_the_name) TextView firstLetterOfTheNameTextView;

        public FavoriteViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener != null){
                        clickListener.onItemClick(getContact(getAdapterPosition()));
                    }
                }
            });
        }

        @OnClick(R.id.favoriteImageCall)
        void call(){
            if(clickListener != null){
                String n = getContact(getAdapterPosition()).getNumber();
                clickListener.onItemCall(n);
            }
        }

        public void setData(Contact contact){
            String letter = contact.getName().toUpperCase();
            char c = letter.charAt(0);

            nameTextView.setText(contact.getName());
            firstLetterOfTheNameTextView.setText(String.valueOf(c));
        }
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.favorite_list_view, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.setData(favoriteContactList.get(position));
    }

    @Override
    public int getItemCount() {
        return favoriteContactList.size();
    }

    public void removeContact(int index) {
        if(index != -1){
            favoriteContactList.remove(index);
        }

    }

    public void setContact(int index, Contact c){
        favoriteContactList.set(index, c);
    }

    public void addContact(Contact contact) {
        favoriteContactList.add(contact);
    }

    public int getItemIndex(int id) {
        for (int counter = 0; counter < favoriteContactList.size(); counter++) {
            if (favoriteContactList.get(counter).getId() == id) {
                return counter;
            }
        }
        return -1;
    }

    public void addData(ArrayList<Contact> contactList){
        favoriteContactList.addAll(contactList);
    }

    public Contact getContact(int position) {
        return favoriteContactList.get(position);
    }

    public void setClickListener(FavoriteListAdapterListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void changeAdapterData(ArrayList<Contact> favoriteList){
        this.favoriteContactList = favoriteList;
    }
}