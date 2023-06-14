import ComposableArchitecture
import FeatureActionLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI

public struct StatisticsDetailsCharts: Reducer {
	public struct State: Equatable {
		public var aggregation: TrackableFilter.Aggregation

		init(aggregation: TrackableFilter.Aggregation) {
			self.aggregation = aggregation
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didChangeAggregation(TrackableFilter.Aggregation)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didChangeAggregation(aggregation):
					state.aggregation = aggregation
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

	struct ViewState: Equatable {
		let aggregation: TrackableFilter.Aggregation

		init(state: StatisticsDetailsCharts.State) {
			self.aggregation = state.aggregation
		}
	}

	enum ViewAction {
		case didChangeAggregation(TrackableFilter.Aggregation)
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsDetailsCharts.Action.init) { viewStore in
			VStack {
				Spacer()

				Picker(
					Strings.Statistics.Filter.aggregation,
					selection: viewStore.binding(get: \.aggregation, send: ViewAction.didChangeAggregation)
				) {
					ForEach(TrackableFilter.Aggregation.allCases) {
						Text(String(describing: $0)).tag($0)
					}
				}
				.pickerStyle(.segmented)
				.padding()
			}
		}
	}
}

extension StatisticsDetailsCharts.Action {
	init(action: StatisticsDetailsChartsView.ViewAction) {
		switch action {
		case let .didChangeAggregation(aggregation):
			self = .view(.didChangeAggregation(aggregation))
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
