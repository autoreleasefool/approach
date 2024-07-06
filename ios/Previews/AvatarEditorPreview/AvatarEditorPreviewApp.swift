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
				$0.featureFlags.isEnabled = {
					$0 == FeatureFlag.photoAvatars
				}
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
