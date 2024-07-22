import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct StatisticPicker: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		let groups: [StatisticsGroup]
		let selectedStatistic: String

		init(selected: String) {
			self.selectedStatistic = selected
			self.groups = StatisticCategory.allCases.compactMap { category in
				let statistics = Statistics
					.supportingWidgets()
					.filter { $0.category == category }
					.map { $0.title }

				guard !statistics.isEmpty else { return nil }

				return StatisticsGroup(category: category, statistics: statistics)
			}
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didTapStatistic(String)
		}
		@CasePathable public enum Delegate {
			case didSelectStatistic(String)
		}
		@CasePathable public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public struct StatisticsGroup: Identifiable, Equatable {
		public let category: StatisticCategory
		public let statistics: [String]

		public var id: StatisticCategory { category }
	}

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapStatistic(statistic):
					return .concatenate(
						.send(.delegate(.didSelectStatistic(statistic))),
						.run { _ in await dismiss() }
					)
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}

@ViewAction(for: StatisticPicker.self)
public struct StatisticPickerView: View {
	public let store: StoreOf<StatisticPicker>

	init(store: StoreOf<StatisticPicker>) {
		self.store = store
	}

	public var body: some View {
		List {
			ForEach(store.groups) { group in
				Section(String(describing: group.category)) {
					ForEach(group.statistics, id: \.self) { statistic in
						Button { send(.didTapStatistic(statistic)) } label: {
							HStack(alignment: .center, spacing: .standardSpacing) {
								Image(systemSymbol: store.selectedStatistic == statistic ? .checkmarkCircleFill : .circle)
									.resizable()
									.frame(width: .smallIcon, height: .smallIcon)
									.foregroundColor(Asset.Colors.Action.default)

								Text(statistic)
									.frame(maxWidth: .infinity, alignment: .leading)
							}
							.frame(maxWidth: .infinity)
							.contentShape(Rectangle())
						}
						.buttonStyle(TappableElement())
					}
				}
			}
		}
		.navigationTitle(Strings.Statistics.Picker.title)
	}
}
