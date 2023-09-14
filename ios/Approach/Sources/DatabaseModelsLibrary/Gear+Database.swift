import Foundation
import GRDB
import ModelsLibrary

extension Gear {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: Gear.ID
		public var name: String
		public var kind: Kind
		public var bowlerId: Bowler.ID?
		public var avatarId: Avatar.ID?

		public init(
			id: Gear.ID,
			name: String,
			kind: Kind,
			bowlerId: Bowler.ID?,
			avatarId: Avatar.ID?
		) {
			self.id = id
			self.name = name
			self.kind = kind
			self.bowlerId = bowlerId
			self.avatarId = avatarId
		}
	}
}

extension Gear.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "gear"
}

extension Gear.Kind: DatabaseValueConvertible {}

extension Gear.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let kind = Column(CodingKeys.kind)
		public static let bowlerId = Column(CodingKeys.bowlerId)
		public static let avatarId = Column(CodingKeys.avatarId)
	}
}

extension DerivableRequest<Gear.Database> {
	public func includingOwnerName() -> Self {
		let ownerName = Bowler.Database.Columns.name.forKey("ownerName")
		return annotated(withOptional: Gear.Database.bowler.select(ownerName))
	}

	public func includingAvatar() -> Self {
		annotated(withOptional: Gear.Database.avatar.select(
			Avatar.Database.Columns.value
		))
	}
}

extension Gear.Summary: FetchableRecord {}
