import Foundation
import SharedModelsLibrary

extension Frame {
	public static func mock(
		id: UUID,
		ordinal: Int,
		rolls: [Roll] = []
	) -> Frame {
		.init(
			game: id,
			ordinal: ordinal,
			rolls: rolls
		)
	}
}
