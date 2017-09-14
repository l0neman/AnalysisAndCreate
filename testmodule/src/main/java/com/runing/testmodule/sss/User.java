package com.runing.testmodule.sss;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DSI on 2017/9/13.
 */

public class User implements Parcelable {

  private String name;
  private String gender;
  private int age;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public User(String name, String gender, int age) {
    this.name = name;
    this.gender = gender;
    this.age = age;
  }

  protected User(Parcel in) {
    this.name = in.readString();
    this.gender = in.readString();
    this.age = in.readInt();
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

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.gender);
    dest.writeInt(this.age);
  }
}
