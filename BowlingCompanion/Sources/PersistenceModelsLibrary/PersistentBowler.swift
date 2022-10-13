import Foundation
import RealmSwift
import SharedModelsLibrary

public class PersistentBowler: Object, ObjectKeyIdentifiable {
	@Persisted(primaryKey: true) public var _id: UUID
	@Persisted public var name = ""
}

// MARK: - Model

extension PersistentBowler {
	public convenience init(from bowler: Bowler) {
		self.init()
		self._id = bowler.id
		self.name = bowler.name
	}

	public var bowler: Bowler {
		.init(
			id: _id,
			name: name
		)
	}
}
