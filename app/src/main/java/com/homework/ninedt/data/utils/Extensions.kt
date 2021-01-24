package com.homework.ninedt.data.utils

import androidx.fragment.app.FragmentManager

fun FragmentManager.fragmentAdded(tag: String): Boolean {
    return findFragmentByTag(tag) != null
}