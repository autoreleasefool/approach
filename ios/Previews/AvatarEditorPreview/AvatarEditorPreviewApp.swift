import AvatarEditorFeature
import ComposableArchitecture
import SwiftUI

@main
public struct AvatarEditorPreviewApp: App {
	let store: Store = {
		return Store(
			initialState: AvatarEditor.State(avatar: nil),
			reducer: { AvatarEditor() }
		)
	}()

	public init() {}

	public var body: some Scene {
		WindowGroup {
			NavigationStack {
				AvatarEditorView(store: store)
			}
		}
	}
}
