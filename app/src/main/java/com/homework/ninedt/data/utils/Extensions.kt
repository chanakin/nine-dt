package com.homework.ninedt.data.utils

import androidx.fragment.app.FragmentManager

// TODO I may not need this anymore
//fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
//    observe(lifecycleOwner, object : Observer<T> {
//        override fun onChanged(t: T?) {
//            observer.onChanged(t)
//            removeObserver(this)
//        }
//    })
//}

fun FragmentManager.fragmentAdded(tag: String): Boolean {
    return findFragmentByTag(tag) != null
}