import Foundation
import SharedModelsLibrary

extension Gear {
	public static func mock(
		bowler: UUID? = nil,
		id: UUID,
		name: String = "Ball",
		kind: Kind = .bowlingBall
	) -> Gear {
		.init(
			bowler: bowler,
			id: id,
			name: name,
			kind: kind
		)
	}
}
