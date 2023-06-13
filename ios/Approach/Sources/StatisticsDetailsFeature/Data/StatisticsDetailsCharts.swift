import ComposableArchitecture
import FeatureActionLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI

public struct StatisticsDetailsCharts: Reducer {
	public struct State: Equatable {
		public var timePrecision: TrackableFilter.TimePrecision

		init(timePrecision: TrackableFilter.TimePrecision) {
			self.timePrecision = timePrecision
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didChangeTimePrecision(TrackableFilter.TimePrecision)
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
				case let .didChangeTimePrecision(timePrecision):
					state.timePrecision = timePrecision
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
		let timePrecision: TrackableFilter.TimePrecision

		init(state: StatisticsDetailsCharts.State) {
			self.timePrecision = state.timePrecision
		}
	}

	enum ViewAction {
		case didChangeTimePrecision(TrackableFilter.TimePrecision)
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsDetailsCharts.Action.init) { viewStore in
			VStack {
				Spacer()

				Picker(
					Strings.Statistics.Filter.timePrecision,
					selection: viewStore.binding(get: \.timePrecision, send: ViewAction.didChangeTimePrecision)
				) {
					ForEach(TrackableFilter.TimePrecision.allCases) {
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
		case let .didChangeTimePrecision(timePrecision):
			self = .view(.didChangeTimePrecision(timePrecision))
		}
	}
}

extension TrackableFilter.TimePrecision: CustomStringConvertible {
	public var description: String {
		switch self {
		case .aggregate: return Strings.Statistics.Filter.TimePrecision.aggregate
		case .all: return Strings.Statistics.Filter.TimePrecision.all
		}
	}
}
