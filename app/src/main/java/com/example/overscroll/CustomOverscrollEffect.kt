import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

// our custom offset overscroll that offset the element it is applied to when we hit the bound
// on the scrollable container.
// Uses OverscrollEffect interface to create a custom overscroll effect class
class CustomOverscrollEffect(val scope: CoroutineScope, val orientation: Orientation) :
    OverscrollEffect {
    private val overscrollOffset = Animatable(0f)

    //Applies overscroll to performScroll
    override fun applyToScroll(
        delta: Offset, source: NestedScrollSource, performScroll: (Offset) -> Offset
    ): Offset {
        // in pre scroll we relax the overscroll if needed
        // relaxation: when we are in progress of the overscroll and user scrolls in the
        // different direction = substract the overscroll first

//        isVertical = abs(delta.y) > abs(delta.x)

        val scrollDelta = if (orientation == Orientation.Vertical) delta.y else delta.x
        val sameDirection = sign(scrollDelta) == sign(overscrollOffset.value)
        val consumedByPreScroll = if (abs(overscrollOffset.value) > 0.5 && !sameDirection) {
            val prevOverscrollValue = overscrollOffset.value
            val newOverscrollValue = overscrollOffset.value + scrollDelta
            //if sign changes, coerce to start scrolling and exit
            if (sign(prevOverscrollValue) != sign(newOverscrollValue)) {
                scope.launch { overscrollOffset.snapTo(0f) }
                // Do not consume any offset
                if (orientation == Orientation.Vertical) {
                    Offset(
                        x = 0f, y = scrollDelta + prevOverscrollValue
                    )
                } else {
                    Offset(x = scrollDelta + prevOverscrollValue, y = 0f)
                }
            } else {
                scope.launch { overscrollOffset.snapTo(overscrollOffset.value + scrollDelta) }
                if (orientation == Orientation.Vertical) delta.copy(x = 0f) else delta.copy(y = 0f)
            }
        } else {
            Offset.Zero
        }
        val leftForScroll = delta - consumedByPreScroll
        val consumedByScroll = performScroll(leftForScroll)
        val overscrollDelta = leftForScroll - consumedByScroll
        // if it is a drag, not a fling, add the delta left to our over scroll value
        val overscrollToAdd =
            if (orientation == Orientation.Vertical) overscrollDelta.y else overscrollDelta.x
        if (abs(overscrollToAdd) > 0.5 && source.equals(MutatePriority.UserInput)) {
            scope.launch {
                // multiply by 0.1 for the sake of parallax effect
                overscrollOffset.snapTo(overscrollOffset.value + overscrollToAdd * 0.1f)
            }
        }
        return consumedByPreScroll + consumedByScroll
    }

    override suspend fun applyToFling(
        velocity: Velocity, performFling: suspend (Velocity) -> Velocity
    ) {
        val consumed = performFling(velocity)
        val remaining = velocity - consumed
        val scrollVelocity = if (orientation == Orientation.Vertical) remaining.y else remaining.x
        // when the fling happens, we just gradually animate our overscroll to 0
        overscrollOffset.animateTo(
            targetValue = 0f, initialVelocity = scrollVelocity, animationSpec = spring()
        )
    }

    override val isInProgress: Boolean
        get() = overscrollOffset.value != 0f

    // Create a LayoutModifierNode that offsets by overscrollOffset.value
    override val node: DelegatableNode = object : Modifier.Node(), LayoutModifierNode {
        override fun MeasureScope.measure(
            measurable: Measurable, constraints: Constraints
        ): MeasureResult {
            val placeable = measurable.measure(constraints)
            return layout(placeable.width, placeable.height) {
                val offsetValue = if (orientation == Orientation.Vertical) IntOffset(
                    x = 0, y = overscrollOffset.value.roundToInt()
                ) else IntOffset(x = overscrollOffset.value.roundToInt(), y = 0)

                placeable.placeRelativeWithLayer(offsetValue.x, offsetValue.y)
            }
        }
    }
}

