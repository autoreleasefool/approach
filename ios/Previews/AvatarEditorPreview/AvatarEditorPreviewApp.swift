import AppPreviewFeature
import AvatarEditorFeature
import ComposableArchitecture
import FeatureFlagsLibrary
import SwiftUI

@main
public struct AvatarEditorPreviewApp: App {
	let store: Store = {
		return Store(
			initialState: AvatarEditor.State(avatar: nil),
			reducer: { AvatarEditor() },
			withDependencies: {
				$0.analytics = .mock
				$0.breadcrumbs = .mock
				$0.database = .defaults
				$0.errors = .mock
				$0.featureFlags = .enabling([.photoAvatars])
			}
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
