import DatabaseModelsLibrary
import GearRepositoryInterface
import GRDB
import ModelsLibrary

extension Gear.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Gear.Database.databaseTableName
}

extension Gear.Create: PersistableRecord {
	public static let databaseTableName = Gear.Database.databaseTableName
}

extension Gear.Summary: TableRecord, FetchableRecord, EncodableRecord {
	public static let databaseTableName = Gear.Database.databaseTableName

	static let owner = belongsTo(Bowler.Database.self)
	var owner: QueryInterfaceRequest<Bowler.Database> { request(for: Gear.Summary.owner) }

	static func allAnnotated() -> QueryInterfaceRequest<Gear.Summary> {
		let ownerName = Bowler.Database.Columns.name.forKey("ownerName")
		return all()
			.annotated(withOptional: Gear.Summary.owner.select(ownerName))
	}
}

extension DerivableRequest<Gear.Summary> {
	func orderByName() -> Self {
		let name = Gear.Database.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	func filter(byKind: Gear.Kind?) -> Self {
		guard let byKind else { return self }
		let kind = Gear.Database.Columns.kind
		return filter(kind == byKind)
	}

	func owned(byBowler: Bowler.ID?) -> Self {
		guard let byBowler else { return self }
		let bowler = Gear.Database.Columns.bowler
		return filter(bowler == byBowler)
	}
}
