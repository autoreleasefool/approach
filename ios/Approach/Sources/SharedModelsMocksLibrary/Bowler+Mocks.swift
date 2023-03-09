import Foundation
import SharedModelsLibrary

extension Bowler {
	public static func mock(
		id: UUID,
		name: String = "Joseph",
		avatar: Avatar = .text("JR", .red())
	) -> Bowler {
		.init(
			id: id,
			name: name,
			avatar: avatar
		)
	}
}
