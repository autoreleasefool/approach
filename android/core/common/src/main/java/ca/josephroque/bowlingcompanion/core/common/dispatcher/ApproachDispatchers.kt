package ca.josephroque.bowlingcompanion.core.common.dispatcher

import javax.inject.Qualifier

@Suppress("unused")
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val approachDispatcher: ApproachDispatchers)

enum class ApproachDispatchers {
	Default,
	IO,
}
