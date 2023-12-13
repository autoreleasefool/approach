import ComposableArchitecture
import Foundation
import ModelsLibrary

extension GameDetails {
	func observeGame(gameId: UUID) -> Effect<Action> {
		.run { send in
			for try await game in games.observe(gameId) {
				await send(.internal(.didLoadGame(.success(game))))
			}
		} catch: { error, send in
			await send(.internal(.didLoadGame(.failure(error))))
		}
		.cancellable(id: CancelID.observation, cancelInFlight: true)
	}

	func createMatchPlay(_ matchPlay: MatchPlay.Edit) -> Effect<Action> {
		return .run { send in
			await send(.delegate(.didEditMatchPlay(Result {
				try await matchPlays.create(matchPlay)
				return matchPlay
			})))
		}.cancellable(id: CancelID.saveMatchPlay)
	}

	func deleteMatchPlay(state: inout State) -> Effect<Action> {
		guard let matchPlay = state.game?.matchPlay else { return .none }
		state.game?.matchPlay = nil
		return .concatenate(
			.cancel(id: CancelID.saveMatchPlay),
			.run { send in
				await send(.delegate(.didEditMatchPlay(Result {
					try await matchPlays.delete(matchPlay.id)
					return nil
				})))
			}
		)
	}
}

extension GameDetails.State {
	mutating func loadGameDetails(forGameId: Game.ID, didChangeBowler: Bool) -> Effect<GameDetails.Action> {
		gameId = forGameId
		shouldHeaderShimmer = didChangeBowler
		return .merge(
			.send(.internal(.refreshObservation)),
			startShimmer()
		)
	}

	mutating func startShimmer() -> Effect<GameDetails.Action> {
		guard shouldHeaderShimmer else { return .none }
		shouldHeaderShimmer = false
		return _gameDetailsHeader.shouldStartShimmering()
			.map { .internal(.gameDetailsHeader($0)) }
	}
}

extension GameDetails.State {
	var gameDetailsHeader: GameDetailsHeader.State {
		get {
			var gameDetailsHeader = _gameDetailsHeader
			gameDetailsHeader.currentBowlerName = game?.bowler.name ?? ""
			gameDetailsHeader.currentLeagueName = game?.league.name ?? ""
			gameDetailsHeader.next = nextHeaderElement
			return gameDetailsHeader
		}
		set {
			_gameDetailsHeader = newValue
		}
	}
}
