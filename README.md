# Foursquare Demo [![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)

## A Foursquare Search Engine with REST API, Retrofit, Kotlin.

## About

This application is about gathering the list of Foursquare venues close to the current location and address, and display them on a map. The data are fetched from the [Foursquare API](https://developer.foursquare.com/).

## Features

Application Functionality:
  
- Gets device location and display it using Google maps.
- When user moves the map, a search request for the closest coffee shops is sent based on current center position of the map.
- Results are displayed as Markers on the map.
- When user clicks on a Marker, a view is displayed with short info.
- When the info view is clicked, a second screen appears with detailed info.
- User is able to make direct phone call to the chosen store.

### Dependencies

- [Retrofit2](https://square.github.io/retrofit/)
- [OkHttp](http://square.github.io/okhttp/)
- [Picasso](http://square.github.io/picasso/)
- [Google Play Services](https://developers.google.com/android/guides/setup)
- [Google Places API](https://developers.google.com/places/web-service/intro)
- [Lifecycle Components (ViewModel, LiveData)](https://developer.android.com/jetpack/androidx/releases/lifecycle)
- [Circlular ImageView](https://github.com/hdodenhof/CircleImageView)
- [Runtime Permissions for Kotlin and AndroidX](https://github.com/afollestad/assent)

## How to run the application
You have to create your own Foursquare API credentials. Simply put clientID="YOUR_CLIENT_ID" and clientSecret="YOUR_CLIENT_SECRET" inside gradle.properties. A Google API key is also required inside gradle.properties.


## Screenshots
![](/app/screenshots/1.png) ![](/app/screenshots/2.png)
![](/app/screenshots/3.png) ![](/app/screenshots/4.png)
![](/app/screenshots/5.png) ![](/app/screenshots/6.png)