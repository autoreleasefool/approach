import ComposableArchitecture
import FeatureActionLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI

public struct StatisticsDetailsCharts: Reducer {
	public struct State: Equatable {
		public var timeline: TrackableFilter.Timeline

		init(timeline: TrackableFilter.Timeline) {
			self.timeline = timeline
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didChangeTimeline(TrackableFilter.Timeline)
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
				case let .didChangeTimeline(timeline):
					state.timeline = timeline
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
		let timeline: TrackableFilter.Timeline

		init(state: StatisticsDetailsCharts.State) {
			self.timeline = state.timeline
		}
	}

	enum ViewAction {
		case didChangeTimeline(TrackableFilter.Timeline)
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsDetailsCharts.Action.init) { viewStore in
			Picker(
				Strings.Statistics.Filter.timeline,
				selection: viewStore.binding(get: \.timeline, send: ViewAction.didChangeTimeline)
			) {
				ForEach(TrackableFilter.Timeline.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
			.pickerStyle(.segmented)
		}
	}
}

extension StatisticsDetailsCharts.Action {
	init(action: StatisticsDetailsChartsView.ViewAction) {
		switch action {
		case let .didChangeTimeline(timeline):
			self = .view(.didChangeTimeline(timeline))
		}
	}
}

extension TrackableFilter.Timeline: CustomStringConvertible {
	public var description: String {
		switch self {
		case .perSeries: return Strings.Statistics.Filter.Timeline.perSeries
		case .allTime: return Strings.Statistics.Filter.Timeline.allTime
		}
	}
}
