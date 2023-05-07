import ComposableArchitecture
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary

public struct GameDetails: Reducer {
	public struct State: Equatable {
		public var game: Game.Edit

		init(game: Game.Edit) {
			self.game = game
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didToggleLock
			case didToggleExclude
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didToggleLock:
					state.game.locked.toggle()
					return .none

				case .didToggleExclude:
					state.game.excludeFromStatistics.toggle()
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

extension Game.Lock {
	mutating func toggle() {
		switch self {
		case .locked: self = .open
		case .open: self = .locked
		}
	}
}

extension Game.ExcludeFromStatistics {
	mutating func toggle() {
		switch self {
		case .exclude: self = .include
		case .include: self = .exclude
		}
	}
}
