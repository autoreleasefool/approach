import ComposableArchitecture
import FeatureActionLibrary
import StatisticsOverviewFeature
import SwiftUI

@Reducer
public struct StatisticsTabContent: Reducer, Sendable {

	@ObservableState
	public struct State: Equatable {
		public var statistics = StatisticsOverview.State()

		public static var `default`: Self { Self() }
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case statistics(StatisticsOverview.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	init() {}

	public var body: some ReducerOf<Self> {
		Scope(state: \.statistics, action: \.internal.statistics) {
			StatisticsOverview()
		}

		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .doNothing: return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .statistics(.delegate(.doNothing)):
					return .none

				case .statistics(.internal), .statistics(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

public struct StatisticsTabContentView: View {
	public let store: StoreOf<StatisticsTabContent>

	init(store: StoreOf<StatisticsTabContent>) {
		self.store = store
	}

	public var body: some View {
		NavigationStack {
			StatisticsOverviewView(
				store: store.scope(state: \.statistics, action: \.internal.statistics)
			)
		}
	}
}
