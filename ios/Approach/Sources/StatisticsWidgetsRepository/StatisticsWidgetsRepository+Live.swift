import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import ModelsLibrary
import StatisticsRepositoryInterface
import StatisticsWidgetsRepositoryInterface

extension StatisticsWidgetsRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database
		@Dependency(\.statistics) var statistics

		return Self(
			loadSources: { source in try await statistics.loadWidgetSources(source) },
			loadChart: { configuration in try await statistics.loadWidgetData(configuration) },
			fetchAll: { context in
				database.reader().observe {
					try StatisticsWidget.Database
						.all()
						.orderByPriority()
						.filter(byContext: context)
						.asRequest(of: StatisticsWidget.Configuration.self)
						.fetchAll($0)
				}
			},
			updatePriorities: { ids in
				let contextsCount = try await database.reader().read {
					try StatisticsWidget.Database
						.filter(ids: ids)
						.group(StatisticsWidget.Database.Columns.context)
						.fetchCount($0)
				}

				guard contextsCount <= 1 else { throw StatisticsWidget.ContextError.mismatchedContexts }

				try await database.writer().write { db in
					for (priority, id) in ids.enumerated() {
						if var widget = try StatisticsWidget.Database.fetchOne(db, id: id) {
							widget.priority = priority
							try widget.update(db)
						}
					}
				}
			},
			create: { widget in
				try await database.writer().write {
					try widget.insert($0)
				}
			},
			delete: { id in
				return try await database.writer().write {
					try StatisticsWidget.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
