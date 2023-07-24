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
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			VStack {
				HStack {
					TextField(
						Strings.Lane.Properties.label,
						text: viewStore.$label
					)
				}

				Picker(
					Strings.Lane.Properties.position,
					selection: viewStore.$position
				) {
					ForEach(Lane.Position.allCases) {
						Text(String(describing: $0)).tag($0)
					}
				}
			}
			.swipeActions(allowsFullSwipe: true) {
				DeleteButton { viewStore.send(.didSwipe(.delete)) }
			}
		})
	}
}
