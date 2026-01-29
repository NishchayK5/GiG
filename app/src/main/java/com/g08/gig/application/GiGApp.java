package com.g08.gig.application;

import android.app.Application;

import com.g08.gig.business.services.UserService;
import com.g08.gig.business.services.UserServiceImpl;
import com.g08.gig.persistence.UserRepository;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.persistence.real.user.SqlUserRepository;

public class GiGApp extends Application {

    private UserService userService;

    @Override
    public void onCreate() {
        super.onCreate();

        UserRepository repo = new SqlUserRepository(new AppDbHelper(getApplicationContext()));
        userService = new UserServiceImpl(repo);
    }

    public UserService getUserService() {
        return userService;
    }
}
