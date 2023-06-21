import ComposableArchitecture
import FeatureActionLibrary
import NotificationsServiceInterface
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import SwiftUI

// swiftlint:disable:next type_body_length
public struct StatisticsDetails: Reducer {
	static let chartLoadingAnimationTime: TimeInterval = 0.5
	static let defaultSheetDetent: PresentationDetent = .fraction(0.25)

	public struct State: Equatable {
		public var listEntries: IdentifiedArrayOf<Statistics.ListEntryGroup> = []
		public var isLoadingNextChart = false
		public var chartContent: StatisticsDetailsCharts.ChartContent?

		public var filter: TrackableFilter
		public var sources: TrackableFilter.Sources?

		public var sheetDetent: PresentationDetent = StatisticsDetails.defaultSheetDetent
		public var willAdjustLaneLayoutAt: Date
		public var backdropSize: CGSize = .zero
		public var filtersSize: StatisticsFilterView.Size = .regular

		@PresentationState public var destination: Destination.State?

		public init(filter: TrackableFilter) {
			self.filter = filter

			@Dependency(\.date) var date
			self.willAdjustLaneLayoutAt = date()
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
			case didTapSourcePicker
			case didChangeDetent(PresentationDetent)
			case didAdjustChartSize(backdropSize: CGSize, filtersSize: StatisticsFilterView.Size)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case destination(PresentationAction<Destination.Action>)
			case charts(StatisticsDetailsCharts.Action)

			case didStartLoadingChart
			case adjustBackdrop
			case didLoadSources(TaskResult<TrackableFilter.Sources?>)
			case didLoadListEntries(TaskResult<[Statistics.ListEntryGroup]>)
			case didLoadChartContent(TaskResult<StatisticsDetailsCharts.ChartContent>)
			case orientationChange(UIDeviceOrientation)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case list(StatisticsDetailsList.State)
			case sourcePicker(StatisticsSourcePicker.State)
		}

		public enum Action: Equatable {
			case list(StatisticsDetailsList.Action)
			case sourcePicker(StatisticsSourcePicker.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.list, action: /Action.list) {
				StatisticsDetailsList()
			}
			Scope(state: /State.sourcePicker, action: /Action.sourcePicker) {
				StatisticsSourcePicker()
			}
		}
	}

	public enum CancelID {
		case loadingStaticValues
		case loadingChartValues
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.statistics) var statistics
	@Dependency(\.uiDeviceNotifications) var uiDevice

