import ComposableArchitecture
import FeatureActionLibrary
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI

public struct StatisticsSettings: Reducer {
	public struct State: Equatable {
		@BindingState public var isHidingZeroStatistics: Bool
		@BindingState public var isCountingSplitWithBonusAsSplit: Bool
		@BindingState public var isCountingH2AsH: Bool

		@BindingState public var isHidingWidgetsInBowlerList: Bool
		@BindingState public var isHidingWidgetsInLeagueList: Bool

		init() {
			@Dependency(\.preferences) var preferences
			self.isHidingZeroStatistics = preferences.bool(forKey: .statisticsHideZeroStatistics) ?? true
			self.isCountingH2AsH = preferences.bool(forKey: .statisticsCountH2AsH) ?? true
			self.isCountingSplitWithBonusAsSplit = preferences.bool(forKey: .statisticsCountSplitWithBonusAsSplit) ?? true
			self.isHidingWidgetsInBowlerList = preferences.bool(forKey: .statisticsWidgetHideInBowlerList) ?? false
			self.isHidingWidgetsInLeagueList = preferences.bool(forKey: .statisticsWidgetHideInLeagueList) ?? false
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .never:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .binding(\.$isHidingZeroStatistics):
				return .run { [updatedValue = state.isHidingZeroStatistics] _ in
					preferences.setKey(.statisticsHideZeroStatistics, toBool: updatedValue)
				}
				.cancellable(id: PreferenceKey.statisticsHideZeroStatistics, cancelInFlight: true)

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

			case .delegate, .binding:
				return .none
			}
		}
	}
}

public struct StatisticsSettingsView: View {
	let store: StoreOf<StatisticsSettings>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, content: { viewStore in
			List {
				Section(Strings.Settings.Statistics.PerFrame.title) {
					Toggle(
						Strings.Settings.Statistics.PerFrame.countH2AsH,
						isOn: viewStore.binding(\.$isCountingH2AsH)
					)

					Toggle(
						Strings.Settings.Statistics.PerFrame.countSplitWithBonusAsSplit,
						isOn: viewStore.binding(\.$isCountingSplitWithBonusAsSplit)
					)
				}

				Section(Strings.Settings.Statistics.Overall.title) {
					Toggle(
						Strings.Settings.Statistics.Overall.hideZeroStatistics,
						isOn: viewStore.binding(\.$isHidingZeroStatistics)
					)
				}

				Section(Strings.Settings.Statistics.Widgets.title) {
					Toggle(
						Strings.Settings.Statistics.Widgets.hideInBowlerList,
						isOn: viewStore.binding(\.$isHidingWidgetsInBowlerList)
					)
					Toggle(
						Strings.Settings.Statistics.Widgets.hideInLeagueList,
						isOn: viewStore.binding(\.$isHidingWidgetsInLeagueList)
					)
				}
			}
			.navigationTitle(Strings.Settings.Statistics.title)
		})
	}
}