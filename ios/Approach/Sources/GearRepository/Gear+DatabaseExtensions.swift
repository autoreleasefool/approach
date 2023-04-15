import DatabaseModelsLibrary
import GearRepositoryInterface
import GRDB
import ModelsLibrary

extension Gear.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Gear.DatabaseModel.databaseTableName
}

extension Gear.Create: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Gear.DatabaseModel.databaseTableName
}

extension Gear.Summary: TableRecord, FetchableRecord, EncodableRecord {
	public static let databaseTableName = Gear.DatabaseModel.databaseTableName
	public static let databaseSelection: [any SQLSelectable] = [
		Gear.DatabaseModel.Columns.id,
		Gear.DatabaseModel.Columns.name,
		Gear.DatabaseModel.Columns.kind,
	]

	static let owner = belongsTo(Bowler.DatabaseModel.self)
	var owner: QueryInterfaceRequest<Bowler.DatabaseModel> { request(for: Gear.Summary.owner) }

	static func allAnnotated() -> QueryInterfaceRequest<Gear.Summary> {
		let ownerName = Bowler.DatabaseModel.Columns.name.forKey("ownerName")
		return all()
			.annotated(withOptional: Gear.Summary.owner.select(ownerName))
	}
}

extension DerivableRequest<Gear.Summary> {
	func orderByName() -> Self {
		let name = Gear.DatabaseModel.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(byKind: Gear.Kind?) -> Self {
		guard let byKind else { return self }
		let kind = Gear.DatabaseModel.Columns.kind
		return filter(kind == byKind)
	}

	func owned(byBowler: Bowler.ID?) -> Self {
		guard let byBowler else { return self }
		let bowler = Gear.DatabaseModel.Columns.bowler
		return filter(bowler == byBowler)
	}
}
