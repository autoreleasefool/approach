import ComposableArchitecture
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct GamesSettings: Reducer {
	public struct State: Equatable {
		public let bowlers: IdentifiedArrayOf<Bowler.Summary>
		public let currentBowlerId: Bowler.ID
		public let numberOfGames: Int
		public let gameIndex: Int

		init(bowlers: IdentifiedArrayOf<Bowler.Summary>, currentBowlerId: Bowler.ID, numberOfGames: Int, gameIndex: Int) {
			self.bowlers = bowlers
			self.currentBowlerId = currentBowlerId
			self.numberOfGames = numberOfGames
			self.gameIndex = gameIndex
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapDone
		}
		public enum DelegateAction: Equatable {
			case movedBowlers(source: IndexSet, destination: Int)
			case switchedGame(to: Int)
			case switchedBowler(to: Bowler.ID)
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapDone:
					return .run { _ in await dismiss() }
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

// MARK: - View

public struct GamesSettingsView: View {
	let store: StoreOf<GamesSettings>

	enum ViewAction {
		case didTapDone
		case didMoveBowlers(IndexSet, Int)
		case didSwitchGame(Int)
		case didSwitchBowler(Bowler.ID)
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: GamesSettings.Action.init, content: { viewStore in
			List {
				Section(Strings.Game.Settings.current) {
					Picker(
						Strings.Game.title,
						selection: viewStore.binding(get: \.gameIndex, send: ViewAction.didSwitchGame)
					) {
						ForEach(Array(0..<viewStore.numberOfGames), id: \.self) { index in
							Text(Strings.Game.titleWithOrdinal(index + 1))
								.tag(index)
						}
					}

					Picker(
						Strings.Bowler.title,
						selection: viewStore.binding(get: \.currentBowlerId, send: ViewAction.didSwitchBowler)
					) {
						ForEach(viewStore.bowlers) { bowler in
							Text(bowler.name)
								.tag(bowler.id)
						}
					}
				}

				Section {
					ForEach(viewStore.bowlers) { bowler in
						Text(bowler.name)
					}
					.onMove { viewStore.send(.didMoveBowlers($0, $1)) }
				} header: {
					Text(Strings.Bowler.List.title)
				} footer: {
					Text(Strings.Game.Editor.Bowlers.dragToReorder)
				}
			}
			.environment(\.editMode, .constant(.active))
			.navigationTitle(Strings.Game.Settings.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button(Strings.Action.done) { viewStore.send(.didTapDone) }
				}
			}
		})
	}
}

extension GamesSettings.Action {
	init(action: GamesSettingsView.ViewAction) {
		switch action {
		case .didTapDone:
			self = .view(.didTapDone)
		case let .didMoveBowlers(source, destination):
			self = .delegate(.movedBowlers(source: source, destination: destination))
		case let .didSwitchGame(index):
			self = .delegate(.switchedGame(to: index))
		case let .didSwitchBowler(id):
			self = .delegate(.switchedBowler(to: id))
		}
	}
}
