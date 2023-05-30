import ComposableArchitecture
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct StatisticsDetailsView: View {
	let store: StoreOf<StatisticsDetails>

	struct ViewState: Equatable {
		let isListSheetVisible: Bool
		let staticValues: IdentifiedArrayOf<StaticValueGroup>

		init(state: StatisticsDetails.State) {
			self.isListSheetVisible = state.isListSheetVisible
			self.staticValues = state.staticValues
		}
	}

	enum ViewAction {
		case didAppear
		case didTapStaticValue(id: String)
		case setListSheet(isPresented: Bool)
	}

	public init(store: StoreOf<StatisticsDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsDetails.Action.init) { viewStore in
			Text("Hello")
				.toolbar(.hidden, for: .navigationBar)
				.task { await viewStore.send(.didAppear).finish() }
				.sheet(isPresented: viewStore.binding(
					get: \.isListSheetVisible,
					send: ViewAction.setListSheet(isPresented:))
				) {
					List {
						ForEach(viewStore.staticValues) { group in
							Section(group.category.title) {
								ForEach(group.values) { staticValue in
									Button { viewStore.send(.didTapStaticValue(id: staticValue.id)) } label: {
										HStack {
											LabeledContent(staticValue.title, value: staticValue.value)
											Image(systemName: "chevron.forward")
												.resizable()
												.scaledToFit()
												.frame(width: .tinyIcon, height: .tinyIcon)
												.foregroundColor(Color(uiColor: .secondaryLabel))
										}
										.contentShape(Rectangle())
									}
									.buttonStyle(TappableElement())
								}
							}
						}
					}
					.presentationDetents([.medium])
					.interactiveDismissDisabled()
				}
		}
	}
}

extension StatisticsDetails.Action {
	init(action: StatisticsDetailsView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case let .didTapStaticValue(id):
			self = .view(.didTapStaticValue(id: id))
		case let .setListSheet(isPresented):
			self = .view(.setListSheet(isPresented: isPresented))
		}
	}
}
