/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 *
 * @author Administrator
 */
public class UrlUtils {
    
    static final String SITE_URL = "http://mybwallet.com";
    
    public static URL getSiteUrl(Locale locale) throws MalformedURLException {
        return new URL(SITE_URL);
    }
    
    public static URL getHelpUrl(Locale locale) throws MalformedURLException {
        String language = locale.getLanguage();
        String url = SITE_URL + "/docs/help";
        if (language.equals("zh"))
            url += "/zh";
        else
            url += "/zh";
        return new URL(url);
    }
    
    public static URL getFaqUrl(Locale locale) throws MalformedURLException {
        String language = locale.getLanguage();
        String url = SITE_URL + "/docs/faq";
        if (language.equals("zh"))
            url += "/zh";
        else
            url += "/zh";
        return new URL(url);
    }
    
    public static URL getResourcesUrl(Locale locale) throws MalformedURLException {
        String language = locale.getLanguage();
        String url = SITE_URL + "/resources";
        if (language.equals("zh"))
            url += "/zh";
        else
            url += "/en";
        return new URL(url);
    }
    
    public static URL getBuyUrl(Locale locale) throws MalformedURLException {
        String language = locale.getLanguage();
        String url = null;
        if (language.equals("zh")) {
            url = "https://bidingxing.com/bwallet";
        } else {
            url = "https://bidingxing.com/en/store";
        }
        return new URL(url);
    }
    
}
