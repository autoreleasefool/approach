import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import FramesRepositoryInterface
import GamesRepositoryInterface
import MatchPlaysRepositoryInterface
import ModelsLibrary
import StringsLibrary

@Reducer
public struct GamesEditorStateSerialization: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.currentBowlerId) public var currentBowlerId: Bowler.ID
		@Shared(.currentGameId) public var currentGameId: Game.ID
		@Shared(.currentFrame) public var currentFrame: Frame.Selection

		@Shared(.frames) public var frames: [Frame.Edit]?
		@Shared(.game) public var game: Game.Edit?
		public fileprivate(set) var matchPlay: MatchPlay.Edit?

		@Shared(.gameLastLoadedAt) public var gameLastLoadedAt: GameLoadDate?

		public var errors: Errors<ErrorID>.State = .init()

		func initialize() -> Effect<GamesEditorStateSerialization.Action> {
			.send(.internal(.initialize))
		}
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case initialize

			case didUpdateFrame(Result<Frame.Edit, Error>)
			case didUpdateGame(Result<Game.Edit, Error>)
			case didUpdateMatchPlay(Result<MatchPlayChange, Error>)

			case framesDidChange([Frame.Edit]?)
			case gameDidChange(Game.Edit?)

			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum CancelID: Hashable, Sendable {
		case saveMatchPlay(MatchPlay.ID)
	}

	public enum ErrorID: Hashable, Sendable {
		case failedToSaveGame
		case failedToSaveFrame
		case failedToSaveMatchPlay
	}

	public enum MatchPlayChange {
		case created(MatchPlay.Edit)
		case updated(MatchPlay.Edit)
		case deleted
	}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(FramesRepository.self) var frames
	@Dependency(GamesRepository.self) var games
	@Dependency(MatchPlaysRepository.self) var matchPlays

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .internal(internalAction):
				switch internalAction {
				case .initialize:
					return .merge(
						.publisher {
							state.$frames.publisher
								.map { .internal(.framesDidChange($0)) }
						},
						.publisher {
							state.$game.publisher
								.map { .internal(.gameDidChange($0)) }
						},
					)
				case let .framesDidChange(frames):
					guard let frames, state.currentFrame != .unselected else { return .none }
					return save(frame: frames[state.currentFrame.frameIndex])

				case let .gameDidChange(game):
					guard let game else { return .none }

					return .merge(
						save(matchPlay: game.matchPlay, from: state.matchPlay),
						save(game: game, in: state)
					)

				case .didUpdateFrame(.success), .didUpdateGame(.success), .didUpdateMatchPlay(.success):
					return .none

				case let .didUpdateFrame(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveFrame, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case let .didUpdateGame(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveGame, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case let .didUpdateMatchPlay(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveMatchPlay, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case .errors(.delegate(.doNothing)), .errors(.internal), .errors(.view):
					return .none
				}

			case .view(.doNothing), .delegate:
				return .none
			}
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didUpdateMatchPlay(.success(change))):
				switch change {
				case .created:
					return Analytics.MatchPlay.Created()
				case let .updated(matchPlay):
					return Analytics.MatchPlay.Updated(
						withOpponent: matchPlay.opponent != nil,
						withScore: matchPlay.opponentScore != nil,
						withResult: matchPlay.result?.rawValue ?? ""
					)
				case .deleted:
					return Analytics.MatchPlay.Deleted()
				}

			default:
				return nil
			}
		}

		GameAnalyticsReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didUpdateGame(.success(game))):
				return Analytics.Game.Updated(gameId: game.id)
			case let .internal(.didUpdateFrame(.success(frame))):
				return Analytics.Game.Updated(gameId: frame.gameId)
			default:
				return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didUpdateFrame(.failure(error))),
				let .internal(.didUpdateGame(.failure(error))),
				let .internal(.didUpdateMatchPlay(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}

	private func save(matchPlay: MatchPlay.Edit?, from existing: MatchPlay.Edit?) -> Effect<Action> {
		if let matchPlay {
			if existing == nil {
				return .run { send in
					await send(.internal(.didUpdateMatchPlay(Result {
						try await matchPlays.create(matchPlay)
						return .created(matchPlay)
					})))
				}
				.cancellable(id: CancelID.saveMatchPlay(matchPlay.id))
			} else {
				return .run { send in
					await send(.internal(.didUpdateMatchPlay(Result {
						try await matchPlays.update(matchPlay)
						return .updated(matchPlay)
					})))
				}
				.cancellable(id: CancelID.saveMatchPlay(matchPlay.id))
			}
		} else if let existing {
			return .concatenate(
				.cancel(id: CancelID.saveMatchPlay(existing.id)),
				.run { send in
					await send(.internal(.didUpdateMatchPlay(Result {
						try await matchPlays.delete(existing.id)
						return .deleted
					})))
				}
			)
		}

		return .none
	}

	private func save(frame: Frame.Edit) -> Effect<Action> {
		.run { send in
			try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
			try await frames.update(frame)
			await send(.internal(.didUpdateFrame(.success(frame))))
		} catch: { error, send in
			await send(.internal(.didUpdateFrame(.failure(error))))
		}
		.cancellable(id: frame.id, cancelInFlight: true)
	}

	private func save(game: Game.Edit, in state: GamesEditorStateSerialization.State) -> Effect<Action> {
		var gameWithUpdatedDuration = game
		gameWithUpdatedDuration.duration = state.gameLastLoadedAt?.calculateDuration(at: date()) ?? game.duration

		return .run { [gameWithUpdatedDuration = gameWithUpdatedDuration] send in
			try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
			try await games.update(gameWithUpdatedDuration)
			await send(.internal(.didUpdateGame(.success(gameWithUpdatedDuration))))
		} catch: { error, send in
			await send(.internal(.didUpdateGame(.failure(error))))
		}
		.cancellable(id: game.id, cancelInFlight: true)
	}
}
