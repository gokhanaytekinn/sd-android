# 1. Project Title

**Sub Tracker Android App**

# 2. Overview

> [!NOTE]
> The coding, MVVM architectural design, and Jetpack Compose designs of this application were implemented 100% using Artificial Intelligence (AI).

**Sub Tracker** is a modern Android application where users can easily track and manage all their recurring subscriptions, dues, and regular payments. Built with the latest Kotlin and Jetpack Compose technologies, your expenses are now fully under your control thanks to its high-level user experience interface.

# 3. Features

- **Advanced Subscription Tracking:** The ability to group and list all your active and passive subscriptions under one roof.
- **Reminders and Notifications:** Eliminating the risk of late interest and unwanted interruptions thanks to automatic notifications sent before payment dates.
- **Multi-Language Support:** 100% localized language support in many languages such as English, Turkish, German, French, and Spanish for user comfort.
- **Free and Premium Options:** Free in-app features and Premium plan alternatives for those who want to go beyond the limits.

# 4. Tech Stack

The Mobile Client relies on the most up-to-date and stable technologies of the Android ecosystem:

- **Language:** Kotlin
- **UI & Components:** Android SDK, Jetpack Compose, Material Design 3
- **Network and Asynchronous Operations:** Retrofit, OkHttp, Kotlin Coroutines, Flow
- **Dependency Injection:** Hilt (Dagger-Hilt)
- **Monetization & Analytics:** Google Play Billing Library, Google AdMob, Firebase

# 5. Architecture

**Client (Mobile) Architecture:**
The application is built on the **MVVM (Model-View-ViewModel)** design pattern and references **Clean Architecture** rules:
- **UI Layer (Jetpack Compose):** The outermost layer where only the interface is drawn and is independent of data.
- **ViewModel Layer:** The layer that holds the states of the screens (State Management) and connects user interactions to the service layer.
- **Data (Repository) Layer:** The layer responsible for collecting data from a remote server (API), modeling it, and transferring it asynchronously (Flow) throughout the application.

# 6. Project Structure

The package architecture inside the project's `app/src/main/`:

```
com.gokhanaytekinn.sdandroid/
├── ui/              # Jetpack Compose screens, themes, components
├── viewmodel/       # MVVM State and ViewModel classes
├── data/            # Retrofit services, network data models, Repository classes
├── di/              # Hilt Injection modules (Network Module etc.)
├── model/           # In-app data structures
└── util/            # Constants, currency formatter and other helpful tools
```

# 7. Installation / Setup

Steps you need to test the project in your own environment:

1. Install the latest version of Android Studio on your computer.
2. Download (Clone) this repository and import the `sd-android` folder via Android Studio (Open option).
3. Wait for the Gradle sync to complete.
4. Test your application on a physical device or simulator by pressing the "Run" button. (Minimum SDK: 26+)

# 8. Configuration

Configuration requirements for a smooth installation:

- **Firebase Configuration:** For analytics and configurations, it is mandatory to place a valid `google-services.json` file inside the `app/` directory. (It is available in the project).
- **Backend Address (URL):** The IP address of the service the application will connect to must be set from the constant in the API service configuration (e.g., `Constants.BASE_URL`).

# 9. Screenshots / UI

The user interface design of the application has been developed targeting a modern and fluid user experience (UX). Screenshots reflecting various features of the application (Login, Custom Listings, Profile, Dashboard, etc.) are located in the `Ekranlar/` (Screens) folder in the project's root directory.

# 10. Monetization

The project is built on a business model featuring both a free basic version and paid extra features:
- **Ads (AdMob):** Interstitial ads are presented during the application for free users.
- **In-App Purchase:** There are Subscription plans with **Google Play Billing** support integrated that allow users to get rid of ads.

# 11. Backend Integration

The application ensures secure data exchange with a centralized Spring Boot-based backend:
- Communication is established via HTTP REST protocol using Retrofit in `application/json` format.
- The JSON Web Token (JWT) key allocated to the user is included in all requests requiring authentication via `NetworkInterceptor`.

# 12. Testing

To deliver stable versions to customers:
- **Closed Testing:** The application is currently offered as a closed test over the Google Play Console to a group of users with different device configurations. It will transition to public production following the resolution of issues.

# 13. Roadmap

Modules targeted to be developed on the user side of the application:
- Advanced home screen statistical visualization add-ons (Category graphic limits etc.).
- A wider icon set covering the logos of frequently used subscription companies (Spotify, Netflix, etc.).
- "Shared Subscription" screens where users can merge their own subscription lists with family members in a single common pool.

# 14. Contributing

If you want to contribute to the project and expand the team:
1. Fork the repository to your own Github account.
2. Create a branch (`git checkout -b feature/ExcellentService`).
3. Commit the changes (`git commit -m 'Excellent Service feature added'`).
4. Send a merge proposal by creating a "Pull Request" to this repository.

# 15. License

Copyright (c) 2026 Gökhan Aytekin

All rights reserved.

This repository is shared publicly for viewing and educational purposes only.

You may NOT:
- use this code in production
- copy significant parts of it
- redistribute it
- modify and distribute it

without explicit written permission from the author.

If you would like to use this code, please contact the author.

# 16. Contact

For your feedback and commercial collaborations:
- You can create a tracking card using the "Issues" section on this Github page.
- Or you can directly contact the developer authority via email links.
