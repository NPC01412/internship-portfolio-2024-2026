# 劇情互動 Android App

這是 1142「行動裝置程式設計」的個人 Project APK 作業。我使用 Kotlin 開發一款以相機閃光燈探索黑暗場景的劇情遊戲。玩家先拍照或從相簿選取圖片製作角色證件，再調查房間、蒐集線索並選擇故事走向。

[觀看原作業示範影片](https://www.youtube.com/watch?v=yVEZxtetMaU)

## 主要功能

- 使用相機拍照或從相簿選取圖片，將照片帶入劇情畫面。
- 以文字、圖片、動畫和音效呈現場景。
- 閃光燈電量及底片數量會隨行動減少，影響探索方式。
- 調查四個方向的場景，解開書櫃謎題，觸發不同結局。

## 技術與我的工作

專案使用 Kotlin、Android View Binding 和 Gradle。我負責故事流程、畫面、互動邏輯及程式整合。製作過程使用過 AI 協助；場景圖片由 AI 生成。音樂與音效採用我在作業中使用的可使用素材，原始來源尚待補記。

## 建置方式

1. 使用 Android Studio 開啟本資料夾，安裝 Android SDK 35。
2. 讓 Gradle 同步完成，再執行 `app`。
3. 或在已設定 Android SDK 與 JDK 的 Windows 環境中執行 `gradlew.bat assembleDebug`。

專案需要具備相機的 Android 裝置，最低支援 Android 7.0（API 24）。`local.properties` 只記錄本機 SDK 路徑，因此沒有放進儲存庫。使用相簿或相機時，請選擇自己願意用於測試的照片。

## 公開版內容

這裡放的是 Android 原始碼、Gradle 設定及 App 所需素材。沒有加入 Android Studio 快取、建置產物、個人照片或已編譯 APK。
