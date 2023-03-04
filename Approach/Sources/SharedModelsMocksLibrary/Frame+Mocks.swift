import Foundation
import SharedModelsLibrary

extension Frame {
	public static func mock(
		id: UUID,
		ordinal: Int,
		firstBall: Ball? = nil,
		secondBall: Ball? = nil,
		thirdBall: Ball? = nil
	) -> Frame {
		.init(
			game: id,
			ordinal: ordinal,
			firstBall: firstBall,
			secondBall: secondBall,
			thirdBall: thirdBall
		)
	}
}
