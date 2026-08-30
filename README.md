# تطبيق يوسف العكابي المحاسبي (Android App)

تطبيق محاسبي مخصص لنظام أندرويد تم تطويره باستخدام **Kotlin** و **Jetpack Compose**، مع دعم متكامل للتحويل الصوتي (Text-to-Speech) باللغة العربية، وحفظ البيانات والإعدادات المحلية.

---

## 🏗️ البنية الهيكلية للمشروع (Project Architecture)

تم تقسيم المشروع وفقاً لأفضل ممارسات تطوير تطبيقات أندرويد الحديثة (Clean Architecture / MVVM Pattern):

```text
app/src/main/java/com/example/
│
├── data/
│   └── PreferencesManager.kt       # إدارة التفضيلات والإعدادات المحلية للطلب/التطبيق
│
├── model/
│   └── BookData.kt                 # نماذج البيانات (Data Models) للكتاب والمحتوى المحاسبي
│
├── tts/
│   └── ArabicTtsManager.kt         # محرك النطق والتحويل الصوتي للنصوص العربية (Arabic Text-to-Speech)
│
├── ui/
│   ├── BookReaderScreen.kt         # الشاشة الرئيسية لقراءة الكتب المحاسبية
│   │
│   ├── components/                 # مكونات واجهة المستخدم (UI Components)
│   │   ├── AudioPlayerBar.kt       # شريط التحكم في تشغيل الصوت والتحكم بالسرعة/التشغيل
│   │   ├── AuthorInfoDialog.kt     # نافذة عرض معلومات المؤلف / الكاتب
│   │   ├── BookPageFrame.kt        # إطار عرض صفحة الكتاب والتفاعل معها
│   │   ├── BookmarksSheet.kt       # قائمة العلامات المرجعية (Bookmarks)
│   │   ├── ReadingSettingsDialog.kt# نافذة تخصيص إعدادات القراءة (الخط، الحجم، الألوان)
│   │   ├── SearchDialog.kt         # نافذة البحث في محتوى الكتاب
│   │   └── TableOfContentsSheet.kt # قائمة فهرس المحتويات (Table of Contents)
│   │
│   └── theme/                      # التنسيق والألوان والقوالب (Jetpack Compose Theme)
│       ├── Color.kt                # لوحة الألوان المستخدمة في التطبيق
│       ├── Theme.kt                # إعدادات المظهر العام (Dark/Light Theme)
│       └── Type.kt                 # إعدادات الخطوط والأنماط النصية
│
└── MainActivity.kt                 # النشاط الرئيسي ومจุด الانطلاق للتطبيق
```

---

## ✨ المميزات الرئيسية (Key Features)

- 📖 **قراءة الكتب المحاسبية:** عرض سلس ومريح للصفحات مع دعم تخصيص الخطوط والألوان وحجم النص.
- 🗣️ **القارئ الصوتي العربي (Arabic TTS):** استماع للمحتوى المحاسبي بصوت عربي واضل مع إمكانية التحكم في سرعة النطق والتقديم والتأخير.
- 📑 **فهرس المحتويات والعلامات المرجعية:** الانتقال السريع بين الفصول وحفظ الصفحات المهمة للاسترجاع السريع.
- 🔍 **البحث الذكي:** بحث سريع داخل صفحات ومحتوى الكتاب.
- ⚙️ **إعدادات مخصصة:** حفظ تفضيلات المستخدم تلقائياً باستخدام `PreferencesManager`.

---

## 🛠️ التقنيات المستخدمة (Tech Stack)

* **اللغة:** Kotlin
* **واجهة المستخدم:** Jetpack Compose (Material Design 3)
* **إدارة التفضيلات:** Android DataStore / SharedPreferences
* **محرك الصوت:** Android TextToSpeech (Arabic Engine)
* **الأتمتة و البناء:** Gradle (Kotlin DSL), GitHub Actions CI/CD

---

## 🚀 كيفية التشغيل والتطوير (Getting Started)

### المتطلبات الأساسية:
- Android Studio Ladybug | 2024.2.1 أو أحدث.
- JDK 17 أو أحدث.
- جهاز أندرويد يعمل بنظام Android 7.0 (API Level 24) أو أعلى.

### خطوات التشغيل:
1. قم باستคลون المستودع:
   ```bash
   git clone https://github.com/username/youssef-accounting-app.git
   ```
2. افتح المشروع في برنامج **Android Studio**.
3. قم بعمل Synchronize لمشاريع Gradle.
4. اضغط على **Run** لتشغيل التطبيق على المحاكي أو جهاز حقيقي.
