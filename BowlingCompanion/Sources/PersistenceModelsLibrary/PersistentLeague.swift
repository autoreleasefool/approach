import Foundation
import RealmSwift
import SharedModelsLibrary

public class PersistentLeague: Object, ObjectKeyIdentifiable {
	@Persisted(primaryKey: true) public var _id: UUID
	@Persisted public var name = ""
	@Persisted public var recurrence: PersistentRecurrence = .repeating
	@Persisted public var numberOfGames: Int = 3
	@Persisted public var additionalPinfall: Int = 0
	@Persisted public var additionalGames: Int = 0

	@Persisted public var series: List<PersistentSeries>
	@Persisted(originProperty: "leagues") public var bowler: LinkingObjects<PersistentBowler>
}

extension PersistentLeague {
	public enum PersistentRecurrence: Int, PersistableEnum {
		case repeating
		case oneTimeEvent
	}
}

// MARK: - Model

extension PersistentLeague {
	public convenience init(from league: League) {
		self.init()
		self._id = league.id
		self.name = league.name
		self.recurrence = PersistentRecurrence(from: league.recurrence)
		self.numberOfGames = league.numberOfGames
		self.additionalGames = league.additionalGames
		self.additionalPinfall = league.additionalPinfall
	}

	public var league: League {
		.init(
			id: _id,
			name: name,
			recurrence: recurrence.recurrence,
			numberOfGames: numberOfGames,
			additionalPinfall: additionalPinfall,
			additionalGames: additionalGames
		)
	}
}

extension PersistentLeague.PersistentRecurrence {
	public init(from recurrence: League.Recurrence) {
		switch recurrence {
		case .repeating:
			self = .repeating
		case .oneTimeEvent:
			self = .oneTimeEvent
		}
	}

	public var recurrence: League.Recurrence {
		switch self {
		case .repeating:
			return .repeating
		case .oneTimeEvent:
			return .oneTimeEvent
		}
	}
}
