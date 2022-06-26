package com.applocker.app.Config;

import com.applocker.app.BuildConfig;

public class AppConfig {

    public static final String PURCHASE = "purchase";
    public static final String OWN = "owned";

    public static final String IN_APP = "in_APP";

    public static final String LOCK_IS_INIT_FAVITER = "lock_is_init_faviter";//boolean
    public static final String LOCK_IS_INIT_DB = "lock_is_init_db";//boolean

    public static final String LOCK_STATE = "app_lock_state";//boolean
    public static final String LOCK_FAVITER_NUM = "lock_faviter_num";//int
    public static final String LOCK_AUTO_SCREEN = "lock_auto_screen";//boolean
    public static final String LOCK_AUTO_SCREEN_TIME = "lock_auto_screen_time"; //boolean
    public static final String LOCK_CURR_MILLISECONDS = "lock_curr_milliseconds";//long
    public static final String LOCK_APART_MILLISECONDS = "lock_apart_milliseconds";//long
    public static final String LOCK_LAST_LOAD_PKG_NAME = "last_load_package_name";//string
    public static final String LOCK_APART_TITLE = "lock_apart_title";//string

    public static final String LOCK_PACKAGE_NAME = "lock_package_name";
    public static final String LOCK_FROM = "lock_from";
    public static final String LOCK_FROM_FINISH = "lock_from_finish";
    public static final String LOCK_FROM_SETTING = "lock_from_setting";
    public static final String LOCK_FROM_UNLOCK = "lock_from_unlock";

    public static final String APP_PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    public static final String PASSWORD = "password";
    public static final String IS_PASSWORD_SET = "is_password_set";

    public static final String PATTERN = "pattern";
    public static final String PATTERN_VISIBLE = "pattern_visibility";
    public static final String IS_PATTERN_SET = "is_pattern_set";
    public static final String IS_PATTERN_SKIP = "is_pattern_skip";

    public static final String LOCK = "lock";
    public static final String LOCK_SETTING = "lock_setting";
    public static final String SPY = "spy";

    public static final String VIBRATE = "vibrate";
    public static final String FINGER = "finger";
    public static final String FINGER_SHOW = "finger_show";

    public static final String ANSWER = "answer";
    public static final String QUESTION_NUMBER = "question_number";

    public static final String SET_APP = "set_app";
    public static final String LOAD_QUESTION = "load_question";
    public static final String SETTING = "setting";
    public static final String IS_SETTING = "is_setting";

    public static final String UNINSTALL_APP = "uninstall_app";
    public static final String CLEAR_CACHE = "clear_cache";

    public static final String NEW_APP_INSTALL = "new_app_install";
    public static final String FIRST_TIME_APP_INSTALL = "first_time_install_app";

    //Data
    public static final String IMAGE = "image";
    public static final String CUSTOM_APP = "custom_app";
    public static final String SET_CUSTOME = "set_custom";
    public static final String DATA = "set_data";
}
