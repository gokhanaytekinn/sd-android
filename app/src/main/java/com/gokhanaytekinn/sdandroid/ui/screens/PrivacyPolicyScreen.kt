package com.gokhanaytekinn.sdandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit = {}
) {
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
                text = "Gizlilik Politikası",
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

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Son Güncelleme: 20 Şubat 2026",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            PolicySection(
                title = "1. Giriş",
                content = "Sub Tracker (\"Uygulama\") olarak gizliliğinize önem veriyoruz. Bu Gizlilik Politikası, uygulamamızı kullanırken verilerinizin nasıl toplandığını, kullanıldığını ve korunduğunu açıklar."
            )
            
            PolicySection(
                title = "2. Toplanan Veriler",
                content = "Uygulamamız, size daha iyi hizmet verebilmek için aşağıdaki verileri toplayabilir veya işleyebilir:\n\n" +
                        "• Abonelik Bilgileri: Eklediğiniz aboneliklerin adı, fiyatı, ödeme tarihleri.\n" +
                        "• Kullanım Verileri: Uygulama içi etkileşimler ve tercihler.\n" +
                        "• Cihaz Bilgileri: Uygulamanın çalışması için gerekli temel cihaz tanımlayıcıları."
            )
            
            PolicySection(
                title = "3. Verilerin Kullanımı",
                content = "Toplanan veriler yalnızca aşağıdaki amaçlarla kullanılır:\n\n" +
                        "• Aboneliklerinizi takip etmenizi sağlamak.\n" +
                        "• Ödeme günü yaklaşan abonelikler için hatırlatıcı bildirimleri göndermek.\n" +
                        "• Uygulama performansını iyileştirmek ve hataları gidermek."
            )
            
            PolicySection(
                title = "4. Veri Güvenliği",
                content = "Kişisel verileriniz cihazınızda güvenli bir şekilde saklanır. Sunucularımızda tutulan veriler (hesap oluşturmanız durumunda) endüstri standardı şifreleme yöntemleri ile korunur. Verileriniz asla izniniz olmaksızın üçüncü taraflara satılmaz veya paylaşılmaz."
            )
            
            PolicySection(
                title = "5. İzinler",
                content = "Uygulama, size bildirim gönderebilmek için bildirim izni talep edebilir. Bu izin, yalnızca abonelik hatırlatıcıları için kullanılır."
            )
            
            PolicySection(
                title = "6. İletişim",
                content = "Gizlilik politikamızla ilgili sorularınız için bizimle iletişime geçebilirsiniz:\ngokhanaytekinn@yandex.com"
            )
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun PolicySection(
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
    }
}
