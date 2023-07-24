import ComposableArchitecture
import FeatureActionLibrary
import PreferenceServiceInterface
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct StatisticsDetailsList: Reducer {
	public struct State: Equatable {
		@BindingState public var isHidingZeroStatistics: Bool
		public var listEntries: IdentifiedArrayOf<Statistics.ListEntryGroup> = []

		init(listEntries: IdentifiedArrayOf<Statistics.ListEntryGroup>) {
			self.listEntries = listEntries

			@Dependency(\.preferences) var preferences
			self.isHidingZeroStatistics = preferences.bool(forKey: .statisticsHideZeroStatistics) ?? true
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didTapEntry(id: String)
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {
			case didRequestEntryDetails(id: String)
			case listRequiresReload
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	enum CancelID { case setHidingZeroStatistics }

	public init() {}

	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapEntry(id):
					return .send(.delegate(.didRequestEntryDetails(id: id)))

				case .binding(\.$isHidingZeroStatistics):
					return .concatenate(
						.run { [updatedValue = state.isHidingZeroStatistics] _ in
							preferences.setKey(.statisticsHideZeroStatistics, toBool: updatedValue)
						},
						.send(.delegate(.listRequiresReload))
					)
					.cancellable(id: CancelID.setHidingZeroStatistics, cancelInFlight: true)

				case .binding:
					return .none
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

// MARK: - View

public struct StatisticsDetailsListView: View {
	let store: StoreOf<StatisticsDetailsList>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			ForEach(viewStore.listEntries) { group in
				Section(String(describing: group.category)) {
					ForEach(group.entries) { entry in
						Button { viewStore.send(.didTapEntry(id: entry.id)) } label: {
							LabeledContent(entry.title, value: entry.value)
						}
						.buttonStyle(.navigation)
					}
				}
			}

			Section {
				Toggle(
					Strings.Statistics.List.hideZeroStatistics,
					isOn: viewStore.$isHidingZeroStatistics
				)
			} footer: {
				if viewStore.isHidingZeroStatistics {
					Text(Strings.Statistics.List.HideZeroStatistics.help)
				}
			}
		})
	}
}
