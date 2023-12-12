import AnalyticsServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI

@Reducer
public struct StatisticsSettings: Reducer {
	public struct State: Equatable {
		@BindingState public var isHidingZeroStatistics: Bool
		@BindingState public var isHidingStatisticsDescriptions: Bool
		@BindingState public var isCountingSplitWithBonusAsSplit: Bool
		@BindingState public var isCountingH2AsH: Bool

		@BindingState public var isHidingWidgetsInBowlerList: Bool
		@BindingState public var isHidingWidgetsInLeagueList: Bool

		public let isStatisticsDescriptionsEnabled: Bool

		init() {
			@Dependency(\.preferences) var preferences
			self.isHidingZeroStatistics = preferences.bool(forKey: .statisticsHideZeroStatistics) ?? true
			self.isHidingStatisticsDescriptions = preferences.bool(forKey: .statisticsHideStatisticsDescriptions) ?? false
			self.isCountingH2AsH = preferences.bool(forKey: .statisticsCountH2AsH) ?? true
			self.isCountingSplitWithBonusAsSplit = preferences.bool(forKey: .statisticsCountSplitWithBonusAsSplit) ?? true
			self.isHidingWidgetsInBowlerList = preferences.bool(forKey: .statisticsWidgetHideInBowlerList) ?? false
			self.isHidingWidgetsInLeagueList = preferences.bool(forKey: .statisticsWidgetHideInLeagueList) ?? false

			@Dependency(\.featureFlags) var featureFlags
			self.isStatisticsDescriptionsEnabled = featureFlags.isEnabled(.statisticsDescriptions)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case onAppear
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable { case doNothing }
		public enum InternalAction: Equatable { case doNothing }

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .binding(\.$isHidingZeroStatistics):
					return .run { [updatedValue = state.isHidingZeroStatistics] _ in
						preferences.setKey(.statisticsHideZeroStatistics, toBool: updatedValue)
					}
					.cancellable(id: PreferenceKey.statisticsHideZeroStatistics, cancelInFlight: true)

				case .binding(\.$isHidingStatisticsDescriptions):
					return .run { [updatedValue = state.isHidingStatisticsDescriptions] _ in
						preferences.setKey(.statisticsHideStatisticsDescriptions, toBool: updatedValue)
					}
					.cancellable(id: PreferenceKey.statisticsHideStatisticsDescriptions, cancelInFlight: true)

				case .binding(\.$isCountingH2AsH):
					return .run { [updatedValue = state.isCountingH2AsH] _ in
						preferences.setKey(.statisticsCountH2AsH, toBool: updatedValue)
					}
					.cancellable(id: PreferenceKey.statisticsCountH2AsH, cancelInFlight: true)

				case .binding(\.$isCountingSplitWithBonusAsSplit):
					return .run { [updatedValue = state.isCountingSplitWithBonusAsSplit] _ in
						preferences.setKey(.statisticsCountSplitWithBonusAsSplit, toBool: updatedValue)
					}
					.cancellable(id: PreferenceKey.statisticsCountSplitWithBonusAsSplit, cancelInFlight: true)

				case .binding(\.$isHidingWidgetsInBowlerList):
					return .run { [updatedValue = state.isHidingWidgetsInBowlerList] _ in
						preferences.setKey(.statisticsWidgetHideInBowlerList, toBool: updatedValue)
					}
					.cancellable(id: PreferenceKey.statisticsWidgetHideInBowlerList, cancelInFlight: true)

				case .binding(\.$isHidingWidgetsInLeagueList):
					return .run { [updatedValue = state.isHidingWidgetsInLeagueList] _ in
						preferences.setKey(.statisticsWidgetHideInLeagueList, toBool: updatedValue)
					}
					.cancellable(id: PreferenceKey.statisticsWidgetHideInLeagueList, cancelInFlight: true)

				case .binding:
					return .none
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}

public struct StatisticsSettingsView: View {
	let store: StoreOf<StatisticsSettings>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			List {
				Section(Strings.Settings.Statistics.PerFrame.title) {
					Toggle(
						Strings.Settings.Statistics.PerFrame.countH2AsH,
						isOn: viewStore.$isCountingH2AsH
					)

					Toggle(
						Strings.Settings.Statistics.PerFrame.countSplitWithBonusAsSplit,
						isOn: viewStore.$isCountingSplitWithBonusAsSplit
					)
				}

				Section(Strings.Settings.Statistics.Overall.title) {
					Toggle(
						Strings.Settings.Statistics.Overall.hideZeroStatistics,
						isOn: viewStore.$isHidingZeroStatistics
					)

					if viewStore.isStatisticsDescriptionsEnabled {
						Toggle(
							Strings.Settings.Statistics.Overall.hideStatisticsDescriptions,
							isOn: viewStore.$isHidingStatisticsDescriptions
						)
					}
				}

				Section(Strings.Settings.Statistics.Widgets.title) {
					Toggle(
						Strings.Settings.Statistics.Widgets.hideInBowlerList,
						isOn: viewStore.$isHidingWidgetsInBowlerList
					)
					Toggle(
						Strings.Settings.Statistics.Widgets.hideInLeagueList,
						isOn: viewStore.$isHidingWidgetsInLeagueList
					)
				}
			}
			.navigationTitle(Strings.Settings.Statistics.title)
			.onAppear { viewStore.send(.onAppear) }
		})
	}
}
