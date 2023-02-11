import ComposableArchitecture
import FeatureActionLibrary
import LeaguesDataProviderInterface
import SharedModelsLibrary

public struct LeaguesFilter: ReducerProtocol {
	public struct State: Equatable {
		@BindableState public var recurrence: League.Recurrence?

		public init() {}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapClearButton
			case didTapApplyButton
		}
		public enum DelegateAction: Equatable {
			case didChangeFilters
			case didApplyFilters
		}
		public enum InternalAction: Equatable {}

		case binding(BindingAction<State>)
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapClearButton:
					state = .init()
					return .task { .delegate(.didApplyFilters) }

				case .didTapApplyButton:
					return .task { .delegate(.didApplyFilters) }
				}

			case .binding:
				return .task { .delegate(.didChangeFilters) }

			case .internal, .delegate:
				return .none
			}
		}
	}
}

extension LeaguesFilter.State {
	public var hasFilters: Bool {
		recurrence != nil
	}

	public func filter(withBowler: Bowler.ID) -> League.FetchRequest.Filter {
		.properties(withBowler, recurrence: recurrence)
	}
}
