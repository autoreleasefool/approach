import Foundation
import SharedModelsLibrary

extension Lane {
	public static func mock(
		id: UUID,
		label: String = "",
		isAgainstWall: Bool = false,
		alley: Alley.ID
	) -> Lane {
		.init(
			id: id,
			label: label,
			isAgainstWall: isAgainstWall,
			alley: alley
		)
	}
}
