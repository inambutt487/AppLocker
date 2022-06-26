package com.applocker.app.Interface;

public interface AppConfigListener {

    void onSuccess(String Message, int status);

    void onFailed(String Message, int status);

    void onAskQuestion(String Message, int status);

    void onSetPassword(String Message, int status);

    void onPermission(String Message, int Position, PermissionListener permissionListener);

}
