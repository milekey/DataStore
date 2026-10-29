package com.scaredeer.datastore.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val TAG = UserPreferencesRepository::class.simpleName

/*
 * Concrete class implementation to access data store
 * cf. https://github.com/google-developer-training/basic-android-kotlin-compose-training-dessert-release/blob/main/app/src/main/java/com/example/dessertrelease/data/UserPreferencesRepository.kt
 *
 * この UserPreferencesRepository クラスの定義と、それに Application で DataStore オブジェクトを渡して
 * 初期化するまでが、Preference DataStore オブジェクトの準備として一通りのセットだということがわかる。
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
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        /*
         * cf. データ定義
         * https://developer.android.com/topic/libraries/architecture/datastore?hl=ja#schema
         * DessertRelease では private、PreferencesDataStore ではデフォルトという違いはあるものの、いずれも
         * companion object 内に定義している点は共通しており、Singleton として使うことが想定されているのがわかる。
         *
         * SharedPreferences と大きく違い、利用する側・段階においてカジュアルにキーを定義できるわけでなく、
         * このように、Repository 側でハードコード的に予め定義しておかなければならないわけで、
         * Preference DataStore によって、従来の SharedPreferences でプログラマーがやれていたことを、
         * 単純に置換できるとはとうてい思えない。
         */
        val ASSIGNEE = stringPreferencesKey("assignee")
    }

    /*
     * cf. DataStore から読み取る
     * https://developer.android.com/topic/libraries/architecture/datastore?hl=ja#read
     */
    val assignee: Flow<String?> = dataStore.data
        .catch {
            /*
             * cf. 例外処理
             * https://developer.android.com/codelabs/basic-android-kotlin-compose-datastore?hl=ja#3
             */
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            if (preferences[ASSIGNEE] == "") null else preferences[ASSIGNEE]
        }

    /*
     * cf. DataStore に書き込む
     * https://developer.android.com/topic/libraries/architecture/datastore?hl=ja#write
     * DessertRelease では .edit を使っているが、PreferencesDataStore では .updateData を使うやり方も
     * 併記されている。
     */
    suspend fun saveAssignee(assignee: String?) {
        dataStore.edit { preferences ->
            preferences[ASSIGNEE] = assignee ?: ""
        }
    }
}