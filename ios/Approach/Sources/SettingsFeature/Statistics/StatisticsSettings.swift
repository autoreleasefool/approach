import ComposableArchitecture
import FeatureActionLibrary
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI

public struct StatisticsSettings: Reducer {
	public struct State: Equatable {
		@BindingState public var isHidingZeroStatistics: Bool

		init() {
			@Dependency(\.preferences) var preferences
			self.isHidingZeroStatistics = preferences.bool(forKey: .statisticsHideZeroStatistics) ?? true
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

	enum CancelID { case setHidingZeroStatistics }

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
				.cancellable(id: CancelID.setHidingZeroStatistics, cancelInFlight: true)

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
				Section {
					Toggle(Strings.Settings.Statistics.hideZeroStatistics, isOn: viewStore.binding(\.$isHidingZeroStatistics))
				}
			}
			.navigationTitle(Strings.Settings.Statistics.title)
		})
	}
}
