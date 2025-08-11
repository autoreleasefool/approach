import ComposableArchitecture
import Foundation
import ModelsLibrary

// MARK: Initial State

extension SharedReaderKey where Self == InMemoryKey<[Bowler.ID]>.Default {
	static var bowlerIds: Self { Self[.inMemory("gamesEditor.bowlerIds"), default: []] }
}

extension SharedReaderKey where Self == InMemoryKey<[Bowler.ID: [Game.ID]]>.Default {
	static var bowlerGameIds: Self { Self[.inMemory("gamesEditor.bowlerGameIds"), default: [:]] }
}

// MARK: Current Entities

extension SharedReaderKey where Self == InMemoryKey<Bowler.ID>.Default {
	static var currentBowlerId: Self { Self[.inMemory("gamesEditor.currentBowlerId"), default: .init()] }
}

extension SharedReaderKey where Self == InMemoryKey<Game.ID>.Default {
	static var currentGameId: Self { Self[.inMemory("gamesEditor.currentGameId"), default: .init()] }
}

extension SharedReaderKey where Self == InMemoryKey<Frame.Selection>.Default {
	static var currentFrame: Self { Self[.inMemory("gamesEditor.currentFrame"), default: .unselected] }
}

// MARK: Reloadable Data

extension SharedReaderKey where Self == InMemoryKey<IdentifiedArrayOf<Bowler.Summary>?>.Default {
	static var bowlers: Self { Self[.inMemory("gamesEditor.bowlers"), default: nil] }
}

extension SharedReaderKey where Self == InMemoryKey<Game.Edit?>.Default {
	static var game: Self { Self[.inMemory("gamesEditor.game"), default: nil] }
}

extension SharedReaderKey where Self == InMemoryKey<[Frame.Edit]?>.Default {
	static var frames: Self { Self[.inMemory("gamesEditor.frames"), default: nil] }
}

extension SharedReaderKey where Self == InMemoryKey<ScoredGame?>.Default {
	static var score: Self { Self[.inMemory("gamesEditor.score"), default: nil] }
}

// MARK: Editing

public struct GameLoadDate: Sendable, Equatable {
	let gameId: Game.ID
	let durationWhenLoaded: TimeInterval
	let loadedAt: Date

	func calculateDuration(at date: Date) -> TimeInterval {
		durationWhenLoaded + loadedAt.distance(to: date)
	}
}

extension SharedReaderKey where Self == InMemoryKey<GameDetailsHeaderNext.State.NextElement?>.Default {
	static var nextHeaderElement: Self { Self[.inMemory("gamesEditor.nextHeaderElement"), default: nil] }
}

extension SharedReaderKey where Self == InMemoryKey<GameLoadDate?>.Default {
	static var gameLastLoadedAt: Self { Self[.inMemory("gamesEditor.gameLastLoadedAt"), default: nil] }
}

extension SharedReaderKey where Self == InMemoryKey<Bool>.Default {
	// TODO: set to false
	static var isEditable: Self { Self[.inMemory("gamesEditor.isEditable"), default: true] }
}
