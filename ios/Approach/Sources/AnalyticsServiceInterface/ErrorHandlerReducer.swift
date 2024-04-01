import ComposableArchitecture

@Reducer
public struct ErrorHandlerReducer<State, Action>: Reducer {
	let reducer: (State, Action) -> Error?

	public init(reducer: @escaping (_ state: State, _ action: Action) -> Error?) {
		self.reducer = reducer
	}

	@Dependency(AnalyticsService.self) var analytics

	public var body: some Reducer<State, Action> {
		Reduce { state, action in
			guard let error = reducer(state, action) else { return .none }
			return .run { _ in analytics.captureException(error) }
		}
	}
}
