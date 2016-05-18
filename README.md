# Capstone Project - MG2016

## Background

Udacity Android Developer Nanodegree final project (presently under construction: task 12 complete).

**Project Requirements**

* Import and build on the latest version of Android Studio (Eclipse users take note).
* Be entirely self-contained on an Android device (No external devices/bluetooth peripherals).
* Include a problem description of the problem your app solves.
* Include mocks for all user-facing screens.
* Include a signed .apk of your app. Instructions on creating a signed .apk can be found here.
* Include at least one alternate mock for tablet / large screens.
* Implement all mockups, including your tablet layout.
* Have at least two distinct screens (ex. a list view and a detail view).
* Work properly with the app lifecycle (i.e. you must preserve any dynamic instance state on orientation change).
* Use permissions responsibly.
* Use Intents to move between activities inside your app or to an outside app.
* Create and use your own ContentProvider.
* Use Loaders to move your data to your views.
* If the application pulls or sends data to/from a web service or API, it handles this network activity properly (i.e on the correct thread, does not abuse network resources).
* Include only safe-for-work content in your app.

## MG2016 App Description

This app delivers content and information related to the MG 2016 national event. This event is the fifth national gathering of MG enthusiasts and is hosted by the North American Council of MG Registers.

With this app you can always stay up to date with MG 2016 activities:

* View the latest MG 2016 news, events and lodging details.
* Not miss out on any of the excitement with the “MG 2016 at a Glance” widget and the ability to export events to your calendar.
* See where you are, launch maps and find the way to your hotel and event locations.
* You can also share events and news with your fellow MG enthusiasts via Facebook, Twitter, etc.

**See doc/Capstone_Stage1.pdf for additional information** 

## Capstone Project Rubric - Required Components

To “meet specifications”, the app must fulfill all of the criteria listed in this section of the rubric.

### Common Project Requirements

* App conforms to common standards found in the Android Nanodegree General Project Guidelines.

### Core Platform Development

* App integrates a third-party library: 
**Picasso (and Google Maps).**
* App validates all input from servers and users. If data does not exist or is in the wrong format, the app logs this fact and does not crash.
**App handles no input and possibility of JSON from server being invalid.**
* App includes support for accessibility. That includes content descriptions,  navigation using a D-pad, and, if applicable, non-audio versions of audio cues.
**Content descriptions set for all images. Checked navigation flow and operation for consistency.**
* App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts.
**All "user text" is in strings.xml and RTL is supported in all layouts.**
* App provides a widget to provide relevant information to the user on the home screen.
**MG At a Glance widget is provided.**

### Google Play Services

* App integrates two or more Google services.
**Location and Maps.**
* Each service imported in the build.gradle is used in the app.
* If Location is used, the app customizes the user’s experience by using the device’s location.
**MapActivity shows both user location and destination on map (if user is a long way from the destination may need to zoom out to see this).**
* If Admob is used, the app displays test ads. If admob was not used, student meets specifications.
* If Analytics is used, the app creates only one analytics instance. If analytics was not used, student meets specifications.
* If Maps is used, the map provides relevant information to the user. If maps was not used, student meets specifications.
**MapActivity shows event title and information in the snippet.**
* If Identity is used, the user’s identity influences some portion of the app. If identity was not used, student meets specifications.

### Material Design

* App theme extends AppCompat.
**Yes**
* App uses an app bar and associated toolbars.
**Yes**
* App uses standard and simple transitions between activities.
**Yes**

### Building

* App builds from a clean repository checkout with no additional configuration.
**Yes**
* App builds and deploys using the installRelease Gradle task.
**Will be done in task 13**
* App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.
**Will be done in task 13**
* All app dependencies are managed by Gradle.
**Yes**

### Data Persistence

* App implements a ContentProvider to access locally stored data.
**See AppContentProvider.**
* Must implement at least one of the three:

If it regularly pulls or sends data to/from a web service or API, app updates data in its cache at regular intervals using a SyncAdapter. 
**See AppSyncAdapter.** OR

If it needs to pull or send data to/from a web service or API only once, or on a per request basis (such as a search application), app uses an IntentService to do so. OR

If it performs short duration, on-demand requests(such as search), app uses an AsyncTask.

* App uses a Loader to move its data to its views.

## Capstone Project Rubric - Optional Components

To receive “exceeds specifications”, the app must fully implement all of the criteria listed under at least two of the five categories
below (e.g. Notifications, ShareActionProvider, Broadcast Events, and Custom Views).
**Optional Components were not implemented.**

### Material Design

* App uses shared element transitions.
* App makes use of parallax scrolling when more two or more items must scroll in the same activity.

### Notifications

* Notifications do not contain advertising or content unrelated to the core function of the app.
* Notifications are persistent only if related to ongoing events (such as music playback or a phone call).
* Multiple notifications are stacked into a single notification object, where possible.
* App uses notifications only to indicate a context change relating to the user personally (such as an incoming message).
* App uses notifications only to expose information/controls relating to an ongoing event (such as music playback or a phone call).

### ShareActionProvider

* Uses ShareActionProvider to share content with an outside application.
* Makes use of Intent Extras to send rich content (i.e. a paragraph of content-specific text, a link and description, an image, etc).

### Broadcast Events

* App intercepts broadcast events.
* App responds to Broadcast events in a meaningful way.

### Custom Views

* App creates and uses a custom View.
* App uses a novel View that couldn’t sufficiently be satisfied by the core Views in Android.



