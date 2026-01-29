# Component Usage Guide

This document provides detailed examples of how to use the reusable UI components in the SD Android application.

## Table of Contents
- [SDButton](#sdbutton)
- [SDCard](#sdcard)
- [SubscriptionCard](#subscriptioncard)
- [Theme System](#theme-system)

---

## SDButton

A highly customizable button component that supports both solid and outlined styles.

### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `text` | String | Required | Button label text |
| `onClick` | () -> Unit | Required | Click handler function |
| `modifier` | Modifier | Modifier | Compose modifier |
| `outlined` | Boolean | false | If true, displays outlined style |
| `enabled` | Boolean | true | Button enabled state |
| `backgroundColor` | Color | #2196F3 | Background/border color |
| `textColor` | Color | White | Text color |
| `height` | Dp | 56.dp | Button height |

### Examples

#### Solid Button
```kotlin
SDButton(
    text = "Get Started",
    onClick = { /* Navigate to dashboard */ },
    backgroundColor = Color(0xFF2196F3),
    textColor = Color.White
)
```

#### Outlined Button
```kotlin
SDButton(
    text = "Learn More",
    onClick = { /* Show info */ },
    outlined = true,
    backgroundColor = Color(0xFF2196F3)
)
```

#### Custom Styling
```kotlin
SDButton(
    text = "Delete",
    onClick = { /* Delete action */ },
    backgroundColor = Color(0xFFF44336),
    textColor = Color.White,
    height = 48.dp
)
```

#### Disabled Button
```kotlin
SDButton(
    text = "Submit",
    onClick = { /* Submit form */ },
    enabled = false
)
```

---

## SDCard

A reusable card container with customizable elevation and corner radius.

### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `modifier` | Modifier | Modifier | Compose modifier |
| `backgroundColor` | Color | White | Card background color |
| `elevation` | Dp | 4.dp | Shadow elevation |
| `cornerRadius` | Dp | 16.dp | Corner radius |
| `content` | @Composable ColumnScope.() -> Unit | Required | Card content |

### Examples

#### Basic Card
```kotlin
SDCard {
    Text(
        text = "Welcome",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "This is a basic card",
        fontSize = 14.sp
    )
}
```

#### Colored Card with Stats
```kotlin
SDCard(
    backgroundColor = Color(0xFF2196F3),
    elevation = 4.dp
) {
    Text(
        text = "Total Monthly Cost",
        fontSize = 12.sp,
        color = Color.White.copy(alpha = 0.9f)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "$80.97",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}
```

#### Card with Custom Radius
```kotlin
SDCard(
    cornerRadius = 24.dp,
    elevation = 8.dp
) {
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = "Premium"
    )
    Text(text = "Premium Feature")
}
```

---

## SubscriptionCard

A specialized card component for displaying subscription information.

### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `subscription` | Subscription | Required | Subscription data object |
| `onClick` | () -> Unit | {} | Click handler |
| `modifier` | Modifier | Modifier | Compose modifier |

### Subscription Data Model

```kotlin
data class Subscription(
    val id: String,
    val name: String,
    val description: String? = null,
    val cost: Double,
    val currency: String = "USD",
    val billingCycle: BillingCycle,
    val nextBillingDate: String? = null,
    val category: String? = null,
    val iconUrl: String? = null,
    val isActive: Boolean = true
)

enum class BillingCycle {
    MONTHLY, YEARLY, WEEKLY, QUARTERLY
}
```

### Examples

#### Basic Subscription Card
```kotlin
val subscription = Subscription(
    id = "1",
    name = "Netflix",
    cost = 15.99,
    billingCycle = BillingCycle.MONTHLY,
    nextBillingDate = "2024-02-15",
    category = "Entertainment"
)

SubscriptionCard(
    subscription = subscription,
    onClick = { /* Navigate to details */ }
)
```

#### In a List
```kotlin
LazyColumn {
    items(subscriptions) { subscription ->
        SubscriptionCard(
            subscription = subscription,
            onClick = { viewModel.onSubscriptionClick(subscription.id) }
        )
    }
}
```

#### With Custom Modifier
```kotlin
SubscriptionCard(
    subscription = subscription,
    modifier = Modifier.padding(horizontal = 16.dp)
)
```

---

## Theme System

The app uses Material3 theming with custom color schemes.

### Color Palette

```kotlin
// Primary Colors
val PrimaryBlue = Color(0xFF2196F3)
val PrimaryDark = Color(0xFF1976D2)
val AccentColor = Color(0xFF4CAF50)

// Background Colors
val BackgroundLight = Color(0xFFF5F5F5)
val CardBackground = Color(0xFFFFFFFF)

// Text Colors
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)

// Status Colors
val ErrorColor = Color(0xFFF44336)
val SuccessColor = Color(0xFF4CAF50)
val WarningColor = Color(0xFFFF9800)
```

### Using Theme Colors

```kotlin
@Composable
fun MyScreen() {
    val colorScheme = MaterialTheme.colorScheme
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Text(
            text = "Title",
            color = colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )
        
        Text(
            text = "Description",
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
```

### Typography

```kotlin
// Available Typography Styles
MaterialTheme.typography.displayLarge
MaterialTheme.typography.displayMedium
MaterialTheme.typography.displaySmall
MaterialTheme.typography.titleLarge    // 22.sp, Bold
MaterialTheme.typography.titleMedium
MaterialTheme.typography.titleSmall
MaterialTheme.typography.bodyLarge     // 16.sp, Normal
MaterialTheme.typography.bodyMedium
MaterialTheme.typography.bodySmall
MaterialTheme.typography.labelLarge
MaterialTheme.typography.labelMedium
MaterialTheme.typography.labelSmall    // 11.sp, Medium
```

### Example Usage

```kotlin
Text(
    text = "Heading",
    style = MaterialTheme.typography.titleLarge,
    color = MaterialTheme.colorScheme.primary
)

Text(
    text = "Body text content",
    style = MaterialTheme.typography.bodyLarge,
    color = MaterialTheme.colorScheme.onSurface
)
```

---

## Best Practices

1. **Consistent Spacing**: Use multiples of 4dp (4, 8, 12, 16, 24, 32, etc.)
2. **Color Usage**: Always use theme colors instead of hardcoded values when possible
3. **Modifiers**: Pass modifiers from parent to child components for flexibility
4. **Reusability**: Use these components consistently across the app
5. **Accessibility**: Provide contentDescription for icons and images

---

## Creating New Components

When creating new reusable components:

1. Place them in `ui/components/` package
2. Make them configurable with parameters
3. Use Material3 design principles
4. Follow the naming convention: `SD{ComponentName}`
5. Document parameters and usage
6. Test with different configurations

### Example Component Template

```kotlin
@Composable
fun SDCustomComponent(
    modifier: Modifier = Modifier,
    // Add parameters with defaults
    text: String,
    onClick: () -> Unit = {},
    // More parameters...
) {
    // Component implementation
}
```

---

## Integration Examples

### Complete Screen Example

```kotlin
@Composable
fun ExampleScreen() {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "My Screen",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            item {
                SDCard {
                    Text("Card Content")
                }
            }
            
            item {
                SDButton(
                    text = "Action",
                    onClick = { /* Handle click */ }
                )
            }
        }
    }
}
```
