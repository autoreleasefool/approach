import ComposableArchitecture

@Reducer
public struct AnalyticsReducer<State, Action>: Reducer, Sendable {
	let reducer: (State, Action) -> TrackableEvent?

	public init(reducer: @escaping (_ state: State, _ action: Action) -> TrackableEvent?) {
		self.reducer = reducer
	}

	@Dependency(\.analytics) var analytics

	public var body: some Reducer<State, Action> {
		Reduce { state, action in
			guard let event = reducer(state, action) else { return .none }
			return .run { _ in try? await analytics.trackEvent(event) }
		}
	}
}

@Reducer
public struct GameAnalyticsReducer<State, Action>: Reducer, Sendable {
	let reducer: (State, Action) -> GameSessionTrackableEvent?

	public init(reducer: @escaping (_ state: State, _ action: Action) -> GameSessionTrackableEvent?) {
		self.reducer = reducer
	}

	@Dependency(\.gameAnalytics) var gameAnalytics

	public var body: some Reducer<State, Action> {
		Reduce { state, action in
			guard let event = reducer(state, action) else { return .none }
			return .run { _ in await gameAnalytics.trackEvent(event) }
		}
	}
}
