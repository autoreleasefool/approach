// swiftlint:disable file_length
import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import NotificationsServiceInterface
import PreferenceServiceInterface
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI

// swiftlint:disable:next type_body_length
public struct StatisticsDetails: Reducer {
	static let chartLoadingAnimationTime: TimeInterval = 0.5
	static let defaultSheetDetent: PresentationDetent = .fraction(0.25)

	public struct State: Equatable {
		public var listEntries: IdentifiedArrayOf<Statistics.ListEntryGroup> = []
		public var isLoadingNextChart = false
		public var chartContent: Statistics.ChartContent?

		public var filter: TrackableFilter
		public var sources: TrackableFilter.Sources?
		public var selectedStatistic: Statistics.ListEntry.ID?

		public var errors: Errors<ErrorID>.State = .init()

		@BindingState public var sheetDetent: PresentationDetent = StatisticsDetails.defaultSheetDetent
		public var willAdjustLaneLayoutAt: Date
		public var backdropSize: CGSize = .zero
		public var filtersSize: StatisticsFilterView.Size = .regular
		public var lastOrientation: UIDeviceOrientation?

		@PresentationState public var destination: Destination.State?

		public init(filter: TrackableFilter, withInitialStatistic: Statistics.ListEntry.ID? = nil) {
			self.filter = filter
			self.selectedStatistic = withInitialStatistic

			@Dependency(\.date) var date
			self.willAdjustLaneLayoutAt = date()
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case onAppear
			case didFirstAppear
			case didTapSourcePicker
			case didAdjustChartSize(backdropSize: CGSize, filtersSize: StatisticsFilterView.Size)
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case destination(PresentationAction<Destination.Action>)
			case charts(StatisticsDetailsCharts.Action)
			case errors(Errors<ErrorID>.Action)

			case didStartLoadingChart
			case adjustBackdrop
			case scrollListToEntry(Statistics.ListEntry.ID)
			case didLoadSources(TaskResult<TrackableFilter.Sources?>)
			case didLoadListEntries(TaskResult<[Statistics.ListEntryGroup]>)
			case didLoadChartContent(TaskResult<Statistics.ChartContent>)
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

	public enum ErrorID: Hashable {
		case failedToLoadSources
		case failedToLoadList
		case failedToLoadChart
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.preferences) var preferences
	@Dependency(\.statistics) var statistics
	@Dependency(\.uiDeviceNotifications) var uiDevice

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Scope(state: \.charts, action: /Action.internal..Action.InternalAction.charts) {
			StatisticsDetailsCharts()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					let loadingChartEffect: Effect<Action>
					if let statisticId = state.selectedStatistic, let statisticToLoad = Statistics.type(of: statisticId) {
						loadingChartEffect = loadChart(forStatistic: statisticToLoad, withFilter: state.filter)
					} else {
						loadingChartEffect = .none
					}

					return .merge(
						refreshStatistics(state: state),
						loadingChartEffect,
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

				case .binding(\.$sheetDetent):
					return .run { send in
						try await clock.sleep(for: .milliseconds(25))
						await send(.internal(.adjustBackdrop))
					}

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didStartLoadingChart:
					state.isLoadingNextChart = true
					return .none

				case let .didLoadSources(.success(sources)):
					state.sources = sources
					return .none

				case let .didLoadListEntries(.success(statistics)):
					state.listEntries = .init(uniqueElements: statistics)
					let presentEffect = presentDestinationForLastOrientation(withState: &state, scrollingTo: state.selectedStatistic)

					let statisticChartToLoad: Statistic.Type?
					if let statisticId = state.selectedStatistic,
						 let statisticToLoad = Statistics.type(of: statisticId) {
						statisticChartToLoad = statisticToLoad
					} else if let firstGroup = state.listEntries.first,
										let firstEntry = firstGroup.entries.first,
										let firstStatistic = Statistics.type(of: firstEntry.id) {
						statisticChartToLoad = firstStatistic
					} else {
						statisticChartToLoad = nil
					}

					if state.chartContent == nil, !state.isLoadingNextChart, let statisticChartToLoad {
						return .merge(
							loadChart(forStatistic: statisticChartToLoad, withFilter: state.filter),
							presentEffect
						)
					} else {
						return presentEffect
					}

				case let .didLoadChartContent(.success(chartContent)):
					state.chartContent = chartContent
					state.isLoadingNextChart = false
					switch state.chartContent {
					case .averaging, .counting, .percentage:
						return .none
					case .chartUnavailable, .dataMissing, .none:
						return .send(.view(.binding(.set(\.$sheetDetent, .medium))))
					}

				case let .didLoadSources(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadSources, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didLoadListEntries(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadList, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didLoadChartContent(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadChart, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case .adjustBackdrop:
					state.willAdjustLaneLayoutAt = date()
					return presentDestinationForLastOrientation(
						withState: &state,
						scrollingTo: state.listEntries.isEmpty ? nil : state.selectedStatistic
					)

				case let .scrollListToEntry(id):
					switch state.destination {
					case let .list(list):
						return list.scrollTo(id: id)
							.map { .internal(.destination(.presented(.list($0)))) }
					case .sourcePicker, .none:
						return .none
					}

				case let .orientationChange(orientation):
					state.lastOrientation = orientation
					return .merge(
						presentDestinationForLastOrientation(
							withState: &state,
							scrollingTo: state.listEntries.isEmpty ? nil : state.selectedStatistic
						),
						.run { send in
							try await clock.sleep(for: .milliseconds(300))
							await send(.internal(.adjustBackdrop))
						}
					)

				case let .destination(.presented(.list(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didRequestEntryDetails(id):
						guard let statistic = Statistics.type(of: id) else { return .none }
						state.selectedStatistic = id
						return .merge(
							loadChart(forStatistic: statistic, withFilter: state.filter),
							.send(.view(.binding(.set(\.$sheetDetent, StatisticsDetails.defaultSheetDetent))))
						)

					case .listRequiresReload:
						return refreshStatistics(state: state)
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

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .destination(.presented(.list(.internal))),
						.destination(.presented(.list(.view))),
						.destination(.presented(.sourcePicker(.internal))),
						.destination(.presented(.sourcePicker(.view))),
						.errors(.internal), .errors(.view),
						.charts(.internal), .charts(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case let .internal(.destination(.presented(.list(.delegate(.didRequestEntryDetails(id)))))):
				guard let statistic = Statistics.type(of: id) else { return nil }
				return Analytics.Statistic.Viewed(
					statisticName: statistic.title,
					countsH2AsH: preferences.bool(forKey: .statisticsCountH2AsH) ?? true,
					countsS2AsS: preferences.bool(forKey: .statisticsCountSplitWithBonusAsSplit) ?? true,
					hidesZeroStatistics: preferences.bool(forKey: .statisticsHideZeroStatistics) ?? true
				)
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
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

				let result = await TaskResult { try await self.statistics.chart(statistic: forStatistic, filter: withFilter) }

				let timeSpent = date().timeIntervalSince(startTime)
				if timeSpent < Self.chartLoadingAnimationTime {
					try await clock.sleep(for: .milliseconds((Self.chartLoadingAnimationTime - timeSpent) * 1000))
				}

				await send(.internal(.didLoadChartContent(result)), animation: .easeInOut)
			}
		)
		.cancellable(id: CancelID.loadingChartValues, cancelInFlight: true)
	}

	private func presentDestinationForLastOrientation(
		withState state: inout State,
		scrollingTo entryId: Statistics.ListEntry.ID? = nil
	) -> Effect<StatisticsDetails.Action> {
		var list: StatisticsDetailsList.State?
		switch state.lastOrientation {
		case .portrait, .portraitUpsideDown, .faceUp, .faceDown, .unknown, .none:
			list = .init(listEntries: state.listEntries, hasTappableElements: true)
		case .landscapeLeft, .landscapeRight:
			list = nil
		@unknown default:
			list = .init(listEntries: state.listEntries, hasTappableElements: true)
		}

		switch state.destination {
		case let .list(existingState):
			list?.entryToHighlight = existingState.entryToHighlight
		case .sourcePicker, .none:
			break
		}

		if let list {
			state.destination = .list(list)
		} else {
			state.destination = nil
		}

		guard let entryId else { return .none }
		return .run { send in
			try await clock.sleep(for: .milliseconds(25))
			await send(.internal(.scrollListToEntry(entryId)))
		}
	}
}

extension StatisticsDetails.State {
	var charts: StatisticsDetailsCharts.State {
		get {
			.init(
				aggregation: filter.aggregation,
				chartContent: chartContent,
				filterSource: filter.source,
				isFilterTooNarrow: filter.isTooNarrowForCharts,
				isLoadingNextChart: isLoadingNextChart
			)
		}
		// We aren't observing any values from this reducer, so we ignore the setter
		// swiftlint:disable:next unused_setter_value
		set {}
	}
}
