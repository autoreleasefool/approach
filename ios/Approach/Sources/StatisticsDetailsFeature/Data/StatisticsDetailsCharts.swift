import ComposableArchitecture
import FeatureActionLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI

public struct StatisticsDetailsCharts: Reducer {
	public struct State: Equatable {
		@BindingState public var aggregation: TrackableFilter.Aggregation
		public var chartContent: Statistics.ChartContent?
		public var filterSource: TrackableFilter.Source
		public var isFilterTooNarrow: Bool
		public var isLoadingNextChart: Bool
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {
			case didChangeAggregation(TrackableFilter.Aggregation)
		}
		public enum InternalAction: Equatable { case doNothing }

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .binding(\.$aggregation):
					return .send(.delegate(.didChangeAggregation(state.aggregation)))

				case .binding:
					return .none
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

public struct StatisticsDetailsChartsView: View {
	let store: StoreOf<StatisticsDetailsCharts>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			VStack {
				if viewStore.isLoadingNextChart {
					ProgressView()
						.padding(.bottom)
				}

				if let chartContent = viewStore.chartContent {
					switch chartContent {
					case let .counting(data):
						CountingChart.Default(data)
					case let .averaging(data):
						AveragingChart.Default(data)
					case let .percentage(data):
						PercentageChart.Default(data)
					case let .chartUnavailable(statistic), let .dataMissing(statistic):
						emptyChart(statistic, warnTooNarrow: viewStore.isFilterTooNarrow)
					}
				}

				Spacer()

				if viewStore.chartContent?.showsAggregationPicker ?? false {
					Picker(
						Strings.Statistics.Filter.aggregation,
						selection: viewStore.$aggregation
					) {
						ForEach(TrackableFilter.Aggregation.allCases) {
							Text($0.description(forSource: viewStore.filterSource)).tag($0)
						}
					}
					.pickerStyle(.segmented)
					.padding()
				}
			}
		})
	}

	private func emptyChart(_ statistic: String, warnTooNarrow: Bool) -> some View {
		VStack(alignment: .leading) {
			Text(statistic)
				.font(.headline)

			Spacer()

			Text(Strings.Statistics.Charts.unavailable)
				.font(.body)
				.fontWeight(.light)
				.frame(maxWidth: .infinity)
				.padding(.top, .standardSpacing)

			if warnTooNarrow {
				Text(Strings.Statistics.Charts.filterTooNarrow)
					.font(.caption)
					.frame(maxWidth: .infinity)
					.padding(.top, .smallSpacing)
			} else {
				Text(Strings.Statistics.Charts.Unavailable.description)
					.font(.caption)
					.frame(maxWidth: .infinity)
					.padding(.top, .smallSpacing)
			}

			Spacer()
		}
	}
}

extension TrackableFilter.Aggregation {
	func description(forSource: TrackableFilter.Source) -> String {
		switch (self, forSource) {
		case (.accumulate, _):
			return Strings.Statistics.Filter.Aggregation.accumulate
		case (.periodic, .series):
			return Strings.Statistics.Filter.Aggregation.byGame
		case (.periodic, _):
			return Strings.Statistics.Filter.Aggregation.periodic
		}
	}
}
