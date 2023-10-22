import DatabaseModelsLibrary
import GearRepositoryInterface
import GRDB
import ModelsLibrary

// MARK: - Edit

extension Gear.Edit: PersistableRecord, FetchableRecord {
	public static let databaseTableName = Gear.Database.databaseTableName

	public func encode(to container: inout PersistenceContainer) throws {
		container[Gear.Database.Columns.id] = id
		container[Gear.Database.Columns.name] = name
		container[Gear.Database.Columns.bowlerId] = owner?.id
		container[Gear.Database.Columns.avatarId] = avatar.id
	}
}

// MARK: - Create

extension Gear.Create: PersistableRecord {
	public static let databaseTableName = Gear.Database.databaseTableName

	public func encode(to container: inout PersistenceContainer) throws {
		container[Gear.Database.Columns.id] = id
		container[Gear.Database.Columns.name] = name
		container[Gear.Database.Columns.kind] = kind
		container[Gear.Database.Columns.bowlerId] = owner?.id
		container[Gear.Database.Columns.avatarId] = avatar.id
	}
}
