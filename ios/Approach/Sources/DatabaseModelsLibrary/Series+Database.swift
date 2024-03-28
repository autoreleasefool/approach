import Dependencies
import Foundation
import GRDB
import ModelsLibrary

extension Series {
	public struct Database: Archivable, Sendable, Identifiable, Codable, Equatable {
		public let leagueId: League.ID
		public let id: Series.ID
		public var date: Date
		public var appliedDate: Date?
		public var preBowl: PreBowl
		public var excludeFromStatistics: ExcludeFromStatistics
		public var alleyId: Alley.ID?
		public var archivedOn: Date?

		public init(
			leagueId: League.ID,
			id: Series.ID,
			date: Date,
			appliedDate: Date?,
			preBowl: PreBowl,
			excludeFromStatistics: ExcludeFromStatistics,
			alleyId: Alley.ID?,
			archivedOn: Date?
		) {
			self.leagueId = leagueId
			self.id = id
			self.date = date
			self.appliedDate = appliedDate
			self.preBowl = preBowl
			self.excludeFromStatistics = excludeFromStatistics
			self.alleyId = alleyId
			self.archivedOn = archivedOn
		}
	}
}

extension Series.PreBowl: DatabaseValueConvertible {}
extension Series.ExcludeFromStatistics: DatabaseValueConvertible {}

extension Series.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "series"
}

extension Series.Database {
	public enum Columns {
		public static let leagueId = Column(CodingKeys.leagueId)
		public static let id = Column(CodingKeys.id)
		public static let date = Column(CodingKeys.date)
		public static let appliedDate = Column(CodingKeys.appliedDate)
		public static let preBowl = Column(CodingKeys.preBowl)
		public static let excludeFromStatistics = Column(CodingKeys.excludeFromStatistics)
		public static let alleyId = Column(CodingKeys.alleyId)
		public static let archivedOn = Column(CodingKeys.archivedOn)

		public static var coalescedDate = "COALESCE(\(appliedDate.name), \(date.name))"
	}
}

extension DerivableRequest<Series.Database> {
	public func orderByDate() -> Self {
		order(sql: "\(Series.Database.Columns.coalescedDate) DESC")
	}

	public func bowled(inLeague: League.ID) -> Self {
		let league = Series.Database.Columns.leagueId
		return filter(league == inLeague)
	}

	public func isIncludedInStatistics() -> Self {
		let excludeFromStatistics = Series.Database.Columns.excludeFromStatistics
		return self
			.filter(excludeFromStatistics == Series.ExcludeFromStatistics.include)
			.isNotArchived()
	}

	public func withNumberOfGames() -> Self {
		annotated(with: Series.Database.games.all().isNotArchived().count.forKey("numberOfGames") ?? 0)
	}
}

extension Series.Summary: FetchableRecord {}
extension Series.List: FetchableRecord {}
extension Series.Archived: FetchableRecord {}
extension Series.GameHost: FetchableRecord {}

extension Series {
	// swiftlint:disable:next function_parameter_count
	public static func insertGames(
		forSeries seriesId: Series.ID,
		excludeFromStatistics: Series.ExcludeFromStatistics,
		withPreferredGear preferredGear: [Gear.Database],
		startIndex: Int,
		count: Int,
		db: GRDB.Database
	) throws {
		@Dependency(\.uuid) var uuid
		for index in (startIndex..<startIndex + count) {
			let game = Game.Database(
				seriesId: seriesId,
				id: uuid(),
				index: index,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .init(from: excludeFromStatistics),
				duration: 0,
				archivedOn: nil
			)
			try game.insert(db)

			for frameIndex in Game.FRAME_INDICES {
				let frame = Frame.Database(
					gameId: game.id,
					index: frameIndex,
					roll0: nil,
					roll1: nil,
					roll2: nil,
					ball0: nil,
					ball1: nil,
					ball2: nil
				)
				try frame.insert(db)
			}

			for gear in preferredGear {
				try GameGear.Database(gameId: game.id, gearId: gear.id).insert(db)
			}
		}
	}
}
