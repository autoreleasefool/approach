import DatabaseModelsLibrary
import GRDB
import LeaguesRepositoryInterface
import ModelsLibrary

extension League.Editable: FetchableRecord, PersistableRecord {
	public static let databaseTableName = League.Database.databaseTableName
}

extension DerivableRequest<League.Database> {
	func orderByName() -> Self {
		let name = League.Database.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func bowled(byBowler: Bowler.ID) -> Self {
		let bowler = League.Database.Columns.bowlerId
		return filter(bowler == byBowler)
	}

	func filter(byRecurrence: League.Recurrence?) -> Self {
		guard let byRecurrence else { return self }
		let recurrence = League.Database.Columns.recurrence
		return filter(recurrence == byRecurrence)
	}
}
