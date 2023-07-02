import Dependencies
import ModelsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StatisticsWidgetsLibrary

public struct StatisticsWidgetsRepository: Sendable {
	public var loadSources: @Sendable (StatisticsWidget.Source) async throws -> StatisticsWidget.Sources?
	public var loadChart: @Sendable (StatisticsWidget.Configuration) async throws -> Statistics.ChartContent
	public var fetchAll: @Sendable (String?) -> AsyncThrowingStream<[StatisticsWidget.Configuration], Error>
	public var updatePriorities: @Sendable ([StatisticsWidget.ID]) async throws -> Void
	public var create: @Sendable (StatisticsWidget.Create) async throws -> Void
	public var delete: @Sendable (StatisticsWidget.ID) async throws -> Void

	public init(
		loadSources: @escaping @Sendable (StatisticsWidget.Source) async throws -> StatisticsWidget.Sources?,
		loadChart: @escaping @Sendable (StatisticsWidget.Configuration) async throws -> Statistics.ChartContent,
		fetchAll: @escaping @Sendable (String?) -> AsyncThrowingStream<[StatisticsWidget.Configuration], Error>,
		updatePriorities: @escaping @Sendable ([StatisticsWidget.ID]) async throws -> Void,
		create: @escaping @Sendable (StatisticsWidget.Create) async throws -> Void,
		delete: @escaping @Sendable (StatisticsWidget.ID) async throws -> Void
	) {
		self.loadSources = loadSources
		self.loadChart = loadChart
		self.fetchAll = fetchAll
		self.updatePriorities = updatePriorities
		self.create = create
		self.delete = delete
	}

	public func chart(_ configuration: StatisticsWidget.Configuration) async throws -> Statistics.ChartContent {
		try await self.loadChart(configuration)
	}

	public func fetchAll(forContext context: String?) -> AsyncThrowingStream<[StatisticsWidget.Configuration], Error> {
		self.fetchAll(context)
	}
}

extension StatisticsWidgetsRepository: TestDependencyKey {
	public static var testValue = Self(
		loadSources: { _ in unimplemented("\(Self.self).loadSources") },
		loadChart: { _ in unimplemented("\(Self.self).loadChart") },
		fetchAll: { _ in unimplemented("\(Self.self).fetchAll") },
		updatePriorities: { _ in unimplemented("\(Self.self).updatePriorities") },
		create: { _ in unimplemented("\(Self.self).create") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var statisticsWidgets: StatisticsWidgetsRepository {
		get { self[StatisticsWidgetsRepository.self] }
		set { self[StatisticsWidgetsRepository.self] = newValue }
	}
}
