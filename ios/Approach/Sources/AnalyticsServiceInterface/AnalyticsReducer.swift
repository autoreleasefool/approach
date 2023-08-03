import ComposableArchitecture

public struct AnalyticsReducer<State, Action>: Reducer {
	let reducer: (State, Action) -> TrackableEvent?

	public init(reducer: @escaping (_ state: State, _ action: Action) -> TrackableEvent?) {
		self.reducer = reducer
	}

	@Dependency(\.analytics) var analytics

	public var body: some Reducer<State, Action> {
		Reduce { state, action in
			guard let event = reducer(state, action) else { return .none }
			return .run { _ in await analytics.trackEvent(event) }
		}
	}
}
