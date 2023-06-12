import ComposableArchitecture
import FeatureActionLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct StatisticsDetailsList: Reducer {
	public struct State: Equatable {
		public var staticValues: IdentifiedArrayOf<StaticValueGroup> = []

		init(staticValues: IdentifiedArrayOf<StaticValueGroup>) {
			self.staticValues = staticValues
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapStaticValue(id: String)
		}
		public enum DelegateAction: Equatable {
			case didRequestStaticValue(id: String)
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapStaticValue(id):
					return .send(.delegate(.didRequestStaticValue(id: id)))
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

	struct ViewState: Equatable {
		let staticValues: IdentifiedArrayOf<StaticValueGroup>

		init(state: StatisticsDetailsList.State) {
			self.staticValues = state.staticValues
		}
	}

	enum ViewAction {
		case didTapStaticValue(id: String)
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsDetailsList.Action.init) { viewStore in
			ForEach(viewStore.staticValues) { group in
				Section(group.category.title) {
					ForEach(group.values) { staticValue in
						Button { viewStore.send(.didTapStaticValue(id: staticValue.id)) } label: {
							LabeledContent(staticValue.title, value: staticValue.value)
						}
						.buttonStyle(.navigation)
					}
				}
			}
		}
	}
}

extension StatisticsDetailsList.Action {
	init(action: StatisticsDetailsListView.ViewAction) {
		switch action {
		case let .didTapStaticValue(id):
			self = .view(.didTapStaticValue(id: id))
		}
	}
}
