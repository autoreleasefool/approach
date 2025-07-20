import AccessoriesOverviewFeature
import ComposableArchitecture
import FeatureActionLibrary
import SwiftUI

@Reducer
public struct AccessoriesTabContent: Reducer, Sendable {

	@ObservableState
	public struct State: Equatable {
		public var accessories = AccessoriesOverview.State()

		public static var `default`: Self { Self() }
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case accessories(AccessoriesOverview.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	init() {}

	public var body: some ReducerOf<Self> {
		Scope(state: \.accessories, action: \.internal.accessories) {
			AccessoriesOverview()
		}

		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .doNothing: return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .accessories(.delegate(.doNothing)):
					return .none

				case .accessories(.internal), .accessories(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

public struct AccessoriesTabContentView: View {
	public let store: StoreOf<AccessoriesTabContent>

	init(store: StoreOf<AccessoriesTabContent>) {
		self.store = store
	}

	public var body: some View {
		NavigationStack {
			AccessoriesOverviewView(
				store: store.scope(state: \.accessories, action: \.internal.accessories)
			)
		}
	}
}
