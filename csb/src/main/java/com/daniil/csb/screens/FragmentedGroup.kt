package com.daniil.csb.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.settings.utils.ComposeSetting
import com.daniil.csb.settings.utils.SettingBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FragmentedGroup(
    override val id: String,
    isHide: Boolean,
    val initialActive: String,
    val paddingValues: PaddingValues = PaddingValues.Zero,
    override val groupTitle: GroupTitle? = null,
    val modifier: Modifier = Modifier,
    val onChangedGroup: (String) -> Unit = {},
    val unfragmentedGroup: Group? = null,
    val groups: Map<String, Group>,
    private val controller: FragmentController? = null
): GroupSealed() {

    private val _activeFragment = MutableStateFlow(groups[initialActive]!!.id)
    val activeFragmentId = _activeFragment.asStateFlow()
    val currentFragment = MutableStateFlow<Group>(groups[activeFragmentId.value]!!)

    init {
        controller?.bind(this)
    }
    fun changeFragment(key: String) {
        _activeFragment.value = key
        controller?.updateId(key)
        currentFragment.update {
            onChangedGroup(key)
            groups[key]
                ?: error("Not found fragment key: \"${activeFragmentId.value}\" in fragmentated group: \"$id\"")
        }
    }

    private val _hide = MutableStateFlow(isHide)
    override val settings: List<ComposeSetting<*>>
        get() = groups.flatMap { it.value.settings }
    override val hide = _hide.asStateFlow()
    override fun hide() { _hide.value = true }
    override fun show() { _hide.value = false }
}

@CsbDslMarkers
class FragmentScope() : GroupScope() {
//    var groupTitle: GroupTitle? = DefaultGroupTitle
}

@CsbDslMarkers
class FragmentedScopeBuilder(): GroupScope() {
    val fragmentsHeap: MutableMap<String, Group> = mutableMapOf()
    var onChangeFragment: (String) -> Unit = {}
    var initialFragmentId: String? = null
    var paddingValues = PaddingValues.Zero
    var modifier: Modifier = Modifier

    private var _controller: FragmentController? = null
    var controller: FragmentController
        get() {
            if (_controller == null) _controller = FragmentController()
            return _controller!!
        }
        set(value) { _controller = value }

    fun fragment(id: String, fragmentedScope: FragmentScope.() -> Unit) {
        val fragment = FragmentScope().apply(fragmentedScope)
        val groupTitle = if (fragment.groupTitle is DefaultGroupTitle) GroupTitle(id) else fragment.groupTitle
        fragmentsHeap[id] = Group(id, groupTitle, false, fragment.settings)
    }

    internal fun build(id: String, isHide: Boolean): FragmentedGroup {
        val groupTitle = if (groupTitle is DefaultGroupTitle) null else groupTitle
        val unfragmentedGroup = super.settings.takeIf { it.isNotEmpty() }?.let { Group("unfragmented_$id", null, false, super.settings) }
        val fragmented = FragmentedGroup(
            id = id,
            isHide = isHide,
            modifier = modifier,
            groupTitle = groupTitle,
            paddingValues = paddingValues,
            initialActive = initialFragmentId ?: fragmentsHeap.keys.first(),
            onChangedGroup = onChangeFragment,
            unfragmentedGroup = unfragmentedGroup,
            groups = fragmentsHeap,
            controller = _controller
        )
        return fragmented
    }
}

class FragmentController(fragmentedGroup: FragmentedGroup? = null) {
    private var _fragmentedGroup: FragmentedGroup? = null

    private val _groups = MutableStateFlow<Map<String, Group>>(emptyMap())
    val groups: StateFlow<Map<String, Group>> = _groups.asStateFlow()

    private val _currentFragmentId = MutableStateFlow("")
    val currentFragmentId: StateFlow<String> = _currentFragmentId.asStateFlow()

    init {
        fragmentedGroup?.let { bind(it) }
    }

    internal val initialValue: String get() = _fragmentedGroup?.initialActive ?: ""

    fun changeFragment(key: String) {
        _fragmentedGroup?.changeFragment(key)
        _currentFragmentId.value = key
    }

    fun isShow(state: Boolean) {
        if (state) _fragmentedGroup?.show() else _fragmentedGroup?.hide()
    }

    fun isDisable(state: Boolean) {
        _fragmentedGroup?.settings?.forEach { it.enabled(!state) }
    }

    internal fun bind(fragmentedGroup: FragmentedGroup) {
        this._fragmentedGroup = fragmentedGroup
        this._groups.value = fragmentedGroup.groups
        this._currentFragmentId.value = fragmentedGroup.activeFragmentId.value
    }

    internal fun updateId(key: String) {
        _currentFragmentId.value = key
    }
}
