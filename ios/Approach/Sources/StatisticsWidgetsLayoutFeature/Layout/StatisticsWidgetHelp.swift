import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StringsLibrary
import SwiftUI

public struct StatisticsWidgetHelp: Reducer {
	public struct State: Equatable {
		public let missingStatistic: StatisticsWidget.Configuration

		init(missingStatistic: StatisticsWidget.Configuration?) {
			self.missingStatistic = missingStatistic ?? .init(
				id: UUID(0),
				source: .bowler(UUID(0)),
				timeline: .allTime,
				statistic: .average
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapDoneButton
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
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

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

public struct StatisticsWidgetHelpView: View {
	let store: StoreOf<StatisticsWidgetHelp>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			List {
				Section {
					VStack(alignment: .leading, spacing: .smallSpacing) {
						SquareWidget(
							configuration: viewStore.missingStatistic,
							chartContent: .dataMissing(statistic: viewStore.missingStatistic.statistic.type.title),
							onPress: {}
						)
						.frame(maxWidth: 150)

						Text(Strings.Widget.Help.NotEnoughData.title)
							.font(.headline)

						Text(Strings.Widget.Help.NotEnoughData.description1)
						Text(Strings.Widget.Help.NotEnoughData.description2)
					}
				}
				.listRowSeparator(.hidden)

				Section {
					VStack(alignment: .leading, spacing: .smallSpacing) {
						SquareWidget(
							configuration: viewStore.missingStatistic,
							chartContent: .chartUnavailable(statistic: viewStore.missingStatistic.statistic.type.title),
							onPress: {}
						)
						.frame(maxWidth: 150)

						Text(Strings.Widget.Help.Error.title)
							.font(.headline)

						Text(Strings.Widget.Help.Error.description1)
						Text(Strings.Widget.Help.Error.description2)
					}
				}
				.listRowSeparator(.hidden)
			}
			.navigationTitle(Strings.Widget.Help.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.done) { viewStore.send(.didTapDoneButton) }
				}
			}
		})
	}
}

#if DEBUG
struct StatisticsWidgetHelpPreview: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			StatisticsWidgetHelpView(store: .init(
				initialState: .init(missingStatistic: .init(
					id: UUID(0),
					source: .bowler(UUID(0)),
					timeline: .allTime,
					statistic: .middleHits
				)),
				reducer: StatisticsWidgetHelp.init
			))
		}
	}
}
#endif
