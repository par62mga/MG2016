package com.pkrobertson.demo.mg2016;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Phil Robertson on 3/14/2016.
 */
public class EventItem implements Comparable {

    private static final String emptyString = "";

    private static List<EventItem> ITEMS = new ArrayList<EventItem>();

    private static HashMap<String, EventItem> eventItemDataBase = new HashMap<String,EventItem> ();

    private static int eventItemCounter = 0;

    private String   mEventItemNumber;
    private String   mEventItemStart;
    private String   mEventItemEnd;
    private String   mEventItemTitle;
    private String   mEventItemSubtitle;

    public static EventItem getEventItemByNumber (String eventItemNumber) {
        return eventItemDataBase.get(eventItemNumber);
    }

    public static List<EventItem> getEventItemList () {
        Collections.sort(ITEMS);
        return ITEMS;
    }

    public static void putEventItem (EventItem eventItem) {
        if (eventItemDataBase.containsKey(eventItem.mEventItemNumber)) {
            for (int i = 0; i < ITEMS.size(); i++) {
                EventItem element = ITEMS.get(i);
                if ((element != null) &&
                        element.mEventItemNumber.contentEquals(eventItem.mEventItemNumber)) {
                    ITEMS.remove(i);
                    break;
                }
            }
        }

        ITEMS.add(eventItem);
        eventItemDataBase.put(eventItem.mEventItemNumber, eventItem);
    }

    public static void putEventItem (String   eventItemNumber,
                                     String   eventItemStart,
                                     String   eventItemEnd,
                                     String   eventItemTitle,
                                     String   eventItemSubtitle) {
        EventItem eventItem = new EventItem (
                eventItemNumber, eventItemStart, eventItemEnd, eventItemTitle, eventItemSubtitle);
        putEventItem(eventItem);
    }

    public static void createEventItem (String   eventItemStart,
                                        String   eventItemEnd,
                                        String   eventItemTitle,
                                        String   eventItemSubtitle) {
        EventItem eventItem = new EventItem (
                String.valueOf(++eventItemCounter), eventItemStart, eventItemEnd, eventItemTitle, eventItemSubtitle);
        putEventItem(eventItem);
    }

    public static boolean eventItemsEmpty () {
        return eventItemDataBase.isEmpty();
    }

    public EventItem (String   eventItemNumber,
                      String   eventItemStart,
                      String   eventItemEnd,
                      String   eventItemTitle,
                      String   eventItemSubtitle) {
        mEventItemNumber   = eventItemNumber;
        mEventItemStart    = eventItemStart;
        mEventItemEnd      = eventItemEnd;
        mEventItemTitle    = eventItemTitle;
        mEventItemSubtitle = eventItemSubtitle;
    }

    @Override
    public int compareTo(Object object) {
        EventItem eventItem = (EventItem)object;
        return mEventItemNumber.compareTo(eventItem.mEventItemNumber);
    }

    @Override
    public String toString () {
        return mEventItemNumber + " " + mEventItemStart + "-" + mEventItemEnd + " " + mEventItemTitle;
    }

    @NonNull
    public String getEventItemNumber () {
        return nonNullString(mEventItemNumber);
    }

    @NonNull
    public String getEventItemStart () {
        return nonNullString(mEventItemStart);
    }

    @NonNull
    public String getEventItemEnd () {
        return nonNullString(mEventItemEnd);
    }

    @NonNull
    public String getEventItemTitle () {
        return nonNullString(mEventItemTitle);
    }

    @NonNull
    public String getEventItemSubtitle () {
        return nonNullString(mEventItemSubtitle);
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

