import DatabaseModelsLibrary
import GRDB
import LeaguesRepositoryInterface
import ModelsLibrary

extension League.Editable: FetchableRecord, PersistableRecord {
	public static let databaseTableName = League.DatabaseModel.databaseTableName
}

extension League.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = League.DatabaseModel.databaseTableName
}

extension DerivableRequest<League.Summary> {
	func orderByName() -> Self {
		let name = League.DatabaseModel.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func bowled(byBowler: Bowler.ID) -> Self {
		let bowler = League.DatabaseModel.Columns.bowler
		return filter(bowler == byBowler)
	}

	func filter(byRecurrence: League.Recurrence?) -> Self {
		guard let byRecurrence else { return self }
		let recurrence = League.DatabaseModel.Columns.recurrence
		return filter(recurrence == byRecurrence)
	}
}
