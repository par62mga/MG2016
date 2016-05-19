package com.pkrobertson.demo.mg2016;

/**
 * OnFragmentInteraction -- interface implemented by MainActivity to allow the news list fragment
 *     to show the selected news item. Also allows RecyclerView adapters to turn on/off
 *     menu options for Calendar, Call, Locate and Website
 */
public interface OnFragmentInteraction {
    void onNewsListInteraction(String newsItemUri);

    void disableMenuItems ();

    void showMenuItems ();

    void enableMenuItemCall (String phoneNumber);

    void enableMenuItemLocate (String locationName, String locationAddress, String mapLocation);

    void enableMenuItemWebsite (String websiteURL);

    void enableMenuItemCalendar (
            long startDate, long startTime, long endTime,
            String title, String description, String location);
}
