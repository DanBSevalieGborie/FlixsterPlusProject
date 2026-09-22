# Flixster+ Part 2

Submitted by: **Dan Sevalie Gborie**

**Flixster+** is an Android app that browses The Movie Database. The home screen
pulls **this week's trending movies** into a horizontal carousel and **popular TV
series** into a vertical list underneath. Tapping any poster opens a details page
with information that isn't shown on the main screen.

Time spent: **6** hours spent in total

## Required Features

The following **required** functionality is completed:

- [x] **Make an API call to an endpoint of your choosing** (other than `now_playing`)
  - Main list uses `GET /3/trending/movie/week`
- [x] **Parse through JSON data and implement a RecyclerView to display all entries**
  - Parsed manually with `org.json` in `Movie.fromJson()` / `TvShow.fromJson()`
- [x] **Use Glide to load and display at least one image per entry**
  - Every list row loads its poster from `https://image.tmdb.org/t/p/w500/...`
- [x] **Details page:** clicking an entry opens a new Activity using **Intents**
- [x] **At least 3 new pieces of data on the details page** — the details screen adds
  the **overview**, **vote count**, **popularity score**, **original language**,
  **release / first-air date**, and the **backdrop image**, none of which appear in
  the main list (which shows only poster, title, rating and year).

## Stretch Features

The following **stretch** functionality is implemented:

- [x] **Another API call and RecyclerView** that lets the user browse different data
  - Second call is `GET /3/tv/popular`, shown in its own RecyclerView with its own
    adapter, model and row layout, so the screen browses both movies and TV shows.
- [x] **Rounded corners on the images using Glide transformations**
  - `.transform(CenterCrop(), RoundedCorners(radius))` in both adapters and on the
    details page poster.
- [x] **Shared element transition when the user clicks into the details of a movie**
  - Each poster gets a unique `transitionName`, and the detail Activity is launched
    with `ActivityOptionsCompat.makeSceneTransitionAnimation(...)` so the poster
    animates from the list into the details page (and back out via
    `finishAfterTransition()`).

The following **additional** features are implemented:

- [x] Pull-to-refresh on the home screen (SwipeRefreshLayout)
- [x] Error state with a "Try again" button when the network call fails
- [x] Movie and TvShow are `@Parcelize` Parcelables, so the whole object travels in
      the Intent instead of a dozen separate extras
- [x] Custom dark theme, custom app launcher icon, and placeholder art while images load

## Video Walkthrough

Here's a walkthrough of implemented features:

<img src='walkthrough.gif' title='Video Walkthrough' width='300' alt='Video Walkthrough' />

> Record this with the Android Studio screen recorder (Running Devices → camera icon),
> then convert to a GIF, drop `walkthrough.gif` in the repo root, and push.
> GIF created with [ScreenToGif](https://www.screentogif.com/) / [Kap](https://getkap.co/).

## Notes

Things I worked through while building this:

- **Two lists in one scrolling screen.** The vertical TV RecyclerView lives inside a
  `NestedScrollView`, so it needs `isNestedScrollingEnabled = false` or the inner list
  steals the scroll and only shows a couple of rows.
- **Shared element transitions across two data types.** The transition name has to
  match on both sides *and* be unique per row, so the names are built from the entry
  id (`poster_movie_123` / `poster_tv_456`) and passed along in the Intent.
- **One details Activity, two models.** `Movie` and `TvShow` share a small `MediaItem`
  interface, which let a single `DetailActivity` and a single layout serve both
  instead of duplicating the screen.
- **`getParcelableExtra` is deprecated on API 33+,** so the Activity checks
  `Build.VERSION.SDK_INT` and uses the typed overload on newer devices.

## Running the project

1. Unzip the folder and open it in Android Studio (**File → Open**, pick the
   `FlixsterPlus` folder — the one containing `settings.gradle.kts`).
2. Let Gradle sync; Android Studio downloads the Android SDK pieces it needs.
   No `local.properties` is included, Android Studio writes its own.
3. Run on an emulator or device with API 24+ and an internet connection.

Verified building: `./gradlew assembleDebug` produces `app-debug.apk`.

## Project structure

```
app/src/main/java/com/codepath/flixsterplus/
├── MainActivity.kt          # two RecyclerViews, both API calls, launches details
├── DetailActivity.kt        # details page, receives the entry through an Intent
├── adapters/
│   ├── MovieAdapter.kt      # horizontal trending-movies carousel
│   └── TvShowAdapter.kt     # vertical popular-TV list (stretch feature)
├── models/
│   ├── MediaItem.kt         # shared contract for the two models
│   ├── Movie.kt             # JSON parsing + Parcelable
│   ├── TvShow.kt            # JSON parsing + Parcelable
│   └── TmdbImage.kt         # builds the image CDN urls
└── network/
    └── TmdbClient.kt        # OkHttp calls on Dispatchers.IO
```

## License

    Copyright 2026 Dan Sevalie Gborie

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
