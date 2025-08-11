import AnalyticsServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import GamesRepositoryInterface
import ModelsLibrary
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI

@Reducer
public struct GamesSettingsNext: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.bowlers) public var bowlers: IdentifiedArrayOf<Bowler.Summary>?
		@Shared(.bowlerGameIds) public var bowlerGameIds: [Bowler.ID: [Game.ID]]
		@Shared(.currentBowlerId) public var currentBowlerId: Bowler.ID
		@Shared(.currentGameId) public var currentGameId: Game.ID

		public let isTeamsEnabled: Bool
		public var isFlashEditorChangesEnabled: Bool

		var numberOfGames: Int { bowlerGameIds.first?.value.count ?? 1 }
		var gameIndex: Int { bowlerGameIds[currentBowlerId]?.firstIndex(of: currentGameId) ?? 0 }

		init() {
			@Dependency(\.featureFlags) var featureFlags
			self.isTeamsEnabled = featureFlags.isFlagEnabled(.teams)

			@Dependency(\.preferences) var preferences
			self.isFlashEditorChangesEnabled = preferences.bool(forKey: .gameShouldNotifyEditorChanges) ?? true
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case onAppear
			case didTapDone
			case didSwitchGame(to: Int)
			case didSwitchBowler(to: Bowler.ID)
			case didMoveBowlers(source: IndexSet, destination: Int)
		}
		@CasePathable
		public enum Delegate {
			case movedBowlers(source: IndexSet, destination: Int)
			case switchedGame(to: Int)
			case switchedBowler(to: Bowler.ID)
		}
		@CasePathable
		public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapDone:
					return .run { _ in await dismiss() }

				case let .didSwitchGame(index):
					return .concatenate(
						.send(.delegate(.switchedGame(to: index))),
						.run { _ in await dismiss() }
					)

				case let .didSwitchBowler(id):
					return .concatenate(
						.send(.delegate(.switchedBowler(to: id))),
						.run { _ in await dismiss() }
					)

				case let .didMoveBowlers(source, destination):
					return .send(.delegate(.movedBowlers(source: source, destination: destination)))
				}

			case .internal(.doNothing):
				return .none

			case .binding(\.isFlashEditorChangesEnabled):
				return .run { [updatedValue = state.isFlashEditorChangesEnabled] _ in
					preferences.setBool(forKey: .gameShouldNotifyEditorChanges, to: updatedValue)
				}
				.cancellable(id: PreferenceKey.gameShouldNotifyEditorChanges, cancelInFlight: true)

			case .delegate, .binding:
				return .none
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: GamesSettingsNext.self)
public struct GamesSettingsNextView: View {
	@Bindable public var store: StoreOf<GamesSettingsNext>

	public var body: some View {
		List {
			Section(Strings.Game.Settings.current) {
				Picker(
					Strings.Game.title,
					selection: $store.gameIndex.sending(\.view.didSwitchGame)
				) {
					ForEach(Array(0..<store.numberOfGames), id: \.self) { index in
						Text(Strings.Game.titleWithOrdinal(index + 1))
							.tag(index)
					}
				}

				if store.isTeamsEnabled, let bowlers = store.bowlers {
					Picker(
						Strings.Bowler.title,
						selection: $store.currentBowlerId.sending(\.view.didSwitchBowler)
					) {
						ForEach(bowlers) { bowler in
							Text(bowler.name)
								.tag(bowler.id)
						}
					}
				}
			}

			if store.isTeamsEnabled, let bowlers = store.bowlers {
				Section {
					ForEach(bowlers) { bowler in
						Text(bowler.name)
					}
					.onMove { send(.didMoveBowlers(source: $0, destination: $1)) }
				} header: {
					Text(Strings.Bowler.List.title)
				} footer: {
					Text(Strings.Game.Editor.Bowlers.dragToReorder)
				}
			}

			Section {
				Toggle(
					Strings.Game.Editor.Preferences.flashEditorChanges,
					isOn: $store.isFlashEditorChangesEnabled
				)
			} header: {
				Text(Strings.Game.Editor.Preferences.title)
			} footer: {
				Text(Strings.Game.Editor.Preferences.FlashEditorChanges.footer)
			}
		}
		.environment(\.editMode, .constant(.active))
		.navigationTitle(Strings.Game.Settings.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarLeading) {
				Button(Strings.Action.done) { send(.didTapDone) }
			}
		}
		.onAppear { send(.onAppear) }
	}
}
