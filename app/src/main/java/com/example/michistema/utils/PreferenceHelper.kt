package com.example.michistema.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    private const val PREF_NAME = "UserPrefs"

    /**
     * Obtiene las preferencias predeterminadas.
     */
    fun defaultPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Obtiene preferencias con un nombre personalizado.
     */
    fun customPrefs(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    /**
     * Edita las preferencias de forma segura.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    /**
     * Guarda un valor en las preferencias con la clave especificada.
     */
    operator fun SharedPreferences.set(key: String, value: Any?) {
        edit {
            when (value) {
                is String? -> it.putString(key, value ?: "")
                is Int -> it.putInt(key, value)
                is Boolean -> it.putBoolean(key, value)
                is Float -> it.putFloat(key, value)
                is Long -> it.putLong(key, value)
                else -> throw UnsupportedOperationException("Tipo no soportado: ${value?.javaClass?.simpleName}")
            }
        }
    }

    /**
     * Obtiene un valor de las preferencias con la clave dada.
     */
    inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T): T {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String ?: "") as T
            Int::class -> getInt(key, defaultValue as? Int ?: 0) as T
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
            Float::class -> getFloat(key, defaultValue as? Float ?: 0f) as T
            Long::class -> getLong(key, defaultValue as? Long ?: 0L) as T
            else -> throw UnsupportedOperationException("Tipo no soportado: ${T::class.simpleName}")
        }
    }

    /**
     * Verifica si una clave existe en las preferencias.
     */
    fun SharedPreferences.containsKey(key: String): Boolean {
        return this.contains(key)
    }

    /**
     * Elimina una clave de las preferencias.
     */
    fun SharedPreferences.remove(key: String) {
        edit { it.remove(key) }
    }

    /**
     * Limpia todas las preferencias almacenadas.
     */
    fun SharedPreferences.clear() {
        edit { it.clear() }
    }
}
