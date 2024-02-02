import AnalyticsServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI

@Reducer
public struct StatisticsSettings: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var isHidingZeroStatistics: Bool
		public var isHidingStatisticsDescriptions: Bool
		public var isCountingSplitWithBonusAsSplit: Bool
		public var isCountingH2AsH: Bool

		public var isHidingWidgetsInBowlerList: Bool
		public var isHidingWidgetsInLeagueList: Bool

		init() {
			@Dependency(\.preferences) var preferences
			self.isHidingZeroStatistics = preferences.bool(forKey: .statisticsHideZeroStatistics) ?? true
			self.isHidingStatisticsDescriptions = preferences.bool(forKey: .statisticsHideStatisticsDescriptions) ?? false
			self.isCountingH2AsH = preferences.bool(forKey: .statisticsCountH2AsH) ?? true
			self.isCountingSplitWithBonusAsSplit = preferences.bool(forKey: .statisticsCountSplitWithBonusAsSplit) ?? true
			self.isHidingWidgetsInBowlerList = preferences.bool(forKey: .statisticsWidgetHideInBowlerList) ?? false
			self.isHidingWidgetsInLeagueList = preferences.bool(forKey: .statisticsWidgetHideInLeagueList) ?? false
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none
				}

			case .internal(.doNothing):
				return .none

			case .binding(\.isHidingZeroStatistics):
				return .run { [updatedValue = state.isHidingZeroStatistics] _ in
					preferences.setKey(.statisticsHideZeroStatistics, toBool: updatedValue)
				}
				.cancellable(id: PreferenceKey.statisticsHideZeroStatistics, cancelInFlight: true)

			case .binding(\.isHidingStatisticsDescriptions):
				return .run { [updatedValue = state.isHidingStatisticsDescriptions] _ in
					preferences.setKey(.statisticsHideStatisticsDescriptions, toBool: updatedValue)
				}
				.cancellable(id: PreferenceKey.statisticsHideStatisticsDescriptions, cancelInFlight: true)

			case .binding(\.isCountingH2AsH):
				return .run { [updatedValue = state.isCountingH2AsH] _ in
					preferences.setKey(.statisticsCountH2AsH, toBool: updatedValue)
				}
				.cancellable(id: PreferenceKey.statisticsCountH2AsH, cancelInFlight: true)

			case .binding(\.isCountingSplitWithBonusAsSplit):
				return .run { [updatedValue = state.isCountingSplitWithBonusAsSplit] _ in
					preferences.setKey(.statisticsCountSplitWithBonusAsSplit, toBool: updatedValue)
				}
				.cancellable(id: PreferenceKey.statisticsCountSplitWithBonusAsSplit, cancelInFlight: true)

			case .binding(\.isHidingWidgetsInBowlerList):
				return .run { [updatedValue = state.isHidingWidgetsInBowlerList] _ in
					preferences.setKey(.statisticsWidgetHideInBowlerList, toBool: updatedValue)
				}
				.cancellable(id: PreferenceKey.statisticsWidgetHideInBowlerList, cancelInFlight: true)

			case .binding(\.isHidingWidgetsInLeagueList):
				return .run { [updatedValue = state.isHidingWidgetsInLeagueList] _ in
					preferences.setKey(.statisticsWidgetHideInLeagueList, toBool: updatedValue)
				}
				.cancellable(id: PreferenceKey.statisticsWidgetHideInLeagueList, cancelInFlight: true)

			case .delegate, .binding:
				return .none
			}
		}
	}
}

@ViewAction(for: StatisticsSettings.self)
public struct StatisticsSettingsView: View {
	@Perception.Bindable public var store: StoreOf<StatisticsSettings>

	public var body: some View {
		WithPerceptionTracking {
			List {
				Section(Strings.Settings.Statistics.PerFrame.title) {
					Toggle(
						Strings.Settings.Statistics.PerFrame.countH2AsH,
						isOn: $store.isCountingH2AsH
					)

					Toggle(
						Strings.Settings.Statistics.PerFrame.countSplitWithBonusAsSplit,
						isOn: $store.isCountingSplitWithBonusAsSplit
					)
				}

				Section(Strings.Settings.Statistics.Overall.title) {
					Toggle(
						Strings.Settings.Statistics.Overall.hideZeroStatistics,
						isOn: $store.isHidingZeroStatistics
					)

					Toggle(
						Strings.Settings.Statistics.Overall.hideStatisticsDescriptions,
						isOn: $store.isHidingStatisticsDescriptions
					)
				}

				Section(Strings.Settings.Statistics.Widgets.title) {
					Toggle(
						Strings.Settings.Statistics.Widgets.hideInBowlerList,
						isOn: $store.isHidingWidgetsInBowlerList
					)
					Toggle(
						Strings.Settings.Statistics.Widgets.hideInLeagueList,
						isOn: $store.isHidingWidgetsInLeagueList
					)
				}
			}
			.navigationTitle(Strings.Settings.Statistics.title)
			.onAppear { send(.onAppear) }
		}
	}
}
