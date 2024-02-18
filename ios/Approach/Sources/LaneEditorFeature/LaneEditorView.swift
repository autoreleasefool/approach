import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

@ViewAction(for: LaneEditor.self)
public struct LaneEditorView: View {
	@Perception.Bindable public var store: StoreOf<LaneEditor>

	public init(store: StoreOf<LaneEditor>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
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
}
