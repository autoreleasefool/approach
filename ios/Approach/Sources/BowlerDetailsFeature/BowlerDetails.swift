import BowlerEditorFeature
import ComposableArchitecture
import FeatureActionLibrary
import LeaguesListFeature
import ModelsLibrary
import SwiftUI

@Reducer
public struct BowlerDetails: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let bowler: Bowler.Summary
		public var header: BowlerDetailsHeader.State

		public init(bowler: Bowler.Summary) {
			self.bowler = bowler
			self.header = BowlerDetailsHeader.State(bowler: bowler)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View { case doNothing }
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case header(BowlerDetailsHeader.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Scope(state: \.header, action:
				\.internal.header) {
			BowlerDetailsHeader()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .doNothing:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .header(.delegate(delegateAction)):
					switch delegateAction {
					case .doNothing:
						return .none
					}

				case .header(.view), .header(.internal):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

@ViewAction(for: BowlerDetails.self)
public struct BowlerDetailsView: View {
	public var store: StoreOf<BowlerDetails>

	public init(store: StoreOf<BowlerDetails>) {
		self.store = store
	}

	public var body: some View {
		VStack {
			BowlerDetailsHeaderView(store: store.scope(state: \.header, action: \.internal.header))
		}
	}
}
