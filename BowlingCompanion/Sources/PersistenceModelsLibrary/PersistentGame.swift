import Foundation
import RealmSwift
import SharedModelsLibrary

public class PersistentGame: Object, ObjectKeyIdentifiable {
	@Persisted(primaryKey: true) public var _id: UUID
	@Persisted public var ordinal: Int = 0
	@Persisted public var locked: PersistentLockedState = .unlocked
	@Persisted public var manualScore: Int?

	@Persisted(originProperty: "games") public var series: LinkingObjects<PersistentSeries>
}

extension PersistentGame {
	public enum PersistentLockedState: Int, PersistableEnum {
		case unlocked
		case locked
	}
}

// MARK: - Model

extension PersistentGame {
	public convenience init(from game: Game) {
		self.init()
		self._id = game.id
		self.ordinal = game.ordinal
		self.locked = PersistentLockedState(from: game.locked)
		self.manualScore = game.manualScore
	}

	public var game: Game {
		.init(
			id: _id,
			ordinal: ordinal,
			locked: locked.locked,
			manualScore: manualScore
		)
	}
}

extension PersistentGame.PersistentLockedState {
	public init(from locked: Game.LockedState) {
		switch locked {
		case .locked:
			self = .locked
		case .unlocked:
			self = .unlocked
		}
	}

	public var locked: Game.LockedState {
		switch self {
		case .unlocked:
			return .unlocked
		case .locked:
			return .locked
		}
	}
}
