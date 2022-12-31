import ComposableArchitecture

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

	public enum Action: Equatable {
		case setSheetPresented(isPresented: Bool)
		case optionTapped(Ordering)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case let .setSheetPresented(isPresented):
				state.isSheetPresented = isPresented
				return .none

			case let .optionTapped(option):
				state.ordering = option
				return .task { .setSheetPresented(isPresented: false) }
			}
		}
	}
}
