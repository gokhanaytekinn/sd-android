package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokhanaytekinn.sdandroid.R

data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
fun HelpCenterScreen(
    onBackClick: () -> Unit = {}
) {
    val faqList = remember {
        listOf(
            FaqItem(
                question = "Abonelik nasıl iptal edilir?",
                answer = "Abonelik detay sayfasına giderek en altta bulunan 'Aboneliği İptal Et' butonuna tıklayabilirsiniz. Bu işlem sadece uygulama içi takibi sonlandırır, servis sağlayıcı üzerinden de iptal etmeyi unutmayın."
            ),
            FaqItem(
                question = "Verilerim güvende mi?",
                answer = "Evet, tüm verileriniz cihazınızda şifrelenmiş olarak saklanır. Sub Tracker, kişisel verilerinizi üçüncü taraflarla paylaşmaz."
            ),
            FaqItem(
                question = "Banka hesabımı nasıl bağlarım?",
                answer = "Şu an için doğrudan banka entegrasyonu yerine, SMS ve bildirim okuma izni ile otomatik takip sağlıyoruz. Banka entegrasyonu yakında gelecek."
            ),
            FaqItem(
                question = "Para iadesi alabilir miyim?",
                answer = "Uygulama içi satın alımlarda Google Play/App Store politikaları geçerlidir. İade taleplerinizi ilgili mağaza üzerinden yapabilirsiniz."
            ),
            FaqItem(
                question = "Hatırlatıcılar çalışmıyor, ne yapmalıyım?",
                answer = "Cihaz ayarlarından uygulamanın bildirim izinlerinin açık olduğundan emin olun. Ayrıca pil tasarrufu modunun bildirimleri engellemediğini kontrol edin."
            ),
            FaqItem(
                question = "Şüpheli işlem bildirimi nedir?",
                answer = "Uygulama, normal harcama düzeninizin dışındaki veya tanımlanamayan ödemeleri tespit ettiğinde sizi uyarır. Bu işlemleri 'Şüpheli İşlemler' ekranından inceleyebilirsiniz."
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    
    val filteredFaqList = remember(searchQuery) {
        if (searchQuery.isBlank()) faqList
        else faqList.filter { 
            it.question.contains(searchQuery, ignoreCase = true) || 
            it.answer.contains(searchQuery, ignoreCase = true) 
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Geri",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = "Yardım Merkezi",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.size(40.dp)) // Balance header
        }

        // Search Bar
        PaddingValues(horizontal = 16.dp, vertical = 8.dp).let { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = Color.Transparent
                )
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text(
                            text = "Nasıl yardımcı olabiliriz?",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true
                )
            }
        }

        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Popüler Sorular",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(filteredFaqList) { item ->
                FaqAccordionItem(item)
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
                // Clean bottom spacing
            }
        }
    }
}

@Composable
fun FaqAccordionItem(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Rotation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Kapat" else "Aç",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .rotate(rotationState)
                        .size(24.dp)
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = item.answer,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
