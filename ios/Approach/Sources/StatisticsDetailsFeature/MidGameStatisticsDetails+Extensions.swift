import ComposableArchitecture

extension MidGameStatisticsDetails {
	func refreshStatistics(state: State) -> Effect<Action> {
		.run { [filter = state.filter] send in
			await send(.internal(.didLoadListEntries(TaskResult {
				try await statistics.load(for: filter)
			})))
		}
		.cancellable(id: CancelID.loadingStaticValues, cancelInFlight: true)
	}
}

extension MidGameStatisticsDetails.State {
	var list: StatisticsDetailsList.State {
		get {
			var list = _list
			list.listEntries = listEntries
			return list
		}
		set {
			_list = newValue
		}
	}
}
