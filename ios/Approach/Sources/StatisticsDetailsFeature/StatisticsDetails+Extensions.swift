import ComposableArchitecture
import StatisticsLibrary
import StatisticsRepositoryInterface

extension StatisticsDetails {
	func refreshStatistics(state: State) -> Effect<Action> {
		.merge(
			.run { [filter = state.filter] send in
				await send(.internal(.didLoadListEntries(Result {
					try await statistics.load(for: filter)
				})))
			},
			.run { [source = state.filter.source] send in
				await send(.internal(.didLoadSources(Result {
					try await statistics.loadSources(source)
				})))
			}
		)
		.cancellable(id: CancelID.loadingStaticValues, cancelInFlight: true)
	}

	func loadChart(forStatistic: Statistic.Type, withFilter: TrackableFilter) -> Effect<Action> {
		.concatenate(
			.run { send in await send(.internal(.didStartLoadingChart), animation: .easeInOut) },
			.run { send in
				let startTime = date()

				let result = await Result { try await self.statistics.chart(statistic: forStatistic, filter: withFilter) }

				let timeSpent = date().timeIntervalSince(startTime)
				if timeSpent < Self.chartLoadingAnimationTime {
					try await clock.sleep(for: .milliseconds((Self.chartLoadingAnimationTime - timeSpent) * 1000))
				}

				await send(.internal(.didLoadChartContent(result)), animation: .easeInOut)
			}
		)
		.cancellable(id: CancelID.loadingChartValues, cancelInFlight: true)
	}

	func presentDestinationForLatestOrientation(
		withState state: inout State,
		scrollingTo entryId: Statistics.ListEntry.ID? = nil
	) -> Effect<StatisticsDetails.Action> {
		var list: StatisticsDetailsList.State?
		switch state.latestOrientation {
		case .portrait, .portraitUpsideDown, .faceUp, .faceDown, .unknown, .none:
			list = .init(listEntries: state.listEntries, hasTappableElements: true)
		case .landscapeLeft, .landscapeRight:
			list = nil
		@unknown default:
			list = .init(listEntries: state.listEntries, hasTappableElements: true)
		}

		switch state.destination {
		case let .list(existingState):
			list?.entryToHighlight = existingState.entryToHighlight
		case .sourcePicker, .sharing, .none:
			break
		}

		if let list {
			state.destination = .list(list)
		} else {
			state.destination = nil
		}

		guard let entryId else { return .none }
		return .run { send in
			try await clock.sleep(for: .milliseconds(25))
			await send(.internal(.scrollListToEntry(entryId)))
		}
	}
}
