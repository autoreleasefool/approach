import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@ViewAction(for: LaneEditor.self)
public struct LaneEditorView: View {
	@Bindable public var store: StoreOf<LaneEditor>

	public init(store: StoreOf<LaneEditor>) {
		self.store = store
	}

	public var body: some View {
		VStack {
			HStack {
				TextField(
					Strings.Lane.Properties.label,
					text: $store.label
				)
			}

			Picker(
				Strings.Lane.Properties.position,
				selection: $store.position
			) {
				ForEach(Lane.Position.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		}
		.swipeActions(allowsFullSwipe: true) {
			DeleteButton { send(.didSwipe(.delete)) }
		}
	}
}
