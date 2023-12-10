import ComposableArchitecture
import ErrorsFeature
import Foundation
import ModelsLibrary

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

extension GamesEditor {
	func reduce(
		into state: inout State,
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
