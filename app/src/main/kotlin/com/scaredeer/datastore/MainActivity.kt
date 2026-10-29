package com.scaredeer.datastore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scaredeer.datastore.ui.MainScreen
import com.scaredeer.datastore.ui.MainViewModel
import com.scaredeer.datastore.ui.theme.AppTheme

/*
 * DataStore（Flow を前提）の話は、Compose の話とは別で、Compose で使わないのであれば、ViewModel は関係なく、
 * 単に、Coroutine として（Coroutine スコープの中で）使えばよい。
 * ViewModel 云々は Compose から用意した DataStore を使うための話であり、State を emit したり observe する
 * という、MutableState（LiveData の Kotlin 版）一般の話が絡んできている。このような Compose から observe する
 * データについては、ViewModel を介して扱うのが通常のユースケースとして想定されている。
 * そのため、DataStore に関しても、ViewModel 経由で、Compose から使っているわけである。DataStore 自体の使い方の
 * 話としては、ViewModel 云々は本質ではない。公式サンプルではさらに、ViewModel で使うべき DataStore の初期化を、
 * Application コンテキストで行っている。これも、他の処理に先立ってできるだけ先行して行う、という一般的な話によるもの
 * であり、DataStore に固有のルールというわけではない。
 * 毎度のことながら、公式サンプルは、特定テーマの学習用のサンプルとしての立ち位置から軌道を外れて、多面的なノウハウを
 * 盛り込んだ模範的コードに仕立て上げてショーケースに飾られた感じになっているので、解読に負荷を強いるものと化している。
 * 従来の SharedPreferences を使っていた状況に比して、何でこんなわかり辛くなっているんだと、尻込みする旧来のプログ
 * ラマーがいたとしても不思議ではない。半分以上、公式サンプル（とそれに基くチュートリアル）のせい。
 *
 * 断片的な解説
 * https://developer.android.com/topic/libraries/architecture/datastore?hl=ja
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:datastore/datastore-sampleapp/src/main/java/com/example/datastore/snippets/preferences/PreferencesDataStore.kt?hl=ja
 *
 * おそらく最新のチュートリアル
 * https://developer.android.com/codelabs/basic-android-kotlin-compose-datastore?hl=ja&authuser=2#0
 * https://github.com/google-developer-training/basic-android-kotlin-compose-training-dessert-release/tree/main/app/src/main/java/com/example/dessertrelease
 *
 * 微妙に古いチュートリアル
 * https://developer.android.com/codelabs/android-preferences-datastore?hl=ja&authuser=2#0
 *
 * 他、個人のブログ
 * https://zenn.dev/yt_hsgw/books/android-basics/viewer/async-datastore
 * https://qiita.com/akikuchi/items/1868bd7064a0b09033a7
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)

            AppTheme {
                MainScreen(
                    /*
                     * cf. Safely collect the state
                     * https://developer.android.com/topic/libraries/architecture/datastore?hl=ja#compose-sample
                     */
                    assignee = mainViewModel.assignee.collectAsStateWithLifecycle().value,
                    assignTo = mainViewModel::assignTo
                )
            }
        }
    }
}