import Foundation
import SharedModelsLibrary

extension Bowler {
	public static func mock(
		id: UUID,
		name: String = "Joseph"
	) -> Bowler {
		.init(
			id: id,
			name: name
		)
	}
}
