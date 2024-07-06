import AnalyticsServiceInterface
import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ExtensionsPackageLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct AvatarEditor: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let id: UUID
		public let initialAvatar: Avatar.Summary?

		public var avatarKind: AvatarKind
		public var text: TextAvatarEditor.State
		public var photo: PhotoAvatarEditor.State

		public let isPhotoAvatarsEnabled: Bool

		public init(avatar: Avatar.Summary?) {
			@Dependency(\.uuid) var uuid
			self.id = avatar?.id ?? uuid()
			self.initialAvatar = avatar
			self.avatarKind = avatar?.kind ?? .text
			self.text = TextAvatarEditor.State(avatar: avatar)
			self.photo = PhotoAvatarEditor.State(avatar: avatar)

			@Dependency(\.featureFlags) var featureFlags
			let isPhotoAvatarsEnabled = featureFlags.isFlagEnabled(.photoAvatars)
			self.isPhotoAvatarsEnabled = isPhotoAvatarsEnabled
		}

		var hasChanges: Bool {
			initialAvatar?.value != avatar?.value
		}

		var avatar: Avatar.Summary? {
			switch avatarKind {
			case .text:
				Avatar.Summary(id: id, value: text.value)
			case .photo:
				if let photo = photo.value {
					Avatar.Summary(id: id, value: photo)
				} else {
					nil
				}
			}
		}

		var isLoadingAvatar: Bool {
			switch avatarKind {
			case .text: false
			case .photo: photo.imageState.isLoading
			}
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case didTapCancel
			case didTapDone
		}
		@CasePathable public enum Delegate {
			case didFinishEditing(Avatar.Summary?)
		}
		@CasePathable public enum Internal {
			case text(TextAvatarEditor.Action)
			case photo(PhotoAvatarEditor.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum AvatarKind: Identifiable, CaseIterable, CustomStringConvertible {
		case text
		case photo

		public var id: Self { self }
		public var description: String {
			switch self {
			case .text: Strings.Avatar.Editor.Kind.text
			case .photo: Strings.Avatar.Editor.Kind.photo
			}
		}
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.text, action: \.internal.text) {
			TextAvatarEditor()
		}

		Scope(state: \.photo, action: \.internal.photo) {
			PhotoAvatarEditor()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapCancel:
					return .concatenate(
						.send(.delegate(.didFinishEditing(state.initialAvatar))),
						.run { _ in await dismiss() }
					)

				case .didTapDone:
					return .concatenate(
						.send(.delegate(.didFinishEditing(state.avatar))),
						.run { _ in await dismiss() }
					)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .text(.delegate(delegateAction)):
					switch delegateAction {
					case .doNothing:
						return .none
					}

				case let .photo(.delegate(delegateAction)):
					switch delegateAction {
					case .doNothing:
						return .none
					}

				case .text(.view), .text(.internal), .text(.binding),
						.photo(.view), .photo(.internal), .photo(.binding):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

// MARK: - Extensions

extension Color {
	var rgb: Avatar.Background.RGB {
		let (red, green, blue, _) = UIColor(self).rgba
		return .init(red, green, blue)
	}
}

extension Avatar.Summary {
	var kind: AvatarEditor.AvatarKind {
		switch value {
		case .url, .text: .text
		case .data: .photo
		}
	}
}

// MARK: - View

@ViewAction(for: AvatarEditor.self)
public struct AvatarEditorView: View {
	@Bindable public var store: StoreOf<AvatarEditor>

	public init(store: StoreOf<AvatarEditor>) {
		self.store = store
	}

	public var body: some View {
		VStack {
			if store.isPhotoAvatarsEnabled {
				editorTabs
			} else {
				textEditor
			}
		}
		.navigationTitle(Strings.Avatar.Editor.title)
		.navigationBarTitleDisplayMode(.inline)
		.navigationBarBackButtonHidden(true)
		.toolbar {
			ToolbarItem(placement: .navigationBarLeading) {
				Button(Strings.Action.cancel) { send(.didTapCancel) }
			}

			ToolbarItem(placement: .navigationBarTrailing) {
				Button(Strings.Action.done) { send(.didTapDone) }
					.disabled(!store.hasChanges)
			}
		}
		.onAppear { send(.onAppear) }
	}

	@ViewBuilder private var editorTabs: some View {
		Picker(
			Strings.Avatar.Editor.Kind.title,
			selection: $store.avatarKind.animation()
		) {
			ForEach(AvatarEditor.AvatarKind.allCases) {
				Text(String(describing: $0)).tag($0)
			}
		}
		.pickerStyle(.segmented)
		.padding(.horizontal)

		HStack {
			AvatarView(store.avatar, size: .extraExtraLargeIcon)
				.shadow(radius: .standardShadowRadius)
				.overlay(content: {
					if store.isLoadingAvatar {
						ProgressView()
							.progressViewStyle(.circular)
					}
				})
		}
		.frame(maxWidth: .infinity)
		.padding()

		TabView(selection: $store.avatarKind) {
			List {
				TextAvatarEditorView(
					store: store.scope(state: \.text, action: \.internal.text)
				)
			}
			.tag(AvatarEditor.AvatarKind.text)

			List {
				PhotoAvatarEditorView(
					store: store.scope(state: \.photo, action: \.internal.photo)
				)
			}
			.tag(AvatarEditor.AvatarKind.photo)
		}
		.tabViewStyle(.page(indexDisplayMode: .never))
		.ignoresSafeArea(.container, edges: .bottom)
	}

	@ViewBuilder private var textEditor: some View {
		List {
			Section {
				AvatarView(store.avatar, size: .extraExtraLargeIcon)
					.shadow(radius: .standardShadowRadius)
			}
			.listRowBackground(Color.clear)
			.frame(maxWidth: .infinity)

			TextAvatarEditorView(
				store: store.scope(state: \.text, action: \.internal.text)
			)
		}
	}
}
