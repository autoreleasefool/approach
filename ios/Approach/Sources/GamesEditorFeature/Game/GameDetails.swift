import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

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
		public enum DelegateAction: Equatable {
			case didEditGame
		}
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
					return .send(.delegate(.didEditGame))

				case .didToggleExclude:
					state.game.excludeFromStatistics.toggle()
					return .send(.delegate(.didEditGame))
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

// MARK: - View

public struct GameDetailsView: View {
	let store: StoreOf<GameDetails>

	enum ViewAction {
		case didToggleLock
		case didToggleExclude
	}

	init(store: StoreOf<GameDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: GameDetails.Action.init, content: { viewStore in
			if let alley = viewStore.game.series.alley?.name {
				Section(Strings.Alley.title) {
					LabeledContent(Strings.Alley.Title.bowlingAlley, value: alley)
					LabeledContent(Strings.Lane.List.title, value: viewStore.game.series.laneLabels)
				}
			}

			Section {
				Toggle(
					Strings.Game.Editor.Fields.Lock.label,
					isOn: viewStore.binding(get: { $0.game.locked == .locked }, send: ViewAction.didToggleLock)
				)
			} footer: {
				Text(Strings.Game.Editor.Fields.Lock.help)
			}

			Section {
				Toggle(
					Strings.Game.Editor.Fields.ExcludeFromStatistics.label,
					isOn: viewStore.binding(get: { $0.game.excludeFromStatistics == .exclude }, send: ViewAction.didToggleExclude)
				)
			} footer: {
				// TODO: check if series or league is locked and display different help message
				Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.help)
			}
		})
	}
}

extension GameDetails.Action {
	init(action: GameDetailsView.ViewAction) {
		switch action {
		case .didToggleLock:
			self = .view(.didToggleLock)
		case .didToggleExclude:
			self = .view(.didToggleExclude)
		}
	}
}

extension Game.Edit.SeriesInfo {
	var laneLabels: String {
		lanes.isEmpty ? Strings.none : lanes.map(\.label).joined(separator: ", ")
	}
}
