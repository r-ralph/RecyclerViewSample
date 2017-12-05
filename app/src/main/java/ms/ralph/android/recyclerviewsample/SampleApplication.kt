package ms.ralph.android.recyclerviewsample

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule


class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Iconify.with(FontAwesomeModule())
    }
}