import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import SwiftUI

@Reducer
public struct BowlersTabContent: Reducer, Sendable {

	@ObservableState
	public struct State: Equatable {
		public var bowlersList = BowlersList.State()

		public static var `default`: Self { Self() }
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case bowlersList(BowlersList.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	init() {}

	public var body: some ReducerOf<Self> {
		Scope(state: \.bowlersList, action: \.internal.bowlersList) {
			BowlersList()
		}

		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .doNothing: return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .bowlersList(.delegate(.doNothing)):
					return .none

				case .bowlersList(.internal), .bowlersList(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

public struct BowlersTabContentView: View {
	public let store: StoreOf<BowlersTabContent>

	init(store: StoreOf<BowlersTabContent>) {
		self.store = store
	}

	public var body: some View {
		NavigationStack {
			BowlersListView(
				store: store.scope(state: \.bowlersList, action: \.internal.bowlersList)
			)
		}
	}
}
