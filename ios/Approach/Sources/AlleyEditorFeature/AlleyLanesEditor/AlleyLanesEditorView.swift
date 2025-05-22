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
	@Bindable public var store: StoreOf<AlleyLanesEditor>

	@Environment(\.safeAreaInsetsProvider) private var safeAreaInsetsProvider

	@State private var addLaneSheetHeight: CGFloat = .zero

	public init(store: StoreOf<AlleyLanesEditor>) {
		self.store = store
	}

	public var body: some View {
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
					Label(Strings.Lane.List.add, systemImage: "plus.square")
				}

				Button { send(.didTapAddMultipleLanesButton) } label: {
					Label(Strings.Lane.List.addMultiple, systemImage: "plus.square.on.square")
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
				.onGeometryChange(
					for: CGFloat.self,
					of: { $0.size.height },
					action: { addLaneSheetHeight = $0 }
				)
				.presentationDetents([.height(addLaneSheetHeight), .medium])
		}
	}
}
