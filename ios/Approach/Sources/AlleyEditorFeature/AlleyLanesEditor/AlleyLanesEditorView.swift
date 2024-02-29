import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import LaneEditorFeature
import LanesRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

@ViewAction(for: AlleyLanesEditor.self)
public struct AlleyLanesEditorView: View {
	@Perception.Bindable public var store: StoreOf<AlleyLanesEditor>

	@Environment(\.safeAreaInsets) private var safeAreaInsets

	@State private var addLaneSheetHeight: CGFloat = .zero

	public init(store: StoreOf<AlleyLanesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			List {
				Section {
					ForEach(store.scope(state: \.existingLaneEditors, action: \.internal.laneEditor), id: \.state.id) {
						LaneEditorView(store: $0)
					}
					ForEach(store.scope(state: \.newLaneEditors, action: \.internal.laneEditor), id: \.state.id) {
						LaneEditorView(store: $0)
					}
				} footer: {
					Text(Strings.Lane.Editor.Fields.Position.help)
				}

				Section {
					Button { send(.didTapAddLaneButton) } label: {
						Label(Strings.Lane.List.add, systemSymbol: .plusSquare)
					}

					Button { send(.didTapAddMultipleLanesButton) } label: {
						Label(Strings.Lane.List.addMultiple, systemSymbol: .plusSquareOnSquare)
					}
				}
			}
			.navigationTitle(Strings.Lane.List.title)
			.onAppear { send(.onAppear) }
			.alert($store.scope(state: \.destination?.alert, action: \.internal.destination.alert))
			.errors(store: store.scope(state: \.errors, action: \.internal.errors))
			.sheet(item: $store.scope(state: \.destination?.addLaneForm, action: \.internal.destination.addLaneForm)) {
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
}

private struct HeightPreferenceKey: PreferenceKey {
	static var defaultValue: CGFloat = .zero
	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = nextValue()
	}
}
