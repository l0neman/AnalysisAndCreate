// UserManager.aidl
package com.runing.testmodule;

// Declare any non-default types here with import statements
import com.runing.testmodule.User;

interface UserManager {

    void setUser(in User user);

    User getUser();
}
