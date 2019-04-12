package com.garikalaverdyan.contacts.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Contact implements Parcelable{
    private int id;
    private String name, surname, number, gender, day, month, year, imagePath;
    private boolean isFavorite;

    public Contact(String name, String surname, String number, String gender,
                   String day, String month, String year, boolean isFavorite, String imagePath){
        this.name = name;
        this.surname = surname;
        this.number = number;
        this.gender = gender;
        this.day = day;
        this.month = month;
        this.year = year;
        this.isFavorite = isFavorite;
        this.imagePath = imagePath;
    }

    protected Contact(Parcel in) {
        id = in.readInt();
        name = in.readString();
        surname = in.readString();
        number = in.readString();
        gender = in.readString();
        day = in.readString();
        month = in.readString();
        year = in.readString();
        isFavorite = in.readByte() != 0;
        imagePath = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(number);
        dest.writeString(gender);
        dest.writeString(day);
        dest.writeString(month);
        dest.writeString(year);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeString(imagePath);
    }

    public String getImagePath()  {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getNumber() {
        return number;
    }

    public String getGender() {return gender; }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @SerializedName("isFavorite")
    public boolean isFavorite() {
        return isFavorite;
    }

    @SerializedName("isFavorite")
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}