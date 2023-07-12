package com.audit.pass.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.audit.pass.app.App
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 */
object SpUtil {
    private const val FILE_NAME = "share_data_game_${Const.APP_KEY_ID}"

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param `object`
     */
    fun put(key: String?, data: Any) {
        val sp = App.instance().getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Int -> editor.putInt(key, data)
            is Boolean -> editor.putBoolean(key, data)
            is Float -> editor.putFloat(key, data)
            is Long -> editor.putLong(key, data)
            else -> editor.putString(key, data.toString())
        }
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultData
     * @return
     */
    operator fun get(key: String?, defaultData: Any?): Any? {
        val sp = App.instance().getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        return when (defaultData) {
            is String -> sp.getString(key, defaultData as String?)
            is Int -> sp.getInt(key, (defaultData as Int?)!!)
            is Boolean -> sp.getBoolean(key, (defaultData as Boolean?)!!)
            is Float -> sp.getFloat(key, (defaultData as Float?)!!)
            is Long -> sp.getLong(key, (defaultData as Long?)!!)
            else -> null
        }
    }

    /**
     * 移除某个key值已经对应的值
     * @param key
     */
    fun remove(key: String?) {
        val sp = App.instance().getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.remove(key)
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 清除所有数据
     */
    fun clear() {
        val sp = App.instance().getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.clear()
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 查询某个key是否已经存在
     * @param key
     * @return
     */
    fun contains(key: String?): Boolean {
        val sp = App.instance().getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        return sp.contains(key)
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    fun getAll(): Map<String, *> {
        val sp = App.instance().getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        return sp.all
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     */
    private object SharedPreferencesCompat {
        private val sApplyMethod = findApplyMethod()

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        private fun findApplyMethod(): Method? {
            try {
                val clz: Class<*> = SharedPreferences.Editor::class.java
                return clz.getMethod("apply")
            } catch (_: NoSuchMethodException) {
            }
            return null
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        fun apply(editor: SharedPreferences.Editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor)
                    return
                }
            } catch (_: IllegalArgumentException) {
            } catch (_: IllegalAccessException) {
            } catch (_: InvocationTargetException) {
            }
            editor.commit()
        }
    }

}
