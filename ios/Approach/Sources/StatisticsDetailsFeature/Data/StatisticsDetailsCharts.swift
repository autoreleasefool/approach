import ComposableArchitecture
import FeatureActionLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI

@Reducer
public struct StatisticsDetailsCharts: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var aggregation: TrackableFilter.Aggregation
		public var chartContent: Statistics.ChartContent?
		public var filterSource: TrackableFilter.Source
		public var isFilterTooNarrow: Bool
		public var isLoadingNextChart: Bool
	}

	public enum Action: FeatureAction, BindableAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate {
			case didChangeAggregation(TrackableFilter.Aggregation)
		}
		@CasePathable
		public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case .view(.doNothing):
				return .none

			case .internal(.doNothing):
				return .none

			case .binding(\.aggregation):
				return .send(.delegate(.didChangeAggregation(state.aggregation)))

			case .delegate, .binding:
				return .none
			}
		}
	}
}

// MARK: - View

public struct StatisticsDetailsChartsView: View {
	@Bindable public var store: StoreOf<StatisticsDetailsCharts>

	public var body: some View {
		VStack {
			if store.isLoadingNextChart {
				ProgressView()
					.padding(.bottom)
			}

			if let chartContent = store.chartContent {
				switch chartContent {
				case let .counting(data):
					CountingChart.Default(data)
				case let .averaging(data):
					AveragingChart.Default(data)
				case let .percentage(data):
					PercentageChart.Default(data)
				case let .chartUnavailable(statistic), let .dataMissing(statistic):
					emptyChart(statistic, warnTooNarrow: store.isFilterTooNarrow)
				}
			}

			Spacer()

			if store.chartContent?.showsAggregationPicker ?? false {
				Picker(
					Strings.Statistics.Filter.aggregation,
					selection: $store.aggregation
				) {
					ForEach(TrackableFilter.Aggregation.allCases) { aggregation in
						Text(aggregation.description(forSource: store.filterSource))
							.tag(aggregation)
					}
				}
				.pickerStyle(.segmented)
				.padding()
			}
		}
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
			Strings.Statistics.Filter.Aggregation.accumulate
		case (.periodic, .series):
			Strings.Statistics.Filter.Aggregation.byGame
		case (.periodic, _):
			Strings.Statistics.Filter.Aggregation.periodic
		}
	}
}
