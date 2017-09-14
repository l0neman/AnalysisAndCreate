// UserManager.aidl
package com.runing.mytest;

// Declare any non-default types here with import statements
import com.runing.mytest.User;

interface UserManager {

    void setUser(in User user);

    User getUser();
}
