import AnalyticsServiceInterface
import ComposableArchitecture
import Foundation
import ModelsLibrary

extension GamesEditor {
	func loadBowlers(state: inout State) -> Effect<Action> {
		state.elementsRefreshing.insert(.bowlers)
		return .run { [bowlerIds = state.bowlerIds] send in
			await send(.internal(.bowlersResponse(TaskResult {
				try await bowlers.summaries(forIds: bowlerIds)
			})))
		}
	}

	func loadGameDetails(state: inout State) -> Effect<Action> {
		state.elementsRefreshing.insert(.frames)
		state.elementsRefreshing.insert(.game)
		return .merge(
			.run { [gameId = state.currentGameId] send in
				await send(.internal(.framesResponse(TaskResult {
					try await frames.frames(forGame: gameId) ?? []
				})))
			},
			.run { [gameId = state.currentGameId] send in
				await send(.internal(.gameResponse(TaskResult {
					try await games.edit(gameId)
				})))
			}
		)
		.cancellable(id: CancelID.observation, cancelInFlight: true)
	}

	func updateScoreSheet(from state: State) -> Effect<Action> {
		guard let frames = state.frames else { return .none }
		return .run { send in
			let steps = await scoring.calculateScoreWithSteps(for: frames.map { $0.rolls })
			await send(.internal(.calculatedScore(steps)))
		}
	}

	func save(frame: Frame.Edit?) -> Effect<Action> {
		guard let frame else { return .none }
		return .run { send in
			do {
				try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
				try await frames.update(frame)
				await send(.internal(.didUpdateFrame(.success(frame))))
			} catch {
				await send(.internal(.didUpdateFrame(.failure(error))))
			}
		}.cancellable(id: frame.id, cancelInFlight: true)
	}

	func save(game: Game.Edit?) -> Effect<Action> {
		guard let game else { return .none }
		return .run { send in
			do {
				try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
				try await games.update(game)
				await send(.internal(.didUpdateGame(.success(game))))
			} catch {
				await send(.internal(.didUpdateGame(.failure(error))))
			}
		}.cancellable(id: game.id, cancelInFlight: true)
	}

	func save(matchPlay: MatchPlay.Edit?) -> Effect<Action> {
		guard let matchPlay else { return .none }
		return .run { send in
			do {
				try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
				try await matchPlays.update(matchPlay)
				await send(.internal(.didUpdateMatchPlay(.success(matchPlay))))
			} catch {
				await send(.internal(.didUpdateMatchPlay(.failure(error))))
			}
		}.cancellable(id: matchPlay.id, cancelInFlight: true)
	}
}
