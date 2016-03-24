package com.pkrobertson.demo.mg2016;

import android.app.Application;
import android.net.Uri;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private final String URI_PARM_CUSTOMER   = "cust";
    private final String URI_PARM_PTT_NUMBER = "ptt";
    private final String URI_PARM_PTT_NAME   = "name";
    private final String URI_PARM_EVENT      = "event";

    public ApplicationTest() {
        super(Application.class);
        Uri buildResult = testUriBuilder ();
        parseUri (buildResult);
        Log.d ("ApplicationTest()", "Tests Finished");
    }

    private Uri testUriBuilder () {
        Uri result = new Uri.Builder()
                .scheme("http")
                .authority("demo.wo.com")
                .path ("create")
                .appendQueryParameter(URI_PARM_CUSTOMER, "123456")
                .appendQueryParameter(URI_PARM_PTT_NUMBER, "6666600001")
                .appendQueryParameter(URI_PARM_PTT_NAME, "Dispatcher 01")
                .appendQueryParameter(URI_PARM_EVENT, "Alarm #209")
                .build();
        Log.d("testUriBuilder()", result.toString());
        return result;
        // http://demo.wo.com/create?cust=123456&ptt=6666600001&name=Dispatcher%2001
    }

    private void parseUri (Uri createUri) {
        String path           = createUri.getPath ();
        String customerNumber = createUri.getQueryParameter(URI_PARM_CUSTOMER);
        String pttNumber      = createUri.getQueryParameter(URI_PARM_PTT_NUMBER);
        String pttName        = createUri.getQueryParameter(URI_PARM_PTT_NAME);
        String eventType      = createUri.getQueryParameter(URI_PARM_EVENT);
        String testUnknownKey = createUri.getQueryParameter ("unknown");
        assertNull(testUnknownKey);
        Log.d ("parseUri()", "path ==> " + path +
                " customerNumber ==> " + customerNumber +
                " pttNumber ==> " + pttNumber +
                " pttName ==> " + pttName +
                " eventType ==> " + eventType);
    }
}