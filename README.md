> **Fork mantenido por [HUEVOMAN77](https://github.com/HUEVOMAN77)** — proyecto original de [Spikeysanju/Einsen](https://github.com/Spikeysanju/Einsen) (Apache 2.0).
>
> ### Novedades de este fork (v1.1.0)
> - Migrado a Gradle 8.9, AGP 8.7, Kotlin 2.0, Compose BOM 2024.12, KSP, Hilt 2.52, Room 2.6, `compileSdk/targetSdk 35`.
> - Navegación con `navigation-compose` 2.8 y bottom sheets oficiales (se eliminaron APIs de Accompanist ya retiradas).
> - Sin Firebase: la analítica ahora es local (solo Logcat en debug), nada sale del dispositivo.
> - Permiso de notificaciones para Android 13+, recordatorios corregidos (antes se programaban con `id = 0`) y notificación que abre la app.
> - Corregido cierre inesperado al borrar una tarea, y colectores de base de datos duplicados en cada recomposición.
> - Soporte edge-to-edge de Android 15 y teclado (`adjustResize`).
> - Traducción completa al español.
> - CI en GitHub Actions: pruebas unitarias, prueba E2E en emulador y APK debug + release firmado como artefactos; los tags `v*` publican un Release con el APK.
>
> **Descargar APK:** pestaña [Releases](https://github.com/HUEVOMAN77/Einsen/releases) o los artefactos del último run en [Actions](https://github.com/HUEVOMAN77/Einsen/actions).
>
> **Compilar localmente:** JDK 17 + Android SDK 35 → `./gradlew assembleDebug`. Para release firmado define `EINSEN_KEYSTORE_FILE`, `EINSEN_KEYSTORE_PASSWORD`, `EINSEN_KEY_ALIAS` y `EINSEN_KEY_PASSWORD` (variables de entorno o `keystore.properties`).

![GitHub Cards Preview](https://github.com/Spikeysanju/Einsen/blob/master/art/einsen_cover.png?raw=true)

![Build (Android)](https://github.com/Spikeysanju/Einsen/workflows/Android%20CI/badge.svg)
[![GitHub license](https://img.shields.io/badge/License-Apache2.0-blue.svg)](LICENSE)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

![GitHub stars](https://img.shields.io/github/stars/SpikeySanju/Einsen?style=social)
![GitHub stars](https://img.shields.io/github/forks/SpikeySanju/Einsen?style=social)
![GitHub stars](https://img.shields.io/github/watchers/SpikeySanju/Einsen?style=social)
![GitHub follow](https://img.shields.io/github/followers/SpikeySanju?label=Follow&style=social)
![Twitter Follow](https://img.shields.io/twitter/follow/sanjay_spikey?label=Twitter&style=social)

# 🎯 Einsen

 Einsen is a prioritization app that uses Eisenhower matrix technique as workflow to prioritize a list of tasks & built to Demonstrate use of Jetpack Compose with Modern Android Architecture Components & MVVM Architecture.<br />

***Try latest Einsen app apk from below 👇***

[![Einsen](https://img.shields.io/badge/Einsen-APK-black.svg?style=for-the-badge&logo=android)](https://github.com/Spikeysanju/Einsen/releases/download/v1.0.0-alpha04/Einsen.apk)

<br />

## 🎨 UI Design

***Click to View `Einsen` app Design from below 👇***

[![Einsen Figma Design](https://img.shields.io/badge/Einsen-FIGMA-black.svg?style=for-the-badge&logo=figma)](https://www.figma.com/file/s0jhjixtXtOZmmIyHIN3Yy/Einsen?node-id=0%3A1)
[![Einsen FigJam Design](https://img.shields.io/badge/Einsen-FIJAM-black.svg?style=for-the-badge&logo=figma)](https://www.figma.com/file/dbEZWjbmog9vYtsZx08fFM/Einsen)


<br />

## 🌞 Day Mode

|   Dashboard    | All Tasks    |   Task Details   
|---	|---	|---
|  ![](https://github.com/Spikeysanju/Einsen/blob/master/art/dashboard_day.png)    |  ![](https://github.com/Spikeysanju/Einsen/blob/master/art/all_task_day.png)    |   ![](https://github.com/Spikeysanju/Einsen/blob/master/art/task_details_day_v2.png)    

|   Add Task  |   Emoji    | Empty State    |
|---    |---	|---	|
|   ![](https://github.com/Spikeysanju/Einsen/blob/master/art/add_task_day.png)    |   ![](https://github.com/Spikeysanju/Einsen/blob/master/art/choose_emoji_day.png)      |   ![](https://github.com/Spikeysanju/Einsen/blob/master/art/empty_state_day.png)

<br />

## 🌚 We Support Dark Mode Too

|   Dashboard    | All Tasks    |   Task Details      
|---	|---	|---		
|  ![](https://github.com/Spikeysanju/Einsen/blob/master/art/dashboard_night.png)    |  ![](https://github.com/Spikeysanju/Einsen/blob/master/art/all_task_night.png)    |   ![](https://github.com/Spikeysanju/Einsen/blob/master/art/task_details_night.png)      

|   Add Task  |   Emoji   | Empty State    |
|---    |---	|---	|
|   ![](https://github.com/Spikeysanju/Einsen/blob/master/art/add_task_night.png)  |   ![](https://github.com/Spikeysanju/Einsen/blob/master/art/choose_emoji_night.png)     |   ![](https://github.com/Spikeysanju/Einsen/blob/master/art/empty_state_night.png)

<br />

## 🛠 Built With

- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android
  development.
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Jetpack Compose is Android’s
  modern toolkit for building native UI.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - A coroutine is a
  concurrency design pattern that you can use on Android to simplify code that executes
  asynchronously.
- [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - A flow is an asynchronous
  version of a Sequence, a type of collection whose values are lazily produced.
- [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) -
  Jetpack DataStore is a data storage solution that allows you to store key-value pairs or typed
  objects with protocol buffers. DataStore uses Kotlin coroutines and Flow to store data
  asynchronously, consistently, and transactionally.
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) -
  Collection of libraries that help you design robust, testable, and maintainable apps.
    - [Stateflow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) - StateFlow is
      a state-holder observable flow that emits the current and new state updates to its collectors.
    - [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - A flow is an asynchronous
      version of a Sequence, a type of collection whose values are lazily produced.
    - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores
      UI-related data that isn"t destroyed on UI changes.
    - [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation) - The
      Navigation component provides support for Jetpack Compose applications.
    - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Jetpack
      DataStore is a data storage solution that allows you to store key-value pairs or typed objects
      with protocol buffers. DataStore uses Kotlin coroutines and Flow to store data asynchronously,
      consistently, and transactionally.
- [Material Components for Android](https://github.com/material-components/material-components-android)
    - Modular and customizable Material Design UI components for Android.
- [Accompanist](https://github.com/google/accompanist)
    - A collection of extension libraries for Jetpack Compose.
- [Figma](https://figma.com/) - Figma is a vector graphics editor and prototyping tool which is
  primarily web-based.

<br />

## 📦 Package Structure

 ```
dev.spikeysanju.einsen
├── app                   # Application class
├── components            # All resuable components for this app
├── data                  # For data handling
│   ├── local               # Local Persistence Database. Room (SQLite) database
│   │   ├── Dao               # Data Access Object for Room
│   │   └── Database          # Database Instance
│   └── datastore
│       └── ThemePref         # Datastore Theme Preference 
├── di                        # Hilt DI Modules
├── model                     # Model class for [Task] & [Emoji]
├── navigation                # For navigation handling
│   ├── Routes                # All unique navigation routes of this app
│   └── NavGraph              # Single source for Navigation Routes of this app
├── repository                # Used to handle all data operations
├── ui.theme                  # Theme setup for this app
├── utils                     # Extension functions
├── view                      # All composables screens root folder
│   ├── add                   # Add Task Screen
│   ├── edit_task             # Edit Task Screen
│   ├── dashboard             # Dashboard Screen
│   ├── all_task              # All Task Screen
│   ├── task_details          # Task Details Screen
│   ├── emoji                 # Choose Emoji Screen
│   ├── webview               # WebView Screen
│   ├── animation             # Animation Placeholders for State handling(Loading, Error, Empty etc.)
│   ├── about                 # About Screen
│   └── viewmodel             # ViewModel 
└── MainActivity.kt           # MainActivity 

```

<br />

## 🗼 Architecture

This app uses [***MVVM (Model View
View-Model)***](https://developer.android.com/jetpack/docs/guide#recommended-app-arch) architecture.

![](https://github.com/TheCodeMonks/Notes-App/blob/master/screenshots/ANDROID%20ROOM%20DB%20DIAGRAM.jpg)

## 🧰 Build-tool

You need to
have [Android Studio Beta 3 (BumbleBee) or above](https://developer.android.com/studio/preview) to
build this project.
<br>

<img src="./beta_android.png" height="200" alt="Beta-studio"/>


<br>

## 💬 Frequently Asked Questions

<details open> <summary> When was project Einsen started? </summary>  

> 🎯 I started working on this idea 6 months before founding `Einsen app` (April 2021). The idea was exploring `mental models` + `productivity tips` to achieve your `goals`. I tried to test the idea with `Productivity` & `Time Management Trackers` . So I built `initial MVP`..

</details>

<details> <summary> Where can I find the research process for Einsen app?</summary>  

> 📝 The biggest lesson I learned while designing this was that, I need to be `focused` & `working relentlessly` on the same project, to get the right flow of thought & complete it. Each `iteration` led to more `insights` on improving the design, `solving problems` for users. You can find the link for 📝 Design Process [here](https://www.figma.com/file/dbEZWjbmog9vYtsZx08fFM/Einsen) & 🎨 Figma Designs [here](https://www.figma.com/file/s0jhjixtXtOZmmIyHIN3Yy/Einsen?node-id=0%3A1)

</details>

<details> <summary> What is the future goals of this project Einsen? </summary>  

> 🦄 For each week I will be implementing some new cool features for this application. Here are some of the amazing features you will come to expect from this platform :point_down:

- `Due Date` for each task
- `Recurring task`
- `Pomodoro timer` for each task
- `Task Template` as per user preference
- `Calendar` based task management
- `Project management`
- `Statistics` for completed task
- Introducing new `mental model templates` like Eisenhower

</details>

<br />

## 🤝 Contribute

If you want to contribute to this app, you're always welcome!
See [Contributing Guidelines](https://github.com/Spikeysanju/Einsen/blob/master/CONTRIBUTING.md).

<br>

## 📩 Contact

I know that first and foremost you are looking for a tool to solve your problems, but if you enjoy
it that much, why not tell us? We would love to hear from you 😉

DM me at 👇

* Twitter: <a href="https://twitter.com/sanjay_spikey" target="_blank">@spikeysanju</a>
* Email: spikeysanju98@gmail.com

<br>

## ❤️ Show your Support

I love it when people use my tool and we’d love to make it even better. If you like this tool and
want to support me in developing more free tools for you, I’d really appreciate a donation. Feel
free to `buy me a cup of coffee` 😄. Thanks!


<a href="https://www.buymeacoffee.com/Li0hsl4" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="160">
</a>

<a href="https://www.patreon.com/spikeysanju" target="_blank">
  <img alt="Patron Link" src="https://c5.patreon.com/external/logo/become_a_patron_button@2x.png" width="160"/>

<a href="https://www.paypal.com/paypalme2/spikeysanju" target="_blank">
  <img alt="PayPal Link" src="https://luunvacationgroup.com/wp-content/uploads/2019/08/paypal-donate-button-high-quality-png-1_orig.png" width="160"/>

<br>

<br>

## 🤗 Credits

- 🤓 Icons are from [tablericons.com](https://tablericons.com)

<br>

## 🔖 License

```
    Apache 2.0 License


    Copyright 2021 Spikey sanju

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

```

