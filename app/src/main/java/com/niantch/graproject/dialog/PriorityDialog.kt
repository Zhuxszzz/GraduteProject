package com.niantch.graproject.dialog

/**
 * @description: Interface for multiple dialog with priority,
 * i.e. when a dialog is showing and another dialog needs to show
 * @since 2021-02-09 14:11
 * @author jitaoguo@tencent.com
 */
interface PriorityDialog {
    /**
     * 设置优先级
     */
    fun setPriority(priority: Int)

    /**
     * 获取优先级
     */
    fun getPriority(): Int

    /**
     * 是否需要等待，如果不等待且优先级不够，则直接抛弃
     */
    fun isNeedWait(): Boolean

    /**
     * 设置等待
     */
    fun setNeedWait(wait: Boolean)

    /**
     * 销毁
     */
    fun dismiss(): Boolean

    /**
     * 展示
     */
    fun show(): Boolean

    companion object {
        /**
         * 普通优先级, 队尾排队处理
         */
        const val PRIORITY_NORMAL = Int.MAX_VALUE

        /**
         * 高优先级，插队头处理
         */
        const val PRIORITY_HIGH = 0
    }
}
