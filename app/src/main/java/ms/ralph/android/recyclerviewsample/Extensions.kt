package ms.ralph.android.recyclerviewsample

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.subscribeOnIoThread(): Observable<T> = subscribeOn(Schedulers.io())

fun <T> Observable<T>.observeOnMainThread(): Observable<T> = observeOn(AndroidSchedulers.mainThread())

fun Completable.subscribeOnIoThread(): Completable = subscribeOn(Schedulers.io())

fun Completable.observeOnMainThread(): Completable = observeOn(AndroidSchedulers.mainThread())