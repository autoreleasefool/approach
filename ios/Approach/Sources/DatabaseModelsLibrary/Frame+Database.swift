import Foundation
import GRDB
import ModelsLibrary

extension Frame {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "frame"

		public let game: Game.ID
		public let ordinal: Int
		public var roll0: String?
		public var roll1: String?
		public var roll2: String?

		public var id: String { "\(game)-\(ordinal)" }

		public init(
			game: Game.ID,
			ordinal: Int,
			roll0: String?,
			roll1: String?,
			roll2: String?
		) {
			self.game = game
			self.ordinal = ordinal
			self.roll0 = roll0
			self.roll1 = roll1
			self.roll2 = roll2
		}
	}
}

extension Frame.Database: FetchableRecord, PersistableRecord {}

extension Frame.Database {
	public enum Columns {
		public static let game = Column(CodingKeys.game)
		public static let ordinal = Column(CodingKeys.ordinal)
		public static let roll0 = Column(CodingKeys.roll0)
		public static let roll1 = Column(CodingKeys.roll1)
		public static let roll2 = Column(CodingKeys.roll2)
	}
}
