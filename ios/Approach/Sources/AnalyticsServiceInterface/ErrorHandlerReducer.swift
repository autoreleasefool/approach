import ComposableArchitecture
import ErrorReportingClientPackageLibrary

@Reducer
public struct ErrorHandlerReducer<State, Action>: Reducer, Sendable {
	let reducer: (State, Action) -> Error?

	public init(reducer: @escaping (_ state: State, _ action: Action) -> Error?) {
		self.reducer = reducer
	}

	@Dependency(\.errors) var errors

	public var body: some Reducer<State, Action> {
		Reduce { state, action in
			guard let error = reducer(state, action) else { return .none }
			return .run { _ in errors.captureError(error) }
		}
	}
}
