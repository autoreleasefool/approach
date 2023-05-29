import ComposableArchitecture
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct StatisticsDetailsView: View {
	let store: StoreOf<StatisticsDetails>

	struct ViewState: Equatable {
		let isListSheetVisible: Bool
		let statistics: IdentifiedArrayOf<TrackedGroup>

		init(state: StatisticsDetails.State) {
			self.isListSheetVisible = state.isListSheetVisible
			self.statistics = state.statistics
		}
	}

	enum ViewAction {
		case didAppear
		case didTapTrackedValue(id: String)
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
						ForEach(viewStore.statistics) { trackableGroup in
							Section(trackableGroup.category.title) {
								ForEach(trackableGroup.values) { trackable in
									Button { viewStore.send(.didTapTrackedValue(id: trackable.id)) } label: {
										HStack {
											LabeledContent(trackable.title, value: trackable.value)
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
		case let .didTapTrackedValue(id):
			self = .view(.didTapTrackedValue(id: id))
		case let .setListSheet(isPresented):
			self = .view(.setListSheet(isPresented: isPresented))
		}
	}
}
