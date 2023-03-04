import Foundation
import SharedModelsLibrary

extension Bowler {
	public static func mock(
		id: UUID,
		name: String = "Joseph",
		avatar: Avatar = .text("JR", .random())
	) -> Bowler {
		.init(
			id: id,
			name: name,
			avatar: avatar
		)
	}
}
