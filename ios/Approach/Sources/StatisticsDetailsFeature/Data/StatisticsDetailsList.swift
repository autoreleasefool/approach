import ComposableArchitecture
import FeatureActionLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct StatisticsDetailsList: Reducer {
	public struct State: Equatable {
		public var listEntries: IdentifiedArrayOf<Statistics.ListEntryGroup> = []

		init(listEntries: IdentifiedArrayOf<Statistics.ListEntryGroup>) {
			self.listEntries = listEntries
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapEntry(id: String)
		}
		public enum DelegateAction: Equatable {
			case didRequestEntryDetails(id: String)
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapEntry(id):
					return .send(.delegate(.didRequestEntryDetails(id: id)))
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

	enum ViewAction {
		case didTapEntry(id: String)
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: StatisticsDetailsList.Action.init, content: { viewStore in
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
		})
	}
}

extension StatisticsDetailsList.Action {
	init(action: StatisticsDetailsListView.ViewAction) {
		switch action {
		case let .didTapEntry(id):
			self = .view(.didTapEntry(id: id))
		}
	}
}
