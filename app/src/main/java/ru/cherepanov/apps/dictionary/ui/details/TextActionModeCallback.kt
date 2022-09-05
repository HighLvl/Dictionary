package ru.cherepanov.apps.dictionary.ui.details

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.compose.ui.geometry.Rect
import ru.cherepanov.apps.dictionary.MainActivity


private enum class MenuItemOption(val id: Int, val resource: Int) {
    Copy(0, android.R.string.copy),
    Paste(1, android.R.string.paste),
    Cut(2, android.R.string.cut),
    SelectAll(3, android.R.string.selectAll);

    val order = id
}

typealias ActionCallback = (() -> Unit)?

class TextActionModeCallback(
    val view: View,
    val provideSelectedText: () -> String,
    var rect: Rect = Rect.Zero,
    var onCopyRequested: ActionCallback = null,
    var onPasteRequested: ActionCallback = null,
    var onCutRequested: ActionCallback = null,
    var onSelectAllRequested: ActionCallback = null
) : ActionMode.Callback2() {

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        requireNotNull(menu)
        requireNotNull(mode)

        mapOf(
            MenuItemOption.Copy to onCopyRequested,
            MenuItemOption.Paste to onPasteRequested,
            MenuItemOption.Cut to onCutRequested,
            MenuItemOption.SelectAll to onSelectAllRequested
        ).forEach { (option, callback) ->
            callback ?: return@forEach
            menu.add(0, option.id, option.order, option.resource)
        }

        var menuItemOrder = 100
        for (resolveInfo in getSupportedActivities()) {
            menu.add(
                Menu.NONE, menuItemOrder,
                menuItemOrder++,
                resolveInfo.loadLabel(view.context.packageManager)
            )
                .setIntent(createProcessTextIntentForResolveInfo(resolveInfo))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        return true
    }

    private fun createProcessTextIntent(): Intent {
        return Intent()
            .setAction(Intent.ACTION_PROCESS_TEXT)
            .setType("text/plain")
    }

    private fun getSupportedActivities(): List<ResolveInfo> {
        val packageManager: PackageManager = view.context.packageManager
        val activities = if (Build.VERSION.SDK_INT >= 33) packageManager.queryIntentActivities(
            createProcessTextIntent(),
            PackageManager.ResolveInfoFlags.of(0)
        )
        else packageManager.queryIntentActivities(createProcessTextIntent(), 0)
        val appActivity =
            activities.first { it.activityInfo.name == MainActivity::class.qualifiedName }
        val appActivityFirstActivities = activities.toMutableList().apply {
            remove(appActivity)
            add(0, appActivity)
        }
        return appActivityFirstActivities
    }

    private fun createProcessTextIntentForResolveInfo(info: ResolveInfo): Intent {
        return createProcessTextIntent()
            .putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
            .setClassName(
                info.activityInfo.packageName,
                info.activityInfo.name
            )
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item!!.itemId) {
            MenuItemOption.Copy.id -> onCopyRequested?.invoke()
            MenuItemOption.Paste.id -> onPasteRequested?.invoke()
            MenuItemOption.Cut.id -> onCutRequested?.invoke()
            MenuItemOption.SelectAll.id -> onSelectAllRequested?.invoke()
            else -> handleProcessTextMenuItem(item)
        }
        mode?.finish()
        return true
    }

    private fun handleProcessTextMenuItem(item: MenuItem) {
        item.intent?.putExtra(Intent.EXTRA_PROCESS_TEXT, provideSelectedText())?.let {
            view.context.startActivity(it)
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
    }

    override fun onGetContentRect(mode: ActionMode?, view: View?, outRect: android.graphics.Rect?) {
        outRect?.set(
            rect.left.toInt(),
            rect.top.toInt(),
            rect.right.toInt(),
            rect.bottom.toInt()
        )
    }
}