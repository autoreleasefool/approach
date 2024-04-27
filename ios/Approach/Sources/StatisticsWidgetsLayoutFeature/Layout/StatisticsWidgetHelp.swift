import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct StatisticsWidgetHelp: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let missingStatistic: StatisticsWidget.Configuration

		init(missingStatistic: StatisticsWidget.Configuration?) {
			self.missingStatistic = missingStatistic ?? .init(
				id: UUID(0),
				bowlerId: UUID(0),
				leagueId: nil,
				timeline: .allTime,
				statistic: "Average"
			)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didTapDoneButton
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		Reduce { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapDoneButton:
					return .run { _ in await dismiss() }
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}

@ViewAction(for: StatisticsWidgetHelp.self)
public struct StatisticsWidgetHelpView: View {
	public let store: StoreOf<StatisticsWidgetHelp>

	public var body: some View {
		List {
			Section {
				RectangleWidget(
					configuration: store.missingStatistic,
					chartContent: .dataMissing(statistic: store.missingStatistic.statistic),
					onPress: {}
				)
				.listRowInsets(EdgeInsets())

				Text(Strings.Widget.Help.NotEnoughData.title)
					.font(.headline)

				Text(Strings.Widget.Help.NotEnoughData.description1)
				Text(Strings.Widget.Help.NotEnoughData.description2)
			}
			.listRowSeparator(.hidden)

			Section {
				RectangleWidget(
					configuration: store.missingStatistic,
					chartContent: .chartUnavailable(statistic: store.missingStatistic.statistic),
					onPress: {}
				)
				.listRowInsets(EdgeInsets())

				Text(Strings.Widget.Help.Error.title)
					.font(.headline)

				Text(Strings.Widget.Help.Error.description1)
				Text(Strings.Widget.Help.Error.description2)
			}
			.listRowSeparator(.hidden)
		}
		.navigationTitle(Strings.Widget.Help.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				Button(Strings.Action.done) { send(.didTapDoneButton) }
			}
		}
	}
}

#if DEBUG
struct StatisticsWidgetHelpPreview: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			StatisticsWidgetHelpView(store: .init(
				initialState: .init(missingStatistic: .init(
					id: UUID(0),
					bowlerId: UUID(0),
					leagueId: nil,
					timeline: .allTime,
					statistic: "Average"
				)),
				reducer: StatisticsWidgetHelp.init
			))
		}
	}
}
#endif
