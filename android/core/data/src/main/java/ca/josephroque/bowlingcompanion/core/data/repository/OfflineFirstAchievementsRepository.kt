package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.achievements.ConsumableAchievementEvent
import ca.josephroque.bowlingcompanion.core.achievements.EarnableAchievement
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.AchievementDao
import ca.josephroque.bowlingcompanion.core.database.dao.AchievementEventDao
import ca.josephroque.bowlingcompanion.core.database.model.AchievementCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.toCreateEntity
import ca.josephroque.bowlingcompanion.core.model.Achievement
import ca.josephroque.bowlingcompanion.core.model.AchievementEvent
import ca.josephroque.bowlingcompanion.core.model.AchievementEventID
import ca.josephroque.bowlingcompanion.core.model.AchievementID
import ca.josephroque.bowlingcompanion.core.model.AchievementListItem
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class OfflineFirstAchievementsRepository @Inject constructor(
	private val achievementDao: AchievementDao,
	private val achievementEventDao: AchievementEventDao,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : AchievementsRepository {

	private val eventLock = Mutex()

	override fun getEarnedAchievements(): Flow<List<AchievementListItem>> {
		val indexedOrder = EarnableAchievement.all
			.map { it.id.name }
			.withIndex()
			.associate { it.value to it.index }

		return achievementDao.getEarnedAchievements()
			.map {
				it.sortedBy { indexedOrder[it.title] ?: Int.MAX_VALUE }
			}
	}

	override fun getLatestAchievement(startDate: Instant): Flow<Achievement?> {
		return achievementDao.getLatestAchievement(startDate)
	}

	override suspend fun hasEarnedAchievement(achievement: EarnableAchievement): Boolean {
		return achievementDao.hasEarnedAchievement(achievement.id.name)
	}

	override suspend fun insertEvent(event: ConsumableAchievementEvent) = withContext(ioDispatcher) {
		eventLock.withLock {
			val achievement = EarnableAchievement.fromEvent(event) ?: return@withLock
			val relevantEvents = achievement.events.map { it.title }

			val newEvent = AchievementEvent(id = AchievementEventID.randomID(), title = event.title, isConsumed = false)
			val unconsumedEvents = achievementEventDao.getUnconsumedEvents(relevantEvents) + newEvent

			val (earned, consumed) = achievement.consume(
				unconsumedEvents
					.map { relevantEvents.indexOf(it.title) }
					.filter { it != -1 }
					.map { achievement.events[it] },
			)

			if (earned.isEmpty()) return@withLock

			val earnedAchievements = earned.map { it.toCreateEntity(id = AchievementID.randomID(), date = Clock.System.now()) }
			achievementDao.insertAll(earnedAchievements)

			val remainingConsumed = consumed.toMutableList()
			val consumedEvents = unconsumedEvents.filter { unconsumed ->
				val matchingRemainingIndex = remainingConsumed.indexOfFirst { remaining -> remaining.title == unconsumed.title }
				if (matchingRemainingIndex != -1) {
					remainingConsumed.removeAt(matchingRemainingIndex)
					true
				} else {
					false
				}
			}

			achievementEventDao.insertEvent(newEvent.toCreateEntity())
			achievementEventDao.consumeEvents(consumedEvents.map { it.id })
		}
	}
}

fun EarnableAchievement.toCreateEntity(id: AchievementID, date: Instant) = AchievementCreateEntity(
	id = id,
	title = this.id.name,
	earnedAt = date,
)
