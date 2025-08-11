import ComposableArchitecture
import ErrorsFeature
import Foundation
import ModelsLibrary

// migrated, keeping
enum GamesEditorError: LocalizedError {
	case outdatedFrames(forGame: Game.ID?, expectedGame: Game.ID?)

	public var errorDescription: String? {
		switch self {
		case let .outdatedFrames(forGame, expectedGame):
			return """
			Received outdated frames, expected frames for game '\(String(describing: expectedGame))', \
			received '\(String(describing: forGame))'
			"""
		}
	}
}

// migrated
extension GamesEditor {
	func reduce(
		into _: inout State,
		errorsAction: Errors<ErrorID>.Action
	) -> Effect<Action> {
		switch errorsAction {
		case .delegate(.doNothing):
			return .none

		case .view, .internal:
			return .none
		}
	}
}
