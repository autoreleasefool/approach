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

		init(game: Game.Edit) {
			self.game = game

			@Dependency(\.featureFlags) var featureFlags
			self.isGearEnabled = featureFlags.isEnabled(.gear)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
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
			case didSetMatchPlayResult(MatchPlay.Result?)
			case didSetMatchPlayScore(String)
			case didSetAlertScore(String)
			case didSwipeGear(SwipeAction, id: Gear.ID)
		}
		public enum DelegateAction: Equatable {
			case didRequestOpponentPicker
			case didRequestGearPicker
			case didEditGame(Game.Edit)
			case didEditMatchPlay(MatchPlay.Edit?)
			case didClearManualScore
		}
		public enum InternalAction: Equatable {
			case didUpdateMatchPlay(TaskResult<Never>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	enum CancelID { case saveMatchPlay }

	public enum SwipeAction: Equatable {
		case delete
	}

	@Dependency(\.analytics) var analytics
	@Dependency(\.matchPlays) var matchPlays
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didToggleLock:
					state.game.locked.toNext()
					return .send(.delegate(.didEditGame(state.game)))

				case .didToggleExclude:
					state.game.excludeFromStatistics.toNext()
					return .send(.delegate(.didEditGame(state.game)))

				case .didTapGear:
					return .send(.delegate(.didRequestGearPicker))

				case .didTapOpponent:
					return .send(.delegate(.didRequestOpponentPicker))

				case let .didSetMatchPlayResult(result):
					state.game.matchPlay?.result = result
					return .send(.delegate(.didEditMatchPlay(state.game.matchPlay)))

				case .didToggleScoringMethod:
					return toggleScoringMethod(in: &state)

				case .didDismissScoreAlert:
					state.didJustToggleScoringMethod = false
					state.isScoreAlertPresented = false
					return .none

				case .didTapSaveScore:
					state.game.score = max(min(state.alertScore, 450), 0)
					return .merge(
						.send(.delegate(.didEditGame(state.game))),
						.run { [gameId = state.game.id] _ in await analytics.trackEvent(Analytics.Game.ManualScoreSet(gameId: gameId)) }
					)

				case .didTapCancelScore:
					if state.didJustToggleScoringMethod {
						state.didJustToggleScoringMethod = false
						return toggleScoringMethod(in: &state)
					} else {
						return .none
					}

				case .didTapManualScore:
					state.alertScore = state.game.score
					state.isScoreAlertPresented = true
					return .none

				case let .didSetAlertScore(string):
					if !string.isEmpty, let score = Int(string) {
						state.alertScore = max(min(score, 450), 0)
					}
					return .none

				case let .didSetMatchPlayScore(string):
					if !string.isEmpty, let score = Int(string) {
						state.game.matchPlay?.opponentScore = score
					} else {
						state.game.matchPlay?.opponentScore = nil
					}
					return .send(.delegate(.didEditMatchPlay(state.game.matchPlay)))

				case .didToggleMatchPlay:
					if state.game.matchPlay == nil {
						return createMatchPlay(state: &state)
					} else {
						return deleteMatchPlay(state: &state)
					}

				case let .didSwipeGear(.delete, id):
					state.game.gear.remove(id: id)
					return .send(.delegate(.didEditGame(state.game)))
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didUpdateMatchPlay(.failure):
					// TODO: handle error updating match play
					return .none
				}

			case .delegate:
				return .none
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
		return .merge(
			.concatenate(
				.run { send in
					do {
						try await matchPlays.create(matchPlay)
					} catch {
						await send(.internal(.didUpdateMatchPlay(.failure(error))))
					}
				},
				.send(.delegate(.didEditMatchPlay(matchPlay)))
			).cancellable(id: CancelID.saveMatchPlay),
			.run { _ in
				await analytics.trackEvent(Analytics.MatchPlay.Created())

			}
		)
	}

	private func deleteMatchPlay(state: inout State) -> Effect<Action> {
		guard let matchPlay = state.game.matchPlay else { return .none }
		state.game.matchPlay = nil
		return .merge(
			.concatenate(
				.cancel(id: CancelID.saveMatchPlay),
				.run { send in
					do {
						try await matchPlays.delete(matchPlay.id)
					} catch {
						await send(.internal(.didUpdateMatchPlay(.failure(error))))
					}
				}
			),
			.run { _ in await analytics.trackEvent(Analytics.MatchPlay.Deleted()) }
		)
	}
}
