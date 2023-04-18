import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary
import RepositoryLibrary
import SeriesRepositoryInterface

extension SeriesRepository: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			list: { league, _ in
				@Dependency(\.database) var database
				return database.reader().observe {
					try Series.Database
						.all()
						.orderByDate()
						.bowled(inLeague: league)
						.asRequest(of: Series.Summary.self)
						.fetchAll($0)
				}
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					let lanesAlias = TableAlias(name: "lanes")
					return try Series.Database
						.filter(id: id)
						.including(optional: Series.Database.alley.forKey("location"))
						.including(
							all: Series.Database
								.lanes
								.order(Lane.Database.Columns.label.collating(.localizedCaseInsensitiveCompare))
								.aliased(lanesAlias)
						)
						.asRequest(of: Series.EditWithLanes.self)
						.fetchOne($0)
				}
			},
			create: { series in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try series.insert($0)
				}
			},
			update: { series in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try series.update($0)
				}
			},
			delete: { id in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try Series.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