	public var body: some ReducerOf<Self> {
		Scope(state: \.charts, action: /Action.internal..Action.InternalAction.charts) {
			StatisticsDetailsCharts()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .merge(
						refreshStatistics(state: state),
						.run { send in
							for await orientation in uiDevice.orientationDidChange() {
								await send(.internal(.orientationChange(orientation)))
							}
						}
					)

				case .didTapSourcePicker:
					state.destination = .sourcePicker(.init(source: state.filter.source))
					return .none

				case let .didAdjustChartSize(backdropSize, filtersSize):
					state.backdropSize = backdropSize
					state.filtersSize = filtersSize
					return .none

				case let .didChangeDetent(newDetent):
					state.sheetDetent = newDetent
					return .run { send in
						try await clock.sleep(for: .milliseconds(25))
						await send(.internal(.adjustBackdrop))
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didStartLoadingChart:
					state.isLoadingNextChart = true
					return .none

				case let .didLoadSources(.success(sources)):
					state.sources = sources
					return .none

				case .didLoadSources(.failure):
					// TODO: handle error loading sources
					return .none

				case let .didLoadListEntries(.success(statistics)):
					state.listEntries = .init(uniqueElements: statistics)
					state.destination = .list(.init(listEntries: state.listEntries))
					if state.chartContent == nil,
						 let firstGroup = state.listEntries.first,
						 let firstEntry = firstGroup.entries.first,
						 let firstStatistic = Statistics.type(of: firstEntry.id) {
						return loadChart(forStatistic: firstStatistic, withFilter: state.filter)
					} else {
						return .none
					}

				case .didLoadListEntries(.failure):
					// TODO: show statistics loading failure
					return .none

				case let .didLoadChartContent(.success(chartContent)):
					state.chartContent = chartContent
					state.isLoadingNextChart = false
					return .none

				case .didLoadChartContent(.failure):
					// TODO: show statistics loading failure
					return .none

				case .adjustBackdrop:
					state.willAdjustLaneLayoutAt = date()
					return .none

				case let .orientationChange(orientation):
					switch orientation {
					case .portrait, .portraitUpsideDown, .faceUp, .faceDown, .unknown:
						state.destination = .list(.init(listEntries: state.listEntries))
					case .landscapeLeft, .landscapeRight:
						state.destination = nil
					@unknown default:
						state.destination = .list(.init(listEntries: state.listEntries))
					}
					return .run { send in
						try await clock.sleep(for: .milliseconds(300))
						await send(.internal(.adjustBackdrop))
					}

				case let .destination(.presented(.list(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didRequestEntryDetails(id):
						guard let statistic = Statistics.type(of: id) else { return .none }
						state.sheetDetent = StatisticsDetails.defaultSheetDetent
						return loadChart(forStatistic: statistic, withFilter: state.filter)
					}

				case let .destination(.presented(.sourcePicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSource(source):
						state.filter.source = source
						return refreshStatistics(state: state)
					}

				case let .charts(.delegate(delegateAction)):
					switch delegateAction {
					case let .didChangeAggregation(aggregation):
						guard let statisticId = state.chartContent?.title,
									let statistic = Statistics.type(of: statisticId)
						else {
							return .none
						}
						state.filter.aggregation = aggregation
						return loadChart(forStatistic: statistic, withFilter: state.filter)
					}

				case .destination(.dismiss):
					return .run { [entries = state.listEntries] send in
						await send(.internal(.didLoadListEntries(.success(entries.elements))))
					}

				case .destination(.presented(.list(.internal))),
						.destination(.presented(.list(.view))),
						.destination(.presented(.sourcePicker(.internal))),
						.destination(.presented(.sourcePicker(.view))):
					return .none

				case .charts(.internal), .charts(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}
	}

	private func refreshStatistics(state: State) -> Effect<Action> {
		.merge(
			.run { [filter = state.filter] send in
				await send(.internal(.didLoadListEntries(TaskResult {
					try await statistics.load(for: filter)
				})))
			},
			.run { [source = state.filter.source] send in
				await send(.internal(.didLoadSources(TaskResult {
					try await statistics.loadSources(source)
				})))
			}
		)
		.cancellable(id: CancelID.loadingStaticValues, cancelInFlight: true)
	}

	private func loadChart(forStatistic: Statistic.Type, withFilter: TrackableFilter) -> Effect<Action> {
		.concatenate(
			.run { send in await send(.internal(.didStartLoadingChart), animation: .easeInOut) },
			.run { send in
				let startTime = date()

				var result: TaskResult<StatisticsDetailsCharts.ChartContent>?
				if let countingStatistic = forStatistic as? CountingStatistic.Type {
					result = await loadChart(forCountingStatistic: countingStatistic, withFilter: withFilter)
				} else if let highestOfStatistic = forStatistic as? HighestOfStatistic.Type {
					result = await loadChart(forHighestOfStatistic: highestOfStatistic, withFilter: withFilter)
				} else if let averagingStatistic = forStatistic as? AveragingStatistic.Type {
					result = await loadChart(forAveragingStatistic: averagingStatistic, withFilter: withFilter)
				} else if let percentageStatistic = forStatistic as? PercentageStatistic.Type {
					result = await loadChart(forPercentageStatistic: percentageStatistic, withFilter: withFilter)
				}

				let timeSpent = date().timeIntervalSince(startTime)
				if timeSpent < Self.chartLoadingAnimationTime {
					try await clock.sleep(for: .milliseconds((Self.chartLoadingAnimationTime - timeSpent) * 1000))
				}

				if let result {
					await send(.internal(.didLoadChartContent(result)), animation: .easeInOut)
				} else {
					await send(
						.internal(.didLoadChartContent(.success(.chartUnavailable(statistic: forStatistic.title)))),
						animation: .easeInOut
					)
				}
			}
		)
		.cancellable(id: CancelID.loadingChartValues, cancelInFlight: true)
	}

	private func loadChart(
		forCountingStatistic statistic: CountingStatistic.Type,
		withFilter: TrackableFilter
	) async -> TaskResult<StatisticsDetailsCharts.ChartContent> {
		await .init {
			if let data = try await self.statistics.chart(statistic: statistic, filter: withFilter), !data.isEmpty {
				return .countingChart(data)
			} else {
				return .chartUnavailable(statistic: statistic.title)
			}
		}
	}

	private func loadChart(
		forAveragingStatistic statistic: AveragingStatistic.Type,
		withFilter: TrackableFilter
	) async -> TaskResult<StatisticsDetailsCharts.ChartContent> {
		await .init {
			if let data = try await self.statistics.chart(statistic: statistic, filter: withFilter), !data.isEmpty {
				return .averagingChart(data)
			} else {
				return .chartUnavailable(statistic: statistic.title)
			}
		}
	}

	private func loadChart(
		forHighestOfStatistic statistic: HighestOfStatistic.Type,
		withFilter: TrackableFilter
	) async -> TaskResult<StatisticsDetailsCharts.ChartContent> {
		await .init {
			if let data = try await self.statistics.chart(statistic: statistic, filter: withFilter), !data.isEmpty {
				return .countingChart(data)
			} else {
				return .chartUnavailable(statistic: statistic.title)
			}
		}
	}

	private func loadChart(
		forPercentageStatistic statistic: PercentageStatistic.Type,
		withFilter: TrackableFilter
	) async -> TaskResult<StatisticsDetailsCharts.ChartContent> {
		await .init {
			if let data = try await self.statistics.chart(statistic: statistic, filter: withFilter), !data.isEmpty {
				return .percentageChart(data)
			} else {
				return .chartUnavailable(statistic: statistic.title)
			}
		}
	}
}

extension StatisticsDetails.State {
	var charts: StatisticsDetailsCharts.State {
		get {
			.init(
				chartContent: chartContent,
				isLoadingNextChart: isLoadingNextChart,
				aggregation: filter.aggregation
			)
		}
		// We aren't observing any values from this reducer, so we ignore the setter
		// swiftlint:disable:next unused_setter_value
		set {}
	}
}
