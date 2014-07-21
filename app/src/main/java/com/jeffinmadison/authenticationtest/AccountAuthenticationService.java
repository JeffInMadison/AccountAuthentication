package com.jeffinmadison.authenticationtest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AccountAuthenticationService extends Service {
    private static AccountAuthenticatorImpl sAccountAuthenticator;

    public AccountAuthenticationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticatorImpl authenticator = new AccountAuthenticatorImpl(this);
        return authenticator.getIBinder();
    }
}
