import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct LaneEditorView: View {
	let store: StoreOf<LaneEditor>

	public init(store: StoreOf<LaneEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, content: { viewStore in
			VStack {
				HStack {
					TextField(
						Strings.Lane.Properties.label,
						text: viewStore.binding(\.$label)
					)
				}

				Picker(
					Strings.Lane.Properties.position,
					selection: viewStore.binding(\.$position)
				) {
					ForEach(Lane.Position.allCases) {
						Text(String(describing: $0)).tag($0)
					}
				}
			}
			.swipeActions(allowsFullSwipe: true) {
				DeleteButton { viewStore.send(.view(.didSwipe(.delete))) }
			}
		})
	}
}
