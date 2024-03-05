import Dependencies
import Foundation
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary

public struct StatisticsRepository: Sendable {
	public var loadSources: @Sendable (TrackableFilter.Source) async throws -> TrackableFilter.Sources
	public var loadDefaultSources: @Sendable () async throws -> TrackableFilter.Sources?
	public var saveLastUsedSource: @Sendable (TrackableFilter.Source) async -> Void
	public var loadValues: @Sendable (TrackableFilter) async throws -> [Statistics.ListEntryGroup]
	public var loadChart: @Sendable (Statistic.Type, TrackableFilter) async throws -> Statistics.ChartContent
	public var loadWidgetSources: @Sendable (StatisticsWidget.Source) async throws -> StatisticsWidget.Sources
	public var loadDefaultWidgetSources: @Sendable () async throws -> StatisticsWidget.Sources?
	public var loadWidgetData: @Sendable (StatisticsWidget.Configuration) async throws -> Statistics.ChartContent
	public var hideNewStatisticLabels: @Sendable () async -> Void

	public init(
		loadSources: @escaping @Sendable (TrackableFilter.Source) async throws -> TrackableFilter.Sources,
		loadDefaultSources: @escaping @Sendable () async throws -> TrackableFilter.Sources?,
		saveLastUsedSource: @escaping @Sendable (TrackableFilter.Source) async -> Void,
		loadValues: @escaping @Sendable (TrackableFilter) async throws -> [Statistics.ListEntryGroup],
		loadChart: @escaping @Sendable (Statistic.Type, TrackableFilter) async throws -> Statistics.ChartContent,
		loadWidgetSources: @escaping @Sendable (StatisticsWidget.Source) async throws -> StatisticsWidget.Sources,
		loadDefaultWidgetSources: @escaping @Sendable () async throws -> StatisticsWidget.Sources?,
		loadWidgetData: @escaping @Sendable (StatisticsWidget.Configuration) async throws -> Statistics.ChartContent,
		hideNewStatisticLabels: @escaping @Sendable () async -> Void
	) {
		self.loadSources = loadSources
		self.loadDefaultSources = loadDefaultSources
		self.saveLastUsedSource = saveLastUsedSource
		self.loadValues = loadValues
		self.loadChart = loadChart
		self.loadWidgetSources = loadWidgetSources
		self.loadDefaultWidgetSources = loadDefaultWidgetSources
		self.loadWidgetData = loadWidgetData
		self.hideNewStatisticLabels = hideNewStatisticLabels
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
		loadDefaultSources: { unimplemented("\(Self.self).loadDefaultSource") },
		saveLastUsedSource: { _ in unimplemented("\(Self.self).saveLastUsedSource") },
		loadValues: { _ in unimplemented("\(Self.self).loadValues") },
		loadChart: { _, _ in unimplemented("\(Self.self).loadChart") },
		loadWidgetSources: { _ in unimplemented("\(Self.self).loadWidgetSources") },
		loadDefaultWidgetSources: { unimplemented("\(Self.self).loadDefaultWidgetSources") },
		loadWidgetData: { _ in unimplemented("\(Self.self).loadWidgetData") },
		hideNewStatisticLabels: { unimplemented("\(Self.self).hideNewStatisticsLabels") }
	)
}
