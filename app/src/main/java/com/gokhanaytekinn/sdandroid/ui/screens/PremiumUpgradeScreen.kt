package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.ui.theme.*

@Composable
fun PremiumUpgradeScreen(
    onCloseClick: () -> Unit = {},
    onUpgradeClick: (Boolean) -> Unit = {} // true for yearly, false for monthly
) {
    var selectedPlan by remember { mutableStateOf(true) } // true=yearly, false=monthly
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(40.dp))
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PREMIUM PLAN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        letterSpacing = 2.sp
                    )
                }
                
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                // Hero Text
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Paranın Kontrolü\nSende Olsun",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 36.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Gereksiz ödemeleri durdur, birikimlerini artır.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Features List
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FeatureCard(
                            icon = Icons.Filled.TrackChanges,
                            title = "Otomatik Yakalama",
                            description = "Gizli ödemeleri anında bulur"
                        )
                        FeatureCard(
                            icon = Icons.Filled.AllInclusive,
                            title = "Sınırsız Takip",
                            description = "İstediğin kadar abonelik ekle"
                        )
                        FeatureCard(
                            icon = Icons.Filled.Insights,
                            title = "Gelişmiş Analiz",
                            description = "Detaylı harcama raporları"
                        )
                    }
                }
                
                // Pricing Section
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Size uygun planı seçin",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Pricing Cards
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Yearly Plan (Recommended)
                        PricingCard(
                            title = "Yıllık",
                            price = "₺399.99",
                            period = "/yıl",
                            originalPrice = "₺599.99",
                            badge = "En Avantajlı",
                            features = listOf(
                                "12 ay kesintisiz erişim",
                                "%33 Tasarruf et"
                            ),
                            selected = selectedPlan,
                            onClick = { selectedPlan = true }
                        )
                        
                        // Monthly Plan
                        PricingCard(
                            title = "Aylık",
                            price = "₺49.99",
                            period = "/ay",
                            originalPrice = null,
                            badge = null,
                            features = listOf(
                                "Esnek ödeme planı",
                                "İstediğin zaman iptal"
                            ),
                            selected = !selectedPlan,
                            onClick = { selectedPlan = false }
                        )
                    }
                }
                
                // Trust Badge
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ödemeleriniz App Store güvencesi altındadır.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
        
        // Sticky Bottom CTA
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BackgroundDark,
                            BackgroundDark
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Button(
                onClick = { onUpgradeClick(selectedPlan) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Premium'a Geç",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = BackgroundDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = BackgroundDark
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Legal Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Satın Alımı Geri Yükle",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = " • ",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Kullanım Koşulları",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = " • ",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Gizlilik",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1c271c)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PricingCard(
    title: String,
    price: String,
    period: String,
    originalPrice: String?,
    badge: String?,
    features: List<String>,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFF1c271c) else Color(0xFF1c271c).copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) PrimaryBlue else Color(0xFF2a402a)
        )
    ) {
        Box {
            // Badge
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-12).dp)
                        .background(PrimaryBlue, RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BackgroundDark,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (originalPrice != null) {
                            Text(
                                text = originalPrice,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                    
                    // Radio button
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) PrimaryBlue else Color.Transparent
                            )
                            .then(
                                if (!selected) Modifier.then(
                                    Modifier.background(
                                        color = Color.Transparent,
                                        shape = CircleShape
                                    )
                                ) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = BackgroundDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = price,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = period,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFF2a402a))
                Spacer(modifier = Modifier.height(16.dp))
                
                // Features
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    features.forEach { feature ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = if (selected) PrimaryBlue else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = feature,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
