import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GamesHeader: Reducer {
	public struct State: Equatable {
		public let currentGameIndex: Int

		init(currentGameIndex: Int) {
			self.currentGameIndex = currentGameIndex
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {}
		public enum DelegateAction: Equatable {
			case didCloseEditor
			case didOpenSettings
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .never:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

public struct GamesHeaderView: View {
	let store: StoreOf<GamesHeader>

	enum ViewAction {
		case didTapClose
		case didTapSettings
	}

	init(store: StoreOf<GamesHeader>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: GamesHeader.Action.init, content: { viewStore in
			HStack {
				headerButton(systemName: "chevron.backward") { viewStore.send(.didTapClose) }
				Spacer()
				Text(Strings.Game.titleWithOrdinal(viewStore.currentGameIndex + 1))
					.font(.caption)
					.foregroundColor(.white)
				Spacer()
				headerButton(systemName: "gear") { viewStore.send(.didTapSettings) }
			}
		})
	}

	private func headerButton(systemName: String, action: @escaping () -> Void) -> some View {
		Button(action: action) {
			Image(systemName: systemName)
				.resizable()
				.scaledToFit()
				.frame(width: .smallIcon, height: .smallIcon)
				.foregroundColor(.white)
				.padding()
		}
	}
}

extension GamesHeader.Action {
	init(action: GamesHeaderView.ViewAction) {
		switch action {
		case .didTapClose:
			self = .delegate(.didCloseEditor)
		case .didTapSettings:
			self = .delegate(.didOpenSettings)
		}
	}
}
