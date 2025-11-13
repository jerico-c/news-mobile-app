# Mandiri News Application 📱

**Mandiri News App** is a modern, native Android application developed as part of the **Bank Mandiri Internship Program**. Built with **Kotlin**, this app delivers a seamless news-reading experience by fetching real-time data from [NewsAPI.org](https://newsapi.org/).

The project focuses on a clean User Interface (UI), efficient network handling using Retrofit, and smooth user interactions with features like infinite scrolling and dynamic filtering.

---

## ✨ Key Features

### 1. Modern Home Screen
- **Top News:** A horizontal swipeable list displaying the latest breaking news headlines.
- **Recommendations:** A vertical list of curated news articles.
- **Infinite Scrolling:** Seamlessly loads more articles as the user scrolls down, ensuring a continuous reading experience without manual pagination.

### 2. Discover & Search
- **Dedicated Discover Page:** A separate screen for exploring news.
- **Smart Filtering:** Users can filter news by categories (e.g., Sports, Health, Technology, Business) or search for specific topics using the search bar.
- **Real-time Updates:** The list updates instantly based on the selected category or search query.

### 3. Enhanced News Detail
- **Clean Reading:** Displays the full article detail with a distraction-free layout.
- **Smart Parsing:** Programmatically cleans up API truncation markers (e.g., `[+1234 chars]`) for a polished presentation.
- **Source Linking:** Displays the author and publication date clearly.

---

## 🛠 Tech Stack

* **Language:** Kotlin
* **UI:** XML (Modern Views), ConstraintLayout, CardView
* **Architecture:** ViewBinding for safer, faster interaction with views.
* **Networking:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
* **Image Loading:** [Glide](https://github.com/bumptech/glide)
* **API:** [NewsAPI.org](https://newsapi.org/)

---

## 📸 App Overview

| **Home Screen** | **Discover Screen** | **Detail Screen** |
|:---:|:---:|:---:|
| <img src="IMAGE 2025-10-31 12:16:37.jpg" width="250" /> | <img src="IMAGE 2025-10-31 12:20:26.jpg" width="250" /> | <img src="IMAGE 2025-10-31 12:30:34.jpg" width="250" /> |

*(Note: The images above demonstrate the modern UI overhaul, featuring the official Mandiri News branding and a clean, whitespace-driven layout.)*

---

## 🚀 How to Run

1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/mandiri-news-app.git](https://github.com/YOUR_USERNAME/mandiri-news-app.git)
    ```
2.  **Open in Android Studio**
    Open Android Studio and select "Open an Existing Project", then navigate to the cloned directory.
3.  **Setup API Key**
    * Register at [NewsAPI.org](https://newsapi.org/) to get a free API Key.
    * Open `app/src/main/java/com/example/mandirinewsapp/util/Constants.kt`.
    * Replace the placeholder with your key:
        ```kotlin
        const val API_KEY = "YOUR_API_KEY_HERE"
        ```
4.  **Build and Run**
    Connect your Android device or use an emulator, then press the **Run** button (Shift+F10).

---

