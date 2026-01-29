# SD Android - Subscription Management App

A modern Android application built with Jetpack Compose for managing and tracking subscriptions.

## Features

- **Onboarding Screen**: Welcome experience for new users
- **Dashboard**: Overview of all active subscriptions with statistics
- **Subscription Management**: Track monthly and yearly subscriptions
- **API-Ready**: Integrated API interfaces for backend communication

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **Networking**: Retrofit2 + OkHttp3
- **Async**: Kotlin Coroutines
- **Navigation**: Jetpack Navigation Compose

## Requirements

- Android Studio Hedgehog | 2023.1.1 or newer
- JDK 17
- Gradle 8.2
- Android SDK:
  - compileSdk: 34
  - minSdk: 26
  - targetSdk: 35

## Project Structure

```
app/
├── src/main/
│   ├── java/com/gokhanaytekinn/sdandroid/
│   │   ├── data/
│   │   │   ├── api/           # API service interfaces
│   │   │   ├── model/         # Data models
│   │   │   └── repository/    # Repository layer
│   │   ├── ui/
│   │   │   ├── components/    # Reusable UI components
│   │   │   ├── screens/       # Screen composables
│   │   │   ├── theme/         # Theme and styling
│   │   │   └── navigation/    # Navigation setup
│   │   └── MainActivity.kt
│   └── res/                   # Android resources
└── build.gradle.kts
```

## Building the Project

1. Clone the repository:
```bash
git clone https://github.com/gokhanaytekinn/sd-android.git
cd sd-android
```

2. Open the project in Android Studio:
   - File → Open → Select the project folder

3. Sync Gradle:
   - Android Studio will automatically prompt you to sync
   - Or click "Sync Project with Gradle Files" in the toolbar

4. Build and run:
   - Select a device/emulator
   - Click the "Run" button or press Shift+F10

## Reusable Components

### SDButton
A customizable button component supporting both solid and outlined styles:

```kotlin
SDButton(
    text = "Get Started",
    onClick = { /* action */ },
    outlined = false,  // true for outlined style
    backgroundColor = Color(0xFF2196F3),
    textColor = Color.White
)
```

### SDCard
A reusable card component with customizable elevation and corner radius:

```kotlin
SDCard(
    backgroundColor = Color.White,
    elevation = 4.dp,
    cornerRadius = 16.dp
) {
    // Card content
}
```

### SubscriptionCard
Displays subscription information with cost and billing cycle:

```kotlin
SubscriptionCard(
    subscription = subscription,
    onClick = { /* handle click */ }
)
```

## API Integration

The app includes ready-to-use API interfaces:

### Subscription API
- `GET /api/subscriptions` - Fetch all subscriptions
- `GET /api/subscriptions/{id}` - Get subscription details
- `POST /api/subscriptions` - Create new subscription
- `PUT /api/subscriptions/{id}` - Update subscription
- `DELETE /api/subscriptions/{id}` - Delete subscription
- `GET /api/subscriptions/stats` - Get subscription statistics

### Auth API
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user

To connect to your backend, update the `BASE_URL` in `ApiClient.kt`:

```kotlin
private const val BASE_URL = "https://your-api-url.com/"
```

## Screens

### 1. Onboarding Screen
First-time user experience with:
- Welcome message
- App description
- "Get Started" button to navigate to Dashboard

### 2. Dashboard Screen
Main screen showing:
- Total monthly cost statistic
- Active subscriptions count
- List of recent subscriptions
- Floating action button to add new subscriptions

## Navigation Flow

```
Onboarding → Dashboard
```

The app uses Jetpack Navigation Compose for screen transitions. Routes are defined in `Screen.kt` and navigation graph in `NavGraph.kt`.

## Future Enhancements

- Suspicious subscription verification
- Premium dashboard features
- Subscription categories and filtering
- Payment reminders and notifications
- Analytics and spending insights
- Multi-currency support

## License

This project is part of a portfolio/assignment and is available for educational purposes.