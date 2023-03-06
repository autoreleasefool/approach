import ComposableArchitecture
import FeatureActionLibrary
import LaneEditorFeature
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

public struct AlleyLanesEditorView: View {
	let store: StoreOf<AlleyLanesEditor>

	@Environment(\.safeAreaInsets) private var safeAreaInsets

	@State private var addLaneSheetHeight: CGFloat = .zero

	struct ViewState: Equatable {
		let lanes: IdentifiedArrayOf<LaneEditor.State>?
		let isAddLaneFormPresented: Bool

		init(state: AlleyLanesEditor.State) {
			self.lanes = state.lanes
			self.isAddLaneFormPresented = state.addLaneForm != nil
		}
	}

	enum ViewAction {
		case didAppear
		case didTapAddLaneButton
		case didTapAddMultipleLanesButton
		case setAddLaneForm(isPresented: Bool)
	}

	public init(store: StoreOf<AlleyLanesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleyLanesEditor.Action.init) { viewStore in
			List {
				Section {
					ForEachStore(
						store.scope(state: \.lanes, action: /AlleyLanesEditor.Action.InternalAction.laneEditor(id:action:))
					) {
						LaneEditorView(store: $0)
					}
				} footer: {
					Text(Strings.Lane.Editor.Fields.IsAgainstWall.help)
				}

				Section {
					Button { viewStore.send(.didTapAddLaneButton) } label: {
						Label(Strings.Lane.List.add, systemImage: "plus.square")
					}

					Button { viewStore.send(.didTapAddMultipleLanesButton) } label: {
						Label(Strings.Lane.List.addMultiple, systemImage: "plus.square.on.square")
					}
				}
			}
			.navigationTitle(Strings.Lane.List.title)
			.onAppear { viewStore.send(.didAppear) }
			.alert(
				self.store.scope(state: \.alert, action: { AlleyLanesEditor.Action.view(.alert($0)) }),
				dismiss: .didTapDismissButton
			)
			.sheet(isPresented: viewStore.binding(
				get: \.isAddLaneFormPresented,
				send: ViewAction.setAddLaneForm(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.addLaneForm, action: /AlleyLanesEditor.Action.InternalAction.addLaneForm)) {
					AddLaneFormView(store: $0)
						.padding()
						.overlay {
							GeometryReader { geometryProxy in
								Color.clear
									.preference(
										key: HeightPreferenceKey.self,
										value: geometryProxy.size.height + safeAreaInsets.bottom
									)
							}
						}
				}
				.onPreferenceChange(HeightPreferenceKey.self) { newHeight in
					addLaneSheetHeight = newHeight
				}
				.presentationDetents([.height(addLaneSheetHeight), .medium])
			}
		}
	}
}

extension AlleyLanesEditor.Action {
	init(action: AlleyLanesEditorView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case .didTapAddLaneButton:
			self = .view(.didTapAddLaneButton)
		case .didTapAddMultipleLanesButton:
			self = .view(.didTapAddMultipleLanesButton)
		case let .setAddLaneForm(isPresented):
			self = .view(.setAddLaneForm(isPresented: isPresented))
		}
	}
}

private struct HeightPreferenceKey: PreferenceKey {
	static var defaultValue: CGFloat = .zero
	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = nextValue()
	}
}
