package com.scaredeer.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.scaredeer.datastore.data.UserPreferencesRepository

/*
 * cf. DataStore を作成する
 * https://developer.android.com/topic/libraries/architecture/datastore?hl=ja#create
 * ただし、ここでは、DessertRelease に倣い、Application で用意して、Repository の初期化用引数として渡している
 * https://github.com/google-developer-training/basic-android-kotlin-compose-training-dessert-release/blob/main/app/src/main/java/com/example/dessertrelease/DessertReleaseApplication.kt
 *
 * つまり、UserPreferencesRepository クラスの定義と、それに Application で DataStore オブジェクトを渡して
 * 初期化するまでが、DataStore オブジェクトの準備として一通りのセットだということがわかる。
 *
 * ViewModel 以下の下流における部分は、存在する Preference DataStore オブジェクトの利用の仕方にかかる話である。
 * そして主にそれは ViewModel を使った、StateFlow を Compose から collect できるようにするための工夫なのであり、
 * ほとんどが、DataStore そのもの固有の話とは関係がないのものである。
 * というか、例え、SharedPreferences を使うにしても、Compose が絡むと、ViewModel 経由でアクセスする形になるわけ
 * であり、大がかりなっているのはその部分なので、そういう意味では、DataStore にせよ、SharedPreferences にしても
 * 大差はない。
 * Google が、現状は ViewModel 経由でプログラマーが実装する必要のある部分をもっと、カプセル化してくれて、いきなり
 * Compose の中から使えるように何らかの API を用意してくれるべき問題だろう。
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainApplication: Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}