import ComposableArchitecture
import FeatureActionLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI

public struct StatisticsDetailsCharts: Reducer {
	public struct State: Equatable {
		public var chartData: StatisticsChart.Data?
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
				if let title = viewStore.chartData?.title {
					HStack {
						Text(title)
							.font(.title3)
							.frame(maxWidth: .infinity, alignment: .leading)

						if viewStore.isLoadingNextChart {
							ProgressView()
						}
					}
				} else if viewStore.isLoadingNextChart {
					ProgressView()
				}

				if let chartData = viewStore.chartData {
					StatisticsChart(chartData)
				}

				Spacer()

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
		})
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
