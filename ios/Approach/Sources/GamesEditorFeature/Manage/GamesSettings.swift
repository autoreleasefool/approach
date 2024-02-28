import AnalyticsServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import GamesRepositoryInterface
import ModelsLibrary
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI

@Reducer
public struct GamesSettings: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let bowlers: IdentifiedArrayOf<Bowler.Summary>
		public let currentBowlerId: Bowler.ID
		public let numberOfGames: Int
		public let gameIndex: Int

		public let isTeamsEnabled: Bool

		public var isFlashEditorChangesEnabled: Bool

		init(bowlers: IdentifiedArrayOf<Bowler.Summary>, currentBowlerId: Bowler.ID, numberOfGames: Int, gameIndex: Int) {
			self.bowlers = bowlers
			self.currentBowlerId = currentBowlerId
			self.numberOfGames = numberOfGames
			self.gameIndex = gameIndex

			@Dependency(\.featureFlags) var featureFlags
			self.isTeamsEnabled = featureFlags.isEnabled(.teams)

			@Dependency(\.preferences) var preferences
			self.isFlashEditorChangesEnabled = preferences.bool(forKey: .gameShouldNotifyEditorChanges) ?? true
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case didTapDone
			case didSwitchGame(to: Int)
			case didSwitchBowler(to: Bowler.ID)
			case didMoveBowlers(source: IndexSet, destination: Int)
		}
		@CasePathable public enum Delegate {
			case movedBowlers(source: IndexSet, destination: Int)
			case switchedGame(to: Int)
			case switchedBowler(to: Bowler.ID)
		}
		@CasePathable public enum Internal { case doNothing }

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
					preferences.setKey(.gameShouldNotifyEditorChanges, toBool: updatedValue)
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

@ViewAction(for: GamesSettings.self)
public struct GamesSettingsView: View {
	@Perception.Bindable public var store: StoreOf<GamesSettings>

	public var body: some View {
		WithPerceptionTracking {
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

					if store.isTeamsEnabled {
						Picker(
							Strings.Bowler.title,
							selection: $store.currentBowlerId.sending(\.view.didSwitchBowler)
						) {
							ForEach(store.bowlers) { bowler in
								Text(bowler.name)
									.tag(bowler.id)
							}
						}
					}
				}

				if store.isTeamsEnabled {
					Section {
						ForEach(store.bowlers) { bowler in
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
}
