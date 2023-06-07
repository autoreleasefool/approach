import ComposableArchitecture
import FeatureActionLibrary

public typealias Orderable = Hashable & Equatable & CaseIterable & CustomStringConvertible

public struct SortOrder<Ordering: Orderable>: Reducer {
	public struct State: Equatable {
		public let options: [Ordering] = Array(Ordering.allCases)
		public var ordering: Ordering

		public init(initialValue: Ordering) {
			self.ordering = initialValue
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapOption(Ordering)
		}
		public enum InternalAction: Equatable {}
		public enum DelegateAction: Equatable {
			case didTapOption(Ordering)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
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
