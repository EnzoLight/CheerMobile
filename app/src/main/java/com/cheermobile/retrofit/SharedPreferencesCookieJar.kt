package com.cheermobile.retrofit

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SharedPreferencesCookieJar(context: Context) : CookieJar {
    private val preferences = context.getSharedPreferences("cheer_cookies", Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val editor = preferences.edit()

        cookies.forEach { cookie ->
            val key = cookieKey(cookie)

            if (cookie.expiresAt < System.currentTimeMillis()) {
                editor.remove(key)
            } else {
                editor.putString(key, cookie.toString())
            }
        }

        editor.apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val now = System.currentTimeMillis()
        val editor = preferences.edit()
        val cookies = preferences.all.mapNotNull { (key, value) ->
            val cookie = Cookie.parse(url, value as? String ?: return@mapNotNull null)

            when {
                cookie == null -> {
                    editor.remove(key)
                    null
                }
                cookie.expiresAt < now -> {
                    editor.remove(key)
                    null
                }
                cookie.matches(url) -> cookie
                else -> null
            }
        }

        editor.apply()
        return cookies
    }

    private fun cookieKey(cookie: Cookie): String {
        return "${cookie.domain}|${cookie.path}|${cookie.name}"
    }
}
