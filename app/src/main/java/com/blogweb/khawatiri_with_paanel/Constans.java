package com.blogweb.khawatiri_with_paanel;

public class Constans {
    public static final String URL="https://api.khawatiri.com/";
    public static final String HOME=URL+"api";
    public static final String Login=HOME+"/login";
    public static final String LOGOUT=HOME+"/logout";
    public static final String Register=HOME+"/register";
    public static final String SAVE_USER_INFO=HOME+"/save_user_info";

    public static final String UPDATE_USER_INFO=HOME+"/update_user_info";
    public static final String QUOTES =HOME+"/quotes";
    public static final String CATEGORIES =HOME+"/categories";
    public static final String ADD_QUOTE =QUOTES+"/create";
    public static final String UPDATE_QUOTE =QUOTES+"/update";
    public static final String DELETE_QUOTE =QUOTES+"/delete";
    public static final String LIKE_QUOTE =QUOTES+"/like";
    public static final String QUOTE_COMMENTS = QUOTES+"/comments";
    public static final String ADD_COMMENT=HOME+"/comments/create";
    public static final String DELETE_COMMENT=HOME+"/comments/delete";
    public static final String MY_QUOTES=QUOTES+"/my_quotes";
}
