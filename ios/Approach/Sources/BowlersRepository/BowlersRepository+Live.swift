import BowlersRepositoryInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary

extension BowlersRepository: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			bowlers: {
				@Dependency(\.database) var database
				return database.reader().observe {
					let bowlers = try Bowler.DatabaseModel
						.all()
						.orderByName()
						.filter(byStatus: .playable)
						.fetchAll($0)
					return bowlers.map { .init($0) }
				}
			},
			opponents: {
				@Dependency(\.database) var database
				return database.reader().observe {
					let bowlers = try Bowler.DatabaseModel
						.all()
						.orderByName()
						.fetchAll($0)
					return bowlers.map { .init($0) }
				}
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try .init(Bowler.DatabaseModel.fetchOne($0, id: id))
				}
			},
			save: { bowler in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try bowler.databaseModel.save($0)
				}
			}
		)
	}()
}
