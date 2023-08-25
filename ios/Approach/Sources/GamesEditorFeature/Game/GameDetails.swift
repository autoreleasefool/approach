import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import ExtensionsLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import GamesRepositoryInterface
import MatchPlaysRepositoryInterface
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GameDetails: Reducer {
	public struct State: Equatable {
		public var game: Game.Edit
		public var isScoreAlertPresented = false
		public var didJustToggleScoringMethod = false
		public var alertScore: Int = 0

		public let isGearEnabled: Bool
		public let isOpponentsEnabled: Bool

		@BindingState public var isSelectingLanes: Bool

		var isEditable: Bool { game.locked != .locked }

		var laneLabels: String {
			game.lanes.isEmpty ? Strings.none : game.lanes.map(\.label).joined(separator: ", ")
		}

		init(game: Game.Edit) {
			self.game = game
			self.isSelectingLanes = !game.lanes.isEmpty

			@Dependency(\.featureFlags) var featureFlags
			self.isGearEnabled = featureFlags.isEnabled(.gear)
			self.isOpponentsEnabled = featureFlags.isEnabled(.opponents)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didToggleLock
			case didToggleExclude
			case didToggleMatchPlay
			case didToggleScoringMethod
			case didTapManualScore
			case didTapGear
			case didTapOpponent
			case didDismissScoreAlert
			case didTapSaveScore
			case didTapCancelScore
			case didTapManageLanes
			case didSetMatchPlayResult(MatchPlay.Result?)
			case didSetMatchPlayScore(String)
			case didSetAlertScore(String)
			case didSwipeGear(SwipeAction, id: Gear.ID)
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {
			case didRequestOpponentPicker
			case didRequestGearPicker
			case didRequestLanePicker
			case didEditGame(Game.Edit)
			case didEditMatchPlay(TaskResult<MatchPlay.Edit?>)
			case didClearManualScore
			case didProvokeLock
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	enum CancelID { case saveMatchPlay }

	public enum SwipeAction: Equatable {
		case delete
	}

	@Dependency(\.matchPlays) var matchPlays
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didToggleLock:
					state.game.locked.toNext()
					return .send(.delegate(.didEditGame(state.game)))

				case .didToggleExclude:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.game.excludeFromStatistics.toNext()
					return .send(.delegate(.didEditGame(state.game)))

				case .didTapGear:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					return .send(.delegate(.didRequestGearPicker))

				case .didTapOpponent:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					return .send(.delegate(.didRequestOpponentPicker))

				case .didTapManageLanes:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					return .send(.delegate(.didRequestLanePicker))

				case let .didSetMatchPlayResult(result):
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.game.matchPlay?.result = result
					return .send(.delegate(.didEditMatchPlay(.success(state.game.matchPlay))))

				case .didToggleScoringMethod:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					return toggleScoringMethod(in: &state)

				case .didDismissScoreAlert:
					state.didJustToggleScoringMethod = false
					state.isScoreAlertPresented = false
					return .none

				case .didTapSaveScore:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.game.score = max(min(state.alertScore, 450), 0)
					return .send(.delegate(.didEditGame(state.game)))

				case .didTapCancelScore:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					if state.didJustToggleScoringMethod {
						state.didJustToggleScoringMethod = false
						return toggleScoringMethod(in: &state)
					} else {
						return .none
					}

				case .didTapManualScore:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.alertScore = state.game.score
					state.isScoreAlertPresented = true
					return .none

				case let .didSetAlertScore(string):
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					if !string.isEmpty, let score = Int(string) {
						state.alertScore = max(min(score, 450), 0)
					}
					return .none

				case let .didSetMatchPlayScore(string):
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					if !string.isEmpty, let score = Int(string) {
						state.game.matchPlay?.opponentScore = score
					} else {
						state.game.matchPlay?.opponentScore = nil
					}
					return .send(.delegate(.didEditMatchPlay(.success(state.game.matchPlay))))

				case .didToggleMatchPlay:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					if state.game.matchPlay == nil {
						return createMatchPlay(state: &state)
					} else {
						return deleteMatchPlay(state: &state)
					}

				case let .didSwipeGear(.delete, id):
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.game.gear.remove(id: id)
					return .send(.delegate(.didEditGame(state.game)))

				case .binding(\.$isSelectingLanes):
					guard state.isEditable else {
						if state.game.lanes.isEmpty {
							state.isSelectingLanes = false
						} else {
							state.isSelectingLanes = true
						}
						return .send(.delegate(.didProvokeLock))
					}

					if !state.isSelectingLanes {
						state.game.lanes.removeAll()
						return .send(.delegate(.didEditGame(state.game)))
					}
					return .none

				case .binding:
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

		AnalyticsReducer<State, Action> { state, action in
			switch action {
			case .view(.didTapSaveScore):
				return Analytics.Game.ManualScoreSet(gameId: state.game.id)
			default:
				return nil
			}
		}
	}

	private func toggleScoringMethod(in state: inout State) -> Effect<Action> {
		state.game.scoringMethod.toNext()
		switch state.game.scoringMethod {
		case .byFrame:
			return .send(.delegate(.didClearManualScore))
		case .manual:
			state.alertScore = state.game.score
			state.didJustToggleScoringMethod = true
			state.isScoreAlertPresented = true
			return .none
		}
	}

	private func createMatchPlay(state: inout State) -> Effect<Action> {
		let matchPlay = MatchPlay.Edit(gameId: state.game.id, id: uuid())
		state.game.matchPlay = matchPlay
		return .run { send in
			await send(.delegate(.didEditMatchPlay(TaskResult {
				try await matchPlays.create(matchPlay)
				return matchPlay
			})))
		}.cancellable(id: CancelID.saveMatchPlay)
	}

	private func deleteMatchPlay(state: inout State) -> Effect<Action> {
		guard let matchPlay = state.game.matchPlay else { return .none }
		state.game.matchPlay = nil
		return .concatenate(
			.cancel(id: CancelID.saveMatchPlay),
			.run { send in
				await send(.delegate(.didEditMatchPlay(TaskResult {
					try await matchPlays.delete(matchPlay.id)
					return nil
				})))
			}
		)
	}
}
