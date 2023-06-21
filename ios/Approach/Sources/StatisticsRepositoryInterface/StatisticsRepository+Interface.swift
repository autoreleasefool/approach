import Dependencies
import Foundation
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary

public struct StatisticsRepository: Sendable {
	public var loadSources: @Sendable (TrackableFilter.Source) async throws -> TrackableFilter.Sources?
	public var loadValues: @Sendable (TrackableFilter) async throws -> [Statistics.ListEntryGroup]
	public var loadCountingChart: @Sendable (
		CountingStatistic.Type, TrackableFilter
	) async throws -> CountingChart.Data?
	public var loadHighestOfChart: @Sendable(
		HighestOfStatistic.Type, TrackableFilter
	) async throws -> CountingChart.Data?
	public var loadAveragingChart: @Sendable (
		AveragingStatistic.Type, TrackableFilter
	) async throws -> AveragingChart.Data?
	public var loadPercentageChart: @Sendable (
		PercentageStatistic.Type, TrackableFilter
	) async throws -> PercentageChart.Data?

	public init(
		loadSources: @escaping @Sendable (TrackableFilter.Source) async throws -> TrackableFilter.Sources?,
		loadValues: @escaping @Sendable (TrackableFilter) async throws -> [Statistics.ListEntryGroup],
		loadCountingChart: @escaping @Sendable
			(CountingStatistic.Type, TrackableFilter) async throws -> CountingChart.Data?,
		loadHighestOfChart: @escaping @Sendable
			(HighestOfStatistic.Type, TrackableFilter) async throws -> CountingChart.Data?,
		loadAveragingChart: @escaping @Sendable
			(AveragingStatistic.Type, TrackableFilter) async throws -> AveragingChart.Data?,
		loadPercentageChart: @escaping @Sendable
			(PercentageStatistic.Type, TrackableFilter) async throws -> PercentageChart.Data?
	) {
		self.loadSources = loadSources
		self.loadValues = loadValues
		self.loadCountingChart = loadCountingChart
		self.loadHighestOfChart = loadHighestOfChart
		self.loadAveragingChart = loadAveragingChart
		self.loadPercentageChart = loadPercentageChart
	}

	public func load(for filter: TrackableFilter) async throws -> [Statistics.ListEntryGroup] {
		try await self.loadValues(filter)
	}

	public func chart(statistic: CountingStatistic.Type, filter: TrackableFilter) async throws -> CountingChart.Data? {
		try await self.loadCountingChart(statistic, filter)
	}

	public func chart(statistic: HighestOfStatistic.Type, filter: TrackableFilter) async throws -> CountingChart.Data? {
		try await self.loadHighestOfChart(statistic, filter)
	}

	public func chart(statistic: AveragingStatistic.Type, filter: TrackableFilter) async throws -> AveragingChart.Data? {
		try await self.loadAveragingChart(statistic, filter)
	}

	public func chart(statistic: PercentageStatistic.Type, filter: TrackableFilter) async throws -> PercentageChart.Data? {
		try await self.loadPercentageChart(statistic, filter)
	}
}

extension StatisticsRepository: TestDependencyKey {
	public static var testValue = Self(
		loadSources: { _ in unimplemented("\(Self.self).loadSources") },
		loadValues: { _ in unimplemented("\(Self.self).loadValues") },
		loadCountingChart: { _, _ in unimplemented("\(Self.self).loadCountingChart") },
		loadHighestOfChart: { _, _ in unimplemented("\(Self.self).loadHighestOfChart") },
		loadAveragingChart: { _, _ in unimplemented("\(Self.self).loadAveragingChart") },
		loadPercentageChart: { _, _ in unimplemented("\(Self.self).loadPercentageChart") }
	)
}

extension DependencyValues {
	public var statistics: StatisticsRepository {
		get { self[StatisticsRepository.self] }
		set { self[StatisticsRepository.self] = newValue }
	}
}
