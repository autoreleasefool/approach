import ComposableArchitecture
import FeatureActionLibrary

public typealias Orderable = Hashable & Equatable & CaseIterable & CustomStringConvertible

@Reducer
public struct SortOrder<Ordering: Orderable>: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let options: [Ordering] = Array(Ordering.allCases)
		public var ordering: Ordering

		public init(initialValue: Ordering) {
			self.ordering = initialValue
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum View {
			case didTapOption(Ordering)
		}
		@CasePathable public enum Internal { case doNothing }
		@CasePathable public enum Delegate {
			case didTapOption(Ordering)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapOption(ordering):
					state.ordering = ordering
					return .concatenate(
						.send(.delegate(.didTapOption(ordering))),
						.run { _ in await dismiss() }
					)
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}
