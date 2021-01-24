# 98point6 Drop Token
This application is a simple "Connect 4" app that works on a 4x4 grid. Just as in Connect 4, a user drops tokens into columns in attempt to win.
A win is achieved when 4 tokens of the same color are placed in an unbroken line; this can be vertically, horizontally, or diagonally (ascending or descending). If all columns
are filled with tokens without the win condition criteria met, the game is a draw - no one wins.

This app currently supports only "single player" mode against an AI.

## Running the app
The Android Docs contain [extensive instructions on how to build and run your app](https://developer.android.com/studio/run).

You can either build the source code into an APK and [install the APK onto your device](https://www.androidauthority.com/how-to-install-apks-31494/) or you can run the app on a connected device/emulator using Android Studio.

If using Android Studio, pulling the source code onto your local machine with a default configuration of Android Studio will be sufficient. Sync the dependencies using Gradle using the instructions [here](https://stackoverflow.com/questions/29565263/android-studio-how-to-run-gradle-sync-manually), then use the default run configuration, selecting your emulator or device, to install and run the app.

## Key Features
This app relies on [Android Room](https://developer.android.com/training/data-storage/room?gclid=Cj0KCQiA0rSABhDlARIsAJtjfCf9iV0nBkkUBjQxqctBcvEOn1mal-ttzQi4PxenTnXyQi8-EgqXH1gaAjfYEALw_wcB&gclsrc=aw.ds) for persistence, [Retrofit](https://square.github.io/retrofit/) for RESTful network calls, and [Kotlin Flow](https://developer.android.com/kotlin/flow) and [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) to back the [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) in the [Android ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel?gclid=Cj0KCQiA0rSABhDlARIsAJtjfCfW3HnOfVw2jaRw09EFP0cMvvkWj9IaQ8rNKnaV1mZHM0zFgNo82b8aAtlbEALw_wcB&gclsrc=aw.ds) with the data layer. It makes use of the blueprint and best practices described in the [Android Architecture Components](https://developer.android.com/topic/libraries/architecture).

## Tests
Unit test coverage is in place for the database and service layers. At this time no UI tests exist for this app.




