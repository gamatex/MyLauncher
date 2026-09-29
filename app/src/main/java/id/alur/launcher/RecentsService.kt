package id.alur.launcher

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class RecentsService : AccessibilityService() {
    companion object { var active: RecentsService? = null; private set }
    override fun onServiceConnected() { super.onServiceConnected(); active = this }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
    override fun onInterrupt() = Unit
    override fun onDestroy() { if (active === this) active = null; super.onDestroy() }
    fun showRecents(): Boolean = performGlobalAction(GLOBAL_ACTION_RECENTS)
}
