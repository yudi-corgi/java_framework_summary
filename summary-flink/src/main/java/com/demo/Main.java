package com.demo;

import java.util.Arrays;

/**
 * @author YUDI-Corgi
 * @description
 */
public class Main {
    public static void main(String[] args) {

        // System.out.println(checkInclusion("ab", "abc"));
        // System.out.println(checkInclusion("ab", "ddbabc"));
        // System.out.println(checkInclusion("ab", "cbdda"));
        // System.out.println(checkInclusion("ab", "a"));
        System.out.println(checkInclusion2("ab", "ddbabc"));

    }

    /**
     * 滑动窗口实现
     */
    public static boolean checkInclusion(String s1, String s2) {
        int s1Len = s1.length(), s2Len = s2.length();
        if (s1Len > s2Len) {
            return false;
        }

        int[] s1Arr = new int[26];
        for(char c : s1.toCharArray()) {
            s1Arr[c - 'a']++;
        }

        int[] s2Arr = new int[26];
        for (int i = 0; i < s1Len; i++) {
            s2Arr[s2.charAt(i) - 'a']++;
        }

        if (Arrays.equals(s1Arr, s2Arr)) {
            return true;
        }

        for (int j = s1Len; j < s2Len; j++) {
            s2Arr[s2.charAt(j) - 'a']++;
            s2Arr[s2.charAt(j - s1Len) - 'a']--;
            if (Arrays.equals(s1Arr, s2Arr)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 滑动窗口实现，优化：只需要一个数组，并且判断数组是否包含只需要通过一个变量判断，而不需要整个数组对比
     * 时间复杂度：O(n+m+∣Σ∣)，其中 n 是字符串 s1 的长度，m 是字符串 s2 的长度，ΣΣ 是字符集，这道题中的字符集是小写字母，∣Σ∣=26。
     * 空间复杂度：O(∣Σ∣)
     */
    public static boolean checkInclusion2(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        if (n > m) {
            return false;
        }
        int[] cnt = new int[26];
        for (int i = 0; i < n; ++i) {
            --cnt[s1.charAt(i) - 'a'];
            ++cnt[s2.charAt(i) - 'a'];
        }
        int diff = 0;
        for (int c : cnt) {
            if (c != 0) {
                ++diff;
            }
        }
        if (diff == 0) {
            return true;
        }
        for (int i = n; i < m; ++i) {
            int x = s2.charAt(i) - 'a', y = s2.charAt(i - n) - 'a';
            if (x == y) {
                continue;
            }
            if (cnt[x] == 0) {
                ++diff;
            }
            ++cnt[x];
            if (cnt[x] == 0) {
                --diff;
            }
            if (cnt[y] == 0) {
                ++diff;
            }
            --cnt[y];
            if (cnt[y] == 0) {
                --diff;
            }
            if (diff == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 双指针
     * 时间复杂度：O(n+m+∣Σ∣)
     * 创建 cnt 需要 O(∣Σ∣)O(∣Σ∣) 的时间。
     * 遍历 s1 需要 O(n) 的时间。
     * 双指针遍历 s2 时，由于 left 和 right 都只会向右移动，故这一部分需要 O(m) 的时间。
     * 空间复杂度：O(∣Σ∣)
     */
    public static boolean checkInclusion3(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        if (n > m) {
            return false;
        }
        int[] cnt = new int[26];
        for (int i = 0; i < n; ++i) {
            --cnt[s1.charAt(i) - 'a'];
        }
        int left = 0;
        for (int right = 0; right < m; ++right) {
            int x = s2.charAt(right) - 'a';
            ++cnt[x];
            while (cnt[x] > 0) {
                --cnt[s2.charAt(left) - 'a'];
                ++left;
            }
            if (right - left + 1 == n) {
                return true;
            }
        }
        return false;
    }

}
