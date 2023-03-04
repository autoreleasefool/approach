import Foundation
import SharedModelsLibrary

extension Opponent {
	public static func mock(
		id: UUID,
		name: String = "Joseph"
	) -> Opponent {
		.init(
			id: id,
			name: name
		)
	}
}
