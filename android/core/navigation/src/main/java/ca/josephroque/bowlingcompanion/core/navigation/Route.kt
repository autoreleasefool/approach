package ca.josephroque.bowlingcompanion.core.navigation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.ResourcePickerType
import ca.josephroque.bowlingcompanion.core.model.StatisticsDetailsSourceType
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import java.util.UUID

sealed class Route(
	val route: String,
	val isBottomBarVisible: Boolean = true,
) {
	// Sheets
	data object QuickPlay: Route("quick_play", isBottomBarVisible = true)

	// Accessories
	data object AccessoriesOverview: Route("accessories_overview")
	data object AccessoriesOnboarding: Route("accessories_onboarding", isBottomBarVisible = false)

	// Acknowledgements
	data object Acknowledgements: Route("acknowledgements")
	data object AcknowledgementDetails: Route("acknowledgement/{acknowledgement}") {
		const val ARG_ACKNOWLEDGEMENT = "acknowledgement"
		fun createRoute(acknowledgement: String): String = "acknowledgement/${Uri.encode(acknowledgement)}"
		fun getAcknowledgement(savedStateHandle: SavedStateHandle): String? = savedStateHandle.get<String>("acknowledgement")
	}

	// Alleys
	data object AlleysList: Route("alleys")
	data object AddAlley: Route("add_alley", isBottomBarVisible = false)
	data object EditAlley: Route("edit_alley/{alley}", isBottomBarVisible = false) {
		const val ARG_ALLEY = "alley"
		fun createRoute(alley: UUID): String = "edit_alley/${Uri.encode(alley.toString())}"
		fun getAlley(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("alley")?.let { UUID.fromString(it) }
	}

	// Archives
	data object ArchivesList: Route("archives")

	// Avatars
	data object EditAvatar: Route("edit_avatar/{avatar}", isBottomBarVisible = false) {
		const val ARG_AVATAR = "avatar"
		fun createRoute(avatar: String): String = "edit_avatar/${Uri.encode(avatar)}"
		fun getAvatar(savedStateHandle: SavedStateHandle): String? = savedStateHandle.get<String>("avatar")
	}

	// Bowlers
	data object BowlerDetails: Route("bowler/{bowler}") {
		const val ARG_BOWLER = "bowler"
		fun createRoute(bowler: UUID): String = "bowler/${Uri.encode(bowler.toString())}"
		fun getBowler(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("bowler")?.let { UUID.fromString(it) }
	}
	data object EditBowler: Route("edit_bowler/{bowler}", isBottomBarVisible = false) {
		const val ARG_BOWLER = "bowler"
		fun createRoute(bowler: UUID): String = "edit_bowler/${Uri.encode(bowler.toString())}"
		fun getBowler(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("bowler")?.let { UUID.fromString(it) }
	}
	data object AddBowler: Route("add_bowler/{kind}", isBottomBarVisible = false) {
		const val ARG_KIND = "kind"
		fun createRoute(kind: String): String = "add_bowler/${Uri.encode(kind)}"
		fun getKind(savedStateHandle: SavedStateHandle): BowlerKind? = savedStateHandle.get<BowlerKind>("kind")
	}

	// Data Export
	data object DataExport: Route("data_export", isBottomBarVisible = false)
	data object DataImport: Route("data_import", isBottomBarVisible = false)

	// Game
	data object GameSettings: Route("games_settings/{series}/{current_game}", isBottomBarVisible = false) {
		const val ARG_SERIES = "series"
		const val ARG_CURRENT_GAME = "current_game"
		fun createRoute(series: UUID, currentGame: UUID): String = "games_settings/${Uri.encode(series.toString())}/${Uri.encode(currentGame.toString())}"
		fun getSeries(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("series")?.let { UUID.fromString(it) }
		fun getCurrentGame(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("current_game")?.let { UUID.fromString(it) }
	}
	data object EditGame: Route("edit_game/{series}/{game}", isBottomBarVisible = false) {
		const val ARG_SERIES = "series"
		const val ARG_GAME = "game"
		fun createRoute(series: UUID, game: UUID): String = "edit_game/${Uri.encode(series.toString())}/${Uri.encode(game.toString())}"
		fun getGame(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("game")?.let { UUID.fromString(it) }
		fun getSeries(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("series")?.let { UUID.fromString(it) }
	}

	// Gear
	data object GearList: Route("gear")
	data object EditGear: Route("edit_gear/{gear}", isBottomBarVisible = false) {
		const val ARG_GEAR = "gear"
		fun createRoute(gear: UUID): String = "edit_gear/${Uri.encode(gear.toString())}"
		fun getGear(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("gear")?.let { UUID.fromString(it) }
	}
	data object AddGear: Route("add_gear", isBottomBarVisible = false)

	// Lanes
	data object EditLanes: Route("edit_lanes/{lanes}", isBottomBarVisible = false) {
		const val ARG_LANES = "lanes"
		fun createRoute(lanes: List<String>): String = "edit_lanes/${lanes.encode()}"
		fun getLanes(savedStateHandle: SavedStateHandle): List<UUID> = savedStateHandle.get<String>("lanes")?.decodeList()?.mapNotNull { UUID.fromString(it) } ?: emptyList()
	}

	// Leagues
	data object LeagueDetails: Route("league/{league}") {
		const val ARG_LEAGUE = "league"
		fun createRoute(league: UUID): String = "league/${Uri.encode(league.toString())}"
		fun getLeague(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("league")?.let { UUID.fromString(it) }
	}
	data object AddLeague: Route("add_league/{bowler}", isBottomBarVisible = false) {
		const val ARG_BOWLER = "bowler"
		fun createRoute(bowler: UUID): String = "add_league/${Uri.encode(bowler.toString())}"
		fun getBowler(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("bowler")?.let { UUID.fromString(it) }
	}
	data object EditLeague: Route("edit_league/{league}", isBottomBarVisible = false) {
		const val ARG_LEAGUE = "league"
		fun createRoute(league: UUID): String = "edit_league/${Uri.encode(league.toString())}"
		fun getLeague(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("league")?.let { UUID.fromString(it) }
	}

	// Match Plays
	data object EditMatchPlay: Route("edit_match_play/{game}", isBottomBarVisible = false) {
		const val ARG_GAME = "game"
		fun createRoute(game: UUID): String = "edit_match_play/${Uri.encode(game.toString())}"
		fun getGame(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("game")?.let { UUID.fromString(it) }
	}

	// Onboarding
	data object Onboarding: Route("onboarding", isBottomBarVisible = false)

	// Opponents
	data object OpponentsList: Route("opponents")

	// Overview
	data object Overview: Route("overview")

	// Resource Picker
	data object ResourcePicker: Route("resource_picker/{type}/{filter}/{selected}/{hidden}/{limit}/{title}", isBottomBarVisible = false) {
		const val RESOURCE_TYPE = "type"
		const val RESOURCE_FILTER = "filter"
		const val SELECTED_IDS = "selected"
		const val HIDDEN_IDS = "hidden"
		const val SELECTION_LIMIT = "limit"
		const val TITLE_OVERRIDE = "title"
		fun createRoute(
			resourceType: String,
			resourceFilter: String?,
			selectedIds: Set<UUID>,
			hiddenIds: Set<UUID>,
			limit: Int,
			titleOverride: String?,
		): String = "resource_picker/" +
				"${Uri.encode(resourceType)}/" +
				"${Uri.encode(resourceFilter)}/" +
				"${selectedIds.toList().encode()}/" +
				"${hiddenIds.toList().encode()}/" +
				"${Uri.encode(limit.toString())}/" +
				Uri.encode(titleOverride)
		fun getResourceType(savedStateHandle: SavedStateHandle): ResourcePickerType? = savedStateHandle.get<ResourcePickerType>("type")
		fun getResourceFilter(savedStateHandle: SavedStateHandle): String? = savedStateHandle.get<String>("filter")
		fun getSelectedIds(savedStateHandle: SavedStateHandle): Set<UUID> = savedStateHandle.get<String>("selected")?.decodeList()?.mapNotNull { UUID.fromString(it) }?.toSet() ?: emptySet()
		fun getHiddenIds(savedStateHandle: SavedStateHandle): Set<UUID> = savedStateHandle.get<String>("hidden")?.decodeList()?.mapNotNull { UUID.fromString(it) }?.toSet() ?: emptySet()
		fun getLimit(savedStateHandle: SavedStateHandle): Int? = savedStateHandle.get<Int>("limit")
		fun getTitleOverride(savedStateHandle: SavedStateHandle): String? = savedStateHandle.get<String>("title")
	}

	// Series
	data object AddSeries: Route("add_series/{league}", isBottomBarVisible = false) {
		const val ARG_LEAGUE = "league"
		fun createRoute(league: UUID): String = "add_series/${Uri.encode(league.toString())}"
		fun getLeague(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("league")?.let { UUID.fromString(it) }
	}
	data object EditSeries: Route("edit_series/{series}", isBottomBarVisible = false) {
		const val ARG_SERIES = "series"
		fun createRoute(series: UUID): String = "edit_series/${Uri.encode(series.toString())}"
		fun getSeries(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("series")?.let { UUID.fromString(it) }
	}
	data object SeriesDetails: Route("series/{series}") {
		const val ARG_SERIES = "series"
		fun createRoute(series: UUID): String = "series/${Uri.encode(series.toString())}"
		fun getSeries(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("series")?.let { UUID.fromString(it) }
	}
	data object EventDetails: Route("event/{event}") {
		const val ARG_EVENT = "event"
		fun createRoute(event: UUID): String = "event/${Uri.encode(event.toString())}"
		fun getEvent(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("event")?.let { UUID.fromString(it) }
	}

	// Settings
	data object Settings: Route("settings_overview")
	data object AnalyticsSettings: Route("analytics_settings")
	data object DeveloperSettings: Route("developer_settings")

	// Statistics
	data object StatisticsOverview: Route("statistics_overview")
	data object StatisticsSourcePicker: Route("statistics_source_picker")

	data object StatisticsSettings: Route("statistics_settings")
	data object StatisticsDetails: Route("statistics_details/{source_type}/{source_id}", isBottomBarVisible = false) {
		const val ARG_SOURCE_TYPE = "source_type"
		const val ARG_SOURCE_ID = "source_id"
		fun createRoute(sourceType: String, sourceId: UUID): String = "statistics_details/${Uri.encode(sourceType)}/${Uri.encode(sourceId.toString())}"
		fun getSourceType(savedStateHandle: SavedStateHandle): StatisticsDetailsSourceType? = savedStateHandle.get<StatisticsDetailsSourceType>("source_type")
		fun getSourceId(savedStateHandle: SavedStateHandle): UUID? = savedStateHandle.get<String>("source_id")?.let { UUID.fromString(it) }
	}
	data object StatisticsPicker: Route("statistics_picker/{statistic}") {
		const val ARG_STATISTIC = "statistic"
		fun createRoute(statistic: String): String = "statistics_picker/${Uri.encode(statistic)}"
		fun getStatistic(savedStateHandle: SavedStateHandle): StatisticID? = savedStateHandle.get<StatisticID>("statistic")
	}
	data object StatisticsWidgetEditor: Route("statisticswidgeteditor/{context}/{initial_source}/{priority}") {
		const val CONTEXT = "context"
		const val INITIAL_SOURCE = "initial_source"
		const val PRIORITY = "priority"
		fun createRoute(context: String, initialSource: String?, priority: Int): String = "statisticswidgeteditor/${Uri.encode(context)}/${Uri.encode(initialSource)}/$priority"
		fun getContext(savedStateHandle: SavedStateHandle): String? = savedStateHandle.get<String>("context")
		fun getInitialSource(savedStateHandle: SavedStateHandle): String? = savedStateHandle.get<String>("initial_source")
		fun getPriority(savedStateHandle: SavedStateHandle): Int? = savedStateHandle.get<Int>("priority")
	}
	data object StatisticsWidgetLayoutEditor: Route("statisticswidgetlayouteditor/{context}/{initial_source}") {
		const val CONTEXT = "context"
		const val INITIAL_SOURCE = "initial_source"
		fun createRoute(context: String, initialSource: String?): String = "statisticswidgetlayouteditor/${Uri.encode(context)}/${Uri.encode(initialSource)}"
		fun getContext(savedStateHandle: SavedStateHandle): String? = savedStateHandle.get<String>("context")
		fun getInitialSource(savedStateHandle: SavedStateHandle): String? = savedStateHandle.get<String>("initial_source")
	}
}

fun <T> List<T>.encode(): String = if (isEmpty()) "nan" else joinToString(",") { Uri.encode(it.toString()) }
fun String.decodeList(): List<String> = if (this == "nan") emptyList() else split(",").map { Uri.decode(it) }