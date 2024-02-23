import ComposableArchitecture

extension MidGameStatisticsDetails {
	func refreshStatistics(state: State) -> Effect<Action> {
		.run { [filter = state.filter] send in
			await send(.internal(.didLoadListEntries(Result {
				try await statistics.load(for: filter)
			})))
		}
		.cancellable(id: CancelID.loadingStaticValues, cancelInFlight: true)
	}
}
