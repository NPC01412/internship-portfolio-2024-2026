# 訂單管理網頁｜公開展示版

這是 1142「網際網路程式設計」的期末作業，使用 ASP.NET Core 8 MVC 和 Razor 製作。網頁可顯示訂單清單，依地區或關鍵字查詢，並提供分頁、新增、修改和刪除功能。

## 公開版與課堂原版

課堂原版連接 SQL Server。這份展示版已移除原始連線資訊和帳密，改用 `Partice/App_Data/orders.json` 儲存虛構訂單。第一次啟動時會自動建立檔案，並加入 24 筆範例資料。執行時產生的 JSON 檔不會納入版本控制。這項改動只用於公開展示，並非課堂原版的資料庫實作。

整理展示版時，我也修正了新增訂單的自動編號、修改後的頁面導向，以及刪除確認按鈕。`Prog6` 和 `Prog7` 是課堂上的 Session 練習，沒有正式登入或權限控管功能。

## 執行方式

先安裝 [.NET 8 SDK](https://dotnet.microsoft.com/download/dotnet/8.0)，再於專案根目錄執行：

```powershell
dotnet restore Partice/Partice.csproj
dotnet run --project Partice/Partice.csproj --launch-profile http
```

接著開啟 `http://localhost:5129/`。如需重設訂單，請先停止程式，再刪除 `Partice/App_Data/orders.json`。下次啟動時會重新產生 24 筆範例資料。

## 素材與資料

這份展示版不含原始資料庫、帳密、校內伺服器位址、學號、姓名或本機設定檔。建置輸出也已排除。`wwwroot/lib` 中的前端套件保留原有授權文件。
