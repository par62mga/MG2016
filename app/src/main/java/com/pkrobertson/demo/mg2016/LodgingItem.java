package com.pkrobertson.demo.mg2016;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Phil Robertson on 3/15/2016.
 */
public class LodgingItem implements Comparable {

    private static final String emptyString = "";

    private static List<LodgingItem> ITEMS = new ArrayList<LodgingItem>();

    private static HashMap<String, LodgingItem> lodgingItemDataBase = new HashMap<String,LodgingItem> ();

    private static int lodgingItemCounter = 0;

    private String   mLodgingItemNumber;
    private String   mLodgingItemTitle;
    private String   mLodgingItemAddress;
    private String   mLodgingItemPhone;
    private String   mLodgingItemDetail;

    public static LodgingItem getLodgingItemByNumber (String lodgingItemNumber) {
        return lodgingItemDataBase.get(lodgingItemNumber);
    }

    public static List<LodgingItem> getLodgingItemList () {
        Collections.sort(ITEMS);
        return ITEMS;
    }

    public static void putLodgingItem (LodgingItem lodgingItem) {
        if (lodgingItemDataBase.containsKey(lodgingItem.mLodgingItemNumber)) {
            for (int i = 0; i < ITEMS.size(); i++) {
                LodgingItem element = ITEMS.get(i);
                if ((element != null) &&
                        element.mLodgingItemNumber.contentEquals(lodgingItem.mLodgingItemNumber)) {
                    ITEMS.remove(i);
                    break;
                }
            }
        }

        ITEMS.add(lodgingItem);
        lodgingItemDataBase.put(lodgingItem.mLodgingItemNumber, lodgingItem);
    }

    public static void putLodgingItem (String   lodgingItemNumber,
                                       String   lodgingItemTitle,
                                       String   lodgingItemAddress,
                                       String   lodgingItemPhone,
                                       String   lodgingItemDetail) {
        LodgingItem lodgingItem = new LodgingItem (
                lodgingItemNumber, lodgingItemTitle, lodgingItemAddress, lodgingItemPhone, lodgingItemDetail);
        putLodgingItem(lodgingItem);
    }

    public static void createLodgingItem (String   lodgingItemTitle,
                                          String   lodgingItemAddress,
                                          String   lodgingItemPhone,
                                          String   lodgingItemDetail) {
        LodgingItem lodgingItem = new LodgingItem (
                String.valueOf(++lodgingItemCounter), lodgingItemTitle, lodgingItemAddress, lodgingItemPhone, lodgingItemDetail);
        putLodgingItem(lodgingItem);
    }

    public static boolean lodgingItemsEmpty () {
        return lodgingItemDataBase.isEmpty();
    }

    public LodgingItem (String   lodgingItemNumber,
                        String   lodgingItemTitle,
                        String   lodgingItemAddress,
                        String   lodgingItemPhone,
                        String   lodgingItemDetail) {
        mLodgingItemNumber  = lodgingItemNumber;
        mLodgingItemTitle   = lodgingItemTitle;
        mLodgingItemAddress = lodgingItemAddress;
        mLodgingItemPhone   = lodgingItemPhone;
        mLodgingItemDetail  = lodgingItemDetail;
    }

    @Override
    public int compareTo(Object object) {
        LodgingItem lodgingItem = (LodgingItem)object;
        return mLodgingItemNumber.compareTo(lodgingItem.mLodgingItemNumber);
    }

    @Override
    public String toString () {
        return mLodgingItemNumber + " " + mLodgingItemTitle + " " + mLodgingItemAddress + " " + mLodgingItemPhone;
    }

    @NonNull
    public String getLodgingItemNumber () {
        return nonNullString(mLodgingItemNumber);
    }

    @NonNull
    public String getLodgingItemTitle () {
        return nonNullString(mLodgingItemTitle);
    }

    @NonNull
    public String getLodgingItemAddress () {
        return nonNullString(mLodgingItemAddress);
    }

    @NonNull
    public String getLodgingItemPhone () {
        return nonNullString(mLodgingItemPhone);
    }

    @NonNull
    public String getLodgingItemDetail () {
        return nonNullString(mLodgingItemDetail);
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