import Foundation
import SharedModelsLibrary

extension Game {
	public static func mock(
		series: UUID,
		id: UUID,
		ordinal: Int,
		locked: LockedState = .unlocked,
		manualScore: Int? = nil
	) -> Game {
		.init(
			series: series,
			id: id,
			ordinal: ordinal,
			locked: locked,
			manualScore: manualScore
		)
	}
}
