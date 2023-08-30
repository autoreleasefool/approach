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
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapCloseButton
			case didTapSettingsButton
		}
		public enum DelegateAction: Equatable {
			case didCloseEditor
			case didOpenSettings
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapCloseButton:
					return .send(.delegate(.didCloseEditor))

				case .didTapSettingsButton:
					return .send(.delegate(.didOpenSettings))
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

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			HStack {
				headerButton(systemSymbol: .chevronBackward) { viewStore.send(.didTapCloseButton) }
				Spacer()
				Text(Strings.Game.titleWithOrdinal(viewStore.currentGameIndex + 1))
					.font(.caption)
					.foregroundColor(.white)
				Spacer()
				headerButton(systemSymbol: .gear) { viewStore.send(.didTapSettingsButton) }
			}
		})
	}

	private func headerButton(systemSymbol: SFSymbol, action: @escaping () -> Void) -> some View {
		Button(action: action) {
			Image(systemSymbol: systemSymbol)
				.resizable()
				.scaledToFit()
				.frame(width: .smallIcon, height: .smallIcon)
				.foregroundColor(.white)
				.padding()
		}
	}
}
