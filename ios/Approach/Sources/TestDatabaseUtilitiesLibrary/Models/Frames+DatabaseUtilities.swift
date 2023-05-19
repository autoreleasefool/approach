import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
@testable import ModelsLibrary

extension Frame.Database {
	public static func mock(
		gameId: Game.ID = UUID(0),
		index: Int,
		roll0: String? = nil,
		roll1: String? = nil,
		roll2: String? = nil,
		ball0: Gear.ID? = nil,
		ball1: Gear.ID? = nil,
		ball2: Gear.ID? = nil
	) -> Self {
		.init(
			gameId: gameId,
			index: index,
			roll0: roll0,
			roll1: roll1,
			roll2: roll2,
			ball0: ball0,
			ball1: ball1,
			ball2: ball2
		)
	}
}


func insert(
	frames initial: InitialValue<Frame.Database>?,
	into db: Database
) throws {
	let frames: [Frame.Database]
	switch initial {
	case .none, .zero:
		frames = []
	case .default:
		var framesBuilder: [Frame.Database] = []
		for gameIdInt in 0...5 {
			let gameId = UUID(gameIdInt)
			for index in Game.FRAME_INDICES {
				framesBuilder.append(
					.init(
						gameId: gameId,
						index: index,
						roll0: nil,
						roll1: nil,
						roll2: nil,
						ball0: nil,
						ball1: nil,
						ball2: nil
					)
				)
			}
		}
		frames = framesBuilder
	case let .custom(custom):
		frames = custom
	}

	for frame in frames {
		try frame.insert(db)
	}
}
