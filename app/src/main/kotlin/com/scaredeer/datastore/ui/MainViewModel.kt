package com.scaredeer.datastore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.scaredeer.datastore.MainApplication
import com.scaredeer.datastore.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
 * cf. View model of Dessert Release components
 * https://github.com/google-developer-training/basic-android-kotlin-compose-training-dessert-release/blob/main/app/src/main/java/com/example/dessertrelease/ui/DessertReleaseViewModel.kt
 */
class MainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    /*
     * 公式サンプルの DessertRelease では、DataStore に格納しているのは、isLinearLayout: Boolean フラグのみ
     * だが、ViewModel 側においては、ついでに、toggleContentDescription と toggleIcon も抱き合わせて、全体で
     * UiState オブジェクトとしてまとめて定義したものを管理している。もちろん、利用（View）側で条件分岐するコードを
     * 記述しても構わないのだろうが、このように状態管理は ViewModel に任せて View 側では受け身なコードにした方がよ
     * り「（MVVM）らしく」はある。
     * だが一方、Compose 的にはそういった条件分岐コードをも内包して記述する感じであり、本サンプルのケースにおいては、
     * Preference DataStore を Compose から利用するために「仕方なく」ViewModel を介して使っているという、本来の
     * 背景に立ち帰ってみれば、公式サンプルのようにドヤ顔で MVVM パターンを積極的に利用して見せびらかしたいわけでは
     * なく、Compose の宣言型パターンの実現が本命なのであり、ViewModel で記述するのは最低限のフラグとしての扱いに
     * 留めるべき、とも思えるので、そうしている。
     */
    val assignee: StateFlow<String?> =
        userPreferencesRepository.assignee.stateIn(
            scope = viewModelScope,
            // Flow is set to emits value for when app is on the foreground
            // 5 seconds stop delay is added to ensure it flows continuously
            // for cases such as configuration change
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking {
                userPreferencesRepository.assignee.first()
            }
        )

    /*
     * 大元の DataStore を書き換えることになるので、改めて DataStore → ViewModel → Compose と反映されて、
     * UI (View) の状態に反映されることになる。
     * （Compose で直接、Compose 自身の状態を変更し、一方で結果を DataStore「にも」保存しておいて、次回のアプリ
     * 起動時に再現できるようにする、というような手の二股の流れの処理構造にはしていない点に留意）
     */
    fun assignTo(assignee: String?) {
        viewModelScope.launch {
            userPreferencesRepository.saveAssignee(assignee)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MainApplication)
                MainViewModel(application.userPreferencesRepository)
            }
        }
    }
}