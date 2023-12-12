import ComposableArchitecture
import DateTimeLibrary
import Foundation
import StringsLibrary

extension ArchiveList.State {
	mutating func updateItems() {
		let allItems =
			archivedGamesToItems() + archivedSeriesToItems() + archivedBowlersToItems() + archivedLeaguesToItems()
		archived = .init(uniqueElements: allItems.sorted(by: archiveItemSort()))
	}

	func archivedBowlersToItems() -> [ArchiveItem] {
		archivedBowlers.map { .init(
			id: .bowler($0.id),
			title: $0.name,
			subtitle: Strings.Archive.List.Bowler.description(
				$0.totalNumberOfLeagues,
				$0.totalNumberOfSeries,
				$0.totalNumberOfGames
			),
			archivedOn: $0.archivedOn
		)}
	}

	func archivedLeaguesToItems() -> [ArchiveItem] {
		archivedLeagues.map { .init(
			id: .league($0.id),
			title: $0.name,
			subtitle: Strings.Archive.List.League.description(
				$0.bowlerName,
				$0.totalNumberOfSeries,
				$0.totalNumberOfGames
			),
			archivedOn: $0.archivedOn
		)}
	}

	func archivedSeriesToItems() -> [ArchiveItem] {
		archivedSeries.map { .init(
			id: .series($0.id),
			title: $0.date.longFormat,
			subtitle: Strings.Archive.List.Series.description(
				$0.bowlerName,
				$0.leagueName,
				$0.totalNumberOfGames
			),
			archivedOn: $0.archivedOn
		)}
	}

	func archivedGamesToItems() -> [ArchiveItem] {
		archivedGames.map { .init(
			id: .game($0.id),
			title: Strings.Archive.List.Game.title($0.scoringMethod, $0.score),
			subtitle: Strings.Archive.List.Game.description(
				$0.bowlerName,
				$0.leagueName,
				$0.seriesDate.longFormat
			),
			archivedOn: $0.archivedOn
		)}
	}

	private func archiveItemSort() -> ((ArchiveItem, ArchiveItem) -> Bool) {
		let date = Date()
		return { first, second in (first.archivedOn ?? date) > (second.archivedOn ?? date) }
	}
}
