import ComposableArchitecture
import FeatureActionLibrary

public typealias Orderable = Hashable & Equatable & CaseIterable & CustomStringConvertible

@Reducer
public struct SortOrder<Ordering: Orderable>: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let options: [Ordering] = Array(Ordering.allCases)

		@Shared public var ordering: Ordering

		public init(initialValue: Shared<Ordering>) {
			self._ordering = initialValue
		}
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View {
			case didTapOption(Ordering)
		}
		@CasePathable
		public enum Internal { case doNothing }
		@CasePathable
		public enum Delegate { case doNothing }

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
					state.$ordering.withLock { $0 = ordering }
					return .run { _ in await dismiss() }
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}
