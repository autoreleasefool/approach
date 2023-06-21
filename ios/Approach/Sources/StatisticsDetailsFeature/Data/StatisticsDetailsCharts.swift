import ComposableArchitecture
import FeatureActionLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI

public struct StatisticsDetailsCharts: Reducer {
	public struct State: Equatable {
		public var chartContent: ChartContent?
		public var isLoadingNextChart: Bool
		public var aggregation: TrackableFilter.Aggregation
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {}
		public enum DelegateAction: Equatable {
			case didChangeAggregation(TrackableFilter.Aggregation)
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum ChartContent: Equatable {
		case countingChart(CountingChart.Data)
		case averagingChart(AveragingChart.Data)
		case percentageChart(PercentageChart.Data)
		case chartUnavailable(statistic: String)

		var title: String {
			switch self {
			case let .averagingChart(data): return data.title
			case let .countingChart(data): return data.title
			case let .percentageChart(data): return data.title
			case let .chartUnavailable(statistic): return statistic
			}
		}

		var showsAggregationPicker: Bool {
			return Statistics.type(of: title)?.supportsAggregation ?? false
		}
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .never:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

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
		WithViewStore(store, observe: { $0 }, content: { viewStore in
			VStack {
				if viewStore.isLoadingNextChart {
					ProgressView()
						.padding(.bottom)
				}

				if let chartContent = viewStore.chartContent {
					switch chartContent {
					case let .countingChart(data):
						CountingChart(data)
					case let .averagingChart(data):
						AveragingChart(data)
					case let .percentageChart(data):
						PercentageChart(data)
					case let .chartUnavailable(statistic):
						emptyChart(statistic)
					}
				}

				Spacer()

				if viewStore.chartContent?.showsAggregationPicker ?? false {
					Picker(
						Strings.Statistics.Filter.aggregation,
						selection: viewStore.binding(get: \.aggregation, send: { .delegate(.didChangeAggregation($0)) })
					) {
						ForEach(TrackableFilter.Aggregation.allCases) {
							Text(String(describing: $0)).tag($0)
						}
					}
					.pickerStyle(.segmented)
					.padding()
				}
			}
		})
	}

	private func emptyChart(_ statistic: String) -> some View {
		VStack(alignment: .leading) {
			Text(statistic)
				.font(.headline)

			Spacer()

			Text(Strings.Statistics.Charts.unavailable)
				.font(.body)
				.fontWeight(.light)
				.frame(maxWidth: .infinity)
				.padding(.top, .standardSpacing)

			Spacer()
		}
	}
}

extension TrackableFilter.Aggregation: CustomStringConvertible {
	public var description: String {
		switch self {
		case .accumulate: return Strings.Statistics.Filter.Aggregation.accumulate
		case .periodic: return Strings.Statistics.Filter.Aggregation.periodic
		}
	}
}
