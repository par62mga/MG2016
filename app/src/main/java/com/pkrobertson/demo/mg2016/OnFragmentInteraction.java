package com.pkrobertson.demo.mg2016;

/**
 * OnFragmentInteraction -- interface implemented by MainActivity to allow the news list fragment
 *     to show the selected news item. Also allows RecyclerView adapters to turn on/off
 *     menu options for Calendar, Call, Locate and Website
 */
public interface OnFragmentInteraction {
    public void onNewsListInteraction(String newsItemUri);

    public void disableMenuItems ();

    public void enableMenuItemCall (String phoneNumber);

    public void enableMenuItemLocate (
            String locationName, String locationAddress, String mapLocation);

    public void enableMenuItemWebsite (String websiteURL);

    public void enableMenuItemCalendar (
            long startDate, long startTime, long endTime,
            String title, String description, String location);
}
