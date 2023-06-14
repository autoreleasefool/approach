import ComposableArchitecture
import FeatureActionLibrary
import NotificationsServiceInterface
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import SwiftUI

public struct StatisticsDetails: Reducer {
	static let chartLoadingAnimationTime: TimeInterval = 0.5
	static let defaultSheetDetent: PresentationDetent = .fraction(0.25)

	public struct State: Equatable {
		public var staticValues: IdentifiedArrayOf<StaticValueGroup> = []
		public var isLoadingNextChart = false
		public var chartData: StatisticsChart.Data?

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
			case didLoadStaticValues(TaskResult<[StaticValueGroup]>)
			case didLoadChart(TaskResult<StatisticsChart.Data>)
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

				case let .didLoadStaticValues(.success(statistics)):
					state.staticValues = .init(uniqueElements: statistics)
					state.destination = .list(.init(staticValues: state.staticValues))
					return .none

				case .didLoadStaticValues(.failure):
					// TODO: show statistics loading failure
					return .none

				case let .didLoadChart(.success(chartData)):
					state.chartData = chartData
					state.isLoadingNextChart = false
					return .none

				case .didLoadChart(.failure):
					// TODO: show statistics loading failure
					return .none

				case .adjustBackdrop:
					state.willAdjustLaneLayoutAt = date()
					return .none

				case let .orientationChange(orientation):
					switch orientation {
					case .portrait, .portraitUpsideDown, .faceUp, .faceDown, .unknown:
						state.destination = .list(.init(staticValues: state.staticValues))
					case .landscapeLeft, .landscapeRight:
						state.destination = nil
					@unknown default:
						state.destination = .list(.init(staticValues: state.staticValues))
					}
					return .run { send in
						try await clock.sleep(for: .milliseconds(300))
						await send(.internal(.adjustBackdrop))
					}

				case let .destination(.presented(.list(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didRequestStaticValue(id):
						guard let statistic = Statistics.type(fromId: id) as? (any GraphableStatistic.Type) else { return .none }
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
						guard let statisticId = state.chartData?.title,
									let statistic = Statistics.type(fromId: statisticId) as? (any GraphableStatistic.Type)
						else {
							return .none
						}
						state.filter.aggregation = aggregation
						return loadChart(forStatistic: statistic, withFilter: state.filter)
					}

				case .destination(.dismiss):
					return .run { [values = state.staticValues] send in
						await send(.internal(.didLoadStaticValues(.success(values.elements))))
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
				await send(.internal(.didLoadStaticValues(TaskResult {
					try await statistics.load(for: filter).staticValueGroups()
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

	private func loadChart(forStatistic: any GraphableStatistic.Type, withFilter: TrackableFilter) -> Effect<Action> {
		.concatenate(
			.run { send in await send(.internal(.didStartLoadingChart), animation: .easeInOut) },
			.run { send in
				let startTime = date()
				let result = await TaskResult {
					let entries = try await self.statistics.chart(statistic: forStatistic, filter: withFilter)
					return StatisticsChart.Data(title: forStatistic.title, entries: entries)
				}
				let timeSpent = date().timeIntervalSince(startTime)
				if timeSpent < Self.chartLoadingAnimationTime {
					try await clock.sleep(for: .milliseconds((Self.chartLoadingAnimationTime - timeSpent) * 1000))
				}

				await send(.internal(.didLoadChart(result)), animation: .easeInOut)
			}
		)
		.cancellable(id: CancelID.loadingChartValues, cancelInFlight: true)
	}
}

extension StatisticsDetails.State {
	var charts: StatisticsDetailsCharts.State {
		get {
			.init(
				chartData: chartData,
				isLoadingNextChart: isLoadingNextChart,
				aggregation: filter.aggregation
			)
		}
		// We aren't observing any values from this reducer, so we ignore the setter
		// swiftlint:disable:next unused_setter_value
		set {}
	}
}
