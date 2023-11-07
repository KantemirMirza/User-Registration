package com.example.registeruser.utility;

import jakarta.servlet.http.HttpServletRequest;

public class UrlUtil {
    public static String getRequestUrl(HttpServletRequest request){
        String appUrl = request.getRequestURL().toString();
        return appUrl.replace(request.getServletPath(), "");
    }
}
