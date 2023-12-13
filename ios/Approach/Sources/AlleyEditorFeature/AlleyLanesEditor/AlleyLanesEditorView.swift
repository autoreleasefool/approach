import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import LaneEditorFeature
import LanesRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct AlleyLanesEditorView: View {
	let store: StoreOf<AlleyLanesEditor>

	@Environment(\.safeAreaInsets) private var safeAreaInsets

	@State private var addLaneSheetHeight: CGFloat = .zero

	struct ViewState: Equatable {
		let existingLanes: IdentifiedArrayOf<Lane.Edit>
		let newLanes: IdentifiedArrayOf<Lane.Create>

		init(state: AlleyLanesEditor.State) {
			self.existingLanes = state.existingLanes
			self.newLanes = state.newLanes
		}
	}

	public init(store: StoreOf<AlleyLanesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			List {
				Section {
					ForEachStore(store.scope(state: \.existingLaneEditors, action: \.internal.laneEditor)) {
						LaneEditorView(store: $0)
					}
					ForEachStore(store.scope(state: \.newLaneEditors, action: \.internal.laneEditor)) {
						LaneEditorView(store: $0)
					}
				} footer: {
					Text(Strings.Lane.Editor.Fields.Position.help)
				}

				Section {
					Button { viewStore.send(.didTapAddLaneButton) } label: {
						Label(Strings.Lane.List.add, systemSymbol: .plusSquare)
					}

					Button { viewStore.send(.didTapAddMultipleLanesButton) } label: {
						Label(Strings.Lane.List.addMultiple, systemSymbol: .plusSquareOnSquare)
					}
				}
			}
			.navigationTitle(Strings.Lane.List.title)
			.onAppear { viewStore.send(.onAppear) }
		})
		.alert(store: store.scope(state: \.$alert, action: \.view.alert))
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.sheet(store: store.scope(state: \.$addLaneForm, action: \.internal.addLaneForm)) {
			AddLaneFormView(store: $0)
				.padding()
				.overlay {
					GeometryReader { proxy in
						Color.clear
							.preference(
								key: HeightPreferenceKey.self,
								value: proxy.size.height + safeAreaInsets.bottom
							)
					}
				}
				.onPreferenceChange(HeightPreferenceKey.self) { newHeight in
					addLaneSheetHeight = newHeight
				}
				.presentationDetents([.height(addLaneSheetHeight), .medium])
		}
	}
}

private struct HeightPreferenceKey: PreferenceKey {
	static var defaultValue: CGFloat = .zero
	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = nextValue()
	}
}
