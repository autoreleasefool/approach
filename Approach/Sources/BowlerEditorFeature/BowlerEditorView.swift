import AvatarEditorFeature
import BaseFormLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SharedModelsLibrary
import SharedModelsViewsLibrary
import SwiftUI
import ViewsLibrary

public struct BowlerEditorView: View {
	let store: StoreOf<BowlerEditor>

	struct ViewState: Equatable {
		@BindableState var name: String
		let avatar: Avatar
		let isAvatarEditorPresented: Bool

		init(state: BowlerEditor.State) {
			self.name = state.base.form.name
			self.avatar = state.base.form.avatar
			self.isAvatarEditorPresented = state.isAvatarEditorPresented
		}
	}

	enum ViewAction: BindableAction {
		case didTapAvatar
		case setAvatarEditorSheet(isPresented: Bool)
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<BowlerEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlerEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: /BowlerEditor.Action.InternalAction.form)) {
				Section {
					HStack(alignment: .center) {
						Spacer()
						Button { viewStore.send(.didTapAvatar) } label: {
							AvatarView(viewStore.avatar, size: .large, editable: true)
						}
						.buttonStyle(TappableElement())
						Spacer()
					}
				}
				.listRowBackground(Color(uiColor: .systemBackground))

				Section(Strings.Editor.Fields.Details.title) {
					TextField(
						Strings.Editor.Fields.Details.name,
						text: viewStore.binding(\.$name)
					)
					.textContentType(.name)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))
			}
			.navigationBarTitleDisplayMode(.inline)
			.sheet(isPresented: viewStore.binding(
				get: \.isAvatarEditorPresented,
				send: ViewAction.setAvatarEditorSheet(isPresented:)
			)) {
				AvatarEditorView(store: store.scope(state: \.base.form.avatarEditor, action: /BowlerEditor.Action.InternalAction.avatar))
			}
		}
	}
}

extension BowlerEditor.State {
	var view: BowlerEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.name = newValue.name
		}
	}
}

extension BowlerEditor.Action {
	init(action: BowlerEditorView.ViewAction) {
		switch action {
		case .didTapAvatar:
			self = .view(.didTapAvatar)
		case let .setAvatarEditorSheet(isPresented):
			self = .view(.setAvatarEditorPresented(isPresented: isPresented))
		case let .binding(action):
			self = .binding(action.pullback(\BowlerEditor.State.view))
		}
	}
}

#if DEBUG
struct BowlerEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		NavigationView {
			BowlerEditorView(store:
				.init(
					initialState: .init(mode: .edit(.init(id: UUID(), name: "Joseph", avatar: .text("JR", .random())))),
					reducer: BowlerEditor()
				)
			)
		}
	}
}
#endif
