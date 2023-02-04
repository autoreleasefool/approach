import ComposableArchitecture
import FeatureActionLibrary

public typealias Orderable = Hashable & Equatable & CaseIterable & CustomStringConvertible

public struct SortOrder<Ordering: Orderable>: ReducerProtocol {
	public struct State: Equatable {
		public let options: [Ordering] = Array(Ordering.allCases)
		public var ordering: Ordering
		public var isSheetPresented = false

		public init(initialValue: Ordering? = nil) {
			self.ordering = initialValue ?? Ordering.allCases.first!
		}
	}

	public enum Action: Equatable, FeatureAction {
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

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapOption(ordering):
					state.ordering = ordering
					state.isSheetPresented = false
					return .task { .delegate(.didTapOption(ordering)) }

				case let .setSheetPresented(isPresented):
					state.isSheetPresented = isPresented
					return .none
				}

			case .internal, .delegate:
				return .none
			}
		}
	}
}
