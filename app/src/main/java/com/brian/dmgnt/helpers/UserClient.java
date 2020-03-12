package com.brian.dmgnt.helpers;

import android.app.Application;

import com.brian.dmgnt.models.User;

public class UserClient extends Application {

    private User mUser = null;

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }
}
