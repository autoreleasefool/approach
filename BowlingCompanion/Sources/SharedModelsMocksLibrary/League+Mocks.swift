import Foundation
import SharedModelsLibrary

extension League {
	public static func mock(
		bowler: UUID,
		id: UUID,
		name: String = "Majors, 2022",
		recurrence: Recurrence = .repeating,
		numberOfGames: Int? = League.DEFAULT_NUMBER_OF_GAMES,
		additionalPinfall: Int? = nil,
		additionalGames: Int? = nil,
		alley: UUID? = nil
	) -> League {
		.init(
			bowler: bowler,
			id: id,
			name: name,
			recurrence: recurrence,
			numberOfGames: numberOfGames,
			additionalPinfall: additionalPinfall,
			additionalGames: additionalGames,
			alley: alley
		)
	}
}
