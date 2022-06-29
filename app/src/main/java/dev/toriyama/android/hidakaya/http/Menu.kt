package dev.toriyama.android.hidakaya.http

data class Menu(
    /** 商品名 */
    val name: String,
    /** 値段 */
    val price: Int,
    /** カテゴリー */
    val category: String,
    /** リンク */
    val href: String,
    /** サムネイル画像 */
    val thumbnail: String
)
