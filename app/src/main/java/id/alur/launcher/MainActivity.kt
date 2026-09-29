package id.alur.launcher

import android.app.Activity
import android.app.AlertDialog
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private data class App(val label: String, val component: ComponentName, val info: ResolveInfo) {
        val key: String get() = component.flattenToString()
    }
    private val background = Color.rgb(14, 18, 24)
    private val foreground = Color.rgb(242, 245, 248)
    private val muted = Color.rgb(144, 157, 172)
    private val accent = Color.rgb(113, 187, 255)
    private val apps = mutableListOf<App>()
    private lateinit var favoritesBox: LinearLayout
    private lateinit var appsBox: LinearLayout
    private lateinit var scroller: ScrollView
    private lateinit var search: EditText
    private lateinit var clock: TextView
    private val prefs by lazy { getSharedPreferences("launcher", MODE_PRIVATE) }
    private fun dp(n: Int) = (n * resources.displayMetrics.density + .5f).toInt()
    private fun favorites() = prefs.getStringSet("favorites", emptySet())?.toSet() ?: emptySet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = background
        window.navigationBarColor = background
        window.decorView.systemUiVisibility = 0
        renderShell()
        requestHomeRole()
    }

    override fun onResume() {
        super.onResume()
        if (::clock.isInitialized) clock.text = SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date())
        if (::appsBox.isInitialized) { loadApps(); renderApps() }
    }

    private fun requestHomeRole() {
        if (Build.VERSION.SDK_INT >= 29) {
            val role = getSystemService(RoleManager::class.java)
            if (role.isRoleAvailable(RoleManager.ROLE_HOME) && !role.isRoleHeld(RoleManager.ROLE_HOME)) {
                startActivityForResult(role.createRequestRoleIntent(RoleManager.ROLE_HOME), 1)
            }
        }
    }

    private fun text(label: String, size: Float, color: Int = foreground, bold: Boolean = false) = TextView(this).apply {
        text = label; textSize = size; setTextColor(color)
        if (bold) typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        gravity = Gravity.CENTER_VERTICAL
    }

    private fun renderShell() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setBackgroundColor(background)
            setPadding(dp(25), dp(14), dp(16), dp(12))
        }
        setContentView(root)
        val header = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, dp(27), 0, dp(21)) }
        root.addView(header)
        header.addView(text(SimpleDateFormat("EEEE, d MMMM", Locale("id", "ID")).format(Date()), 16f, muted))
        clock = text("", 53f, foreground, true)
        header.addView(clock)
        val tagline = text("Ruang untuk yang penting.", 14f, muted)
        header.addView(tagline)

        val actionRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        root.addView(actionRow, LinearLayout.LayoutParams(-1, dp(52)))
        search = EditText(this).apply {
            hint = "Cari aplikasi"; setHintTextColor(muted); setTextColor(foreground)
            textSize = 15f; isSingleLine = true; setPadding(dp(16), 0, dp(10), 0)
            background = rounded(Color.rgb(29, 35, 44), 18)
            inputType = android.text.InputType.TYPE_CLASS_TEXT
        }
        actionRow.addView(search, LinearLayout.LayoutParams(0, dp(48), 1f))
        val recents = text("▣", 26f, accent).apply {
            gravity = Gravity.CENTER; contentDescription = "Buka Recent Apps"
            setOnClickListener { openRecents() }
        }
        actionRow.addView(recents, LinearLayout.LayoutParams(dp(54), dp(48)))

        val body = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        root.addView(body, LinearLayout.LayoutParams(-1, 0, 1f))
        scroller = ScrollView(this).apply { isFillViewport = false; clipToPadding = false }
        body.addView(scroller, LinearLayout.LayoutParams(0, -1, 1f))
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, dp(20), dp(6), dp(30)) }
        scroller.addView(content)
        content.addView(text("FAVORIT", 11f, accent, true).apply { letterSpacing = .2f })
        favoritesBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, dp(8), 0, dp(22)) }
        content.addView(favoritesBox)
        content.addView(text("SEMUA APLIKASI", 11f, muted, true).apply { letterSpacing = .2f })
        appsBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, dp(8), 0, 0) }
        content.addView(appsBox)

        val index = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER }
        body.addView(index, LinearLayout.LayoutParams(dp(27), -1))
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ".forEach { ch ->
            index.addView(text(ch.toString(), 9f, muted, true).apply {
                gravity = Gravity.CENTER
                setOnClickListener { jumpTo(ch) }
            }, LinearLayout.LayoutParams(-1, 0, 1f))
        }
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { renderApps(); scroller.scrollTo(0, 0) }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        loadApps(); renderApps()
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(radius).toFloat()
    }

    @Suppress("DEPRECATION")
    private fun loadApps() {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        apps.clear()
        val matches = if (Build.VERSION.SDK_INT >= 33)
            packageManager.queryIntentActivities(intent, android.content.pm.PackageManager.ResolveInfoFlags.of(0))
        else packageManager.queryIntentActivities(intent, 0)
        matches.forEach { info ->
            val a = info.activityInfo ?: return@forEach
            if (a.packageName != packageName) apps.add(App(info.loadLabel(packageManager).toString(), ComponentName(a.packageName, a.name), info))
        }
        apps.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
    }

    private fun renderApps() {
        favoritesBox.removeAllViews(); appsBox.removeAllViews()
        val query = search.text.toString().trim()
        val starred = favorites()
        val filtered = apps.filter { it.label.contains(query, true) }
        val fav = filtered.filter { it.key in starred }
        if (fav.isEmpty()) favoritesBox.addView(text(if (query.isEmpty()) "Ketuk ☆ pada aplikasi untuk menambah favorit" else "Tidak ada favorit yang cocok", 13f, muted))
        else fav.forEach { favoritesBox.addView(appRow(it, true)) }
        filtered.forEach { appsBox.addView(appRow(it, it.key in starred)) }
        if (filtered.isEmpty()) appsBox.addView(text("Aplikasi tidak ditemukan", 14f, muted))
    }

    private fun appRow(app: App, starred: Boolean): View {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val icon = ImageView(this).apply {
            try { setImageDrawable(app.info.loadIcon(packageManager)) } catch (_: Exception) { }
            contentDescription = app.label
        }
        row.addView(icon, LinearLayout.LayoutParams(dp(39), dp(39)).apply { rightMargin = dp(17) })
        val label = text(app.label, 17f, foreground)
        label.maxLines = 1; label.ellipsize = android.text.TextUtils.TruncateAt.END
        row.addView(label, LinearLayout.LayoutParams(0, -1, 1f))
        val star = text(if (starred) "★" else "☆", 21f, if (starred) accent else muted).apply {
            gravity = Gravity.CENTER; contentDescription = if (starred) "Hapus ${app.label} dari favorit" else "Tambahkan ${app.label} ke favorit"
            setOnClickListener { toggleFavorite(app) }
        }
        row.addView(star, LinearLayout.LayoutParams(dp(42), -1))
        row.setOnClickListener { launch(app) }
        row.setOnLongClickListener { toggleFavorite(app); true }
        return row.apply { minimumHeight = dp(57); contentDescription = app.label }
    }

    private fun toggleFavorite(app: App) {
        val updated = favorites().toMutableSet()
        if (!updated.add(app.key)) updated.remove(app.key)
        prefs.edit().putStringSet("favorites", updated).apply()
        renderApps()
    }

    private fun launch(app: App) {
        try {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                .setComponent(app.component).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            startActivity(intent)
        } catch (_: Exception) { AlertDialog.Builder(this).setMessage("Aplikasi ${app.label} tidak dapat dibuka.").setPositiveButton("OK", null).show() }
    }

    private fun jumpTo(letter: Char) {
        val child = (0 until appsBox.childCount).firstOrNull { i ->
            (appsBox.getChildAt(i).contentDescription?.toString() ?: "").startsWith(letter.toString(), true)
        }
        if (child != null) {
            val target = appsBox.getChildAt(child)
            scroller.smoothScrollTo(0, appsBox.top + target.top)
        }
    }

    private fun openRecents() {
        if (RecentsService.active?.showRecents() == true) return
        AlertDialog.Builder(this)
            .setTitle("Aktifkan tombol Recent Apps")
            .setMessage("Di pengaturan Aksesibilitas, pilih 'Tombol Recent Apps Alur' lalu aktifkan. Layanan ini hanya membuka Overview saat tombol ditekan dan tidak membaca isi layar.")
            .setPositiveButton("Buka pengaturan") { _, _ -> startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
            .setNegativeButton("Nanti", null).show()
    }
}
