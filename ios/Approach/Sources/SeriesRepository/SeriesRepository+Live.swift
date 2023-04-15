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
			list: { request in
				@Dependency(\.database) var database

				return database.reader().observe {
					try Series.Summary
						.all()
						.orderByDate()
						.bowled(inLeague: request.filter.league)
						.fetchAll($0)
				}
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try Series.Editable.fetchOne($0, id: id)
				}
			},
			save: { series in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try series.save($0)
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
