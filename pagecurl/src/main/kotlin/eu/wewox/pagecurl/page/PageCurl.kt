package eu.wewox.pagecurl.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import eu.wewox.pagecurl.ExperimentalPageCurlApi

/**
 * Shows the pages which may be turned by drag or tap gestures.
 *
 * @param state The state of the PageCurl. Use this to programmatically change the current page or observe changes.
 * @param modifier The modifier for this composable.
 * @param content The content lambda to provide the page composable. Receives the page number.
 */
@ExperimentalPageCurlApi
@Composable
public fun PageCurl(
    state: PageCurlState,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val updatedCurrent by rememberUpdatedState(state.current)

    BoxWithConstraints(modifier) {
        state.setup(constraints)

        val internalState by rememberUpdatedState(state.internalState ?: return@BoxWithConstraints)

        val config by rememberUpdatedState(state.config)

        Box(
            Modifier
                .curlGesture(
                    state = internalState,
                    enabled = state.config.dragForwardEnabled && updatedCurrent < state.max - 1,
                    scope = scope,
                    targetStart = config.dragForwardInteraction.start,
                    targetEnd = config.dragForwardInteraction.end,
                    edgeStart = internalState.rightEdge,
                    edgeEnd = internalState.leftEdge,
                    edge = internalState.forward,
                    onChange = { state.current = updatedCurrent + 1 }
                )
                .curlGesture(
                    state = internalState,
                    enabled = state.config.dragBackwardEnabled && updatedCurrent > 0,
                    scope = scope,
                    targetStart = config.dragBackwardInteraction.start,
                    targetEnd = config.dragBackwardInteraction.end,
                    edgeStart = internalState.leftEdge,
                    edgeEnd = internalState.rightEdge,
                    edge = internalState.backward,
                    onChange = { state.current = updatedCurrent - 1 }
                )
                .tapGesture(
                    config = config,
                    scope = scope,
                    onTapForward = state::next,
                    onTapBackward = state::prev,
                )
        ) {
            if (updatedCurrent + 1 < state.max) {
                content(updatedCurrent + 1)
            }

            if (updatedCurrent < state.max) {
                Box(Modifier.drawCurl(config, internalState.forward.value.top, internalState.forward.value.bottom)) {
                    content(updatedCurrent)
                }
            }

            if (updatedCurrent > 0) {
                Box(Modifier.drawCurl(config, internalState.backward.value.top, internalState.backward.value.bottom)) {
                    content(updatedCurrent - 1)
                }
            }
        }
    }
}
