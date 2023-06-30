import Dependencies
import Foundation
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary

public struct StatisticsRepository: Sendable {
	public var loadSources: @Sendable (TrackableFilter.Source) async throws -> TrackableFilter.Sources?
	public var loadValues: @Sendable (TrackableFilter) async throws -> [Statistics.ListEntryGroup]
	public var loadChart: @Sendable (Statistic.Type, TrackableFilter) async throws -> Statistics.ChartContent

	public init(
		loadSources: @escaping @Sendable (TrackableFilter.Source) async throws -> TrackableFilter.Sources?,
		loadValues: @escaping @Sendable (TrackableFilter) async throws -> [Statistics.ListEntryGroup],
		loadChart: @escaping @Sendable (Statistic.Type, TrackableFilter) async throws -> Statistics.ChartContent
	) {
		self.loadSources = loadSources
		self.loadValues = loadValues
		self.loadChart = loadChart
	}

	public func load(for filter: TrackableFilter) async throws -> [Statistics.ListEntryGroup] {
		try await self.loadValues(filter)
	}

	public func chart(statistic: Statistic.Type, filter: TrackableFilter) async throws -> Statistics.ChartContent {
		try await self.loadChart(statistic, filter)
	}
}

extension StatisticsRepository: TestDependencyKey {
	public static var testValue = Self(
		loadSources: { _ in unimplemented("\(Self.self).loadSources") },
		loadValues: { _ in unimplemented("\(Self.self).loadValues") },
		loadChart: { _, _ in unimplemented("\(Self.self).loadChart") }
	)
}

extension DependencyValues {
	public var statistics: StatisticsRepository {
		get { self[StatisticsRepository.self] }
		set { self[StatisticsRepository.self] = newValue }
	}
}
