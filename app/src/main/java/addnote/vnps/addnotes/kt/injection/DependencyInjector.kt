package addnote.vnps.addnotes.kt.injection

import addnote.vnps.addnotes.analytics.AnalyticsApplication
import androidx.annotation.NonNull

class DependencyInjector {
    companion object {
        private lateinit var applicationComponent: AppComponent

        /**
         * Builds the app Component.
         */
        @JvmStatic
        fun initialize(app: AnalyticsApplication) {
            applicationComponent = DaggerAppComponent.builder()
                // .setRoomModule(RoomModule(app))
                .build()
        }

        @NonNull
        fun applicationComponent(): AppComponent? {
            return applicationComponent
        }
    }
}