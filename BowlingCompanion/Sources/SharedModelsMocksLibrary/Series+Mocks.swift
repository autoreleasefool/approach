import Foundation
import SharedModelsLibrary

extension Series {
	public static func mock(
		league: UUID,
		id: UUID,
		date: Date,
		numberOfGames: Int = League.DEFAULT_NUMBER_OF_GAMES,
		alley: UUID? = nil
	) -> Series {
		.init(
			league: league,
			id: id,
			date: date,
			numberOfGames: numberOfGames,
			alley: alley
		)
	}
}
