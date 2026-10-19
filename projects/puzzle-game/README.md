# 拼圖遊戲

這是 1132「基礎程式設計（二）」的期末個人專案。我用 C# Windows Forms 製作拼圖遊戲。玩家可選擇 3×3、5×5 或 10×10 格，再用滑鼠拖曳圖片交換位置。開發過程有使用 AI 協助；程式整合與修改由我完成。

## 執行

在 Windows 上安裝 .NET Framework 4.7.2 開發元件，並用 Visual Studio 開啟 `puzzle.sln`。建置後即可執行。五張 JPG 放在 `puzzle/assets/`，建置時會自動複製到輸出目錄。

## 整理與修正

- 這份展示版從原始課程專案複製而來，原始檔案仍保留。
- 將圖片放進 `assets/`，修正原版只能從 `bin/Debug` 讀取圖片的問題。
- 調整圖片切割方式，讓三種格數都能完整顯示。
- 在交換動畫結束後檢查是否完成拼圖，並避免一開始就排出完成畫面。

## 圖片來源

我用 Google 圖片反向搜尋，逐張核對了這五張照片。專案使用的是縮放或裁切後的版本。

| 檔案 | 內容 | 攝影者／來源 | 原始照片 |
| --- | --- | --- | --- |
| `img1.jpg` | 水母 | Pawel Kalisinski／Pexels | [照片頁面](https://www.pexels.com/photo/shallow-focus-photo-of-pink-and-brown-jellyfish-1076758/) |
| `img2.jpg` | 黑斑羚 | Frans van Heerden／Pexels | [照片頁面](https://www.pexels.com/photo/an-impala-in-the-wild-12506814/) |
| `img3.jpg` | 聖誕樹薑餅 | KATRIN BOLOVTSOVA／Pexels | [照片頁面](https://www.pexels.com/photo/brown-christmas-tree-shaped-gingerbread-cookies-in-a-wooden-crate-5702700/) |
| `img4.jpg` | 彩色鉛筆 | YEYEQINQIN／Pixabay | [照片頁面](https://pixabay.com/photos/color-color-lead-pencil-draw-kit-1744816/) |
| `img5.jpg` | 臺灣夜市 | Allan So／Pexels | [照片頁面](https://www.pexels.com/photo/aerial-shot-of-stalls-2622170/) |

四張 Pexels 照片依 [Pexels 授權](https://www.pexels.com/license/) 使用。彩色鉛筆照片依 [Pixabay 授權](https://pixabay.com/service/license-summary/) 使用。兩者允許免費使用和修改，沒有強制署名要求；我仍在上表列出攝影者。圖片有各自的授權，不能把它們當作獨立圖庫素材販售或散布。

## 已驗證範圍

已在 Windows 上用 Visual Studio 2022 的 MSBuild 建置 Release 版本。我檢查了五張圖片與三種格數的全部 15 種組合，確認圖片能載入且切片不重疊。程式也能從其他資料夾啟動，顯示格數選擇、規則視窗和主畫面。目前尚未手動完成整盤拼圖的操作測試。
