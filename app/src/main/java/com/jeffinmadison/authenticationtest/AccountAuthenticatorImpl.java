package com.jeffinmadison.authenticationtest;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/**
* Created by Jeff on 7/18/14.
* Copyright (c) 2014 Broadcast Interactive Media, LLC
*/
class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {
    private static final String TAG = AccountAuthenticatorImpl.class.getSimpleName();

    private final Context mContext;

    public AccountAuthenticatorImpl(final Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(final AccountAuthenticatorResponse accountAuthenticatorResponse, final String s) {
        return null;
    }

    @Override
    public Bundle addAccount( AccountAuthenticatorResponse response,
                              String accountType,
                              String authTokenType,
                              String[] requiredFeatures,
                              Bundle options) throws NetworkErrorException {

        Log.d(TAG, "addAccount");
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(final AccountAuthenticatorResponse accountAuthenticatorResponse, final Account account, final Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "getAuthToken");

        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_VIP)) {

            //TODO non VIP return null?
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);

        Log.d(TAG, "peekAuthToken returned - " + authToken);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                try {
                    Log.d(TAG, "re-authenticating with the existing password");
//                    authToken = sServerAuthenticate.userSignIn(account.name, password, authTokenType);
                    authToken = MainActivity.dummyAuthCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(final String authTokenType) {
        if (AccountGeneral.AUTHTOKEN_TYPE_VIP.equals(authTokenType)) {
            return AccountGeneral.AUTHTOKEN_TYPE_VIP_LABEL;
        }
        else {
            return authTokenType + " (Label)";
        }
    }

    @Override
    public Bundle updateCredentials(final AccountAuthenticatorResponse accountAuthenticatorResponse, final Account account, final String s, final Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(final AccountAuthenticatorResponse accountAuthenticatorResponse, final Account account, final String[] strings) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(android.accounts.AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
