package bjtu.cit.yue.Entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zachary on 15/11/6.
 */
public class User implements Parcelable{
    private int id;
    private String username;
    private String phone;
    private String gender;
    private String pic;

    public User(){

    }

    protected User(Parcel in) {
        id = in.readInt();
        username = in.readString();
        phone = in.readString();
        gender = in.readString();
        pic = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(pic);
    }
}
