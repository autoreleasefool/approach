import BowlerEditorFeature
import ComposableArchitecture
import FeatureActionLibrary
import LeaguesListFeature
import LeaguesRepositoryInterface
import ModelsLibrary
import SwiftUI

@Reducer
public struct BowlerDetails: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let bowler: Bowler.Summary
		public var header: BowlerDetailsHeader.State
		public var leagues: LeaguesSection.State

		@Shared public var recurrence: League.Recurrence?
		@Shared public var ordering: League.Ordering

		public init(bowler: Bowler.Summary) {
			self.bowler = bowler
			self.header = BowlerDetailsHeader.State(bowler: bowler)
			self._recurrence = Shared(value: .none)
			self._ordering = Shared(value: .default)
			self.leagues = LeaguesSection.State(bowlerId: bowler.id, ordering: _ordering, recurrence: _recurrence)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case header(BowlerDetailsHeader.Action)
			case leagues(LeaguesSection.Action)
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

		Scope(state: \.leagues, action: \.internal.leagues) {
			LeaguesSection()
		}

		Reduce<State, Action> { _, action in
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

				case .header(.view), .header(.internal),
						.leagues(.view), .leagues(.internal), .leagues(.delegate(.doNothing)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

@ViewAction(for: BowlerDetails.self)
public struct BowlerDetailsView: View {
	public var store: StoreOf<BowlerDetails>

	public init(store: StoreOf<BowlerDetails>) {
		self.store = store
	}

	public var body: some View {
		List {
			BowlerDetailsHeaderView(store: store.scope(state: \.header, action: \.internal.header))

			LeaguesSectionView(store: store.scope(state: \.leagues, action: \.internal.leagues))
		}
		.navigationTitle(store.bowler.name)
	}
}
