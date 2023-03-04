import Foundation
import SharedModelsLibrary

extension Team {
	public static func mock(
		id: UUID,
		name: String = "Junior Boys"
	) -> Team {
		.init(
			id: id,
			name: name
		)
	}
}
