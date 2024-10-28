import AnalyticsServiceInterface
import ComposableArchitecture
import Foundation
import ModelsLibrary

extension GamesEditor {
	func loadBowlers(state: inout State) -> Effect<Action> {
		state.elementsRefreshing.insert(.bowlers)
		state.syncFrameEditorSharedState()
		state.syncRollEditorSharedState()
		return .run { [bowlerIds = state.bowlerIds] send in
			await send(.internal(.bowlersResponse(Result {
				try await bowlers.summaries(forIds: bowlerIds)
			})))
		}
	}

	func loadGameDetails(state: inout State) -> Effect<Action> {
		state.elementsRefreshing.insert(.frames)
		state.elementsRefreshing.insert(.game)
		state.syncFrameEditorSharedState()
		state.syncRollEditorSharedState()
		return .concatenate(
			.run { [gameId = state.currentGameId] send in
				for try await game in self.games.observe(gameId) {
					await send(.internal(.gameResponse(.success(game))))
					break
				}
			} catch: { error, send in
				await send(.internal(.gameResponse(.failure(error))))
			},
			.merge(
				.run { [gameId = state.currentGameId] send in
					for try await scoredGame in self.scores.observeScore(for: gameId) {
						await send(.internal(.calculatedScore(scoredGame)))
					}
				},
				.run { [gameId = state.currentGameId] send in
					for try await frames in self.frames.observe(gameId) {
						await send(.internal(.framesResponse(.success(frames))))
						break
					}
				} catch: { error, send in
					await send(.internal(.framesResponse(.failure(error))))
				},
				.run { [gameId = state.currentGameId] send in
					for try await game in self.games.observe(gameId) {
						await send(.internal(.gameResponse(.success(game))))
					}
				} catch: { error, send in
					await send(.internal(.gameResponse(.failure(error))))
				}
			)
		)
		.cancellable(id: CancelID.observation, cancelInFlight: true)
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
		}
		.cancellable(id: frame.id, cancelInFlight: true)
	}

	func save(game: Game.Edit?, in state: GamesEditor.State) -> Effect<Action> {
		guard var game else { return .none }
		game.duration = state.lastLoadedGameAt?.calculateDuration(at: date()) ?? game.duration

		return .run { [game = game] send in
			do {
				try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))

				try await games.update(game)
				await send(.internal(.didUpdateGame(.success(game))))
			} catch {
				await send(.internal(.didUpdateGame(.failure(error))))
			}
		}
		.cancellable(id: game.id, cancelInFlight: true)
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
		}
		.cancellable(id: matchPlay.id, cancelInFlight: true)
	}
}

extension GamesEditor.GameLoadDate {
	func calculateDuration(at date: Date) -> TimeInterval {
		durationWhenLoaded + loadedAt.distance(to: date)
	}
}
