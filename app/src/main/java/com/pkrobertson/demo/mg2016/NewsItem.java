package com.pkrobertson.demo.mg2016;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Phil Robertson on 11/30/2015.
 */
public class NewsItem implements Comparable {

    private static final String emptyString = "";

    private static  List<NewsItem> ITEMS = new ArrayList<NewsItem>();

    private static HashMap<String, NewsItem> newsItemDataBase = new HashMap<String,NewsItem> ();
	
	private static int newsItemCounter = 0;

    private String   mNewsItemNumber;
    private String   mNewsItemDate;
	private String   mNewsItemTitle;
	private String   mNewsItemSubtitle;
    private String   mNewsItemContent;

    public static NewsItem getNewsItemByNumber (String newsItemNumber) {
        return newsItemDataBase.get(newsItemNumber);
    }

    public static List<NewsItem> getNewsItemList () {
        Collections.sort(ITEMS);
        return ITEMS;
    }

    public static void putNewsItem (NewsItem newsItem) {
        if (newsItemDataBase.containsKey(newsItem.mNewsItemNumber)) {
            for (int i = 0; i < ITEMS.size(); i++) {
                NewsItem element = ITEMS.get(i);
                if ((element != null) && 
				    element.mNewsItemNumber.contentEquals(newsItem.mNewsItemNumber)) {
                    ITEMS.remove(i);
                    break;
                }
            }
        }

        ITEMS.add(newsItem);
        newsItemDataBase.put(newsItem.mNewsItemNumber, newsItem);
    }

    public static void putNewsItem (String   newsItemNumber,
                                    String   newsItemDate,
									String   newsItemTitle,
									String   newsItemSubtitle,
									String   newsItemContent) {
        NewsItem newsItem = new NewsItem (
                newsItemNumber, newsItemDate, newsItemTitle, newsItemSubtitle, newsItemContent);
        putNewsItem(newsItem);
    }
	
	public static void createNewsItem (String   newsItemDate,
									   String   newsItemTitle,
									   String   newsItemSubtitle,
									   String   newsItemContent) {
        NewsItem newsItem = new NewsItem (
                String.valueOf(++newsItemCounter), newsItemDate, newsItemTitle, newsItemSubtitle, newsItemContent);
        putNewsItem(newsItem);
    }

    public static boolean newsItemsEmpty () {
        return newsItemDataBase.isEmpty();
    }

    public NewsItem (String   newsItemNumber,
                     String   newsItemDate,
					 String   newsItemTitle,
					 String   newsItemSubtitle,
					 String   newsItemContent) {
        mNewsItemNumber   = newsItemNumber;
        mNewsItemDate     = newsItemDate;
        mNewsItemTitle    = newsItemTitle;
        mNewsItemSubtitle = newsItemSubtitle;
        mNewsItemContent  = newsItemContent;
    }

    @Override
    public int compareTo(Object object) {
        NewsItem newsItem = (NewsItem)object;
        return mNewsItemNumber.compareTo(newsItem.mNewsItemNumber);
    }

    @Override
    public String toString () {
        return mNewsItemNumber + " " + mNewsItemDate + " " + mNewsItemTitle;
    }

    @NonNull
    public String getNewsItemNumber () {
        return nonNullString(mNewsItemNumber);
    }

    @NonNull
    public String getNewsItemDate () {
        return nonNullString(mNewsItemDate);
    }
	
	@NonNull
    public String getNewsItemTitle () {
        return nonNullString(mNewsItemTitle);
    }
	
	@NonNull
    public String getNewsItemSubtitle () {
        return nonNullString(mNewsItemSubtitle);
    }

    @NonNull
    public String getNewsItemContent () {
        return nonNullString(mNewsItemContent);
    }

    @NonNull
    private String nonNullString (String sourceString) {
        if (sourceString == null) {
            return emptyString;
        } else {
            return sourceString;
        }
    }
}
