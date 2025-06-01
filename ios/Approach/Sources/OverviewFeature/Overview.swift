import AnnouncementsFeature
import ComposableArchitecture
import FeatureActionLibrary
import QuickLaunchRepositoryInterface

@Reducer
public struct Overview: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public init() {}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didFirstAppear
			case didStartTask
		}

		@CasePathable
		public enum Delegate { case doNothing }

		@CasePathable
		public enum Internal { case doNothing }

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return .none

				case .didStartTask:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .doNothing:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
