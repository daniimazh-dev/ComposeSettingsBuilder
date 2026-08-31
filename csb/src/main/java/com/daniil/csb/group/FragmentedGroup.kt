package com.daniil.csb.group

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import com.daniil.csb.CsbDslMarkers
import com.daniil.csb.group.title.GroupTitle
import com.daniil.csb.settings.utils.ComposeSetting
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val controller: FragmentController
): GroupSealed() {

    private val _activeFragment = MutableStateFlow(groups[initialActive]!!.id)
    val activeFragmentId = _activeFragment.asStateFlow()
    val currentFragment = MutableStateFlow<Group>(groups[activeFragmentId.value]!!)

    init {
        controller.bind(this)
    }
    fun changeFragment(key: String) {
        _activeFragment.value = key
        controller.updateCurrentFragmentId(key)
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
class FragmentScope(id: String) : GroupScope(id)

@CsbDslMarkers
class FragmentedScopeBuilder(id: String): GroupScope(id) {
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
        val fragment = FragmentScope(id).apply(fragmentedScope)
        fragmentsHeap[id] = Group(id, fragment.groupTitle, fragment.isHide, fragment.settings)
    }

    internal fun build(id: String): FragmentedGroup {
        val unfragmentedGroup = super.settings.takeIf { it.isNotEmpty() }?.let {
            Group(
                "unfragmented_$id",
                groupTitle,
                false,
                super.settings
            )
        }
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
            controller = controller
        )
        return fragmented
    }
}
