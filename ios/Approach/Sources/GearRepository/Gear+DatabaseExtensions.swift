import DatabaseModelsLibrary
import GearRepositoryInterface
import GRDB
import ModelsLibrary

extension Bowler.Summary: TableRecord {
	public static let databaseTableName = Bowler.Database.databaseTableName
}

extension Gear.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Gear.Database.databaseTableName
	typealias Columns = Gear.Database.Columns

	static let gearOwner = belongsTo(Bowler.Summary.self)
	var gearOwner: QueryInterfaceRequest<Bowler.Summary> { request(for: Gear.Edit.gearOwner) }

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.id] = id
		container[Columns.name] = name
		container[Columns.bowlerId] = owner?.id
	}

	static func withOwner() -> QueryInterfaceRequest<Gear.Edit> {
		return including(optional: Gear.Edit.gearOwner.forKey("owner"))
	}
}

extension Gear.Create: PersistableRecord {
	public static let databaseTableName = Gear.Database.databaseTableName
	typealias Columns = Gear.Database.Columns

	static let gearOwner = belongsTo(Bowler.Database.self)
	var gearOwner: QueryInterfaceRequest<Bowler.Database> { request(for: Gear.Create.gearOwner) }

	public func encode(to container: inout PersistenceContainer) throws {
		container[Columns.id] = id
		container[Columns.name] = name
		container[Columns.kind] = kind
		container[Columns.bowlerId] = owner?.id
	}
}

extension Gear.Summary: TableRecord, FetchableRecord, EncodableRecord {
	public static let databaseTableName = Gear.Database.databaseTableName

	static let owner = belongsTo(Bowler.Database.self)
	var owner: QueryInterfaceRequest<Bowler.Database> { request(for: Gear.Summary.owner) }

	static func withOwnerName() -> QueryInterfaceRequest<Gear.Summary> {
		let ownerName = Bowler.Database.Columns.name.forKey("ownerName")
		return all().annotated(withOptional: Gear.Summary.owner.select(ownerName))
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
		let bowler = Gear.Database.Columns.bowlerId
		return filter(bowler == byBowler)
	}
}
