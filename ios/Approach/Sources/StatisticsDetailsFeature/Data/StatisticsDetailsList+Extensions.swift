import ComposableArchitecture
import StatisticsLibrary
import StatisticsRepositoryInterface

extension StatisticsDetailsList.State {
	func scrollTo(id: Statistics.ListEntry.ID?) -> Effect<StatisticsDetailsList.Action> {
		.send(.internal(.scrollToEntry(id: id)), animation: .easeInOut)
	}
}
