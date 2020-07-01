package com.gcc.smartcity.utils;


import com.gcc.smartcity.BuildConfig;

public class VersionCheckUtils {

    public static int compareInstalledVersionNameWith(String newVersionName) {
        int res = 0;

        String[] versionNameWithoutSuffix = BuildConfig.VERSION_NAME.split("-");
        String[] oldNumbers = versionNameWithoutSuffix[0].split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = Integer.parseInt(oldNumbers[i]);
            int newVersionPart = Integer.parseInt(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length) ? 1 : -1;
        }

        return res;
    }

}
