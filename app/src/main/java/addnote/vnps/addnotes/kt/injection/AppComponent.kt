package addnote.vnps.addnotes.kt.injection

import addnote.vnps.addnotes.analytics.AnalyticsApplication
import dagger.Component

@Component(modules = [ViewModelModule::class])
@AppScope
interface AppComponent {
    fun inject(app: AnalyticsApplication)

   // fun inject(searchFragment: AlbumSearchFragment)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        //fun setRoomModule(module: RoomModule): Builder
    }
}