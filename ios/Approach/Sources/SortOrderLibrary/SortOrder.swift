import ComposableArchitecture
import FeatureActionLibrary

public typealias Orderable = Hashable & Equatable & CaseIterable & CustomStringConvertible

public struct SortOrder<Ordering: Orderable>: Reducer {
	public struct State: Equatable {
		public let options: [Ordering] = Array(Ordering.allCases)
		public var ordering: Ordering
		public var isSheetPresented = false

		public init(initialValue: Ordering? = nil) {
			self.ordering = initialValue ?? Ordering.allCases.first!
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapOption(Ordering)
			case setSheetPresented(isPresented: Bool)
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

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapOption(ordering):
					state.ordering = ordering
					state.isSheetPresented = false
					return .send(.delegate(.didTapOption(ordering)))

				case let .setSheetPresented(isPresented):
					state.isSheetPresented = isPresented
					return .none
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