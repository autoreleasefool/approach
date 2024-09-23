import Collections
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
	public var observeRecentlyUsedFilters: @Sendable () ->
		AsyncThrowingStream<OrderedDictionary<TrackableFilter, TrackableFilter.Sources>, Error>
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
		observeRecentlyUsedFilters: @escaping @Sendable () ->
			AsyncThrowingStream<OrderedDictionary<TrackableFilter, TrackableFilter.Sources>, Error>,
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
		self.observeRecentlyUsedFilters = observeRecentlyUsedFilters
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
	public static var testValue: Self {
		Self(
			loadSources: { _ in unimplemented("\(Self.self).loadSources", placeholder: .placeholder) },
			loadDefaultSources: { unimplemented("\(Self.self).loadDefaultSource", placeholder: nil) },
			saveLastUsedSource: { _ in unimplemented("\(Self.self).saveLastUsedSource") },
			observeRecentlyUsedFilters: { unimplemented("\(Self.self).observeRecentlyUsedFilters", placeholder: .never) },
			loadValues: { _ in unimplemented("\(Self.self).loadValues", placeholder: []) },
			loadChart: { _, _ in unimplemented("\(Self.self).loadChart", placeholder: .chartUnavailable(statistic: "")) },
			loadWidgetSources: { _ in unimplemented("\(Self.self).loadWidgetSources", placeholder: .placeholder) },
			loadDefaultWidgetSources: { unimplemented("\(Self.self).loadDefaultWidgetSources", placeholder: nil) },
			loadWidgetData: { _ in unimplemented("\(Self.self).loadWidgetData", placeholder: .chartUnavailable(statistic: "")) },
			hideNewStatisticLabels: { unimplemented("\(Self.self).hideNewStatisticsLabels") }
		)
	}
}
