package com.github.ebortsov.deezermusicplayer.utils

fun computeLevenshteinDistance(s1: String, s2: String): Int {
    val dp = MutableList(size = s1.length + 1) { MutableList(size = s2.length + 1) { 0 } }
    // dp[i][j] - the minimal number of replacements/removals/insertions
    // to transform prefix of length i of s1 to the prefix j of s2

    for (i in 0..s1.length) {
        for (j in 0..s2.length) {
            if (i == 0) {
                dp[i][j] = j
            } else if (j == 0) {
                dp[i][j] = i
            } else {
                if (s1[i - 1] == s2[j - 1])
                    dp[i][j] = dp[i - 1][j - 1]
                else {
                    dp[i][j] = minOf(
                        dp[i - 1][j],
                        dp[i][j - 1],
                        dp[i - 1][j - 1] + 1
                    )
                }
            }
        }
    }

    return dp[s1.length][s2.length]
}