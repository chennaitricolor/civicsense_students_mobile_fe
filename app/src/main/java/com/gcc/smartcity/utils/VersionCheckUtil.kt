package com.gcc.smartcity.utils

import com.gcc.smartcity.BuildConfig
import kotlin.math.min

class VersionCheckUtil {

    fun compareInstalledVersionNameWith(newVersionName: String): Int {
        var res = 0
        val oldNumbers: List<String> = BuildConfig.VERSION_NAME.split("\\.")
        val newNumbers: List<String> = newVersionName.split("\\.")

        // To avoid IndexOutOfBounds
        val maxIndex = min(oldNumbers.size, newNumbers.size)
        for (i in 0 until maxIndex) {
            val oldVersionPart = Integer.valueOf(oldNumbers[i])
            val newVersionPart = Integer.valueOf(newNumbers[i])
            if (oldVersionPart < newVersionPart) {
                res = -1
                break
            } else if (oldVersionPart > newVersionPart) {
                res = 1
                break
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.size != newNumbers.size) {
            res = if (oldNumbers.size > newNumbers.size) 1 else -1
        }
        return res
    }
}
