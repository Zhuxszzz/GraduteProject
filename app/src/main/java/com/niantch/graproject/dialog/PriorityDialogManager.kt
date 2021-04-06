package com.niantch.graproject.dialog

import java.util.Vector


/**
 * @description: Created by katacai on 2019-05-15. Migrate to current project.
 * @see PriorityDialog
 */
class PriorityDialogManager private constructor() {

    private val mDialogList: MutableList<PriorityDialog> = Vector()

    /**
     * 按优先级显示当前Dialog
     */
    fun showDialog(dialog: PriorityDialog) {
        if (!mDialogList.contains(dialog)) {
            /**
             * 当前正在显示的Dialog
             */
            val currentShowingDialog: PriorityDialog? =
                if (mDialogList.isNotEmpty()) mDialogList[0] else null

            /**
             * 处理优先级显示
             */
            var nextDialog: PriorityDialog? = null
            if (mDialogList.isEmpty()) {
                mDialogList.add(dialog)
                nextDialog = dialog
            } else {
                val priority = dialog.getPriority()
                when (priority) {
                    PriorityDialog.PRIORITY_HIGH -> {
                        mDialogList.add(0, dialog)
                        nextDialog = dialog
                    }
                    PriorityDialog.PRIORITY_NORMAL ->
                        if (dialog.isNeedWait()) {
                            mDialogList.add(dialog)
                        }
                    else -> if (dialog.isNeedWait()) {
                        if (isIndexValid(priority)) {
                            mDialogList.add(priority, dialog)
                        } else {
                            mDialogList.add(dialog)
                        }
                    }
                }
            }
            if (nextDialog != null) {
                if (currentShowingDialog != null) {
                    mDialogList.remove(currentShowingDialog)
                    currentShowingDialog.dismiss()
                }
                val success: Boolean = nextDialog.show()
                if (!success) {
                    mDialogList.remove(nextDialog)
                }
            }
        }
    }

    private fun isIndexValid(index: Int): Boolean {
        return index >= 0 && index < mDialogList.size
    }

    /**
     * 移除显示的Dialog, 并展示队列里下一个要显示的Dialog
     */
    fun removeDialog(dialog: PriorityDialog) {
        if (mDialogList.contains(dialog)) {
            mDialogList.remove(dialog)
            if (!mDialogList.isEmpty()) {
                val nextDialog: PriorityDialog = mDialogList[0]
                val success: Boolean = nextDialog.show()
                if (!success) {
                    mDialogList.remove(nextDialog)
                }
            }
        }
    }

    companion object {
        val instance: PriorityDialogManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PriorityDialogManager()
        }
    }
}