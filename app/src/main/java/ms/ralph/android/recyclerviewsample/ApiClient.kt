package ms.ralph.android.recyclerviewsample

import io.reactivex.Completable

object ApiClient {

    fun like(id: Int): Completable = Completable.create { emitter ->
        try {
            if (id % 2 == 0) {
                Thread.sleep(1000)
                emitter.onComplete()
            } else {
                Thread.sleep(2000)
                emitter.onError(RuntimeException())
            }
        } catch (ex: Exception) {
            emitter.onError(ex)
        }
    }

    fun unlike(id: Int): Completable = Completable.create { emitter ->
        try {
            if (id % 2 == 0) {
                Thread.sleep(1000)
                emitter.onComplete()
            } else {
                Thread.sleep(2000)
                emitter.onError(RuntimeException())
            }
        } catch (ex: Exception) {
            emitter.onError(ex)
        }
    }
}