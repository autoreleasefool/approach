import Foundation
import SharedModelsLibrary

extension Series {
	public static func mock(
		league: UUID,
		id: UUID,
		date: Date,
		numberOfGames: Int = League.DEFAULT_NUMBER_OF_GAMES,
		preBowl: PreBowl = .regularPlay,
		excludeFromStatistics: ExcludeFromStatistics = .include,
		alley: UUID? = nil,
		lane: UUID? = nil
	) -> Series {
		.init(
			league: league,
			id: id,
			date: date,
			numberOfGames: numberOfGames,
			preBowl: preBowl,
			excludeFromStatistics: excludeFromStatistics,
			alley: alley,
			lane: lane
		)
	}
}
